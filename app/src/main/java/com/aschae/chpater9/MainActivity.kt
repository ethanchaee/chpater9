package com.aschae.chpater9

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.aschae.chpater9.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var serviceMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playImageView.setOnClickListener { startMediaPlayer() }
        binding.pauseImageView.setOnClickListener { pauseMediaPlayer() }
        binding.stopImageView.setOnClickListener { stopMediaPlayer() }
    }

    private fun startMediaPlayer() {
        val intent = Intent(this, MyService::class.java)
            .apply { action = MEDIA_PLAYER_START }
        startForegroundService(intent)
    }

    private fun pauseMediaPlayer() {
        val intent = Intent(this, MyService::class.java)
            .apply { action = MEDIA_PLAYER_PAUSE }
        startForegroundService(intent)
    }

    private fun stopMediaPlayer() {
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }


    private fun initVideoView() {
        binding.videoView.apply {
            val mediaController = object : MediaController(context) {
                override fun hide() {}
            }
            setOnPreparedListener { mediaController.show() }
            setMediaController(mediaController)
            setVideoPath(R.raw.without_you.getResourceUri(context))
        }
    }

    private fun sendMessageToService(action: String) {
        val msg = Message.obtain(null, MyService.SEND_MSG_TO_SERVICE, action)
        serviceMessenger?.send(msg)
    }
}

fun Int.getResourceUri(context: Context): String {
    return context.resources.let {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(it.getResourcePackageName(this))
            .appendPath(it.getResourceTypeName(this))
            .appendPath(it.getResourceEntryName(this))
            .build()
            .toString()
    }
}
