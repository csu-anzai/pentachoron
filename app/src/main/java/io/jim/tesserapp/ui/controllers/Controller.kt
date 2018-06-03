package io.jim.tesserapp.ui.controllers

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.common.mapped

/**
 * Control a specific value using a seek bar.
 *
 * @param onValueUpdate
 * Pipe the current value of the seek-bar to the actual value this controller targets.
 * It takes the current seek-bar value, mapped to range between [min] and [max].
 *
 * @property seekBar
 * The seek-bar as the UI value-input.
 *
 * @property watch
 * The text-view displaying the current value.
 *
 */
class Controller(
        private val seekBar: SeekBar,
        private val watch: TextView,
        min: Double,
        max: Double,
        startValue: Double,
        private val formatString: String,
        private val onValueUpdate: (value: Double) -> Unit
) {

    private val valueRange = min..max

    private val seekBarRange = 0.0..seekBar.max.toDouble()

    /**
     * Called when seek-bar progress changes.
     * Updates the watch text and calls [onValueUpdate] with the new value.
     */
    private fun update() {
        val value = mapped(seekBar.progress.toDouble(), seekBarRange, valueRange)
        watch.text = String.format(formatString, formatNumber(value))
        onValueUpdate(value)
    }

    init {
        if (max < min)
            throw RuntimeException("Maximum must be greater than minimum")
        if (startValue < min || startValue > max)
            throw RuntimeException("Start value must be located in min-max range")

        seekBar.progress = mapped(startValue, valueRange, seekBarRange).toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                update()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initially format the value label, using the start value:
        update()
    }

}
