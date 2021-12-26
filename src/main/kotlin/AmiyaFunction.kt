package org.laolittle.plugin

enum class AmiyaFunction {
    NUDGE {
        override fun toString(): String = "戳一戳"
    },
    RESPONSE {
        override fun toString(): String = "普通回应"
    },
    SIGN_IN {
        override fun toString(): String = "签到"
    },
    GACHA_SIMULATE {
        override fun toString(): String = "模拟抽卡"
    },
    QUERY_OPERATOR {
        override fun toString(): String = "干员查询"
    },
    QUERY_ENEMY {
        override fun toString(): String = "敌人查询"
    },
    QUERY_ITEM {
        override fun toString(): String = "物品查询"
    },
    QUERY_OFFER {
        override fun toString(): String = "公招查询"
    },
    CALC_GET {
        override fun toString(): String = "合成玉计算"
    },
    WEIBO_POST {
        override fun toString(): String = "微博推送"
    },
    SEE_SOURCECODE {
        override fun toString(): String = "查看阿米娅的源代码"
    }

}