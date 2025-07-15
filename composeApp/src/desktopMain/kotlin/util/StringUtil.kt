package util

import java.util.regex.Pattern

object StringUtil {

     fun filterInput(input: String): String {
        // 正则表达式匹配英文字符、数字和常见符号
        val pattern = Pattern.compile("[^a-zA-Z0-9\\p{Punct}]")
        val matcher = pattern.matcher(input)
        return matcher.replaceAll("")
    }
}