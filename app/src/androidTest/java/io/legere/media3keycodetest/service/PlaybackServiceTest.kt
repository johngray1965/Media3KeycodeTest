package io.legere.media3keycodetest.service

import android.content.ComponentName
import android.content.Intent
import android.view.KeyEvent
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PlaybackServiceTest {

    private lateinit var context: android.content.Context

    @Before
    fun setUp() {
        playbackResumptionCalled = false
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun testKeyEvents(
        keyCode: Int,
        expectOnPlaybackResumption: Boolean
    ) {
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        println("Problematic KeyEvent created: $keyEvent")

        val serviceIntent =
            Intent(context, PlaybackService::class.java).apply {
                action = Intent.ACTION_MEDIA_BUTTON // Use the action that your service expects
                putExtra(Intent.EXTRA_KEY_EVENT, keyEvent)
                println("Service Intent created: action=${this.action}, extras=${this.extras}")
            }

        println("Attempting to start ReaderService with problematic intent...")
        context.startService(serviceIntent)
        println("context.startService(serviceIntent) called.")

        var mediaController: MediaController? = null
        val connectionLatch = CountDownLatch(1)

        try {
            println("Attempting to create and connect MediaController...")
            val sessionToken =
                SessionToken(context, ComponentName(context, PlaybackService::class.java))

            val controllerFuture: ListenableFuture<MediaController> =
                MediaController
                    .Builder(context, sessionToken)
                    .buildAsync()

            controllerFuture.addListener(
                {
                    try {
                        mediaController =
                            controllerFuture.get() // This can throw if connection failed
                        println("MediaController connected: $mediaController")
                        Assert.assertNotNull(
                            "MediaController should not be null after connection",
                            mediaController
                        )
                    } finally {
                        connectionLatch.countDown()
                    }
                },
                MoreExecutors.directExecutor()
            )

            Assert.assertTrue(
                "MediaController did not connect within timeout",
                connectionLatch.await(10, TimeUnit.SECONDS)
            )
            Assert.assertNotNull("MediaController instance is null after latch", mediaController)

            println("MediaController interaction point reached.")
        } finally {
            if (mediaController != null) {
                val latch = CountDownLatch(1)
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    try {
                        mediaController.release()
                        println("MediaController released on main thread.")
                    } catch (t: Throwable) {
                        Timber.e(t, "Exception during mediaController.release() on main thread")
                    } finally {
                        latch.countDown()
                    }
                }
            } else {
                println("MediaController was null, no release needed.")
            }
        }
        assertThat(playbackResumptionCalled).isEqualTo(expectOnPlaybackResumption)
        println("testServiceReceivesProblematicIntent completed (or is about to, if no crash yet).")
    }

    @Test
    fun testServiceReceivesProblematicIntent_expectingCrash() {
        // We'd like to see OnPlaybackResumption called, but it won't be
        testKeyEvents(KeyEvent.KEYCODE_HEADSETHOOK, false)
    }

    @Test
    fun testServiceReceivesGoodIntent() {
        testKeyEvents(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, true)
    }
}