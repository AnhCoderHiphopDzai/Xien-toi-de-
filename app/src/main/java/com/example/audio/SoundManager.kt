package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * 8-bit Retro Synthesizer & Haptic Feedback Manager
 * Generates custom arcade sound effects on-the-fly without external audio assets.
 */
class SoundManager(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Default)
  private var isMuted: Boolean = false

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  fun setMuted(muted: Boolean) {
    isMuted = muted
  }

  fun isMuted(): Boolean = isMuted

  /** Button tap retro blip */
  fun playClick() {
    if (isMuted) return
    vibrate(15)
    scope.launch {
      playTone(freq = 600f, durationMs = 40, type = ToneType.SQUARE)
    }
  }

  /** Sizzling / frying sound */
  fun playSizzle() {
    if (isMuted) return
    vibrate(25)
    scope.launch {
      playNoise(durationMs = 250, amplitude = 0.25f)
    }
  }

  /** Order completed / Ding! */
  fun playDing() {
    if (isMuted) return
    vibrate(40)
    scope.launch {
      playTone(freq = 880f, durationMs = 120, type = ToneType.SINE)
      playTone(freq = 1320f, durationMs = 180, type = ToneType.SINE)
    }
  }

  /** Coin / Cash earn sound */
  fun playCoin() {
    if (isMuted) return
    vibrate(30)
    scope.launch {
      playTone(freq = 987f, durationMs = 70, type = ToneType.SQUARE)
      playTone(freq = 1318f, durationMs = 160, type = ToneType.SQUARE)
    }
  }

  /** Food burnt buzzer */
  fun playBurn() {
    if (isMuted) return
    vibrate(80)
    scope.launch {
      playTone(freq = 220f, durationMs = 150, type = ToneType.SAWTOOTH)
      playTone(freq = 160f, durationMs = 200, type = ToneType.SAWTOOTH)
    }
  }

  /** Police siren sound effect */
  fun playSiren() {
    if (isMuted) return
    vibrate(150)
    scope.launch {
      for (i in 0 until 3) {
        playTone(freq = 700f, durationMs = 120, type = ToneType.TRIANGLE)
        playTone(freq = 950f, durationMs = 120, type = ToneType.TRIANGLE)
      }
    }
  }

  /** Crash sound for mini-game getaway */
  fun playCrash() {
    if (isMuted) return
    vibrate(180)
    scope.launch {
      playNoise(durationMs = 300, amplitude = 0.5f)
    }
  }

  /** Escape success fanfare */
  fun playFanfare() {
    if (isMuted) return
    vibrate(60)
    scope.launch {
      val notes = listOf(523f, 659f, 784f, 1046f)
      for (n in notes) {
        playTone(freq = n, durationMs = 90, type = ToneType.SQUARE)
      }
    }
  }

  private fun vibrate(ms: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(ms)
      }
    } catch (_: Exception) {
      // Safe fallback
    }
  }

  private enum class ToneType { SINE, SQUARE, SAWTOOTH, TRIANGLE }

  private fun playTone(freq: Float, durationMs: Int, type: ToneType) {
    val sampleRate = 22050
    val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
    if (numSamples <= 0) return
    val buffer = ShortArray(numSamples)

    val twoPi = 2.0 * Math.PI
    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val raw = when (type) {
        ToneType.SINE -> sin(twoPi * freq * t)
        ToneType.SQUARE -> if (sin(twoPi * freq * t) >= 0) 0.6 else -0.6
        ToneType.SAWTOOTH -> (2.0 * (t * freq - Math.floor(t * freq + 0.5))) * 0.5
        ToneType.TRIANGLE -> (2.0 * Math.abs(2.0 * (t * freq - Math.floor(t * freq + 0.5))) - 1.0) * 0.7
      }
      // Apply subtle fade envelope to prevent click
      val envelope = when {
        i < numSamples * 0.1 -> i / (numSamples * 0.1)
        i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
        else -> 1.0
      }
      buffer[i] = (raw * envelope * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    playShortArray(buffer, sampleRate)
  }

  private fun playNoise(durationMs: Int, amplitude: Float) {
    val sampleRate = 22050
    val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
    if (numSamples <= 0) return
    val buffer = ShortArray(numSamples)
    val random = java.util.Random()

    for (i in 0 until numSamples) {
      val noise = (random.nextFloat() * 2f - 1f) * amplitude
      val envelope = (numSamples - i).toDouble() / numSamples
      buffer[i] = (noise * envelope * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    playShortArray(buffer, sampleRate)
  }

  private fun playShortArray(buffer: ShortArray, sampleRate: Int) {
    try {
      val minBuf = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBuf))
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()
      // Release after playing
      Thread.sleep((buffer.size * 1000L / sampleRate) + 20)
      audioTrack.stop()
      audioTrack.release()
    } catch (_: Exception) {
      // Audio fallback
    }
  }
}
