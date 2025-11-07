package com.example.todoapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.data.Task
import com.example.todoapp.ui.TaskViewModel
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.util.Calendar

//MainActivity.ktはアプリの画面制御。UIとユーザー操作のロジックが中心
// ↓関係図
// MainActivity.kt
// ├─ 入力 → dao.insert(Task(...))
// ├─ 表示 ← dao.observeALL() → ListView
// │                             ↑ TaskAdapter.kt で View化
// ├─ 削除 → dao.deleteById(id)
// │
// ├─ DB接続 → AppDatabase.kt（Room本体）
// │                 └─ TaskDao.kt（SQL操作）
// │                 └─ Task.kt（データの形）


class MainActivity : AppCompatActivity() {  // AppCompatActivityクラスを継承、Androidの基本的な画面操作が使える

    private lateinit var adapter:TaskAdapter    // :TaskAdapterでTaskAdapter.ktのクラスを参照　lateinit「後から初期化する」という宣言、55行目で初期化している
    private var currentRows: List<Task> = emptyList()   // Taskオブジェクトの空リストを作成している、Task.kt に定義された Task クラスに紐づく
    private val viewModel: TaskViewModel by viewModels()    // : TaskViewModelクラスを使う、by viewModels()は簡単に初期化して使うためにkotlinの便利な書き方 内部ではViewModelProviderを呼び出し処理している


    override fun onCreate(savedInstanceState: Bundle?) {    // onCreateはこの画面が初めて表示されるときに実行される処理、？はnullを許容、overrideはスーパークラス(AppCompatActivity)で定義されている関数を上書きするという意味
        super.onCreate(savedInstanceState)                  // superはスーパークラスを指す、つまりスーパークラスのonCreateを呼び出している。Androidの画面として正常に動作するための準備をスーパークラスに任せる部分
        setContentView(R.layout.activity_main)              // 画面に表示するレイアウト(XMLファイル)を指定する

        val taskInput: EditText = findViewById(R.id.taskInput)   // 画面にある部品をコードで操作できるように変数に代入。UI要素を取得している
        val tasklist: ListView = findViewById(R.id.tasklist)
        val addButton: Button = findViewById(R.id.addButton)

        // 起動時にDBからメモリへ読み込み DBが変更するたびにListViewが更新される
        lifecycleScope.launch {                                                                 // lifecycleScopeはandroid画面のライフサイクルに合わせて自動で動きを制御する機能、launchは非同期的に処理を進める、
            viewModel.tasks.collect { rows ->                                                   //  viewModel.tasksはFlow<List<Task>>型(リアルタイムにデータ変更を通知する仕組み)　rowsは最新のタスクリストDBの状態によって更新する
                currentRows = rows                                                              // .collect { ... -> ... }の書き方は基本的にFlow型のみデータを受け取れる
                adapter = TaskAdapter(this@MainActivity, rows) { task, isChecked ->      // ここのtaskはチェックされた（または外された）行に対応する Task オブジェクト
                    viewModel.updateDone(task.id, isChecked)                                        // { task, isChecked ->...}は三つ目の引数として、TaskAdapterに処理は実行せずにコードのまま渡される、
                }                                                                               // isCheckedはチェックボックスがオンかオフかを表すBoolean型　　チェックされていれば true外されていれば false
                tasklist.adapter = adapter                                                      // .adapter = adapter で、ListView にアダプター（TaskAdapter）をセットする。
            }
        }

        // タスク追加
        addButton.setOnClickListener {
            handleAddTask(taskInput)
        }

        // タスク長押し処理
        tasklist.setOnItemLongClickListener { _, _, position, _ ->                              // positionはListViewの中で何番目かを表す
            handleLongClick(position)
            true //trueを返して処理が完了したということを、返している
        }
    }

    // タスク追加処理
    private fun handleAddTask(taskInput: EditText) {
        val text = taskInput.text.toString().trim()                                         // textはkotlinのライブラリの一部である、プロパティを読んでいる、ここのプロパティは実際には関数を読んでいる。
        if (text.isEmpty()) {
            Toast.makeText(this, "タスクを入力してください", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.add(text)
        taskInput.text.clear()
    }

    // タスク長押し処理
    private fun handleLongClick(position: Int) {
        val task = currentRows[position] // 画面表示から削除の対象を特定

        AlertDialog.Builder(this)
            .setTitle("操作の選択")
            .setItems(arrayOf("編集","期限を設定","期限をクリア","削除")) { _, which ->
                when (which) {
                    0 -> showEditDialog(task.id, task.title) // 編集先へ遷移

                    1 -> {      // 期限を設定
                        showDatePicker { pickedMillis ->        // fun showDatePickerのonPicked(cal.timeInMillis)でpickedMillisにcal.timeInMillisが入り以降の処理が実行
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = pickedMillis
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                                set(Calendar.MILLISECOND, 999)
                            }
                            viewModel.updateDueAt(task.id, cal.timeInMillis)
                            Toast.makeText(this@MainActivity, "期限を設定しました", Toast.LENGTH_SHORT).show()
                        }
                    }

                    2 -> {      // 期限のクリア
                        viewModel.updateDueAt(task.id, null)
                        Toast.makeText(this@MainActivity, "期限をクリアしました", Toast.LENGTH_SHORT).show()
                    }

                    3 -> {      //  削除
                        AlertDialog.Builder(this)
                            .setTitle("削除の確認")
                            .setMessage("「${task.title}」を削除してもよろしいですか？")
                            .setPositiveButton("削除") { _, _ ->
                                viewModel.deleteById(task.id)
                                Toast.makeText(this@MainActivity, "削除しました", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("キャンセル", null)
                            .show()
                    }
                }
            }
            .show()
    }

    // タスクタイトル編集
    private fun showEditDialog(taskId: String, currentTitle: String) {
        val input = EditText(this).apply {
            setText(currentTitle)
            setSelection(currentTitle.length)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        AlertDialog.Builder(this)
            .setTitle("タイトルの編集")
            .setView(input)
            .setPositiveButton("保存") { _, _ ->
                val newTitle = input.text.toString().trim()

                if (newTitle.isBlank()) {
                    Toast.makeText(this@MainActivity, "タスクを入力してください", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateTitle(taskId, newTitle)
                    Toast.makeText(this@MainActivity, "更新しました", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    // タスク期限の設定
    private fun showDatePicker(onPicked: (Long) -> Unit) {
        val cal = Calendar.getInstance()    // Calendarオブジェクトを生成
        DatePickerDialog(           // Android 標準の日付選択ダイアログを生成　引き数４個
            this,
            { _, y, m, d ->         // ユーザー操作（日付選択）によって、ここの処理{ _, y, m, d ->・・が実行
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
                onPicked(cal.timeInMillis)      // calの結果をミリ秒にして、onPicked（呼び出し元のラムダ式）に渡す
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}