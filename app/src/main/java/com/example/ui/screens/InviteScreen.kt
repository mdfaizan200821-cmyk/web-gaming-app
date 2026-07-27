package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun InviteScreen(
    referralCode: String,
    onCopyCode: () -> Unit,
    onShareCode: () -> Unit,
    onClaimReward: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "INVITE FRIENDS & EARN COINS",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = DarkCharcoal
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceCardVariant,
            border = BorderStroke(1.5.dp, GoldenYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎁 REWARD PER FRIEND", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MediumGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("🪙 +5,000 COINS", fontWeight = FontWeight.Black, fontSize = 28.sp, color = DarkCharcoal)
                Text("Earn 5,000 Coins for every friend who joins with your link!", fontSize = 12.sp, color = MediumGray, textAlign = TextAlign.Center)
            }
        }

        // Referral Code Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite,
            border = BorderStroke(2.dp, RoyalPurple),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("YOUR UNIQUE REFERRAL CODE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RoyalPurple)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RoyalPurple.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = referralCode,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        color = DarkCharcoal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onCopyCode,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onShareCode,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Claim Reward Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = EmeraldGreen.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, EmeraldGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CLAIM INVITE BONUS", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = DarkCharcoal)
                    Text("1 Friend Joined Recently!", fontSize = 12.sp, color = EmeraldGreen)
                }

                Button(
                    onClick = onClaimReward,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("CLAIM 🪙", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
