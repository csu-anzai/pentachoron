package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.GeometryManager
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.math.transform.Projection3dMatrix
import io.jim.tesserapp.math.transform.ViewMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(GeometryManager())

    private val clearColor = Color(context, android.R.color.background_light)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Projection3dMatrix(near = 0.1f, far = 100f)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glLineWidth(4f)

        println("Open GLES version: ${GLES30.glGetString(GLES30.GL_VERSION)}")
        println("GLSL version: ${GLES30.glGetString(GLES30.GL_SHADING_LANGUAGE_VERSION)}")
        println("Vendor: ${GLES30.glGetString(GLES30.GL_VENDOR)}")

        shader = Shader()
        vertexBuffer = VertexBuffer()

        shader.bind()
        shader.uploadProjectionMatrix(projectionMatrix)

        sharedRenderData.geometryManager.vertexBufferRewritten += { buffer ->
            vertexBuffer.instructVertexAttributes(shader, buffer)
        }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        sharedRenderData.camera.aspectRatio = width.toFloat() / height.toFloat()
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        sharedRenderData.synchronized { (geometryManager) ->

            shader.bind()

            // Recompute view matrix:
            viewMatrix.compute()
            shader.uploadViewMatrix(viewMatrix)

            // Upload model matrices:
            geometryManager.computeModelMatrices()
            shader.uploadModelMatrixBuffer(
                    geometryManager.modelMatrixBuffer.buffer,
                    geometryManager.modelMatrixBuffer.activeGeometries)

            // Ensure vertex data is up-to-date:
            geometryManager.updateVertexBuffer()

            // Draw actual geometry:
            GLES30.glDrawArrays(
                    GLES30.GL_LINES, 0,
                    geometryManager.vertexBuffer.writtenElementCounts)
        }
    }

}
