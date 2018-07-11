package io.jim.tesserapp.cpp

import android.opengl.GLES20
import io.jim.tesserapp.cpp.graphics.Color
import io.jim.tesserapp.cpp.graphics.GlVertexBuffer
import io.jim.tesserapp.cpp.graphics.Shader
import io.jim.tesserapp.cpp.vector.VectorN

data class Vertex(
        val position: VectorN,
        val color: Color
)

fun  generateVertexBuffer(shader: Shader) = GlVertexBuffer(GLES20.GL_LINES) {
    // Position attribute:
    GLES20.glEnableVertexAttribArray(shader.positionAttributeLocation)
    GLES20.glVertexAttribPointer(
            shader.positionAttributeLocation,
            ATTRIBUTE_FLOATS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            OFFSET_POSITION
    )
    
    // Color attribute:
    GLES20.glEnableVertexAttribArray(shader.colorAttributeLocation)
    GLES20.glVertexAttribPointer(
            shader.colorAttributeLocation,
            ATTRIBUTE_FLOATS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            OFFSET_COLOR
    )
}

const val FLOAT_BYTE_LENGTH = 4

/**
 * Floats take by one attribute.
 * Due to alignment, each attribute has 4 float values, regardless of how many
 * it actually uses.
 */
const val ATTRIBUTE_FLOATS = 4

/**
 * Counts of different attributes.
 * - Position
 * - Color
 */
const val ATTRIBUTE_COUNTS = 2

/**
 * Floats taken by one complete vertex.
 */
const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS

/**
 * Bytes taken by one complete vertex.
 */
const val BYTES_PER_VERTEX = VERTEX_FLOATS * FLOAT_BYTE_LENGTH

/**
 * Vertex stride, in bytes.
 */
const val STRIDE = BYTES_PER_VERTEX

/**
 * Position attribute offset, in bytes.
 */
const val OFFSET_POSITION = 0 * FLOAT_BYTE_LENGTH

/**
 * Color attribute offset, in bytes.
 */
const val OFFSET_COLOR = OFFSET_POSITION + ATTRIBUTE_FLOATS * FLOAT_BYTE_LENGTH
