package io.jim.tesserapp.ui.controllers

import io.jim.tesserapp.ui.ControllerView

/**
 * [Controller]s set values through this interface.
 */
interface Controllable {

    /**
     * Rotation transform.
     */
    val rotation: Rotatable

    /**
     * Translation transform.
     */
    val translation: Translatable

    /**
     * Camera distance.
     */
    var cameraDistance: Double

    /**
     * Option whether to render the base-grid.
     */
    var renderGrid: Boolean

    fun setup(view: ControllerView)

}
