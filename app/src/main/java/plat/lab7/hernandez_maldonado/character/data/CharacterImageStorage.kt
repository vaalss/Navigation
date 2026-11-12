package plat.lab7.hernandez_maldonado.character.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AtomicFile
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterImageStorage(
    context: Context
) {

    private val directory = File(
        context.applicationContext.filesDir,
        "character_images"
    )

    suspend fun getSavedImage(
        characterId: Int
    ): String? = withContext(Dispatchers.IO) {
        val file = File(directory, "character_$characterId.jpg")

        if (!file.isFile || file.length() == 0L) {
            return@withContext null
        }

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        try {
            AtomicFile(file).openRead().use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }
        } catch (exception: IOException) {
            return@withContext null
        }

        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return@withContext null
        }

        Uri.fromFile(file).toString()
    }

    suspend fun save(
        characterId: Int,
        bytes: ByteArray
    ): String = withContext(Dispatchers.IO) {
        if (bytes.isEmpty()) {
            throw IOException("La imagen está vacía.")
        }

        directory.mkdirs()

        if (!directory.isDirectory) {
            throw IOException("No se pudo crear la carpeta de imágenes.")
        }

        val file = File(directory, "character_$characterId.jpg")
        val atomicFile = AtomicFile(file)
        val output = atomicFile.startWrite()

        try {
            output.write(bytes)
            atomicFile.finishWrite(output)
        } catch (exception: Exception) {
            atomicFile.failWrite(output)
            throw exception
        }

        Uri.fromFile(file).toString()
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            if (directory.exists() && !directory.deleteRecursively()) {
                throw IOException("No se pudieron eliminar las imágenes.")
            }
        }
    }
}