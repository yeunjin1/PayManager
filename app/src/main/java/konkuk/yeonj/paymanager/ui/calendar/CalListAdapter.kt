package konkuk.yeonj.paymanager.ui.calendar

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
import java.time.format.DateTimeFormatter

class CalListAdapter (val data: ArrayList<Work>, val context: Context) : RecyclerView.Adapter<CalListAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(holder: CalListAdapter.ViewHolder, view:View, workId: String)
    }

    var itemClickListener :OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var placeText : TextView
        var timeText: TextView
        var duringText: TextView
        var moneyText: TextView
        var circleIcon: ImageView

        init {
            placeText = itemView.findViewById(R.id.placeText)
            timeText = itemView.findViewById(R.id.timeText)
            duringText = itemView.findViewById(R.id.duringText)
            moneyText = itemView.findViewById(R.id.moneyText)
            circleIcon = itemView.findViewById(R.id.circleIcon)

            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, data[bindingAdapterPosition]!!.id)
                Log.d("mytag", "click")
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_cal_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = data[position]!!
            Log.d("mytag", item.toString())
            holder.placeText.text = item.place?.name
            holder.timeText.text = item.timeStart.convertToTimeString() + " ~ " + item.timeEnd.convertToTimeString()
            holder.duringText.text = String.format("%.1f", item.timeDuring / 60.0) + "시간"
            holder.moneyText.text = (item.place!!.payByHour * (item.timeDuring / 60.0)).toInt().toString() + "원"
            var color = 0
            when(item.place!!.color){
                0-> color = R.color.red
                1-> color = R.color.orange
                2-> color = R.color.green
                3-> color = R.color.blue
                4-> color = R.color.purple
            }
            holder.circleIcon.setColorFilter(context.getColor(color))
        }
    }

}