package com.example.roadstar.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Base repository providing real-time Firestore listeners, CRUD operations,
 * and coroutine-flow streams for managing real-time logistics entities.
 */
abstract class BaseRealtimeRepository(
    protected val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    companion object {
        private const val TAG = "BaseRealtimeRepo"
    }

    /**
     * Get a reference to a specified Firestore collection.
     */
    protected fun getCollection(collectionPath: String): CollectionReference {
        return firestore.collection(collectionPath)
    }

    /**
     * Get a reference to a specific document in a collection.
     */
    protected fun getDocument(collectionPath: String, documentId: String): DocumentReference {
        return firestore.collection(collectionPath).document(documentId)
    }

    /**
     * Real-time stream of an entire collection mapped to domain model [R].
     * Emits new emissions whenever documents are added, updated, or removed in Firestore.
     */
    protected fun <R> listenCollection(
        collectionPath: String,
        transform: (DocumentSnapshot) -> R?
    ): Flow<List<R>> = callbackFlow {
        val collectionRef = getCollection(collectionPath)
        val registration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to collection $collectionPath", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        transform(doc)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse document ${doc.id} in $collectionPath", e)
                        null
                    }
                }
                trySend(items)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Real-time stream of a single document mapped to domain model [R].
     * Emits null if the document does not exist or gets deleted.
     */
    protected fun <R> listenDocument(
        collectionPath: String,
        documentId: String,
        transform: (DocumentSnapshot) -> R?
    ): Flow<R?> = callbackFlow {
        val docRef = getDocument(collectionPath, documentId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to document $documentId in $collectionPath", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    trySend(transform(snapshot))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse doc $documentId in $collectionPath", e)
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }

        awaitClose {
            registration.remove()
        }
    }

    /**
     * Inserts or overwrites a document with merge enabled.
     */
    suspend fun upsert(
        collectionPath: String,
        documentId: String,
        data: Map<String, Any?>
    ): Result<Unit> {
        return try {
            getDocument(collectionPath, documentId)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting doc $documentId in $collectionPath", e)
            Result.failure(e)
        }
    }

    /**
     * Updates specific fields of an existing document.
     */
    suspend fun updateFields(
        collectionPath: String,
        documentId: String,
        fields: Map<String, Any?>
    ): Result<Unit> {
        return try {
            getDocument(collectionPath, documentId)
                .update(fields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating fields for doc $documentId in $collectionPath", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a document by ID.
     */
    suspend fun delete(
        collectionPath: String,
        documentId: String
    ): Result<Unit> {
        return try {
            getDocument(collectionPath, documentId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting doc $documentId in $collectionPath", e)
            Result.failure(e)
        }
    }

    /**
     * One-shot fetch of a single document.
     */
    suspend fun <R> getOnce(
        collectionPath: String,
        documentId: String,
        transform: (DocumentSnapshot) -> R?
    ): Result<R?> {
        return try {
            val snapshot = getDocument(collectionPath, documentId).get().await()
            if (snapshot.exists()) {
                Result.success(transform(snapshot))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching document $documentId in $collectionPath", e)
            Result.failure(e)
        }
    }
}
