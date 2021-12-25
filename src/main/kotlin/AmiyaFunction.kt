package org.laolittle.plugin

enum class AmiyaFunction {
    NUDGE {
        override fun toString(): String = "戳一戳"
    },
    SIGN_IN {
        override fun toString(): String = "签到"
    },
}