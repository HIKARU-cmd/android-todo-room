package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// TaskDao.ktはDB操作、SQLの代わりにここで定義されたDB操作を実行する
@Dao                                                            // このインターフェイスに書くメソッドは、「DB（SQLite）に対する操作ですよ」とRoomに知らせる
interface TaskDao {                                             // interfaceはやること（メソッドの形）だけ決める設計図で、実際の中身（実装コード）は書かない　実際のコードはRoomが自動生成してくれる
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeALL(): Flow<List<Task>>                          // Flow：ストリーム（つまりタスク一覧がリアルタイムで流れてくる）　List<Task>：タスクの一覧

    @Insert
    suspend fun insert(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE tasks SET done = :done WHERE id = :id")
    suspend fun updateDone(id: Int, done: Boolean)

    @Query("UPDATE tasks SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Int, title: String)
}