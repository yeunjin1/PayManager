package konkuk.yeonj.paymanager.ui.setting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.*

class ColorListAdapter (val context: Context) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(holder: ColorListAdapter.ViewHolder, view:View, position: Int)
    }

    var itemClickListener :OnItemClickListener? = null
    private var selectedPos = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleIcon: View
        var circleSelected: View

        init {
            circleIcon = itemView.findViewById(R.id.circleIcon)
            circleSelected = itemView.findViewById(R.id.circleSelected)

            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, bindingAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_color_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.circleIcon.background.colorFilter = position.toColorRes(context).toColorFilter()
            if(selectedPos == position){
                holder.circleSelected.visibility = View.VISIBLE
            }
            else{
                holder.circleSelected.visibility = View.INVISIBLE
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