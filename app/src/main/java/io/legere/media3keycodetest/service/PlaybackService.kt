package io.legere.media3keycodetest.service

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

var playbackResumptionCalled = false
@UnstableApi
class PlaybackService: MediaSessionService() {
    var mediaSession: MediaSession? = null
    var callback: MediaSession.Callback = object : MediaSession.Callback {
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            println("onPlaybackResumption called")
            playbackResumptionCalled = true
            return Futures.immediateFuture(MediaSession.MediaItemsWithStartPosition(emptyList(), 0, 0))
        }

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            println("onConnect called")
            if (session.isMediaNotificationController(controller)) {
                val sessionCommands =
                    MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .build()
                val playerCommands =
                    MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                        .build()
                // Custom button preferences and commands to configure the platform session.
                return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailablePlayerCommands(playerCommands)
                    .setAvailableSessionCommands(sessionCommands)
                    .build()
            }
            // Default commands with default button preferences for all other controllers.
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        println("onStartCommand called with intent=$intent")
        return super.onStartCommand(intent, flags, startId)
    }

    // If desired, validate the controller before returning the media library session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Create your player and media library session in the onCreate lifecycle event
    override fun onCreate() {
        println("onCreate called")
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession
            .Builder(this, player)
            .setCallback(callback)
            .build()
    }

    // Remember to release the player and media library session in onDestroy
    override fun onDestroy() {
        println("onDestroy called")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}