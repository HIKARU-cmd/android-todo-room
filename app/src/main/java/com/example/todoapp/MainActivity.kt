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
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.data.Task
import com.example.todoapp.ui.TaskViewModel
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {  // AppCompatActivityクラスを継承、Androidの基本的な画面操作が使える

    private lateinit var adapter:TaskAdapter    // :TaskAdapterでTaskAdapter.ktのクラスを参照　lateinit「後から初期化する」という宣言、55行目で初期化している
    private var currentRows: List<Task> = emptyList()   // Taskオブジェクトの空リストを作成している、Task.kt に定義された Task クラスに紐づく
    private val viewModel: TaskViewModel by viewModels()    // : TaskViewModelクラスを使う、by viewModels()は簡単に初期化して使うためにkotlinの便利な書き方 内部ではViewModelProviderを呼び出し処理している
    private var showCompleted: Boolean = false
    private  var latestRows: List<Task> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {    // onCreateはこの画面が初めて表示されるときに実行される処理、？はnullを許容、overrideはスーパークラス(AppCompatActivity)で定義されている関数を上書きするという意味
        super.onCreate(savedInstanceState)                  // superはスーパークラスを指す、つまりスーパークラスのonCreateを呼び出している。Androidの画面として正常に動作するための準備をスーパークラスに任せる部分
        FirebaseAuth.getInstance().signInAnonymously()      // 未ログインの場合、匿名認証を実施する

        setContentView(R.layout.activity_main)

        val taskInput: EditText = findViewById(R.id.taskInput)
        val tasklist: ListView = findViewById(R.id.tasklist)
        val addButton: Button = findViewById(R.id.addButton)
        val switchShowCompleted: SwitchCompat = findViewById(R.id.switchShowCompleted)
        switchShowCompleted.isChecked = showCompleted
        switchShowCompleted.setOnCheckedChangeListener { _, isChecked ->
            showCompleted = isChecked
            // スイッチ切り替え時の再描画の処理
            renderList(latestRows)
        }

        // 起動時にDBからメモリへ読み込み DBが変更するたびにListViewが更新される
        lifecycleScope.launch {
            viewModel.tasks.collect { rows ->
                currentRows = rows
                latestRows = rows

                val displayRows = if (showCompleted) {
                    rows
                } else {
                    rows.filter { !it.done }    // 完了フラフは非表示
                }
                adapter = TaskAdapter(this@MainActivity, displayRows) { task, isChecked ->
                    viewModel.updateDone(task.id, isChecked)
                }
                tasklist.adapter = adapter
            }
        }

        // タスク追加
        addButton.setOnClickListener {
            handleAddTask(taskInput)
        }

        // タスククリック処理
        tasklist.setOnItemClickListener { _, _, position, _ ->
            val task = adapter.getItem(position)
            TaskDetailActivity.start(this, task)
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

        //  削除
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

    private fun renderList(rows: List<Task>) {
        val displayRows = if(showCompleted) rows else rows.filter { !it.done }
        adapter = TaskAdapter(this@MainActivity, displayRows) { task, isChecked ->
            viewModel.updateDone(task.id, isChecked)
        }
        findViewById<ListView>(R.id.tasklist).adapter = adapter
    }
}