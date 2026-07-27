package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.UserEntity
import com.example.data.repository.UserRepository
import com.example.ui.models.MainGameType
import com.example.ui.models.FunZoneGameType
import com.example.ui.models.StakeOption
import com.example.ui.models.ChestOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab {
    LOBBY, RANKS, CHESTS, INVITE, SETTINGS
}

sealed class ActiveGameSession {
    data class MainGame(val gameType: MainGameType, val stake: StakeOption) : ActiveGameSession()
    data class FunZone(val gameType: FunZoneGameType, val level: Int = 1) : ActiveGameSession()
}

data class ResultModalData(
    val isWin: Boolean,
    val title: String, // "WIN" or "OOPS"
    val subtitle: String,
    val rewardCoins: Long,
    val xpEarned: Int,
    val gameName: String
)

data class ChestOpeningResult(
    val chestName: String,
    val coinsWon: Long,
    val gemsWon: Int,
    val itemUnlocked: String?
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    val userState: StateFlow<UserEntity?>

    private val _currentTab = MutableStateFlow(AppTab.LOBBY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isProfileModalOpen = MutableStateFlow(false)
    val isProfileModalOpen: StateFlow<Boolean> = _isProfileModalOpen.asStateFlow()

    private val _stakeCarouselGame = MutableStateFlow<MainGameType?>(null)
    val stakeCarouselGame: StateFlow<MainGameType?> = _stakeCarouselGame.asStateFlow()

    private val _activeGameSession = MutableStateFlow<ActiveGameSession?>(null)
    val activeGameSession: StateFlow<ActiveGameSession?> = _activeGameSession.asStateFlow()

    private val _resultModal = MutableStateFlow<ResultModalData?>(null)
    val resultModal: StateFlow<ResultModalData?> = _resultModal.asStateFlow()

    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd: StateFlow<Boolean> = _isWatchingAd.asStateFlow()

    private val _adTimerSeconds = MutableStateFlow(5)
    val adTimerSeconds: StateFlow<Int> = _adTimerSeconds.asStateFlow()

    private val _chestResult = MutableStateFlow<ChestOpeningResult?>(null)
    val chestResult: StateFlow<ChestOpeningResult?> = _chestResult.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = UserRepository(database.userDao())
        
        val mutableUser = MutableStateFlow<UserEntity?>(null)
        userState = mutableUser.asStateFlow()

        viewModelScope.launch {
            repository.ensureUserExists()
            repository.userFlow.collect { user ->
                mutableUser.value = user
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun openProfileModal() {
        _isProfileModalOpen.value = true
    }

    fun closeProfileModal() {
        _isProfileModalOpen.value = false
    }

    fun openStakeCarousel(gameType: MainGameType) {
        _stakeCarouselGame.value = gameType
    }

    fun closeStakeCarousel() {
        _stakeCarouselGame.value = null
    }

    fun startMainGameWithStake(gameType: MainGameType, stake: StakeOption) {
        viewModelScope.launch {
            val user = userState.value ?: return@launch
            if (user.coins < stake.entryFee) {
                showToast("Not enough coins! Required: ${stake.entryFee} coins")
                return@launch
            }
            val deducted = repository.deductCoins(stake.entryFee)
            if (deducted) {
                _stakeCarouselGame.value = null
                _activeGameSession.value = ActiveGameSession.MainGame(gameType, stake)
            } else {
                showToast("Deduction failed!")
            }
        }
    }

    fun startFunZoneGame(gameType: FunZoneGameType) {
        viewModelScope.launch {
            val user = userState.value ?: return@launch
            if (gameType.entryFee > 0) {
                if (user.coins < gameType.entryFee) {
                    showToast("Not enough coins! Entry fee is ${gameType.entryFee} coins")
                    return@launch
                }
                repository.deductCoins(gameType.entryFee)
            }
            val initialLevel = when (gameType) {
                FunZoneGameType.CANDY_MATCH -> user.candyMatchLevel
                FunZoneGameType.STICKMAN_THIEF -> user.stickmanThiefLevel
                else -> 1
            }
            _activeGameSession.value = ActiveGameSession.FunZone(gameType, initialLevel)
        }
    }

    fun exitGameSession() {
        _activeGameSession.value = null
    }

    fun triggerGameWin(gameName: String, rewardCoins: Long) {
        viewModelScope.launch {
            repository.recordGameResult(isWin = true, rewardCoins = rewardCoins)
            // If fun zone levels
            val session = _activeGameSession.value
            if (session is ActiveGameSession.FunZone) {
                when (session.gameType) {
                    FunZoneGameType.CANDY_MATCH -> repository.updateCandyMatchLevel(session.level + 1)
                    FunZoneGameType.STICKMAN_THIEF -> repository.updateStickmanThiefLevel(session.level + 1)
                    else -> {}
                }
            }
            _resultModal.value = ResultModalData(
                isWin = true,
                title = "WIN",
                subtitle = "VICTORY! YOU DOMINATED THE MATCH",
                rewardCoins = rewardCoins,
                xpEarned = 150,
                gameName = gameName
            )
        }
    }

    fun triggerGameDefeat(gameName: String) {
        viewModelScope.launch {
            repository.recordGameResult(isWin = false, rewardCoins = 0L)
            _resultModal.value = ResultModalData(
                isWin = false,
                title = "OOPS",
                subtitle = "DEFEAT! BETTER LUCK NEXT TIME",
                rewardCoins = 0L,
                xpEarned = 50,
                gameName = gameName
            )
        }
    }

    fun dismissResultModal() {
        _resultModal.value = null
    }

    fun equipAvatar(avatarId: String) {
        viewModelScope.launch {
            repository.equipAvatar(avatarId)
            showToast("Equipped Avatar!")
        }
    }

    fun equipFrame(frameId: String) {
        viewModelScope.launch {
            repository.equipFrame(frameId)
            showToast("Equipped Divine Golden Frame!")
        }
    }

    fun watchAdForCoins() {
        if (_isWatchingAd.value) return
        _isWatchingAd.value = true
        _adTimerSeconds.value = 5
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _adTimerSeconds.value = i
                delay(1000L)
            }
            _isWatchingAd.value = false
            repository.addCoins(1000L)
            showToast("Ad Complete! +1,000 Coins added!")
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            repository.addCoins(2500L)
            repository.addGems(10)
            showToast("Daily Bonus Claimed! +2,500 Coins & +10 Gems!")
        }
    }

    fun openChest(chest: ChestOption) {
        viewModelScope.launch {
            val user = userState.value ?: return@launch
            if (user.gems < chest.gemPrice) {
                showToast("Not enough Gems! Needed: ${chest.gemPrice} Gems")
                return@launch
            }
            val deducted = repository.deductGems(chest.gemPrice)
            if (deducted) {
                val coinsWon = (chest.minCoinsReward..chest.maxCoinsReward).random()
                val gemsWon = chest.guaranteedGems
                repository.addCoins(coinsWon)
                repository.addGems(gemsWon)
                _chestResult.value = ChestOpeningResult(
                    chestName = chest.name,
                    coinsWon = coinsWon,
                    gemsWon = gemsWon,
                    itemUnlocked = if ((1..2).random() == 1) "Divine Golden Aura Frame" else "Cyber King Avatar"
                )
            }
        }
    }

    fun dismissChestResult() {
        _chestResult.value = null
    }

    fun updateSettings(music: Boolean, sfx: Boolean, chat: Boolean, language: String) {
        viewModelScope.launch {
            repository.updateSettings(music, sfx, chat, language)
            showToast("Settings Updated!")
        }
    }

    fun claimReferralReward() {
        viewModelScope.launch {
            repository.addCoins(5000L)
            showToast("Referral Reward Claimed! +5,000 Coins!")
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
