package com.gotechdynamics.sitarman

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform