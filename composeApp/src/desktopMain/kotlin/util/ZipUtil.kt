package util

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtil {
    /**
     * 压缩文件或文件夹
     * @param sourcePath 源文件/文件夹路径
     * @param zipPath 目标zip文件路径
     */
    fun zip(sourcePath: String, zipPath: String) {
        File(zipPath).parentFile.mkdirs()
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipPath))).use { zos ->
            val sourceFile = File(sourcePath)
            compress(sourceFile, zos, sourceFile.name)
        }
    }

    private fun compress(file: File, zos: ZipOutputStream, base: String) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                compress(child, zos, "$base/${child.name}")
            }
        } else {
            zos.putNextEntry(ZipEntry(base))
            BufferedInputStream(FileInputStream(file)).use { bis ->
                bis.copyTo(zos)
            }
            zos.closeEntry()
        }
    }

    /**
     * 解压ZIP文件
     * @param zipPath ZIP文件路径
     * @param destPath 解压目标路径
     */
    fun unzip(zipPath: String, destPath: String) {
        val destDir = File(destPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        ZipInputStream(BufferedInputStream(FileInputStream(zipPath))).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val file = File(destDir, entry.name)
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    BufferedOutputStream(FileOutputStream(file)).use { bos ->
                        zis.copyTo(bos)
                    }
                }
                entry = zis.nextEntry
            }
        }
    }
}