package io.silv.pootracker.sync

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.silv.pootracker.domain.logs.interactor.GetLog
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Class used for sync serialization
 *
 * @param id the id in the local database of the log
 * @param logId the uuid of the poop log.
 * @param userId the id of the user.
 * @param order the order of the download in the queue.
 */
@Serializable
private data class SyncObject(val id: Long, val logId: String, val userId: String, val order: Int)


class SyncStore(
    private val settings: Settings,
    private val getPoopLog: GetLog
) {
    /**
     * Counter used to keep the queue order.
     */
    private var counter = 0

    /**
     * Adds a list of downloads to the store.
     *
     * @param requests the list of downloads to add.
     */
    fun addAll(requests: List<SyncRequest>) {
        requests.forEach { request ->
            settings[getKey(request)] = serialize(request)
        }
    }
    /**
     * Removes a download from the store.
     *
     * @param request the download to remove.
     */
    fun remove(request: SyncRequest) {
        settings.remove(getKey(request))
    }

    /**
     * Removes a list of requests from the store.
     *
     * @param requests the download to remove.
     */
    fun removeAll(requests: List<SyncRequest>) {
        requests.forEach { request ->
            settings.remove(getKey(request))
        }
    }

    /**
     * Returns the preference's key for the given download.
     *
     * @param request the sync request.
     */
    private fun getKey(request: SyncRequest): String {
        return request.logId
    }

    /**
     * Removes all the requests from the store.
     */
    fun clear() {
        settings.clear()
    }

    /**
     * Returns the list of downloads to restore. It should be called in a background thread.
     */
    suspend fun restore(): List<SyncRequest> {
        val objs = settings.keys
            .mapNotNull {
                deserialize(
                    settings.getString(it, "")
                )
            }
            .sortedBy { it.order }

        val requests = mutableListOf<SyncRequest>()
        if (objs.isNotEmpty()) {
            for (obj in objs) {

                val log = getPoopLog.await(obj.id) ?: continue

                requests.add(SyncRequest(log.id, log.logId, log.createdBy))
            }
        }

        // Clear the store, downloads will be added again immediately.
        clear()
        return requests
    }

    /**
     * Converts a syncRequest to a string.
     *
     * @param request the SyncRequest to serialize.
     */
    private fun serialize(request: SyncRequest): String {
        val obj = SyncObject(request.id ,request.logId, request.userId, counter++)
        return try {
            Json.encodeToString(obj)
        } catch (e: SerializationException) {
            Logger.d(e.stackTraceToString())
            ""
        }
    }


    /**
     * Restore a syncRequest from a string.
     *
     * @param string the syncRequest as string.
     */
    private fun deserialize(string: String): SyncObject? {
        return try {
            Json.decodeFromString<SyncObject>(string)
        } catch (e: Exception) {
            Logger.d(e.stackTraceToString())
            null
        }
    }
}