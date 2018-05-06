package io.jim.tesserapp.util

data class Flag(

        private var flag: Boolean

) {

    val listeners = ListenerListParam<Boolean>()

    operator fun not() = !flag

    fun set() {
        setTo(true)
    }

    fun unset() {
        setTo(false)
    }

    private fun setTo(newFlag: Boolean) {
        if (flag == newFlag) {
            return
        }
        flag = newFlag
        listeners.fire(newFlag)
    }

}