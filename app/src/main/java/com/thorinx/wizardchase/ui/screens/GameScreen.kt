package com.thorinx.wizardchase.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.DrawableRes
import com.thorinx.wizardchase.R
import com.thorinx.wizardchase.data.PowerUpType
import com.thorinx.wizardchase.ui.theme.*
import com.thorinx.wizardchase.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val player by viewModel.player.collectAsState()
    
    // Dialog state for exit confirmation
    var showExitDialog by remember { mutableStateOf(false) }
    
    // Handle back button
    BackHandler {
        showExitDialog = true
        viewModel.pauseGame() // Oyunu duraklat
    }
    
    // Animation for wizard
    val wizardScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "wizard_scale"
    )
    
    // Animation for power-up
    val powerUpScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "powerup_scale"
    )
    
    // Hand icon artık animasyonsuz
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AnomaDark)
    ) {
        // Game stats header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AnomaDarkGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player name and score
            Column {
                Text(
                    text = player.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Hand icon - try different approach
                    Box(
                        modifier = Modifier.size(44.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hand),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                                androidx.compose.ui.graphics.ColorMatrix(
                                    floatArrayOf(
                                        1f, 0f, 0f, 0f, 0f,      // Red
                                        0f, 1f, 0f, 0f, 0f,      // Green
                                        0f, 0f, 1f, 0f, 0f,      // Blue
                                        -1f, -1f, -1f, 1f, 255f  // Alpha: siyah pikseller şeffaf
                                    )
                                )
                            )
                        )
                    }
                    // Score text
                    Text(
                        text = "${gameState.score}",
                        fontSize = 18.sp,
                        color = AnomaRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Time
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${stringResource(R.string.time)}: ${gameState.timeRemaining}s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (gameState.timeRemaining <= 10) AnomaRed else Color.White
                )
            }
        }
        
        // Game area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
        ) {
            // Wizard (clickable target)
            Box(
                modifier = Modifier
                    .offset(
                        x = gameState.wizardPosition.x.dp,
                        y = gameState.wizardPosition.y.dp
                    )
                    .size(80.dp, 120.dp)
                    .scale(wizardScale)
                    .clickable(enabled = !gameState.isPaused) {
                        viewModel.catchWizard()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.anoma_wizard),
                    contentDescription = stringResource(R.string.wizard_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Power-up (if available)
            gameState.powerUpPosition?.let { position ->
                gameState.powerUpType?.let { powerUpType ->
                                         Box(
                         modifier = Modifier
                             .offset(
                                 x = position.x.dp,
                                 y = position.y.dp
                             )
                             .size(60.dp)
                             .scale(powerUpScale)
                             .clip(CircleShape)
                             .background(
                                 if (powerUpType == PowerUpType.SLOW_MOTION) Color.Transparent 
                                 else getPowerUpColor(powerUpType)
                             )
                             .clickable(enabled = !gameState.isPaused) {
                                 viewModel.catchPowerUp()
                             }
                     ) {
                         // Power-up icon (xan.png for score multiplier, prawn.jpg for slow motion, emoji for time bonus)
                         when (powerUpType) {
                             PowerUpType.SCORE_MULTIPLIER -> {
                                 Image(
                                     painter = painterResource(id = R.drawable.xan),
                                     contentDescription = null,
                                     modifier = Modifier
                                         .size(32.dp)
                                         .align(Alignment.Center)
                                 )
                             }
                                                           PowerUpType.SLOW_MOTION -> {
                                  Image(
                                      painter = painterResource(id = R.drawable.prawn),
                                      contentDescription = null,
                                      modifier = Modifier
                                          .size(50.dp)
                                          .align(Alignment.Center)
                                  )
                              }
                             else -> {
                                 Text(
                                     text = getPowerUpIcon(powerUpType),
                                     fontSize = 24.sp,
                                     modifier = Modifier.align(Alignment.Center)
                                 )
                             }
                         }
                     }
                }
            }
        }
        
        // Exit confirmation dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showExitDialog = false
                    viewModel.resumeGame() // Oyunu devam ettir
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mask),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.exit_game_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.exit_game_message),
                        fontSize = 16.sp,
                        color = AnomaLightGray,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { 
                                showExitDialog = false
                                viewModel.resumeGame() // Oyunu devam ettir
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AnomaBlue
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AnomaBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Button(
                            onClick = {
                                showExitDialog = false
                                viewModel.restartGame()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnomaRed
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.yes),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                containerColor = AnomaDarkGray,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun getPowerUpColor(powerUpType: PowerUpType): Color {
    return when (powerUpType) {
        PowerUpType.SCORE_MULTIPLIER -> AnomaOrange
        PowerUpType.TIME_BONUS -> AnomaBlue
        PowerUpType.SLOW_MOTION -> AnomaPurple
    }
}

@Composable
private fun getPowerUpIcon(powerUpType: PowerUpType): String {
    return when (powerUpType) {
        PowerUpType.SCORE_MULTIPLIER -> "xan"  // xan.png dosyası için özel işaret
        PowerUpType.TIME_BONUS -> "⏰"
        PowerUpType.SLOW_MOTION -> "prawn"  // prawn.jpg dosyası için özel işaret
    }
}
