package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.data.Task

class TaskDetailActivity : AppCompatActivity() {

    // Intentで値を渡すためのkey
    companion object {
        private const val EXTRA_TASK_ID = "task_id"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_MEMO = "memo"
        private const val EXTRA_DUE_AT = "dueAt"
        private const val EXTRA_DONE = "done"

        // 呼び出し側から使用するhelper
        fun start(context: Context, task:Task) {
            val intent = Intent(context,TaskDetailActivity::class.java).apply {
                putExtra(EXTRA_TASK_ID, task.id)
                putExtra(EXTRA_TITLE, task.title)
                putExtra(EXTRA_MEMO, task.memo)
                putExtra(EXTRA_DUE_AT, task.dueAt)
                putExtra(EXTRA_DONE, task.done)
            }
            context.startActivity(intent)
        }
    }

    // 画面上のViewへの参照
    private lateinit var editTitle: EditText
    private lateinit var editMemo: EditText
    private lateinit var textDue: TextView
    private lateinit var buttonChangeDue: Button
    private lateinit var buttonSave: Button
    private lateinit var checkDone: CheckBox

    // 受け取った値を保持する変数
    private var taskId: String = ""
    private var dueAt: Long? = null
    private var done: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // ViewをfindViewById
        editTitle = findViewById(R.id.editTitle)
        editMemo = findViewById(R.id.editMemo)
        textDue = findViewById(R.id.textDue)
        buttonChangeDue = findViewById(R.id.buttonChangeDue)
        buttonSave = findViewById(R.id.buttonSave)
        checkDone = findViewById(R.id.checkDone)

        // Intentから値を取り出す
        taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
        val memo = intent.getStringExtra(EXTRA_TITLE) ?: ""
        dueAt = if (intent.hasExtra(EXTRA_DUE_AT)) {
            intent.getLongExtra(EXTRA_DUE_AT, 0L)
        } else {
            null
        }
        done = intent.getBooleanExtra(EXTRA_DONE, false)

        // 画面へ反映
        editTitle.setText(title)
        editMemo.setText(memo)
        checkDone.isChecked = done
        textDue.text = formatDueText(dueAt)

        // まだ中身は空のボタン処理　後で実装
        buttonChangeDue.setOnClickListener {
            // ここで日付変更ダイアログを開く
        }

        buttonSave.setOnClickListener {
            // ここでFirestore更新を行う
            finish() // とりあえず画面を閉じる
        }
    }

    // 期限表示用のフォーマット
    private fun formatDueText(dueAt: Long?): String {
        return if(dueAt == null){
            "期限：未設定"
        } else {
            "期限：$dueAt"
        }
    }
}