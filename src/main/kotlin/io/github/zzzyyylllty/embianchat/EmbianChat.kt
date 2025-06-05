package io.github.zzzyyylllty.embianchat

import io.github.zzzyyylllty.embianchat.data.PlayerData
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object EmbianChat : Plugin() {

    val dataMap: LinkedHashMap<String, PlayerData> = linkedMapOf()
    // 项目使用TabooLib Start Jar 创建!
    override fun onEnable() {
        info("Successfully running EmbianChat!")
    }
}