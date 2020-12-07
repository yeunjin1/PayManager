package konkuk.yeonj.paymanager.ui.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
import konkuk.yeonj.paymanager.toColorFilter
import konkuk.yeonj.paymanager.toColorRes
import kotlinx.android.synthetic.main.row_cal_list.view.*
import java.time.format.DateTimeFormatter


class CircleGridAdapter (val data: ArrayList<Work>, val context: Context) : RecyclerView.Adapter<CircleGridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleIcon: ImageView

        init {
            circleIcon = itemView.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_grid_circle, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CircleGridAdapter.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = data[position]
            holder.circleIcon.colorFilter = item.place?.color!!.toColorRes(context).toColorFilter()
        }
    }

}