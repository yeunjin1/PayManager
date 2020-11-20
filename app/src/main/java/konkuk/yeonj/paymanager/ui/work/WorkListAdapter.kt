package konkuk.yeonj.paymanager.ui.work

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.convertToTimeString
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import java.text.SimpleDateFormat

class WorkListAdapter (realmResult: OrderedRealmCollection<Work>, val context: Context, val placeResults: RealmResults<Place>) : RealmRecyclerViewAdapter<Work, WorkListAdapter.ViewHolder>(realmResult, true) {
    val dateFormat = SimpleDateFormat("MM월 dd일 EEE")

    interface OnItemClickListener{
        fun OnItemClick(holder: WorkListAdapter.ViewHolder, view:View, workId: String)
    }

    var itemClickListener : OnItemClickListener? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateText:TextView
        var timeText:TextView
        var duringTimeText:TextView
        var moneyText:TextView

        init {
            duringTimeText = itemView.findViewById(R.id.duringTimeText)
            timeText = itemView.findViewById(R.id.timeText)
            moneyText = itemView.findViewById(R.id.moneyText)
            dateText = itemView.findViewById(R.id.dateText)

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
            val placeItem = placeResults.where().equalTo("id", item.placeId).findFirst()!!
            holder.dateText.text = dateFormat.format(item.date)
            holder.duringTimeText.text = String.format("%.1f", item.timeDuring / 60.0) + "시간"
            holder.moneyText.text = (placeItem.payByHour * (item.timeDuring / 60.0)).toInt().toString() + "원"
            holder.timeText.text = item.timeStart.convertToTimeString() + " ~ " + item.timeEnd.convertToTimeString()
//            var color = 0
//            when(placeItem.color){
//                0-> color = R.color.red
//                1-> color = R.color.orange
//                2-> color = R.color.green
//                3-> color = R.color.blue
//                4-> color = R.color.purple
//            }
//            holder.circleIcon.setColorFilter(context.getColor(color))
        }
    }
}