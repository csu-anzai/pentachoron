package io.jim.tesserapp.ui.model

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import io.jim.tesserapp.cpp.Transform
import io.jim.tesserapp.cpp.projectWireframe
import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.geometry.*
import io.jim.tesserapp.util.synchronized

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {
    
    /**
     * The featured geometry.
     */
    val featuredGeometry = Geometry(
            name = "Featured Geometry",
            isFourDimensional = true,
            onTransformUpdate = {
                synchronized {
                    Transform(
                            rotationX = rotationX.smoothed * Math.PI,
                            rotationY = rotationY.smoothed * Math.PI,
                            rotationZ = rotationZ.smoothed * Math.PI,
                            rotationQ = rotationQ.smoothed * Math.PI,
                            translationX = translationX.smoothed,
                            translationY = translationY.smoothed,
                            translationZ = translationZ.smoothed,
                            translationQ = translationQ.smoothed
                    )
                }
            },
            lines = extruded(
                    quadrilateral(
                            VectorN(2.0, 2.0, 2.0, 0.0),
                            VectorN(-2.0, 2.0, 2.0, 0.0),
                            VectorN(-2.0, -2.0, 2.0, 0.0),
                            VectorN(2.0, -2.0, 2.0, 0.0),
                            color = SymbolicColor.ACCENT),
                    direction = VectorN(0.0, 0.0, -4.0, 0.0),
                    keepColors = true,
                    connectorColor = SymbolicColor.ACCENT
            )
    )
    
    /**
     * The grid geometry representing a cartesian coordinate system unit grid.
     */
    val gridGeometry = Geometry(
            name = "Grid",
            lines = gridOmitAxisIndicator(),
            onTransformUpdate = { Transform() }
    )
    
    /**
     * Enable or disable grid rendering.
     */
    var enableGrid: Boolean
        set(value) {
            if (value) {
                geometries += gridGeometry
            } else {
                geometries -= gridGeometry
            }
        }
        get() = geometries.contains(gridGeometry)
    
    /**
     * The axis geometry representing the origin of the cartesian coordinate system.
     */
    val axisGeometry = Geometry(
            name = "Axis",
            lines = axis(),
            onTransformUpdate = { Transform() }
    )
    
    /**
     * List containing all geometries.
     *
     * The geometries are kept inside a hash-set instead of in a simply list because
     * geometries added multiple times should not result in the same geometry stored
     * more once in the set.
     *
     * This happens because this set outlives the UI, and the UI may initially add
     * geometry to the set each time the UI is set up.
     */
    val geometries = HashSet<Geometry>().also {
        it += featuredGeometry
        it += axisGeometry
    }
    
    /**
     * X rotation, in units of PI.
     */
    val rotationX = SmoothedLiveData()
    
    /**
     * Y rotation, in units of PI.
     */
    val rotationY = SmoothedLiveData()
    
    /**
     * Z rotation, in units of PI.
     */
    val rotationZ = SmoothedLiveData()
    
    /**
     * Q rotation, in units of PI.
     */
    val rotationQ = SmoothedLiveData()
    
    /**
     * X translation.
     */
    val translationX = SmoothedLiveData()
    
    /**
     * Y translation.
     */
    val translationY = SmoothedLiveData()
    
    /**
     * Z translation.
     */
    val translationZ = SmoothedLiveData()
    
    /**
     * Q translation.
     */
    val translationQ = SmoothedLiveData(initialValue = 3.7)
    
    /**
     * Camera distance.
     */
    val cameraDistance = SmoothedLiveData(initialValue = 3.0)
    
    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    val horizontalCameraRotation = SmoothedLiveData(initialValue = Math.PI / 3.0, transitionInterval = 80.0)
    
    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    val verticalCameraRotation = SmoothedLiveData(initialValue = -Math.PI / 8.0, transitionInterval = 80.0)
    
    var colorResolver: ColorResolver = { Color.BLACK }
    
    var fourthDimensionVisualizer = projectWireframe
    
}
