package io.jim.tesserapp.math

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Formats [number] into a string, focusing on readability.
 * If [number] is negative zero, no unary minus is printed.
 */
fun formatNumber(number: Float) =
        decimalFormat.format(if ((number * 100).toInt() == 0) 0f else number)
                ?: throw RuntimeException("Formatted decimal string is null")

private val decimalFormat = DecimalFormat(" 0.00;-0.00").apply {
    roundingMode = RoundingMode.HALF_UP
}
