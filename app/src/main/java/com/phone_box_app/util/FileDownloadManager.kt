package com.phone_box_app.util


/**
 * Created by Ram Mandal on 19/11/2025
 * @System: Apple M1 Pro
 */
import android.content.Context
import androidx.core.net.toUri
import com.phone_box_app.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FileDownloadManager(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
    private val appLogger: com.phone_box_app.core.logger.Logger
) {

    private val TAG = "FileDownloadManager"

    /**
     * Downloads a file from the given URL and saves it with the original extension
     * inside the app-specific external storage directory.
     *
     * @param fileUrl Full file URL (e.g., "https://example.com/file.pdf")
     * @param fileName Optional: file name without extension. If null, name is extracted from URL.
     * @param onProgress Callback with progress 0..100
     * @param onComplete Callback with saved file or null if failed
     */
    fun downloadFile(
        fileUrl: String,
        fileName: String? = null,
        onProgress: ((Int) -> Unit)? = null,
        onComplete: (File?) -> Unit
    ): Job {
        return CoroutineScope(dispatcherProvider.io).launch {
            val savedFile = try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    appLogger.e(TAG, "Server returned HTTP ${connection.responseCode}")
                    null
                } else {
                    // Determine file name and extension
                    val actualFileName =
                        fileName ?: fileUrl.toUri().lastPathSegment ?: "downloaded_file"
                    val extension = actualFileName.substringAfterLast('.', "")
                    val finalFileName =
                        if (extension.isNotEmpty()) actualFileName else actualFileName

                    // App-specific external directory
                    val dir = context.getExternalFilesDir(null)
                    val file = File(dir, finalFileName)

                    // Read input stream and write to file
                    val input = connection.inputStream
                    val output = FileOutputStream(file)

                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var totalRead = 0L
                    val contentLength = connection.contentLength

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        if (contentLength > 0) {
                            val progress = (totalRead * 100 / contentLength).toInt()
                            withContext(dispatcherProvider.main) {
                                onProgress?.invoke(progress)
                            }
                        }
                    }

                    output.flush()
                    output.close()
                    input.close()

                    file
                }
            } catch (e: Exception) {
                appLogger.e(TAG, "Download failed: ${e.message}")
                null
            }

            withContext(dispatcherProvider.main) {
                onComplete(savedFile)
            }
        }
    }
}
