package util

import java.io.*
import java.util.Properties

class PropertiesStore(private val filePath: String) {
    private val properties = Properties()

    init {
        try {
            FileInputStream(filePath).use { properties.load(it) }
        } catch (e: FileNotFoundException) {
            // 文件不存在时创建新文件
            save()
        }
    }

    fun put(key: String, value: String) {
        properties.setProperty(key, value)
        save()
    }

    fun get(key: String): String? = properties.getProperty(key)

    fun remove(key: String) {
        properties.remove(key)
        save()
    }

    private fun save() {
        FileOutputStream(filePath).use { properties.store(it, null) }
    }

    fun getAll(): Map<String, String> = properties.map { it.key.toString() to it.value.toString() }.toMap()
}

