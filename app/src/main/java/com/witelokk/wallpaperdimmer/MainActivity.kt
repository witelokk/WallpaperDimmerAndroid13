package com.witelokk.wallpaperdimmer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.topjohnwu.superuser.Shell

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Shell.getShell()
        if (Shell.isAppGrantedRoot() != true) {
            Toast.makeText(this, R.string.no_permission_toast, Toast.LENGTH_LONG).show()
            finishAndRemoveTask()
            return
        }

        val slider = findViewById<Slider>(R.id.dim_slider)
        getDimAmount {
            slider.value = (it*100).toInt().toFloat()
        }
        slider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setDimAmount(slider.value/100)
            }
        })
    }

    private fun getDimAmount(callback: (Float) -> Unit){
        Shell.cmd("cmd wallpaper get-dim-amount").submit { result ->
            Log.i("getDimAmount", result.out.joinToString("\n"))
            callback(result.out[0].split(" ")[6].trim().toFloat())
        }
    }

    private fun setDimAmount(@FloatRange(from=0.0, to=1.0) dimAmount: Float) {
        Shell.cmd("cmd wallpaper set-dim-amount $dimAmount").submit { result ->
            Log.i("setDimAmount", result.out.joinToString("\n"))
        }
    }
}