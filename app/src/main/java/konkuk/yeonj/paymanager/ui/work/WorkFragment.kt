package konkuk.yeonj.paymanager.ui.work

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.ui.calendar.AddWorkActivity
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.fragment_work.rView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class WorkFragment : Fragment() {
    lateinit var v:View
    private lateinit var startDayofWeek: LocalDate
    private lateinit var endDayofWeek: LocalDate
    private lateinit var mainActivity: MainActivity
    private lateinit var weekResults: RealmResults<Work>
    val rViewAdapter: WorkListAdapter by lazy{
        WorkListAdapter(weekResults, mainActivity.applicationContext, mainActivity.placeResults)
    }
    var totalTime = 0f
    var totalMoney = 0



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_work, container, false)
        init()
        return v
    }

    fun init(){
        mainActivity = activity as MainActivity
        initDay()
        updateView()
        rViewAdapter.itemClickListener = object : WorkListAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: WorkListAdapter.ViewHolder,
                view: View,
                workId: String
            ) {
                val intent = Intent(mainActivity, AddWorkActivity::class.java)
                intent.putExtra("workId", workId)
                startActivity(intent)
            }
        }
    }

    fun initDay(){
        val now = LocalDate.now()
        startDayofWeek = now.with(DayOfWeek.MONDAY).minusDays(1)
        endDayofWeek = now.with(DayOfWeek.FRIDAY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = "카페"
//        val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
//        dateText.text = dayStr

        rightButton.setOnClickListener {
            endDayofWeek = endDayofWeek.plusDays(7)
            startDayofWeek = startDayofWeek.plusDays(7)
            updateView()
//            val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
//            dateText.text = dayStr
//            weekResults = mainActivity.workResults.where().greaterThanOrEqualTo("date", Date.from(startDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).lessThanOrEqualTo("date", Date.from(endDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).findAll()
//            totalMoney = 0
//            totalTime = 0f
//            for (result in weekResults){
//                totalMoney += ((result.timeDuring / 60.0) * mainActivity.placeResults.where().equalTo("id", result.placeId).findFirst()!!.payByHour).toInt()
//                totalTime += (result.timeDuring / 60.0).toFloat()
//            }
        }

        leftButton.setOnClickListener {
            endDayofWeek = endDayofWeek.minusDays(7)
            startDayofWeek = startDayofWeek.minusDays(7)
            updateView()

        }

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
        rView.layoutManager = layoutManager

        rView.adapter = rViewAdapter


    }

    fun updateView(){
        val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
        v.findViewById<TextView>(R.id.dateText).text = dayStr
        weekResults = mainActivity.workResults.where().greaterThanOrEqualTo("date", Date.from(startDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).lessThanOrEqualTo("date", Date.from(endDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).findAll()
        rViewAdapter.notifyDataSetChanged()
        totalMoney = 0
        totalTime = 0f
        for (result in weekResults){
            totalMoney += ((result.timeDuring / 60.0) * mainActivity.placeResults.where().equalTo("id", result.placeId).findFirst()!!.payByHour).toInt()
            totalTime += (result.timeDuring / 60.0).toFloat()
        }
        v.findViewById<TextView>(R.id.totalMoneyText).text = "총 " + totalMoney.toString() + "원"
        v.findViewById<TextView>(R.id.totalTimeText).text = totalTime.toString() + "시간"
    }
}