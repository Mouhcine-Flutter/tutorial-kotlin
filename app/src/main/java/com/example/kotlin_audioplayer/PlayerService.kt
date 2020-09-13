package com.example.kotlin_audioplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class PlayerService : Service() {
    private val binder: Binder = Binder()
    private var mediaPlayer: MediaPlayer? = null

    var onErrorListener =
        MediaPlayer.OnErrorListener { _, _, _ -> false }
        set(value) {
            field = value
            mediaPlayer?.setOnErrorListener(value)
        }

    var onPreparedListener = MediaPlayer.OnPreparedListener { }
        set(value) {
            field = value
            mediaPlayer?.setOnPreparedListener(value)
        }

    var onCompletionListener = MediaPlayer.OnCompletionListener { }
        set(value) {
            field = value
            mediaPlayer?.setOnCompletionListener(value)
        }

    val isPlaying
        get() = mediaPlayer?.isPlaying ?: false

    fun play(path: Uri) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(applicationContext, path)
        mediaPlayer?.start()
    }

    fun playPause() {
        TODO("Not yet implemented")
    }

    fun seekToBeginning() {
        TODO("Not yet implemented")
    }

    fun stop() {
        TODO("Not yet implemented")
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(
        intent: Intent, flags: Int, startId: Int
    ): Int = START_STICKY

    inner class Binder : android.os.Binder() {
        val service: PlayerService get() = this@PlayerService
    }
}
