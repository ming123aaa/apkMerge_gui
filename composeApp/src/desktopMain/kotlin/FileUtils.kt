import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset

object FileUtils {

    /**
     * 将内容追加到文件中,如果文件不存在则创建文件
     */
    fun writeTextAppend(fileName:String,content:String){
        File(fileName).parentFile.mkdirs()
        val outputStream: OutputStream = FileOutputStream(File(fileName),true)
        OutputStreamWriter(outputStream, Charset.forName("UTF-8")).use {
            it.write(content)
        }
    }


    fun readText(fileName:String):String{
        val inputStream: InputStream = File(fileName).inputStream()
        val readText = InputStreamReader(inputStream, Charset.forName("UTF-8")).use {
            it.readText()
        }
        return readText
    }

    fun writeText(fileName:String,content:String){
        File(fileName).parentFile.mkdirs()
        val outputStream: OutputStream = File(fileName).outputStream()
        OutputStreamWriter(outputStream, Charset.forName("UTF-8")).use {
            it.write(content)
        }
    }
    fun deleteFile(path:String):Boolean{
        return deleteFile(File(path))
    }
    fun deleteFile(file:File):Boolean{
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteFile(child)
            }
        }
        return file.delete()
    }

}