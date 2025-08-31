package com.example.todoapp

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.CheckBox
import android.view.View
import android.view.ViewGroup
import com.example.todoapp.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt


// リスト変換用の変換処理、TaskデータをListViewに一軒ずつ見た目として表示するViewに変換するクラスのこと(データをViewに変換)
class TaskAdapter(
    private val context: Context,
    private val tasks: List<Task>,
    private val onCheckedChange: (Task, Boolean) -> Unit                // unitは戻り値無しの関数 MainActivityでラムダ式で処理が渡される。　ここの変数onCheckedChangeが実行っされるタイミングで引数として受け取った処理が実行される
) : BaseAdapter() {         // クラスの継承

    override fun getCount(): Int = tasks.size       // overrideは親クラス(BaseAdapter)のgetCount()というメソッドの上書きしている　tasks.sizeでタスクの件数の表す

    override fun getItem(position: Int): Any = tasks[position]  // position → ListView の「何番目の行か」を示す番号 戻り値型は Anyはどんな型でも返してよい

    override fun getItemId(position: Int): Long = tasks[position].id.toLong()   // toLong()でInt型からLong型に変換している

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {         //　convertViewにはview型のため画面に置ける一行分のUI部品オブジェクトが入る、 parentには、その一行を入れる入れ物（今回はListViewのこと）　※View型とは、「画面に置けるもの」AndroidではUI部品全てがViewを継承している
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)      // convertViewが再利用不可であれば、LayoutInflater....で「list_item_task.xml を読み込んで、1行ぶんのViewオブジェクトを新しく作る」処理をしている

        val checkBox = view.findViewById<CheckBox>(R.id.checkDone)      // viewは現在処理している一行分のView、その中からCheckBox部品を探して取り出す処理　　　※(R.id.checkDone)は型をCheckBoxとして取り出している
        val textTitle = view.findViewById<TextView>(R.id.textTitle)
        val textDate  = view.findViewById<TextView>(R.id.textDate)
        val textDue  = view.findViewById<TextView>(R.id.textDue)

        val task = tasks[position]

        checkBox.isChecked = task.done      // チェックが入っているかどうかisCheckedはBoolean型
        textTitle.text = task.title         // textTitle.text → TextView の表示文字を表すプロパティ

        // 取り消し線の表示
        textTitle.paintFlags = if (task.done)
            textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        textDate.text = formatTime(task.createdAt)

        textDue.text = formatDue(task.dueAt)

        val todayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,23)
            set(Calendar.MINUTE,59)
            set(Calendar.SECOND,59)
            set(Calendar.MILLISECOND,999)
        }.timeInMillis

        when {
            task.done -> {
                textDue.setTextColor("#999999".toColorInt())   // 完了はグレー
            }
            task.dueAt != null && task.dueAt < todayEnd -> {              // 期限切れ　赤色
                textDue.setTextColor("#FF0000".toColorInt())
            }
            task.dueAt != null && task.dueAt <= todayEnd -> {             // 今日が期限 オレンジ色
                textDue.setTextColor("#FF8800".toColorInt())
            }
            else -> {
                textDue.setTextColor("#000000".toColorInt())   // 期限未設定 or 期限がまだ先　灰色
            }
        }

        checkBox.setOnCheckedChangeListener(null)       // 一度チェックボックスに設定されているリスナを削除　　ListView では行の View を再利用しているので、リスナが残っている可能性があるので。　リスナとはイベントが起きた時に呼び出される処理を登録しておく仕組み
        checkBox.setOnCheckedChangeListener {_, isChecked ->       // ユーザーがチェックボックスを操作して、ON/OFF が変わった瞬間に呼ばれる処理
            onCheckedChange(task, isChecked)                       // アダプタのコンストラクタで受け取った関数をここで呼び出している 第一引数: どのタスクか（task）第二引数: チェック状態（isChecked）
        }

        return view     // convertView が nullじゃない➡viewの中身は一行分のUI(View オブジェクト)　　　　　convertView が null　➡　XML（list_item_task.xml）から新しく作った 1行分のUI
    }

    private fun formatTime(millis: Long?): String {
        if (millis == null || millis == 0L) return ""
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return df.format(Date(millis))
    }

    private fun formatDue(millis: Long?): String {
        if(millis == null || millis == 0L) return "期限: —"
        val df = SimpleDateFormat("yyyy/MM/dd(E)", Locale.getDefault())
        return "期限: " + df.format(Date(millis))
    }

}