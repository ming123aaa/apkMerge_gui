import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset

object FileUtils {


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