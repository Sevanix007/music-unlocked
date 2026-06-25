package com.example.musicunlocked

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.musicunlocked.database.entity.User
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import android.content.Intent
//
//ДОБАВЬ ВОЗМОЖНОСТЬ ВВОДА ЕМЕЙЛ ЗАВТРА!!!!!!!!!
//

//

class RegisterScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicUnlockedTheme {

                val context = LocalContext.current
                val db = DatabaseProvider.getDb(context)

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

//                        // Место под синюю ноту
//                        Box(
//                            modifier = Modifier
//                                .size(100.dp)
//                        )
                        Image(
                            painter = painterResource(id = R.drawable.blue_note3),
                            contentDescription = null,
                            modifier = Modifier.size(180.dp)
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "Music",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Unlocked",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = {
                                Text(
                                    "Username",
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF090909),
                                unfocusedContainerColor = Color(0xFF090909),
                                focusedBorderColor = Color(0xFF202020),
                                unfocusedBorderColor = Color(0xFF202020),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))



                        //                        Email

//                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = {
                                Text(
                                    "E-Mail",
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF090909),
                                unfocusedContainerColor = Color(0xFF090909),
                                focusedBorderColor = Color(0xFF202020),
                                unfocusedBorderColor = Color(0xFF202020),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                        )




//                        email


                        Spacer(modifier = Modifier.height(12.dp))




                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = {
                                Text(
                                    "Password",
                                    color = Color.Gray
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF090909),
                                unfocusedContainerColor = Color(0xFF090909),
                                focusedBorderColor = Color(0xFF202020),
                                unfocusedBorderColor = Color(0xFF202020),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                        )
////                        Email
//
//                        Spacer(modifier = Modifier.height(18.dp))
//
//                        OutlinedTextField(
//                            value = email,
//                            onValueChange = { email = it },
//                            placeholder = {
//                                Text(
//                                    "email",
//                                    color = Color.Gray
//                                )
//                            },
//                            singleLine = true,
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedContainerColor = Color(0xFF090909),
//                                unfocusedContainerColor = Color(0xFF090909),
//                                focusedBorderColor = Color(0xFF202020),
//                                unfocusedBorderColor = Color(0xFF202020),
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White
//                            ),
//                            shape = RoundedCornerShape(50.dp),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(58.dp)
//                        )
//
//
//
//
////                        email

                        Spacer(modifier = Modifier.height(19.dp))

                        Button(
                            onClick = {
                                lifecycleScope.launch {
                                    try {

                                        val existingUser = db.userDao().getUserByUsername(username)
                                        if (existingUser != null) {
                                            Toast.makeText(
                                                context,
                                                "Такой username уже существует",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@launch
                                        }

                                        val existingEmail = db.userDao().getUserByEmail(email)
                                        if (existingEmail != null) {
                                            Toast.makeText(
                                                context,
                                                "Такой email уже зарегистрирован",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@launch
                                        }

                                        val user = User(
                                            username = username,
                                            email = email,
                                            password = password
                                        )

                                        db.userDao().insert(user)

                                        Toast.makeText(
                                            context,
                                            "Регистрация успешна",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

                                        prefs.edit().clear().apply()

                                        Session.isLoggedIn = false
                                        Session.userId = null
                                        Session.username = null
                                        Session.email = null
                                        val intent = Intent(this@RegisterScreen, LoginScreen::class.java)
                                        startActivity(intent)
                                        finish()

                                    } catch (e: Exception) {

                                        Toast.makeText(
                                            context,
                                            "Ошибка: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF003B49),
                                                Color(0xFF024C73)
                                            )
                                        ),
                                        shape = RoundedCornerShape(50.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "Sign Up",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                val intent = Intent(this@RegisterScreen, LoginScreen::class.java)
                                startActivity(intent)
                               finish()
//                                val intent = Intent(this, LoginScreen::class.java)
//                                startActivity(intent)

                            }
                        ) {

                            Text(
                                text = "Already have an account?" ,
                                color = Color(0xFFFFFFFF)
                            )
                            Text(
                                text = " Sign In",
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }

                    Text(
                        text = "♫ Music Unlocked",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}