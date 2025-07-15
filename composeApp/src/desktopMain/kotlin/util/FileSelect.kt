package util

import java.io.File
import javax.swing.JFileChooser

object FileSelect {

    fun selectFile(): File? {
        var jFileChooser = JFileChooser()

        var returnValue = jFileChooser.showOpenDialog(null)
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.selectedFile
        }
        return null
    }

    fun openFile(path:String){
        val file = File(path)
        if (!file.exists()){
            file.mkdirs()
        }
        RuntimeUtil.execAndPrint(cmd = arrayOf("cmd.exe", "/c","start", "explorer" ,path), timeOut = 30)
    }
}