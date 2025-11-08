package org.tomw.cathrmdr.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService
import org.tomw.cathrmdr.R
import org.tomw.cathrmdr.data.SoundOption

class CathAudioManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        context.getSystemService<VibratorManager>()?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService<Vibrator>()
    }

    fun hasAudioPermission(): Boolean {
        // For playback, we generally don't need explicit permissions
        // We'll check if audio is available
        return try {
            audioManager.mode != AudioManager.MODE_INVALID
        } catch (e: Exception) {
            false
        }
    }

    fun setupAudioSession(): Boolean {
        return try {
            // Request audio focus for alarm playback
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(audioAttributes)
                    .build()
                audioFocusRequest = focusRequest
                audioManager.requestAudioFocus(focusRequest)
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                )
            }
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun playAlertSound(soundOption: SoundOption) {
        try {
            releaseMediaPlayer()

            soundOption.soundId?.let { resourceId ->
                playCustomSound(resourceId)
            } ?: playSystemSound()

            // Always provide haptic feedback
            playHapticFeedback()
        } catch (e: Exception) {
            // Fallback to haptic only
            playHapticFeedback()
        }
    }

    private fun playCustomSound(resourceId: Int) {
        try {
            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setOnCompletionListener { releaseMediaPlayer() }
                start()
            }
        } catch (e: Exception) {
            playSystemSound()
        }
    }

    private fun playSystemSound() {
        try {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(context, soundUri)
                setOnCompletionListener { releaseMediaPlayer() }
                prepare()
                start()
            }
        } catch (e: Exception) {
            // Fallback to system notification sound
            try {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, defaultUri)
                ringtone.play()
            } catch (e2: Exception) {
                // Silent failure - haptic will still work
            }
        }
    }

    private fun playHapticFeedback() {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 250, 150, 250, 150, 250),
                        -1
                    )
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 250, 150, 250, 150, 250), -1)
                }
            }
        } catch (e: Exception) {
            // Haptic feedback failed, continue silently
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    fun cleanup() {
        try {
            releaseMediaPlayer()
            // Release audio focus
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { request ->
                    audioManager.abandonAudioFocusRequest(request)
                }
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
            audioFocusRequest = null
        } catch (e: Exception) {
            // Silent cleanup failure
        }
    }

}