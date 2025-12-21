package com.example.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.remote.FirestoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// TaskViewModel.ktはデータの橋渡し役、UI(画面）とDBのやりとりを綺麗にわけるために存在
class TaskViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    // tasks:Flowの設計図 tasks = 「observeAll → map → List<Task>」という設計図
    val tasks: Flow<List<Task>> = repo.observeAll().map { list ->
        list.sortedWith(
            compareBy<Task> { t -> t.dueAt == null}         // 未設定は最後
                .thenBy { t -> t.dueAt ?: Long.MAX_VALUE }  // 期限は近い順
                .thenByDescending { t -> t.createdAt }      // 作成日は新しい順
        )
    }


    fun add(title: String) = viewModelScope.launch {                        // add関数が呼ばれたら、バックグラウンドでタスクをDBに保存する処理を開始する
        if(title.isBlank()) return@launch                                   // タスク名が空ならこのコルーチンから抜ける
        repo.add(title)
    }

    fun deleteById(id: String) = viewModelScope.launch {
        repo.delete(id)
    }

    fun updateDone(id: String, done: Boolean) = viewModelScope.launch {
        repo.updateDone(id, done)
    }

    fun updateTitle(id: String,newTitle: String) = viewModelScope.launch {
        if(newTitle.isBlank()) return@launch                                   // タスク名が空ならこのコルーチンから抜ける
        repo.updateTitle(id, newTitle)
    }

    fun updateDueAt(id: String,dueAt: Long?) = viewModelScope.launch {
        repo.updateDueAt(id, dueAt)
    }
}