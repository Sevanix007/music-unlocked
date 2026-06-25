package com.example.musicunlocked

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import android.content.Context
import android.content.Intent

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicUnlockedTheme {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFFFA500))
                        .fillMaxSize()

                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Профиль", fontSize = 28.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Добро пожаловать, " + Session.username + " !", fontSize = 18.sp)
                    Text("Ваш E-mail, " + Session.email + " !", fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(32.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

                            prefs.edit().clear().apply()

                            Session.isLoggedIn = false
                            Session.userId = null
                            Session.username = null
                            Session.email = null
                            val intent = Intent(this@ProfileActivity, LoginScreen::class.java)
                            startActivity(intent)
                            finish()  // Закрыть экран и вернуться назад
                            Toast.makeText(context, "Sign Out успешен", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text("Sign Out", fontSize = 18.sp)
                    }
                    Button(
                        onClick = {
                                val intent = Intent(this@ProfileActivity, ManageProfileData::class.java)
                                startActivity(intent)
                                finish()

                        }
                    ){ Text("Изменить данные профиля", fontSize = 18.sp)}
                    Button(
                        onClick = {
                            finish()  // Закрыть экран и вернуться назад
                            Toast.makeText(context, "Welcome to Geeks for Geeks", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text("Назад", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

//    val intent = Intent(this@RegisterScreen, LoginScreen::class.java)
//    startActivity(intent)
//    finish()
