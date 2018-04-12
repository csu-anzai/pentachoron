package io.jim.tesserapp.entity

import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test

class EntityBufferTest {

    private val entityBuffer = EntityBuffer(
            Pair("Cube", Entity::class),
            Pair("Label", Entity::class),
            Pair("Axis", Entity::class))

    private val cube = entityBuffer["Cube"]
    private val axis = entityBuffer["Axis"]

    @Test
    fun implicitRootEntity() {
        assertEquals(entityBuffer["Root"], entityBuffer.root)
    }

    @Test(expected = EntityBuffer.NoSuchEntityException::class)
    fun throwOnGettingNonExistentEntity() {
        entityBuffer["Foo"]
    }

    @Test(expected = Entity.NoSuchChildException::class)
    fun removeNonParentingChild() {
        cube.removeChild(axis)
    }

    @Test
    fun implicitRootParenting() {
        assertEquals("Root must have 3 children", 3, entityBuffer["Root"].count())
    }

    @Test
    fun parentedTransformOverOneLevel() {
        entityBuffer.root.translation(Vector(1.0, 0.0, 0.0, 1.0))
    }

}