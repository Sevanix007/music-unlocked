package com.example.musicunlocked

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.musicunlocked.database.entity.User
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.ui.unit.sp

class ManageProfileData : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MusicUnlockedTheme {

                val context = LocalContext.current
                val db = DatabaseProvider.getDb(context)

                var newUsername by remember { mutableStateOf("") }
                var newEmail by remember { mutableStateOf("") }
                var oldPassword by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(40.dp))

                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("New Username") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("New Email") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text("Current Password") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text("New Password") }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {

                                lifecycleScope.launch {

                                    try {

                                        val userId = Session.userId

                                        if (userId == null) {

                                            Toast.makeText(
                                                context,
                                                "Пользователь не авторизован",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        val currentUser =
                                            db.userDao().getUserById(userId)

                                        if (currentUser == null) {

                                            Toast.makeText(
                                                context,
                                                "Пользователь не найден",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        if (currentUser.password != oldPassword) {

                                            Toast.makeText(
                                                context,
                                                "Неверный текущий пароль",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        if (newUsername.isNotBlank() &&
                                            newUsername != currentUser.username
                                        ) {

                                            val usernameExists =
                                                db.userDao()
                                                    .getUserByUsername(newUsername)

                                            if (usernameExists != null) {

                                                Toast.makeText(
                                                    context,
                                                    "Username уже занят",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                return@launch
                                            }
                                        }

                                        if (newEmail.isNotBlank() &&
                                            newEmail != currentUser.email
                                        ) {

                                            val emailExists =
                                                db.userDao()
                                                    .getUserByEmail(newEmail)

                                            if (emailExists != null) {

                                                Toast.makeText(
                                                    context,
                                                    "Email уже занят",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                return@launch
                                            }
                                        }

                                        val updatedUser = User(
                                            userId = currentUser.userId,
                                            username =
                                                if (newUsername.isBlank())
                                                    currentUser.username
                                                else
                                                    newUsername,
                                            email =
                                                if (newEmail.isBlank())
                                                    currentUser.email
                                                else
                                                    newEmail,
                                            password =
                                                if (newPassword.isBlank())
                                                    currentUser.password
                                                else
                                                    newPassword
                                        )

                                        db.userDao().updateUser(updatedUser)

                                        val prefs =
                                            context.getSharedPreferences(
                                                "session",
                                                Context.MODE_PRIVATE
                                            )

                                        prefs.edit()
                                            .putString(
                                                "username",
                                                updatedUser.username
                                            )
                                            .putString(
                                                "email",
                                                updatedUser.email
                                            )
                                            .apply()

                                        Session.username =
                                            updatedUser.username

                                        Session.email =
                                            updatedUser.email

                                        Toast.makeText(
                                            context,
                                            "Данные успешно обновлены",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        val intent = Intent(this@ManageProfileData, ProfileActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    } catch (e: Exception) {

                                        Toast.makeText(
                                            context,
                                            e.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Save", fontSize = 18.sp)
                        }
                        Button(
                            onClick = {

                                val intent = Intent(this@ManageProfileData, ProfileActivity::class.java)
                                startActivity(intent)

                                finish()

                            }
                        ){ Text("Назад", fontSize = 18.sp)}
                    }
                }
            }
        }
    }

}