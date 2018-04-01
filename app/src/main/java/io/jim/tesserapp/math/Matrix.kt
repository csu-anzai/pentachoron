package io.jim.tesserapp.math

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlin.math.cos
import kotlin.math.sin

/**
 * Homogeneous matrices used for affine and perspective transformation.
 * The [dimension] specifies the vector dimension this matrix can be applied to.
 * The matrix size itself is always one dimension bigger than it specifies,
 * as the matrix is used for homogeneous coordinates.
 * Vectors multiplied to this matrix must be one greater than the dimension as
 * well, since they are homogeneous coordinates or directions as well.
 */
class Matrix(val dimension: Int) : Indexable<Direction> {

    private val coefficients = ArrayList<Direction>()

    init {
        // Initialize to identity matrix:
        assertTrue("Size must be > 0", dimension > 0)
        val initializer = ArrayList<Double>().also {
            for (c in 0..dimension) it.add(0.0)
        }

        for (r in 0..dimension) coefficients.add(Direction(initializer))

        identity()
    }

    constructor(other: Matrix) : this(other.dimension) {
        forEachCoefficient { r, c -> this[r][c] = other[r][c] }
    }

    override operator fun get(index: Int): Direction {
        return coefficients[index]
    }

    override operator fun set(index: Int, value: Direction) {
        for (i in 0 until value.dimension) this[index][i] = value[i]
    }

    var x: Direction by IndexAlias(0)
    var y: Direction by IndexAlias(1)
    var z: Direction by IndexAlias(2)
    var q: Direction by IndexAlias(3)

    var right: Direction by IndexAlias(0)
    var up: Direction by IndexAlias(1)
    var forward: Direction by IndexAlias(2)

    /**
     * Load the identity matrix.
     */
    private fun identity() {
        forEachCoefficient { r, c -> this[r][c] = if (r == c) 1.0 else 0.0 }
    }

    /**
     * Construct a matrix by defining a coordinate space from [axises].
     * The axises themselves are positioned at [base].
     */
    fun space(base: Point, vararg axises: Vector) = this.apply {
        assertEquals("Axis count must match with matrix", axises.size, dimension)
        assertTrue("Axis dimension must match with matrix", axises.all { it.dimension == dimension })
        assertEquals("Base position dimension must match with matrix", base.dimension, dimension)
        axises.forEachIndexed { r, axis ->
            axis.forEachIndexed { c, coefficient ->
                this[r][c] = coefficient
            }
        }
        base.forEachIndexed { index, d -> this[dimension][index] = d }
    }

    /**
     * Construct a matrix, representing an affine linear scaling transformation.
     */
    fun scale(scale: Double) = this.apply {
        for (i in 0 until dimension) {
            this[i][i] = scale
        }
    }

    /**
     * Construct a matrix, representing an affine linear scaling transformation.
     */
    fun scale(scale: Vector) = this.apply {
        assertEquals("Scale vector dimension must match with matrix", dimension, scale.dimension)
        scale.forEachIndexed { i, fi -> this[i][i] = fi }
    }

    /**
     * Construct a matrix, representing an affine rotation transformation of [phi] on the [a]-[b]-plane.
     * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
     */
    fun rotation(a: Int, b: Int, phi: Double) = this.apply {
        assertTrue("Plane-axis not in matrix dimension", a < dimension && b < dimension)
        // Rotation on y-q plane:
        //  -> y-axis rotates towards q-axis
        //  -> q-axis rotates towards negative y-axis
        // Myy = cos(phi)   | decreases
        // Myw = sin(phi)   | increases
        // Mwy = -sin(phi)  | increases, but in negative direction, since y rotates towards q
        // Mww = cos(phi)   | decreases
        this[a][a] = cos(phi)
        this[a][b] = sin(phi)
        this[b][a] = -sin(phi)
        this[b][b] = cos(phi)
    }

    /**
     * Construct a matrix, representing an affine translation transformation by a given [direction] vector.
     * Remember that vectors multiplied to this matrix must be homogeneous, their last component
     * determines whether they are transformed at all.
     */
    fun translation(direction: Direction) = this.apply {
        assertEquals("Translation vector dimension must match with matrix", dimension, direction.dimension)
        direction.forEachIndexed { i, d ->
            this[dimension][i] = d
        }
    }

    /**
     * Construct a matrix, representing an perspective division transformation.
     */
    fun perspective() = this.apply {
        this[dimension - 1][dimension] = -1.0
        this[dimension][dimension] = 0.0
    }

    /**
     * Construct a matrix, representing an perspective division transformation,
     * while remapping the last vector component between a near and far value.
     * @param near Near plane. If point lies on that plane (negated), it will be projected to 0.
     * @param far Far plane. If point lies on that plane (negated), it will be projected to 1.
     */
    fun perspective(near: Double, far: Double) = this.apply {
        perspective()
        assertTrue(near > 0.0)
        assertTrue(far > 0.0)
        assertTrue(far > near)
        this[dimension - 1][dimension - 1] = -far / (far - near)
        this[dimension][dimension - 1] = -(far * near) / (far - near)
    }

    infix fun compatible(rhs: Matrix) = dimension == rhs.dimension
    infix fun compatible(rhs: Vector) = dimension == rhs.dimension

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     */
    operator fun times(rhs: Matrix) =
            Matrix(dimension).also {
                assertTrue("Matrices must be compatible", this compatible rhs)
                forEachCoefficient { r, c ->
                    it[r][c] = (0..dimension).map { i -> this[r][i] * rhs[i][c] }.sum()
                }
            }

    /**
     * Construct a look-at matrix, representing both, translation and rotation.
     * The camera is constructed in such a way that it is positioned at [eye], points to [target],
     * and the upper edge is oriented in the [refUp] direction.
     */
    fun lookAt(eye: Point, target: Point, refUp: Direction) {
        assertTrue("Look at does only work in 3D", dimension == 3 && eye.dimension == 3 && target.dimension == 3)

        forward = (eye - target).apply { normalize() }
        right = (refUp cross forward).apply { normalize() }
        up = (forward cross right).apply { normalize() }

        transpose()

        this[3] = -eye * this
    }

    /**
     * Transpose this matrix.
     */
    fun transpose() {
        var tmp: Double

        for (r in 0..dimension) {
            for (c in 0..r) {
                tmp = this[r][c]
                this[r][c] = this[c][r]
                this[c][r] = tmp
            }
        }
    }

    /**
     * Call a function for each coefficient. Indices of row and column are passed to [f].
     */
    fun forEachCoefficient(f: (Int, Int) -> Unit) {
        coefficients.forEachIndexed { r, row ->
            row.forEachIndexed { c, _ ->
                f(r, c)
            }
        }
    }

    /**
     * Create a string representing this matrix.
     */
    override fun toString() =
            StringBuilder().also {
                it.append("[ (").append(dimension).append("D): ")
                coefficients.forEachIndexed { r, row ->
                    row.forEachIndexed { c, col ->
                        it.append(Format.decimalFormat.format(col))
                        if (c < dimension) it.append(", ")
                    }
                    if (r < dimension) it.append(" | ")
                }
                it.append(" ]")
            }.toString()

}
