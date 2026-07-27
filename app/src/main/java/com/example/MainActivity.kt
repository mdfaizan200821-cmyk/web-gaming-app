package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.games.*
import com.example.ui.models.FunZoneGameType
import com.example.ui.models.MainGameType
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.theme.WebGamingTheme
import com.example.ui.viewmodel.ActiveGameSession
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebGamingTheme {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isProfileModalOpen by viewModel.isProfileModalOpen.collectAsStateWithLifecycle()
    val stakeCarouselGame by viewModel.stakeCarouselGame.collectAsStateWithLifecycle()
    val activeGameSession by viewModel.activeGameSession.collectAsStateWithLifecycle()
    val resultModal by viewModel.resultModal.collectAsStateWithLifecycle()
    val isWatchingAd by viewModel.isWatchingAd.collectAsStateWithLifecycle()
    val adTimerSeconds by viewModel.adTimerSeconds.collectAsStateWithLifecycle()
    val chestResult by viewModel.chestResult.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CrispBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (activeGameSession == null) {
                TopHeader(
                    user = userState,
                    onProfileClick = { viewModel.openProfileModal() },
                    onAddCoinsClick = { viewModel.claimDailyBonus() },
                    onAddGemsClick = { viewModel.selectTab(AppTab.CHESTS) }
                )
            }
        },
        bottomBar = {
            if (activeGameSession == null) {
                BottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CrispBackground)
        ) {
            // Check if active game session is running
            if (activeGameSession != null) {
                when (val session = activeGameSession!!) {
                    is ActiveGameSession.MainGame -> {
                        when (session.gameType) {
                            MainGameType.CARROM_POOL -> CarromGame(
                                stake = session.stake,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Carrom Pool", coins)
                                    else viewModel.triggerGameDefeat("Carrom Pool")
                                }
                            )
                            MainGameType.RUMMY -> RummyGame(
                                stake = session.stake,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Rummy", coins)
                                    else viewModel.triggerGameDefeat("Rummy")
                                }
                            )
                            MainGameType.CHESS -> ChessGame(
                                stake = session.stake,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Chess", coins)
                                    else viewModel.triggerGameDefeat("Chess")
                                }
                            )
                            MainGameType.LUDO -> LudoGame(
                                stake = session.stake,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Ludo", coins)
                                    else viewModel.triggerGameDefeat("Ludo")
                                }
                            )
                            MainGameType.TIC_TAC_TOE -> TicTacToeGame(
                                stake = session.stake,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Tic Tac Toe", coins)
                                    else viewModel.triggerGameDefeat("Tic Tac Toe")
                                }
                            )
                        }
                    }
                    is ActiveGameSession.FunZone -> {
                        when (session.gameType) {
                            FunZoneGameType.SUBWAY_RUNNER -> SubwayRunnerGame(
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Subway Runner", coins)
                                    else viewModel.triggerGameDefeat("Subway Runner")
                                }
                            )
                            FunZoneGameType.FRUIT_SLASH -> FruitSlashGame(
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Fruit Slash", coins)
                                    else viewModel.triggerGameDefeat("Fruit Slash")
                                }
                            )
                            FunZoneGameType.BLOCK_PUZZLE -> BlockPuzzleGame(
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Block Puzzle", coins)
                                    else viewModel.triggerGameDefeat("Block Puzzle")
                                }
                            )
                            FunZoneGameType.TALKING_CAT -> TalkingCatGame(
                                onBack = { viewModel.exitGameSession() }
                            )
                            FunZoneGameType.CANDY_MATCH -> CandyMatchGame(
                                level = session.level,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Candy Match", coins)
                                    else viewModel.triggerGameDefeat("Candy Match")
                                }
                            )
                            FunZoneGameType.STICKMAN_THIEF -> StickmanThiefGame(
                                level = session.level,
                                onBack = { viewModel.exitGameSession() },
                                onGameFinished = { win, coins ->
                                    if (win) viewModel.triggerGameWin("Stickman Thief", coins)
                                    else viewModel.triggerGameDefeat("Stickman Thief")
                                }
                            )
                        }
                    }
                }
            } else {
                // Tab Screens
                when (currentTab) {
                    AppTab.LOBBY -> LobbyScreen(
                        user = userState,
                        onOpenMainGame = { gameType -> viewModel.openStakeCarousel(gameType) },
                        onOpenFunZoneGame = { funGame -> viewModel.startFunZoneGame(funGame) },
                        onWatchAdClick = { viewModel.watchAdForCoins() },
                        onClaimDailyBonusClick = { viewModel.claimDailyBonus() }
                    )
                    AppTab.RANKS -> RanksScreen()
                    AppTab.CHESTS -> ChestsScreen(
                        userGems = userState?.gems ?: 200,
                        onOpenChestClick = { chest -> viewModel.openChest(chest) }
                    )
                    AppTab.INVITE -> InviteScreen(
                        referralCode = userState?.referralCode ?: "MEMBER888",
                        onCopyCode = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", userState?.referralCode ?: "MEMBER888"))
                            viewModel.showToast("Referral Code Copied!")
                        },
                        onShareCode = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Join me on Web Gaming with my code ${userState?.referralCode ?: "MEMBER888"} and get 5,000 FREE COINS!")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Referral Code"))
                        },
                        onClaimReward = { viewModel.claimReferralReward() }
                    )
                    AppTab.SETTINGS -> SettingsScreen(
                        user = userState,
                        onSaveSettings = { music, sfx, chat, lang ->
                            viewModel.updateSettings(music, sfx, chat, lang)
                        },
                        onSignOut = { viewModel.showToast("Signed Out!") }
                    )
                }
            }
        }

        // Modals & Popups
        if (isProfileModalOpen) {
            ProfileCustomizationModal(
                user = userState,
                onDismiss = { viewModel.closeProfileModal() },
                onEquipAvatar = { avatarId -> viewModel.equipAvatar(avatarId) },
                onEquipFrame = { frameId -> viewModel.equipFrame(frameId) }
            )
        }

        if (stakeCarouselGame != null) {
            StakeSelectionCarouselModal(
                gameType = stakeCarouselGame!!,
                userCoins = userState?.coins ?: 10815L,
                onClose = { viewModel.closeStakeCarousel() },
                onSelectStake = { stake ->
                    viewModel.startMainGameWithStake(stakeCarouselGame!!, stake)
                }
            )
        }

        if (resultModal != null) {
            WinDefeatModal(
                result = resultModal!!,
                onDismiss = {
                    viewModel.dismissResultModal()
                    viewModel.exitGameSession()
                }
            )
        }

        if (isWatchingAd) {
            AdWatchModal(secondsRemaining = adTimerSeconds)
        }

        if (chestResult != null) {
            ChestOpeningResultModal(
                result = chestResult!!,
                onDismiss = { viewModel.dismissChestResult() }
            )
        }
    }
}
