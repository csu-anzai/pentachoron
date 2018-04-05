package io.jim.tesserapp.graphics

import android.content.Context
import android.opengl.GLES10.GL_MULTISAMPLE
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * The root for every spatial.
     */
    val rootSpatial = Spatial("Root")

    private lateinit var shader: Shader
    private lateinit var geometryBuffer: GeometryBuffer
    private var rebuildGeometryBuffers = true
    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = MatrixBuffer(3)
    private val projectionMatrix = MatrixBuffer(1)

    companion object {
        private const val MAX_MODELS = 100
        private const val MAX_VERTICES = 1000
        private const val MAX_INDICES = 1000
    }

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_MULTISAMPLE)
        glLineWidth(4f)

        shader = Shader(MAX_MODELS)
        geometryBuffer = GeometryBuffer(MAX_MODELS, MAX_VERTICES, MAX_INDICES)

        projectionMatrix.perspective2D(0, 0.1f, 100f)
        shader.uploadProjectionMatrix(projectionMatrix)

        // Rebuild geometry buffers upon spatial hierarchy change:
        Spatial.addChildrenChangedListener { rebuildGeometryBuffers = true }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        viewMatrix.lookAt(1, Vector(4.0, 0.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0, 1.0))
        viewMatrix.scale(2, Vector(1.0, width.toDouble() / height, 1.0, 1.0))
        viewMatrix.multiply(1, 2, 0)

        shader.uploadViewMatrix(viewMatrix)
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // If geometry structure changes, we need to rebuild all buffers.
        // This is a costly operation, but usually not often performed.
        if (rebuildGeometryBuffers) {
            geometryBuffer.recordGeometries(rootSpatial)
            rebuildGeometryBuffers = false
        }

        // Recompute global model matrices.
        // Since render is only requested when data is changed, and geometry which is not
        // transformed often is not expected in large chunks, we can simply re-compute all
        // global model matrices:
        rootSpatial.computeModelMatricesRecursively()

        geometryBuffer.bind(shader)

        // Re-upload global model matrices:
        shader.uploadModelMatrixBuffer(geometryBuffer.modelMatrices, geometryBuffer.globalModelMatrixCount)

        // Draw actual geometry:
        glDrawElements(GL_LINES, geometryBuffer.indexCount, GL_UNSIGNED_INT, 0)
    }

}
