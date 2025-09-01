package com.thorinx.wizardchase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thorinx.wizardchase.R
import com.thorinx.wizardchase.ui.theme.*
import com.thorinx.wizardchase.viewmodel.GameViewModel

@Composable
fun WelcomeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var playerName by remember { mutableStateOf("") }
    val player by viewModel.player.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AnomaDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = stringResource(R.string.welcome_title),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = AnomaRed,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Subtitle
        Text(
            text = stringResource(R.string.welcome_subtitle),
            fontSize = 18.sp,
            color = AnomaMediumGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        // Game description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnomaDarkGray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.game_rules_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = stringResource(R.string.game_rules),
                    fontSize = 14.sp,
                    color = AnomaLightGray,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )
            }
        }
        
        // Player name input
        OutlinedTextField(
            value = playerName,
            onValueChange = { 
                playerName = it
                viewModel.setPlayerName(it)
            },
            label = { Text(stringResource(R.string.player_name_hint), color = AnomaMediumGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AnomaRed,
                unfocusedBorderColor = AnomaMediumGray,
                focusedLabelColor = AnomaRed,
                unfocusedLabelColor = AnomaMediumGray
            ),
            singleLine = true
        )
        
        // Start game button
        Button(
            onClick = { viewModel.startGame() },
            enabled = playerName.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnomaRed,
                disabledContainerColor = AnomaMediumGray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.start_game),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Settings button
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { viewModel.showSettings() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AnomaRed
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, AnomaRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AnomaRed
            )
        }
        
        // High score display
        if (player.highScore > 0) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                            colors = CardDefaults.cardColors(
                containerColor = AnomaGreen
            ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 En Yüksek Skor: ${player.highScore}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
