package io.jim.tesserapp.cpp.matrix

import io.jim.tesserapp.cpp.vector.VectorN

fun scale(size: Int, factors: VectorN) =
        if (factors.dimension != size - 1)
            throw RuntimeException("Invalid scale-vector dimension")
        else
            quadratic(size) { row, col ->
                if (row == size - 1 && col == size - 1) 1.0
                else if (row == col) factors[row]
                else 0.0
            }
