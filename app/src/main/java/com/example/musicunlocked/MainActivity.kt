package com.example.musicunlocked

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import com.example.musicunlocked.database.entity.User
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

//Internet
import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.material3.Slider








object Session {

    var isLoggedIn = false

    var userId: Int? = null

    var username: String? = null

    var email: String? = null
}
class MainActivity : ComponentActivity() {



//    JUST COPY IT IF YA NEED IT
//    val intent = Intent(this@MainActivity, LoginScreen::class.java)
//    startActivity(intent)
//    finish()



//    val intent = Intent(this@RegisterScreen, LoginScreen::class.java)
//    startActivity(intent)
//    finish()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = DatabaseProvider.getDb(applicationContext)


//
//        lifecycleScope.launch {
//            db.userDao().insert(
//                User(
//                    username = "test",
//                    email = "test@mail.com"
//                )
//            )
//        }
        setContent {
            MusicUnlockedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        onNavigateToProfile = {
                            // ✅ ПРАВИЛЬНЫЙ ПЕРЕХОД
                            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        },
                        onNavigateToRegister = {
                            val intent = Intent(this@MainActivity, RegisterScreen::class.java)
                            startActivity(intent)
                        },
                        onNavigateToLogin = {
                            val intent = Intent(this@MainActivity, LoginScreen::class.java)
                            startActivity(intent)
                            finish()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

        }
    }
}

@Composable
 fun MainScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
){

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = "Music Unlocked",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateToProfile() }
        ) {
            Text(
                text = "Перейти в профиль",
                fontSize = 18.sp
            )
        }

//          Login logic
        val context = LocalContext.current
        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

        Session.isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (Session.isLoggedIn) {
            Session.userId = prefs.getInt("userId", -1)
            Session.username = prefs.getString("username", null)
            Session.email = prefs.getString("email", null)
        }
        if(!Session.isLoggedIn){
            onNavigateToLogin()
        }

//        LaunchedEffect(Session.isLoggedIn) {
//            if (!Session.isLoggedIn) {
//                onNavigateToLogin()
//            }
//        }
//        Button(
//            onClick = {
//                if (!Session.isLoggedIn){
//                onNavigateToRegister()} }
//        ) {
//            Text(
//                text = "Перейти в reg",
//                fontSize = 18.sp
//            )
//        }


        Image(
            painter = painterResource(id = R.drawable.logo), // вместо "logo" напиши имя твоего файла
            contentDescription = "Мой логотип", // для незрячих людей, можно написать "Лого"
            modifier = Modifier.size(150.dp) // размер в пикселах (150 dp)
        )
//        AudioPlayer_s.kt
        AudioPlayer()

    }
//    Spacer(modifier = Modifier.height(20.dp))


}


//@Composable
//fun AudioPlayer() {
//    var isPlaying by remember {
//        mutableStateOf(false)
//    }
//
//
//
//
//
//    val context = LocalContext.current
//
//    val player = remember {
//
//        ExoPlayer.Builder(context).build().apply {
//
//            setMediaItem(
//                MediaItem.fromUri(
//                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
//                )
//            )
//
//            prepare()
//        }
//    }
//    var sliderPosition by remember {
//        mutableStateOf(0f)
//    }
//
//
//    LaunchedEffect(player) {
//
//        while (true) {
//
//            if (player.duration > 0) {
//
//                sliderPosition =
//                    player.currentPosition.toFloat() /
//                            player.duration.toFloat()
//            }
//
//            kotlinx.coroutines.delay(500)
//        }
//    }
//    DisposableEffect(Unit) {
//        onDispose {
//            player.release()
//        }
//    }
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        IconButton(
//            modifier = Modifier.size(80.dp),
//            onClick = {
//
//                if (isPlaying) {
//                    player.pause()
//                } else {
//                    player.play()
//                }
//
//                isPlaying = !isPlaying
//            }
//        ) {
//
//            Icon(
//                imageVector =
//                    if (isPlaying)
//                        Icons.Default.Pause
//                    else
//                        Icons.Default.PlayArrow,
//                contentDescription = null,
//                        modifier = Modifier.size(60.dp)
//            )
//        }
//        Slider(
//            value = sliderPosition,
//
//            onValueChange = {
//                sliderPosition = it
//            },
//
//            onValueChangeFinished = {
//
//                player.seekTo(
//                    (player.duration * sliderPosition).toLong()
//                )
//            },
// //           valueRange = 0f..100f
//                    valueRange = 0f..1f
//        )
//    }
//
//}


