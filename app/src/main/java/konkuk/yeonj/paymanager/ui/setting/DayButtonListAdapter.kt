package konkuk.yeonj.paymanager.ui.setting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.*

class DayButtonListAdapter (val context: Context) : RecyclerView.Adapter<DayButtonListAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(holder: DayButtonListAdapter.ViewHolder, view:View, position: Int)
    }

    var itemClickListener :OnItemClickListener? = null
    private var selectedPos = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayButton: TextView

        init {
            dayButton = itemView.findViewById(R.id.dayButton)

            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, bindingAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_day_button_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.dayButton.text = position.toDayString()
            if(selectedPos == position){
                holder.dayButton.setTextColorRes(R.color.white)
                holder.dayButton.setBackgroundResource(R.drawable.place_button_background)
                holder.dayButton.background.colorFilter = context.getColorCompat(R.color.colorPrimaryDark).toColorFilter()
            }
            else{
                holder.dayButton.setTextColorRes(R.color.colorPrimaryDark)
                holder.dayButton.setBackgroundResource(R.drawable.dialog_button_background)
                holder.dayButton.background.colorFilter = context.getColorCompat(R.color.colorPrimaryDark).toColorFilter()
            }
        }
    }

    fun setSelection(pos: Int){
        selectedPos = pos
        notifyDataSetChanged()
    }

    fun getSelection(): Int{
        return selectedPos
    }

}