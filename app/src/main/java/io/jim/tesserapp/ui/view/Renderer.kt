package io.jim.tesserapp.ui.view

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.jim.tesserapp.cpp.Camera
import io.jim.tesserapp.cpp.generateVertexBuffer
import io.jim.tesserapp.cpp.graphics.Color
import io.jim.tesserapp.cpp.graphics.GlVertexBuffer
import io.jim.tesserapp.cpp.graphics.Shader
import io.jim.tesserapp.cpp.matrix.*
import io.jim.tesserapp.cpp.resolveLineToVertices
import io.jim.tesserapp.cpp.transformed
import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.util.synchronized
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(
        val clearColor: Color,
        val viewModel: MainViewModel,
        val assets: AssetManager,
        val dpi: Double) : GLSurfaceView.Renderer {
    
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: GlVertexBuffer
    
    private var aspectRatio: Double = 1.0
    
    companion object {
        
        /**
         * Converts inches to millimeters.
         */
        private const val MM_PER_INCH = 25.4
        
        /**
         * Specifies width of lines, in millimeters.
         */
        private const val LINE_WIDTH_MM = 0.15
        
    }
    
    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(
                clearColor.red,
                clearColor.green,
                clearColor.blue,
                1f
        )
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        GLES20.glLineWidth((dpi / MM_PER_INCH * LINE_WIDTH_MM).toFloat())
        
        println("Open GLES version: ${GLES20.glGetString(GLES20.GL_VERSION)}")
        println("GLSL version: ${GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)}")
        println("Renderer: ${GLES20.glGetString(GLES20.GL_RENDERER)}")
        println("Vendor: ${GLES20.glGetString(GLES20.GL_VENDOR)}")
        
        // Construct shader:
        shader = Shader(assets)
        
        // Construct vertex buffer:
        vertexBuffer = generateVertexBuffer(shader)
    }
    
    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        aspectRatio = width.toDouble() / height
    }
    
    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        viewModel.synchronized {
    
            val camera = Camera(
                    cameraDistance.smoothed,
                    aspectRatio,
                    horizontalCameraRotation.smoothed,
                    verticalCameraRotation.smoothed
            )
    
            geometries.forEach { geometry ->
        
                val transform = geometry.onTransformUpdate()
        
                /*
                C++ start
                
                Pipeline variables:
                - camera
                - isFourDimensional (per geometry)
                - line (per geometry)
                - matrix (per geometry)
                */
        
                shader.program.bound {
            
                    shader.uploadViewMatrix(view(camera))
                    shader.uploadProjectionMatrix(perspective(near = 0.1, far = 100.0))
            
                    // Process geometries and draw the generated vertices:
                    transformed(
                            transformChain(
                                    rotation(5, RotationPlane.AROUND_X, transform.rotationX),
                                    rotation(5, RotationPlane.AROUND_Y, transform.rotationY),
                                    rotation(5, RotationPlane.AROUND_Z, transform.rotationZ),
                                    rotation(5, RotationPlane.XQ, transform.rotationQ),
                                    translation(5, VectorN(
                                            transform.translationX,
                                            transform.translationY,
                                            transform.translationZ,
                                            transform.translationQ
                                    ))
                            ),
                            geometry.isFourDimensional,
                            geometry.lines,
                            fourthDimensionVisualizer
                    ).flatMap {
                        resolveLineToVertices(it, symbolicColorMapping)
                    }.also {
                        vertexBuffer.draw(it)
                    }
            
                    // C++ end
            
                }
                
                
            }
            
        }
        
    }
    
}