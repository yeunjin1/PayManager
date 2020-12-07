package konkuk.yeonj.paymanager.ui.work

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import java.text.SimpleDateFormat

class WorkListAdapter (realmResult: OrderedRealmCollection<Work>, val context: Context, val placeResults: RealmResults<Place>) : RealmRecyclerViewAdapter<Work, WorkListAdapter.ViewHolder>(realmResult, true) {
    val dateFormat = SimpleDateFormat("MM/dd (E)")

    interface OnItemClickListener{
        fun OnItemClick(holder: WorkListAdapter.ViewHolder, view:View, workId: String)
    }

    var itemClickListener : OnItemClickListener? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateText:TextView
        var timeText:TextView
        var duringTimeText:TextView
        var moneyText:TextView
        var circleIcon: ImageView

        init {
            duringTimeText = itemView.findViewById(R.id.duringTimeText)
            timeText = itemView.findViewById(R.id.timeText)
            moneyText = itemView.findViewById(R.id.moneyText)
            dateText = itemView.findViewById(R.id.dateText)
            circleIcon = itemView.findViewById(R.id.circleIcon)

            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, getItem(bindingAdapterPosition)!!.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_work_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: WorkListAdapter.ViewHolder, position: Int) {
        if (holder is WorkListAdapter.ViewHolder) {
            val item = getItem(position)!!
            holder.dateText.text = dateFormat.format(item.date)
            val timeDuring = item.timeEnd - item.timeStart - item.breakTime
            holder.duringTimeText.text = timeDuring.minToString()
            holder.moneyText.text = calTotalPay(item.timeStart, item.timeEnd, item.overTime, item.breakTime, item.place!!.payByHour).moneyToString()
            holder.timeText.text = timePeriodToString(item.timeStart, item.timeEnd)
            holder.circleIcon.colorFilter = item.place!!.color.toColorRes(context).toColorFilter()
        }
    }
}