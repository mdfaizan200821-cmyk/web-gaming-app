package com.example.data.repository

import com.example.data.database.UserDao
import com.example.data.database.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val userDao: UserDao) {

    val userFlow: Flow<UserEntity?> = userDao.getUserFlow()

    suspend fun ensureUserExists(): UserEntity {
        val existing = userDao.getUser()
        if (existing == null) {
            val newUser = UserEntity()
            userDao.insertUser(newUser)
            return newUser
        }
        return existing
    }

    suspend fun addCoins(amount: Long) {
        val current = ensureUserExists()
        val updated = current.copy(
            coins = (current.coins + amount).coerceAtLeast(0L)
        )
        userDao.updateUser(updated)
    }

    suspend fun deductCoins(amount: Long): Boolean {
        val current = ensureUserExists()
        if (current.coins < amount) return false
        val updated = current.copy(coins = current.coins - amount)
        userDao.updateUser(updated)
        return true
    }

    suspend fun addGems(amount: Int) {
        val current = ensureUserExists()
        val updated = current.copy(gems = (current.gems + amount).coerceAtLeast(0))
        userDao.updateUser(updated)
    }

    suspend fun deductGems(amount: Int): Boolean {
        val current = ensureUserExists()
        if (current.gems < amount) return false
        val updated = current.copy(gems = current.gems - amount)
        userDao.updateUser(updated)
        return true
    }

    suspend fun recordGameResult(isWin: Boolean, rewardCoins: Long) {
        val current = ensureUserExists()
        val newWins = if (isWin) current.wins + 1 else current.wins
        val newLosses = if (!isWin) current.losses + 1 else current.losses
        val newXp = current.xp + if (isWin) 150 else 50
        val newLevel = 1 + (newXp / 300)
        val newCoins = (current.coins + rewardCoins).coerceAtLeast(0L)

        val updated = current.copy(
            wins = newWins,
            losses = newLosses,
            totalMatches = current.totalMatches + 1,
            xp = newXp,
            level = newLevel,
            coins = newCoins
        )
        userDao.updateUser(updated)
    }

    suspend fun equipAvatar(avatarId: String) {
        val current = ensureUserExists()
        val updated = current.copy(equippedAvatarId = avatarId)
        userDao.updateUser(updated)
    }

    suspend fun equipFrame(frameId: String) {
        val current = ensureUserExists()
        val updated = current.copy(equippedFrameId = frameId)
        userDao.updateUser(updated)
    }

    suspend fun unlockAvatar(avatarId: String) {
        val current = ensureUserExists()
        val currentList = current.unlockedAvatarsJson.split(",").toMutableSet()
        currentList.add(avatarId)
        val updated = current.copy(unlockedAvatarsJson = currentList.joinToString(","))
        userDao.updateUser(updated)
    }

    suspend fun unlockFrame(frameId: String) {
        val current = ensureUserExists()
        val currentList = current.unlockedFramesJson.split(",").toMutableSet()
        currentList.add(frameId)
        val updated = current.copy(unlockedFramesJson = currentList.joinToString(","))
        userDao.updateUser(updated)
    }

    suspend fun updateCandyMatchLevel(newLevel: Int) {
        val current = ensureUserExists()
        if (newLevel > current.candyMatchLevel) {
            userDao.updateUser(current.copy(candyMatchLevel = newLevel))
        }
    }

    suspend fun updateStickmanThiefLevel(newLevel: Int) {
        val current = ensureUserExists()
        if (newLevel > current.stickmanThiefLevel) {
            userDao.updateUser(current.copy(stickmanThiefLevel = newLevel))
        }
    }

    suspend fun updateSettings(music: Boolean, sfx: Boolean, chat: Boolean, language: String) {
        val current = ensureUserExists()
        userDao.updateUser(current.copy(
            musicEnabled = music,
            sfxEnabled = sfx,
            chatEnabled = chat,
            selectedLanguage = language
        ))
    }
}
