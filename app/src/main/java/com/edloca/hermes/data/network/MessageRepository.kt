package com.edloca.hermes.data.network

import android.net.Uri
import com.edloca.hermes.utils.UiState

interface MessageRepository {

    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (List<String>) -> Unit)
}