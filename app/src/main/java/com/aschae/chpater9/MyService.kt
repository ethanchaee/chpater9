package com.aschae.chpater9

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MyService : Service() {

    private var mediaPlayer: MediaPlayer? = null


    private val foregroundNotificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(this, applicationContext.packageName).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("title").setContentText("content text")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val mainIntent =Intent(baseContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this, MyService::class.java)
            .apply { action = MEDIA_PLAYER_START }

        val pauseIntent = Intent(this, MyService::class.java)
            .apply { action = MEDIA_PLAYER_PAUSE }

        val stopIntent = Intent(this, MyService::class.java)
            .apply { action = MEDIA_PLAYER_STOP }

        val playIcon = Icon.createWithResource(this, R.drawable.ic_baseline_play_arrow_24)
        val pauseIcon = Icon.createWithResource(this, R.drawable.ic_baseline_pause_24)
        val stopIcon = Icon.createWithResource(this, R.drawable.ic_baseline_stop_24)

        val notification1 = Notification.Builder(this, "CHANNEL_ID")
            .setStyle(Notification.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .addAction(Notification.Action.Builder(pauseIcon, "", PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)).build())
            .addAction(Notification.Action.Builder(playIcon, "", PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)).build())
            .addAction(Notification.Action.Builder(stopIcon, "", PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)).build())
            .setContentIntent(mainPendingIntent)
            .setContentTitle("Wonderful music")
            .setContentText("My Awesome Band")
            .build()

        startForeground(10, notification1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MEDIA_PLAYER_START -> {
                if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(this, R.raw.without_you)
                    .apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                    }
                mediaPlayer?.start()
                return START_STICKY
            }
            MEDIA_PLAYER_PAUSE -> {
                mediaPlayer?.pause()
                return START_STICKY
            }
            MEDIA_PLAYER_STOP -> {
                mediaPlayer?.apply {
                    stop()
                    release()
                }
                stopSelf()
                return START_STICKY
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun createNotificationChannel(
        context: Context, importance: Int, showBadge: Boolean,
        name: String, description: String,
    ) {
        val channelId = "${context.packageName}-$name"
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.setShowBadge(showBadge)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun generateNotification() {
        val manager = baseContext.getSystemService(NotificationManager::class.java)

        val serviceChannel = NotificationChannel(
            "CHANNEL_ID",
            "CHANNEL_NAME",
            NotificationManager.IMPORTANCE_NONE
        )
        manager.createNotificationChannel(serviceChannel)
        val mediaSession = MediaSession(baseContext, "PlayerService")

        mediaSession.setMetadata(
            MediaMetadata.Builder()

                // Title.
                .putString(MediaMetadata.METADATA_KEY_TITLE, "aa")

                // Artist.
                // Could also be the channel name or TV series.
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "aa")

                // Album art.
                // Could also be a screenshot or hero image for video content
                // The URI scheme needs to be "content", "file", or "android.resource".
                .build()
        )


        val icon = Icon.createWithResource(this, R.drawable.ic_baseline_remove_red_eye_24)
        val foregroundNotificationBuilder: Notification.Builder =
            Notification.Builder(applicationContext, applicationContext.packageName)
                .setStyle(Notification.BigPictureStyle()
                    //.setMediaSession(mediaSession.sessionToken)
                    .bigLargeIcon(icon)
                )
                .setChannelId("CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("title")
                .setContentText("content text")
        val notification = foregroundNotificationBuilder.build()
        //generateNotification()
        startForeground(10, notification)

        val NOTIFICATION_ID = 1001;
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel")

        val channelId = "$packageName-${getString(R.string.app_name)}"
        val title = "Don't Say a Word"
        val content = "Ellie Goulding"

        val intent = Intent(baseContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(baseContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = Notification.Builder(this, channelId)
        //builder.setSmallIcon(R.drawable.ic_codechacha)
        builder.setContentTitle(title)  // 1
        builder.setContentText(content)  // 2
        builder.setLargeIcon(
            BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)) // 3
        builder.addAction(Notification.Action(
            R.drawable.ic_launcher_background, "skip prev", pendingIntent))   // 4
        builder.addAction(Notification.Action(
            R.drawable.ic_launcher_background, "skip prev", pendingIntent))
        builder.addAction(Notification.Action(
            R.drawable.ic_launcher_background, "pause", pendingIntent))
        builder.addAction(Notification.Action(
            R.drawable.ic_launcher_background, "skip next", pendingIntent))
        builder.addAction(Notification.Action(
            R.drawable.ic_launcher_background, "skip prev", pendingIntent))
        //.setShowActionsInCompactView(1, 2, 3)
        builder.setStyle(Notification.MediaStyle().setShowActionsInCompactView(1, 2, 3)) // 5
        //builder.priority = Notification.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val SEND_MSG_TO_SERVICE = 221105
    }
}