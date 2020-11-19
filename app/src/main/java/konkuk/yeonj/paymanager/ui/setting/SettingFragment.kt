package konkuk.yeonj.paymanager.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.ui.calendar.CalListAdapter
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_setting.*
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var v:View
    private lateinit var rViewAdapter: SettingListAdapter
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_setting, container, false)
        init()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        settingRView.layoutManager = layoutManager
        rViewAdapter = SettingListAdapter(mainActivity.placeResults, activity!!.applicationContext)
        settingRView.adapter = rViewAdapter
        rViewAdapter.itemClickListener = object :SettingListAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: SettingListAdapter.ViewHolder,
                view: View,
                pos: Int
            ) {
                val intent = Intent(mainActivity, AddPlaceActivity::class.java)
                intent.putExtra("id", mainActivity.placeResults[pos]!!.id)
                startActivity(intent)
            }
        }

        //근무지 추가
        addButton.setOnClickListener {
            val intent = Intent(mainActivity, AddPlaceActivity::class.java)
            intent.putExtra("id", "")
            startActivity(intent)
        }
    }

    private fun init(){
        mainActivity = activity as MainActivity
    }
}
