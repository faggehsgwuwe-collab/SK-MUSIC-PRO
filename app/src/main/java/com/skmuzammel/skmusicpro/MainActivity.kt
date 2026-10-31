package com.skmuzammel.skmusicpro

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : Activity() {

    private val green = Color.rgb(0, 255, 100)

    private val picker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, MusicService::class.java)
            intent.action = MusicService.ACTION_PLAY
            intent.data = it
            startForegroundService(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 30, 24, 30)
            setBackgroundColor(Color.BLACK)
        }

        val logo = TextView(this).apply {
            text = "♫"
            textSize = 65f
            setTextColor(green)
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "SK MUSIC PRO"
            textSize = 28f
            setTextColor(green)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "YOUR MUSIC • YOUR BEAT"
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        fun makeButton(label: String, action: () -> Unit): Button {
            return Button(this).apply {
                text = label
                setOnClickListener { action() }
            }
        }

        val select = makeButton("SELECT MP3 MUSIC") {
            picker.launch(arrayOf("audio/*"))
        }

        val pause = makeButton("PAUSE MUSIC") {
            val i = Intent(this, MusicService::class.java)
            i.action = MusicService.ACTION_PAUSE
            startService(i)
        }

        val resume = makeButton("RESUME MUSIC") {
            val i = Intent(this, MusicService::class.java)
            i.action = MusicService.ACTION_RESUME
            startService(i)
        }

        val stop = makeButton("STOP MUSIC") {
            val i = Intent(this, MusicService::class.java)
            i.action = MusicService.ACTION_STOP
            startService(i)
        }

        listOf(logo, title, subtitle, select, pause, resume, stop).forEach {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 10, 0, 10)
            layout.addView(it, params)
        }

        setContentView(layout)
    }
}
