package org.laolittle.plugin

enum class AmiyaFunction {
    NUDGE {
        override fun toString(): String = "戳一戳"
    },
    RESPONSE{
        override fun toString(): String = "普通回应"
    },
    SIGN_IN {
        override fun toString(): String = "签到"
    },
}