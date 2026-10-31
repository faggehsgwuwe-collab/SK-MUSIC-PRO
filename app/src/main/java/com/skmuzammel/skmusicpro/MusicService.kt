package com.skmuzammel.skmusicpro

import android.app.*
import android.content.*
import android.media.*
import android.os.*

class MusicService : Service() {

    companion object {
        const val ACTION_PLAY = "SK_PLAY"
        const val ACTION_PAUSE = "SK_PAUSE"
        const val ACTION_RESUME = "SK_RESUME"
        const val ACTION_STOP = "SK_STOP"
        const val CHANNEL = "sk_music_channel"
    }

    private var player: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL,
            "SK MUSIC PRO",
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun showNotification() {
        val notification = Notification.Builder(this, CHANNEL)
            .setContentTitle("SK MUSIC PRO")
            .setContentText("Your music is playing")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()

        startForeground(101, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_PLAY -> {
                val uri = intent.data ?: return START_NOT_STICKY

                showNotification()
                player?.release()

                player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )

                    setDataSource(this@MusicService, uri)

                    setOnPreparedListener {
                        it.start()
                    }

                    setOnCompletionListener {
                        stopSelf()
                    }

                    setOnErrorListener { _, _, _ ->
                        stopSelf()
                        true
                    }

                    prepareAsync()
                }
            }

            ACTION_PAUSE -> {
                if (player?.isPlaying == true) {
                    player?.pause()
                }
            }

            ACTION_RESUME -> {
                if (player != null) {
                    player?.start()
                }
            }

            ACTION_STOP -> {
                player?.release()
                player = null
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
