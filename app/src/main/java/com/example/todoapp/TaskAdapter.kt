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
    private val onCheckedChange: (Task, Boolean) -> Unit
) : BaseAdapter() {

    private class TaskViewHolder(
        val checkBox: CheckBox,
        val textTitle: TextView,
        val textDate: TextView,
        val textDue: TextView
    )

    private val todayStart = getTodayStartMillis()
    private val todayEnd = getTodayEndMillis()

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Task = tasks[position]

    override fun getItemId(position: Int): Long = tasks[position].id.hashCode().toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder : TaskViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_task, parent, false)

            holder = TaskViewHolder(
                checkBox = view.findViewById(R.id.checkDone),
                textTitle = view.findViewById(R.id.textTitle),
                textDate  = view.findViewById(R.id.textDate),
                textDue  = view.findViewById(R.id.textDue)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as TaskViewHolder
        }

        val task = tasks[position]

        holder.checkBox.isChecked = task.done      // チェックが入っているかどうかisCheckedはBoolean型
        holder.textTitle.text = task.title
        holder.textDate.text = formatTime(task.createdAt)
        holder.textDue.text = formatDue(task.dueAt)

        // 取り消し線の表示
        holder.textTitle.paintFlags = if (task.done)
            holder.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        when {
            task.done -> {
                holder.textDue.setTextColor("#999999".toColorInt())   // 完了：グレー
            }
            task.dueAt != null && task.dueAt < todayStart -> {              // 期限切れ：赤色
                holder.textDue.setTextColor("#FF0000".toColorInt())
            }
            task.dueAt != null && task.dueAt <= todayEnd -> {             // 今日が期限：オレンジ色
                holder.textDue.setTextColor("#FF8800".toColorInt())
            }
            else -> {
                holder.textDue.setTextColor("#000000".toColorInt())   // 期限未設定 or 期限が先：黒色
            }
        }

        holder.checkBox.setOnCheckedChangeListener(null)       // 一度チェックボックスに設定されているリスナを削除　　ListView では行の View を再利用しているので、リスナが残っている可能性があるので。　リスナとはイベントが起きた時に呼び出される処理を登録しておく仕組み
        holder.checkBox.setOnCheckedChangeListener {_, isChecked ->
            onCheckedChange(task, isChecked)
        }
        return view
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

    private fun getTodayStartMillis(): Long {
        val cal = Calendar.getInstance()

        // 今日の00:00
        cal.set(Calendar.HOUR_OF_DAY,0)
        cal.set(Calendar.MINUTE,0)
        cal.set(Calendar.SECOND,0)
        cal.set(Calendar.MILLISECOND,0)
        return cal.timeInMillis
    }

    private fun getTodayEndMillis(): Long {
        val cal = Calendar.getInstance()

        // 今日の23:59:59.999
        cal.set(Calendar.HOUR_OF_DAY,23)
        cal.set(Calendar.MINUTE,59)
        cal.set(Calendar.SECOND,59)
        cal.set(Calendar.MILLISECOND,999)
        return cal.timeInMillis
    }
}