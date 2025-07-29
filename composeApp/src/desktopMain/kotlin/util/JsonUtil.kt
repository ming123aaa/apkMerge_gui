package util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtil {

    fun mergeJson(json: String, obj: Any): String {
        var type = object : TypeToken<MutableMap<String, Any>>() {}.type
        var map = Gson().fromJson<MutableMap<String, Any>>(json, type)
        var toJson = Gson().toJson(obj)
        var newMap = Gson().fromJson<MutableMap<String, Any>>(toJson, type)
        newMap.forEach { k, v ->
            map[k] = v
        }
        return Gson().toJson(map)
    }


}