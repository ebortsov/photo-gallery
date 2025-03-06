package com.github.ebortsov.photogallery.interfaces

// Not thread safe
class Launchable(private val block: () -> Unit) {
    private var hasLaunched = false
    fun launch() {
        if (!hasLaunched) {
            hasLaunched = true
            block()
        }
    }
}