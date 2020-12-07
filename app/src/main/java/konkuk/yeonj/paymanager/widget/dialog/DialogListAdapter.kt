package konkuk.yeonj.paymanager.widget.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.R

class DialogListAdapter(val data: ArrayList<String>, val context: Context) : RecyclerView.Adapter<DialogListAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(holder: DialogListAdapter.ViewHolder, view:View, position: Int)
    }

    var itemClickListener :OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView:TextView

        init {
            textView = itemView.findViewById(R.id.textView)
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(this, it, bindingAdapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.dialog_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val item = data[position]!!
            holder.textView.text = item
        }
    }
}

