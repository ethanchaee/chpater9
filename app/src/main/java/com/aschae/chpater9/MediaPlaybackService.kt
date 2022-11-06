package com.aschae.chpater9

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.service.media.MediaBrowserService

class MediaPlaybackService : MediaBrowserService() {
    private var mediaSession : MediaSession? = null
    private var stateBuilder : PlaybackState? = null

    override fun onCreate() {
        super.onCreate()


        mediaSession = MediaSession(baseContext, "LOG_TAG").apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY
                        or PlaybackState.ACTION_PLAY_PAUSE
                ).build()
            setPlaybackState(stateBuilder)

            // MySessionCallback() has methods that handle callbacks from a media controller
            //setCallback(MySessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }

        val mainIntent = Intent(baseContext, MainActivity::class.java)
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
                .setMediaSession(mediaSession!!.sessionToken)
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

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowser.MediaItem>>) {
        TODO("Not yet implemented")
    }

}