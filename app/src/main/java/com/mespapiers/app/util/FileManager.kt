package com.mespapiers.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class FileManager @Inject constructor(
    private val context: Context
) {
    private val filesDir: File
        get() = context.filesDir

    private val cacheDir: File
        get() = context.cacheDir

    // Profile directories
    fun getProfileDir(profileId: String): File {
        val dir = File(filesDir, "profiles/$profileId")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getDocumentsDir(profileId: String): File {
        val dir = File(getProfileDir(profileId), "documents")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getDocumentDir(profileId: String, documentId: String): File {
        val dir = File(getDocumentsDir(profileId), documentId)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getExportsDir(profileId: String): File {
        val dir = File(getProfileDir(profileId), "exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    // File operations
    fun saveFile(profileId: String, documentId: String, versionId: String, inputStream: InputStream): String {
        val docDir = getDocumentDir(profileId, documentId)
        val file = File(docDir, "$versionId.pdf")

        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    fun copyFileFromUri(uri: Uri, profileId: String, documentId: String, versionId: String): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                saveFile(profileId, documentId, versionId, inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteFile(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteDocumentDir(profileId: String, documentId: String): Boolean {
        return try {
            getDocumentDir(profileId, documentId).deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteProfileDir(profileId: String): Boolean {
        return try {
            getProfileDir(profileId).deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

    fun getFile(filePath: String): File? {
        val file = File(filePath)
        return if (file.exists()) file else null
    }

    // ZIP export
    fun createZipExport(
        profileId: String,
        files: List<ZipFileEntry>,
        zipFileName: String
    ): File? {
        return try {
            val exportsDir = getExportsDir(profileId)
            val zipFile = File(exportsDir, zipFileName)

            ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                for (entry in files) {
                    val file = File(entry.sourcePath)
                    if (file.exists()) {
                        val zipEntry = ZipEntry(entry.zipPath)
                        zipOut.putNextEntry(zipEntry)

                        FileInputStream(file).use { fis ->
                            fis.copyTo(zipOut)
                        }

                        zipOut.closeEntry()
                    }
                }
            }

            zipFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Temporary file for export
    fun createTempExportFile(sourcePath: String, exportName: String): File? {
        return try {
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists()) return null

            val exportDir = File(cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val exportFile = File(exportDir, exportName)
            sourceFile.copyTo(exportFile, overwrite = true)
            exportFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun createShareIntent(file: File, mimeType: String = "application/pdf"): Intent {
        val uri = getFileUri(file)
        return Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun createZipShareIntent(file: File): Intent {
        return createShareIntent(file, "application/zip")
    }

    // Storage space check
    fun hasEnoughSpace(requiredBytes: Long = 50 * 1024 * 1024): Boolean { // 50MB default
        return filesDir.freeSpace > requiredBytes
    }

    fun getUsedSpace(): Long {
        return filesDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    // Cache cleanup
    fun cleanExportCache() {
        val exportDir = File(cacheDir, "exports")
        if (exportDir.exists()) {
            exportDir.deleteRecursively()
        }
    }

    data class ZipFileEntry(
        val sourcePath: String,
        val zipPath: String
    )
}
