@file:JvmName("VibrateUtil")

package junkuvo.apps.inputhelper.util
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator


fun vibrate(context: Context) {
    val mVibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    val mVibratePattern = longArrayOf(100, 50, 150, 50)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(mVibratePattern, VibrationEffect.DEFAULT_AMPLITUDE)
        mVibrator.vibrate(effect)
    } else {
        mVibrator.vibrate(mVibratePattern, -1)
    }
}

/**
 * エラー時のバイブレーション。入力エラーとかで利用する想定。
 */
fun vibrateError(context: Context) {
    val mVibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    val mVibratePattern = longArrayOf(50, 100)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(mVibratePattern, VibrationEffect.DEFAULT_AMPLITUDE)
        mVibrator.vibrate(effect)
    } else {
        mVibrator.vibrate(mVibratePattern, -1)
    }
}



