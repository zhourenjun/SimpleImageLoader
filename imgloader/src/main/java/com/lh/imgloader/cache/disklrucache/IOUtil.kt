package com.lh.imgloader.cache.disklrucache

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.Reader
import java.io.StringWriter
import java.nio.charset.Charset

/** Junk drawer of utility methods.  */
object IOUtil {
     val US_ASCII: Charset = Charset.forName("US-ASCII")
     val UTF_8: Charset = Charset.forName("UTF-8")


    @Suppress("NAME_SHADOWING")
    @Throws(IOException::class)
    fun readFully(reader: Reader): String {
        reader.use { reader ->
            val writer = StringWriter()
            reader.forEachLine {
                writer.write(it)
            }
            return writer.toString()
        }
    }

    /**
     * Deletes the contents of `dir`. Throws an IOException if any file
     * could not be deleted, or if `dir` is not a readable directory.
     */
    @Throws(IOException::class)
    fun deleteContents(dir: File) {
        val files = dir.listFiles() ?: throw IOException("not a readable directory: " + dir)
        for (file in files) {
            if (file.isDirectory) {
                deleteContents(file)
            }
            if (!file.delete()) {
                throw IOException("failed to delete file: " + file)
            }
        }
    }

    fun closeQuietly(/* Auto */closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }

        }
    }
}
