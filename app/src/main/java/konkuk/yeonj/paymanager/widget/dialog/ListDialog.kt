package konkuk.yeonj.paymanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.R
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.list_dialog_layout.*

open class ListDialog(context: Context) : Dialog(context) {
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    open class Builder(val mContext: Context, val list: ArrayList<String>) {
        open val dialog = CustomDialog(mContext)

        var itemClickListener :OnItemClickListener? = null
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.list_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.listView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                mContext, RecyclerView.VERTICAL, false
            )
            val adapter = DialogListAdapter(list, mContext)
            dialog.listView.adapter = adapter
            adapter.itemClickListener = object : DialogListAdapter.OnItemClickListener{
                override fun onItemClick(
                    holder: DialogListAdapter.ViewHolder,
                    view: View,
                    position: Int
                ) {
                    itemClickListener?.onItemClick(position)
                }
            }
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }

        open fun show(): CustomDialog {
            dialog.show()
            return dialog
        }
    }
}