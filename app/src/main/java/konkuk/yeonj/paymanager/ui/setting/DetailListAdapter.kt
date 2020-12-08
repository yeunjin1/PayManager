package konkuk.yeonj.paymanager.ui.setting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.data.TimeSet

class DetailListAdapter (val data: ArrayList<TimeSet>, val context: Context) : RecyclerView.Adapter<DetailListAdapter.ViewHolder>() {
    interface OnItemLongClickListener{
        fun OnItemLongClick(holder: DetailListAdapter.ViewHolder, view:View, item: TimeSet)
    }

    var itemLongClickListener :OnItemLongClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayText: TextView
        var timeText: TextView

        init {
            dayText = itemView.findViewById(R.id.dayText)
            timeText = itemView.findViewById(R.id.timeText)

            itemView.setOnLongClickListener {
                itemLongClickListener?.OnItemLongClick(this, it, data[bindingAdapterPosition])
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_detail_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.dayText.text = data[position].day.toDayString()
            holder.timeText.text = timePeriodToString(data[position].startHour * 60 + data[position].startMin, data[position].endHour * 60 + data[position].endMin)
        }
    }

}