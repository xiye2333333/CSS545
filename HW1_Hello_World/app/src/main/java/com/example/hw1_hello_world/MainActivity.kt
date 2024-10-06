package com.example.hw1_hello_world

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.hw1_hello_world.ui.theme.HW1_Hello_WorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HW1_Hello_WorldTheme {
                ChangePage(Modifier.padding(16.dp),this)
            }
        }
    }

    fun switchPage() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

}
@Composable
fun ChangePage(modifier: Modifier = Modifier,Ma:MainActivity = MainActivity()) {
    Surface(color =Color.Green) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
        ) {
            Text(
                text = "This is page 1!",
                fontSize = 48.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Blue,
                modifier = modifier
            )
            //change this page to the second page
            Button(
                onClick = { Ma.switchPage() },
                colors = ButtonDefaults.buttonColors(Color.Gray) ,
                modifier = modifier
            ) {
                Text(
                    text = "button1",
                    fontSize = 24.sp,
                    color = Color.Yellow,
                    modifier = modifier
                )
            }
        }
    }


}



