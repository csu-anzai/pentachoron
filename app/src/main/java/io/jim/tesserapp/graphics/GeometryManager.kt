package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.Flag
import io.jim.tesserapp.util.InputStreamBuffer
import io.jim.tesserapp.util.ListenerList

/**
 * Manages a geometry list, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager {

    /**
     * Model matrix buffer.
     * When geometries are transformed, this buffer is updated automatically.
     */
    val modelMatrixBuffer = ModelMatrixBuffer(matrixDimension = 4)

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    //val vertexBuffer = InputStreamBuffer(100, Vertex.COMPONENTS_PER_VERTEX)
    val positionBuffer = InputStreamBuffer(100, VertexBuffer.FLOATS_PER_POSITION)
    val colorBuffer = InputStreamBuffer(100, VertexBuffer.FLOATS_PER_COLOR)
    val modelIndexBuffer = InputStreamBuffer(100, VertexBuffer.FLOATS_PER_MODEL_INDEX)

    /**
     * Listeners are called when the vertex buffer was rewritten and needs to be uploaded to OpenGL.
     */
    val vertexBufferRewritten = ListenerList()

    private val vertexBufferUpdateRequested = Flag(false)

    /**
     * Registers [geometry] into this manager.
     * Does nothing if [geometry] is already registered.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (!(modelMatrixBuffer.register(geometry))) return

        geometry.onGeometryChangedListeners += vertexBufferUpdateRequested::set

        // Guarantee the the geometry is initially uploaded:
        vertexBufferUpdateRequested.set()
    }

    /**
     * Unregisters [geometry] into this manager.
     * Does nothing if [geometry] is not registered.
     */
    operator fun minusAssign(geometry: Geometry) {
        if (!(modelMatrixBuffer.unregister(geometry))) return

        // Unregister this geometry manager:
        geometry.onGeometryChangedListeners -= vertexBufferUpdateRequested::set

        vertexBufferUpdateRequested.set()
    }

    /**
     * Rewrite the vertex buffer if any vertex data changed.
     */
    fun updateVertexBuffer() {

        if (!vertexBufferUpdateRequested) {
            // No update was requested, so buffer rewrite is not necessary:
            return
        }

        // Rewrite vertex buffer:
        positionBuffer.rewind()
        colorBuffer.rewind()
        modelIndexBuffer.rewind()
        modelMatrixBuffer.forEachVertex { position, (red, green, blue), modelIndex ->
            positionBuffer += listOf(position.x, position.y, position.z)
            colorBuffer += listOf(red, green, blue)
            modelIndexBuffer += listOf(modelIndex.toFloat())
        }

        vertexBufferRewritten.fire()

        vertexBufferUpdateRequested.unset()
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        modelMatrixBuffer.computeModelMatrices()
    }

}
