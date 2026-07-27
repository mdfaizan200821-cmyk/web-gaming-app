package com.example.ui.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.GoldAuraGlow
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.GemCyan
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.CrimsonRed

data class AvatarItem(
    val id: String,
    val name: String,
    val iconSymbol: String,
    val gemPrice: Int = 0,
    val badge: String = "Common",
    val primaryColor: Color = RoyalPurple
)

data class AuraFrameItem(
    val id: String,
    val name: String,
    val description: String,
    val gemPrice: Int = 0,
    val glowColors: List<Color> = listOf(GoldenYellow, GoldAuraGlow)
)

data class StakeOption(
    val entryFee: Long,
    val title: String,
    val prizePool: Long,
    val onlinePlayers: Int,
    val platformRank: String
)

enum class MainGameType(val title: String, val subtitle: String, val iconResName: String, val description: String) {
    CARROM_POOL("Carrom Pool", "Pocket & Striker", "ic_carrom", "3D board carrom pool with striker physics & queen pocketing"),
    CHESS("Chess", "Royal Duel", "ic_chess", "Tactical 8x8 chessboard against master bots with valid move highlights"),
    RUMMY("Rummy", "13-Card Classic", "ic_rummy", "Classic 13-card sequence meld table game"),
    LUDO("Ludo", "Race the Board", "ic_ludo", "4-player color dice rolling race to victory"),
    TIC_TAC_TOE("Tic Tac Toe", "Quick Match", "ic_ttt", "3x3 strategic fast-paced duel")
}

enum class FunZoneGameType(val title: String, val subtitle: String, val entryFee: Long, val badge: String) {
    SUBWAY_RUNNER("Subway Runner", "3-Lane Endless Runner", 0L, "3D RUNNER"),
    FRUIT_SLASH("Fruit Slash", "Blade Slice & Bomb Dodge", 0L, "SLICING"),
    BLOCK_PUZZLE("Block Puzzle", "8x8 Grid Line Clear", 0L, "PUZZLE"),
    TALKING_CAT("3D Talking Cat", "Voice pitch mimic & poke reactions", 5000L, "3D PET"),
    CANDY_MATCH("Candy Match Puzzle", "Swipe Match-3 with 20,145 Levels", 0L, "SWIPE 3"),
    STICKMAN_THIEF("Stickman Thief", "Stretch hand thief puzzle 20,145 Levels", 0L, "PHYSICS")
}

data class ChestOption(
    val id: String,
    val name: String,
    val gemPrice: Int,
    val color: Color,
    val minCoinsReward: Long,
    val maxCoinsReward: Long,
    val guaranteedGems: Int,
    val badge: String
)

val PRESET_AVATARS = listOf(
    AvatarItem("avatar_m", "Master M", "M", 0, "Default", RoyalPurple),
    AvatarItem("cyber_king", "Cyber King", "♔", 100, "Rare", ElectricIndigo),
    AvatarItem("royal_queen", "Royal Queen", "♕", 150, "Rare", RoyalPurple),
    AvatarItem("golden_tiger", "Golden Tiger", "🐯", 250, "Epic", GoldenYellow),
    AvatarItem("phoenix_god", "Phoenix God", "🔥", 500, "Divine", GemCyan),
    AvatarItem("shadow_ninja", "Shadow Ninja", "🥷", 120, "Rare", Color(0xFF1E293B)),
    AvatarItem("cosmic_dragon", "Cosmic Dragon", "🐲", 300, "Epic", Color(0xFF10B981)),
    AvatarItem("neon_samurai", "Neon Samurai", "⚔️", 350, "Epic", Color(0xFFEC4899)),
    AvatarItem("mech_titan", "Mech Titan", "🤖", 400, "Epic", Color(0xFF3B82F6)),
    AvatarItem("skull_lord", "Skull Lord", "💀", 450, "Divine", Color(0xFF8B5CF6)),
    AvatarItem("astro_bot", "Astro Bot", "🚀", 550, "Divine", Color(0xFF06B6D4)),
    AvatarItem("galaxy_wizard", "Galaxy Wizard", "🧙", 600, "Ultra Divine", Color(0xFFF59E0B))
)

val PRESET_FRAMES = listOf(
    AuraFrameItem("golden_ring", "Golden Ring", "Classic gold aura circle", 0, listOf(GoldenYellow, GoldAuraGlow)),
    AuraFrameItem("flame_aura", "Flame Aura", "Fiery golden particle aura", 100, listOf(Color(0xFFFF4500), GoldenYellow)),
    AuraFrameItem("celestial_glow", "Celestial Glow", "Glowing electric indigo cosmic ring", 200, listOf(ElectricIndigo, GemCyan)),
    AuraFrameItem("diamond_crown", "Diamond Crown", "Sparkling diamond aura frame", 350, listOf(Color(0xFFE0F2FE), GemCyan)),
    AuraFrameItem("divine_golden", "Divine Golden Aura", "Ultra glowing divine golden majesty frame", 0, listOf(GoldAuraGlow, GoldenYellow, Color(0xFFFFF500))),
    AuraFrameItem("cyber_neon", "Cyber Neon Ring", "Bright pink & blue cyber aura", 150, listOf(Color(0xFFEC4899), Color(0xFF3B82F6))),
    AuraFrameItem("poison_emerald", "Poison Emerald", "Glowing toxic green snake aura", 180, listOf(Color(0xFF10B981), Color(0xFF34D399))),
    AuraFrameItem("crimson_blood", "Crimson Blood", "Intense fiery red blood ring", 220, listOf(Color(0xFFEF4444), Color(0xFFB91C1C))),
    AuraFrameItem("rainbow_prism", "Rainbow Prism", "Multi-spectral rainbow light ring", 300, listOf(Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6))),
    AuraFrameItem("void_purple", "Void Purple", "Deep galaxy purple aura ring", 380, listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
    AuraFrameItem("thunder_voltage", "Thunder Voltage", "Electric blue lightning aura ring", 500, listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF60A5FA)))
)

val STAKE_CAROUSEL_OPTIONS = listOf(
    StakeOption(300L, "Novice Arena", 570L, 1420, "BRONZE"),
    StakeOption(500L, "Standard Table", 950L, 3890, "SILVER"),
    StakeOption(1000L, "Pro Circuit", 1900L, 8910, "GOLD"),
    StakeOption(2500L, "High Roller", 4750L, 6230, "PLATINUM"),
    StakeOption(5000L, "Master Suite", 9500L, 4120, "DIAMOND"),
    StakeOption(10000L, "Champions League", 19000L, 2150, "MASTER"),
    StakeOption(21000L, "Royal Crown", 39900L, 980, "GRANDMASTER"),
    StakeOption(50000L, "Imperial Palace", 95000L, 430, "LEGEND"),
    StakeOption(100000L, "Supreme Deity", 190000L, 190, "GODLIKE")
)

val PRESET_CHESTS = listOf(
    ChestOption("pro_box", "Pro Box", 200, RoyalPurple, 2000L, 8000L, 20, "PRO"),
    ChestOption("master_box", "Master Box", 400, GoldenYellow, 10000L, 30000L, 50, "MASTER"),
    ChestOption("ultra_master", "Ultra Master Box", 1000, GemCyan, 50000L, 200000L, 150, "ULTRA DIVINE")
)
