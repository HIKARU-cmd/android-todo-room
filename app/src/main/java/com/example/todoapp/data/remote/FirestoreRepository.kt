package com.example.todoapp.data.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.example.todoapp.data.Task
import com.google.firebase.firestore.DocumentSnapshot

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository (
    private val db: FirebaseFirestore = Firebase.firestore
) {
    private fun col() = db.collection("tasks")  // コレクション名

    private fun DocumentSnapshot.toTask():Task? {
        val id = id
        val title = getString("title") ?: return null
        val done = getBoolean("done")?: false
        val createdAt = getLong("createdAt") ?: 0L
        val dueAt = getLong("dueAt")
        return Task(id = id, title = title, done = done, createdAt = createdAt, dueAt = dueAt)
    }

    // リアルタイム購読
    fun observeAll(): Flow<List<Task>> = callbackFlow {
        val reg = col().addSnapshotListener { qs, e ->
            if (e != null) {
                trySend(emptyList()); return@addSnapshotListener
            }
            val list = qs?.documents?.mapNotNull { it.toTask() } ?: emptyList()
            trySend(list)
        }
        awaitClose{ reg.remove()}
    }

    // 追加（IDはfirestore自動生成）
    suspend fun add(title: String) {
        val now = System.currentTimeMillis()
        val data = hashMapOf(
            "title" to title,
            "done" to false,
            "createdAt" to now,
            "dueAt" to null
        )
        col().document().set(data).await()
    }

    suspend fun updateDone(id:String, done:Boolean) {
        col().document(id).update("done", done).await()
    }

    suspend fun updateTitle(id:String, title:String) {
        col().document(id).update("title", title).await()
    }

    suspend fun updateDueAt(id:String, dueAt:Long?) {
        col().document(id).update("dueAt", dueAt).await()
    }

   suspend fun delete(id:String) {
       col().document(id).delete().await()
   }
}