package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxLines: Int) : GLSurfaceView.Renderer {

    private val geometries = ArrayList<Geometry>()
    private val maxLineVertices = maxLines * 2
    private lateinit var vertexBuffer: VertexBuffer
    private lateinit var shader: Shader
    private val matrix = Matrix.scale(4, 0.5)
    private var viewMatrix = Matrix(4)
    private var modelMatrix = Matrix(4)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        vertexBuffer = VertexBuffer(maxLineVertices)
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(geometry)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        //project3into2 = Matrix.perspective(4, 10.0, 0.1)
        viewMatrix = Matrix.scale(4, Vector(1.0, (width).toDouble() / height, 1.0))
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        println("Draw, fill vertex buffer")
        for (geometry in geometries) {
            for (point in geometry) {
                vertexBuffer.appendVertex((point * modelMatrix * viewMatrix * matrix).perspectiveProjection, geometry.color)
            }
        }

        vertexBuffer.draw(shader, GL_LINES)
    }

    fun rotate(theta: Double, phi: Double) {
        modelMatrix = Matrix.rotation(4, 0, 1, phi) * Matrix.rotation(4, 1, 2, theta) * modelMatrix
    }

}
