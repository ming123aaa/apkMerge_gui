package util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object RuntimeUtil {

    var allProcess=ArrayList<Process>()

    @Throws(IOException::class, InterruptedException::class)
    fun execAndPrint(
        cmd: Array<String>,
        timeOut: Int,
        errorCall: (String?) -> Unit = {},
        onProcess: (Process)->Unit={},
        call: (String?) -> Boolean = { false }
    ): Boolean {
        val processBuilder = ProcessBuilder(cmd.toList())
        val p =  processBuilder.start()
        allProcess.add(p)
        Thread {
            val reader = BufferedReader(InputStreamReader(p.inputStream, StandardCharsets.UTF_8))
            var line: String? = null
            while ((reader.readLine().also { line = it }) != null) {
                if (call(line)) {
                    p.destroy()
                }
            }
        }.start()
        Thread {
            val reader = BufferedReader(InputStreamReader(p.errorStream, StandardCharsets.UTF_8))
            var line: String? = null
            while ((reader.readLine().also { line = it }) != null) {
                errorCall(line)
            }
        }.start()
        onProcess(p)

        val res = p.waitFor(timeOut.toLong(), TimeUnit.SECONDS)
        allProcess.remove(p)
        return res
    }

    fun destroyAll(){
        val arrayList = ArrayList<Process>(allProcess)
        allProcess.clear()
        arrayList.forEach {
            destroyForcibly(it)
        }
    }

    fun destroyForcibly(mProcess:Process){
        if (mProcess.isAlive) {
            val currentProcess: ProcessHandle = mProcess.toHandle()
            currentProcess.children().forEach { obj: ProcessHandle -> obj.destroyForcibly() }
            currentProcess.descendants().forEach { obj: ProcessHandle -> obj.destroyForcibly() }
            mProcess.destroyForcibly()
        }
    }
}