package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Master Gamer",
    val avatarIcon: String = "M",
    val equippedAvatarId: String = "avatar_m",
    val equippedFrameId: String = "divine_golden",
    val level: Int = 12,
    val xp: Int = 3400,
    val coins: Long = 10815L,
    val gems: Int = 200,
    val wins: Int = 42,
    val losses: Int = 12,
    val totalMatches: Int = 54,
    val referralCode: String = "MEMBER888",
    val unlockedAvatarsJson: String = "avatar_m,cyber_king,royal_queen",
    val unlockedFramesJson: String = "golden_ring,divine_golden",
    val candyMatchLevel: Int = 1,
    val stickmanThiefLevel: Int = 1,
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val chatEnabled: Boolean = true,
    val selectedLanguage: String = "English"
)
