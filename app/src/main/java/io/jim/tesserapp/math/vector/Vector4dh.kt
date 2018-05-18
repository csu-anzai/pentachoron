package io.jim.tesserapp.math.vector

/**
 * A 4d vector with an additional 5th homogeneous component called `w`.
 * The first 4 components are called: `x`, `y`, `z` and `q`.
 */
class Vector4dh(x: Float, y: Float, z: Float, q: Float) : VectorN(x, y, z, q) {

    /**
     * Construct a vector with all components set to zero.
     */
    constructor() : this(0f, 0f, 0f, 0f)

    /**
     * Though this vector is 4d, it technically has 5 columns, including the virtual w-component.
     */
    override val cols = super.cols + 1

    /**
     * The dimension string should underline that the vector is not actually 4d.
     */
    override val dimensionString = "4dh"

    /**
     * X-component.
     */
    var x by IndexAlias(0)

    /**
     * Y-component.
     */
    var y by IndexAlias(1)

    /**
     * Z-component.
     */
    var z by IndexAlias(2)

    /**
     * Q-component.
     */
    var q by IndexAlias(3)

    /**
     * Intercept getting the w-component, which is always 1.
     */
    override fun get(row: Int, col: Int): Float {
        return if (col < 3)
            super.get(row, col)
        else
            w
    }

    /**
     * Intercept setting values to the fourth column, which will effectively lead to w-division.
     */
    override fun set(row: Int, col: Int, value: Float) {
        if (col < 3)
            super.set(row, col, value)
        else {
            w = value
        }
    }

    /**
     * W-component. This is always 1.
     * Setting this value will lead to w-division.
     */
    private var w = 1f
        set(value) {
            this /= value
        }

}