/*
 *  Created by Jim Eckerlein on 8/5/18 10:51 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 8/5/18 10:47 AM
 */

package io.jim.tesserapp.util

/**
 * Stores a pair of values, one representing an old value, and one representing a new value.
 * Only the new value is settable.
 * Before the new value is actually updated, the current new value is copied into the old value.
 * The essence of this class is [delta], computing the difference between the old and new value.
 *
 * Using instances of this class is thought as a replacement of explicitly storing two versions of
 * a state, an updated one and an old one, in order to access the difference rather than an
 * absolute state.
 */
class FloatDeltanizer(initializer: Float) {
    
    private var old = initializer
    
    /**
     * The updated, new state.
     * This value is copied to the old value before it gets updated.
     */
    var new = initializer
        set(value) {
            old = field
            field = value
        }
    
    /**
     * The difference between the old and [new] value.
     */
    val delta: Float
        get() = new - old
    
    /**
     * Resets both states to the same value.
     * [delta] will return zero when called immediately after resetting.
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun reset(value: Float) {
        new = value
        new = value
    }
    
}