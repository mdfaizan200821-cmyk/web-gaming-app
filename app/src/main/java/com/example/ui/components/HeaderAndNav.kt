package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserEntity
import com.example.ui.models.PRESET_AVATARS
import com.example.ui.models.PRESET_FRAMES
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun TopHeader(
    user: UserEntity?,
    onProfileClick: () -> Unit,
    onAddCoinsClick: () -> Unit,
    onAddGemsClick: () -> Unit
) {
    val equippedAvatar = PRESET_AVATARS.find { it.id == user?.equippedAvatarId } ?: PRESET_AVATARS.first()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PureWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, SubtleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Box with Avatar Ring and LV Badge
            val equippedFrame = PRESET_FRAMES.find { it.id == user?.equippedFrameId } ?: PRESET_FRAMES.last()

            Box(
                modifier = Modifier
                    .clickable { onProfileClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(equippedFrame.glowColors),
                                radius = size.width / 2f
                            )
                        }
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(equippedAvatar.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = equippedAvatar.iconSymbol,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Level Badge overlapping bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .background(GemBlue, RoundedCornerShape(10.dp))
                        .border(2.dp, PureWhite, RoundedCornerShape(10.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "LV ${user?.level ?: 24}",
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                }
            }

            // Coins & Gems Header Badges in Pill Format
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Coins Counter Pill
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SurfaceCardVariant,
                    modifier = Modifier.clickable { onAddCoinsClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(GoldenYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🪙",
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("%,d", user?.coins ?: 10815L),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )
                    }
                }

                // Gems Counter Pill
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SurfaceCardVariant,
                    modifier = Modifier.clickable { onAddGemsClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(GemCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💎",
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("%,d", user?.gems ?: 200),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PureWhite,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, SubtleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = "LOBBY",
                icon = Icons.Default.Home,
                isSelected = currentTab == AppTab.LOBBY,
                onClick = { onTabSelected(AppTab.LOBBY) }
            )
            NavItem(
                title = "RANKS",
                icon = Icons.Default.Leaderboard,
                isSelected = currentTab == AppTab.RANKS,
                onClick = { onTabSelected(AppTab.RANKS) }
            )
            NavItem(
                title = "CHESTS",
                icon = Icons.Default.Inventory2,
                isSelected = currentTab == AppTab.CHESTS,
                onClick = { onTabSelected(AppTab.CHESTS) }
            )
            NavItem(
                title = "INVITE",
                icon = Icons.Default.Share,
                isSelected = currentTab == AppTab.INVITE,
                onClick = { onTabSelected(AppTab.INVITE) }
            )
            NavItem(
                title = "SETTINGS",
                icon = Icons.Default.Settings,
                isSelected = currentTab == AppTab.SETTINGS,
                onClick = { onTabSelected(AppTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GemBlue,
                shadowElevation = 4.dp,
                modifier = Modifier.size(width = 48.dp, height = 30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                color = GemBlue
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MediumGray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MediumGray
            )
        }
    }
}

