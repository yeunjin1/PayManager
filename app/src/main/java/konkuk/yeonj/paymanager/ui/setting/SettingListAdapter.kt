package konkuk.yeonj.paymanager.ui.setting

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work

class SettingListAdapter (realmResult: OrderedRealmCollection<Place>, val context: Context) : RealmRecyclerViewAdapter<Place, SettingListAdapter.ViewHolder>(realmResult, true) {
    interface OnItemClickListener{
        fun OnItemClick(holder: ViewHolder, view:View, pos: Int)
    }
    var itemClickListener :OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var placeText : TextView

        init {
            placeText = itemView.findViewById(R.id.placeItemText)

            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, bindingAdapterPosition)
            }

            itemView.setOnLongClickListener {
                val realm = Realm.getDefaultInstance()

                realm.beginTransaction()
                realm.where(Work::class.java).equalTo("placeId", getItem(bindingAdapterPosition)?.id).findAll().deleteAllFromRealm()
                realm.commitTransaction()
                realm.beginTransaction()
                getItem(bindingAdapterPosition)?.deleteFromRealm()
                realm.commitTransaction()
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_place_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = getItem(position)!!
            holder.placeText.text = item.name
            var color = 0
            when(item.color){
                0-> color = R.color.red
                1-> color = R.color.orange
                2-> color = R.color.green
                3-> color = R.color.blue
                4-> color = R.color.purple
            }
            holder.placeText.background.colorFilter = PorterDuffColorFilter(context.getColor(color), PorterDuff.Mode.MULTIPLY)
        }
    }
}