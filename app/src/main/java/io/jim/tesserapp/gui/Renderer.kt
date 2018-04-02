package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxLines: Int, context: Context) : GLSurfaceView.Renderer {

    private val geometries = ArrayList<Geometry>()
    private val maxLineVertices = maxLines * 2
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer
    private lateinit var indexBuffer: IndexBuffer
    private var uploadGeometryBuffers = false
    private var modelMatrix = Matrix(3)
    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = Matrix(3)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        vertexBuffer = VertexBuffer(maxLineVertices)
        indexBuffer = IndexBuffer(maxLineVertices)

        shader.uploadProjectionMatrix(Matrix(3).apply { perspective(0.1, 100.0) })
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(geometry)
        uploadGeometryBuffers = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        viewMatrix.multiplicationFrom(Matrix(3).apply { lookAt(Vector(4.0, 4.0, 0.0), Vector(3), Vector(0.0, 1.0, 0.0)) },
                Matrix(3).apply { scale(Vector(1.0, width.toDouble() / height, 1.0)) })
        shader.uploadViewMatrix(viewMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shader.uploadModelMatrix(modelMatrix)

        if (uploadGeometryBuffers) {
            indexBuffer.baseIndex = 0
            for (geometry in geometries) {
                for (point in geometry.points) {
                    assertEquals("All vertices must be 3D", 3, point.dimension)
                    vertexBuffer.appendVertex(point, geometry.color)
                }
                for (line in geometry.lines) {
                    indexBuffer.appendIndex(line.first)
                    indexBuffer.appendIndex(line.second)
                }
                indexBuffer.baseIndex += geometry.points.size
            }
            uploadGeometryBuffers = false
        }

        vertexBuffer.bind(shader)
        indexBuffer.bind()

        glDrawElements(GL_LINES, indexBuffer.size, GL_UNSIGNED_INT, 0)
    }

    fun rotation(horizontal: Double, vertical: Double) {
        modelMatrix.multiplicationFrom(Matrix(3).apply { rotation(2, 0, horizontal) }, Matrix(3).apply { rotation(1, 0, vertical) })
    }

}
