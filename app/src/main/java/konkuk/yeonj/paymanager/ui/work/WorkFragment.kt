package konkuk.yeonj.paymanager.ui.work

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import konkuk.yeonj.paymanager.convertToDate
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.toColorFilter
import konkuk.yeonj.paymanager.ui.calendar.AddWorkActivity
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_work.*
import java.time.DayOfWeek
import java.time.LocalDate

class WorkFragment : Fragment() {
    lateinit var v:View
    private lateinit var startDayofWeek: LocalDate
    private lateinit var endDayofWeek: LocalDate
    private lateinit var mainActivity: MainActivity
    private lateinit var weekResults: RealmResults<Work>
    private lateinit var rViewAdapter: WorkListAdapter
    private var totalTime = 0f
    private var totalMoney = 0
    lateinit var spinnerAdapter: ArrayAdapter<String>
    var selectedPlace: Place? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_work, container, false)
        init()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mainActivity.placeResults.isNotEmpty()) {
            selectedPlace = mainActivity.placeResults[0]
            updateView()
            rViewAdapter.itemClickListener = object : WorkListAdapter.OnItemClickListener {
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

            val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, RecyclerView.VERTICAL, false
            )
            workListView.layoutManager = layoutManager

            //상단 알바 장소 선택 스피너
            spinnerAdapter = ArrayAdapter(mainActivity.applicationContext, android.R.layout.simple_spinner_dropdown_item)
            for(i in mainActivity.placeResults){
                spinnerAdapter.add(i.name)
            }
            placeName.adapter = spinnerAdapter
            placeName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent?.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    selectedPlace = mainActivity.placeResults[position]!!
                    var color = 0
                    when(selectedPlace!!.color){
                        0-> color = R.color.red
                        1-> color = R.color.orange
                        2-> color = R.color.green
                        3-> color = R.color.blue
                        4-> color = R.color.purple
                    }
                    placeName.background.colorFilter = context!!.getColor(color).toColorFilter()
                    updateView()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            rightButton.setOnClickListener {
                endDayofWeek = endDayofWeek.plusDays(7)
                startDayofWeek = startDayofWeek.plusDays(7)
                updateView()
            }

            leftButton.setOnClickListener {
                endDayofWeek = endDayofWeek.minusDays(7)
                startDayofWeek = startDayofWeek.minusDays(7)
                updateView()

            }
        }
        else{
            defaultText.visibility = View.VISIBLE
        }
    }

    fun init(){
        mainActivity = activity as MainActivity
        initDay()
        initLayout()
    }

    private fun initDay(){
        val now = LocalDate.now()
        startDayofWeek = now.with(DayOfWeek.MONDAY).minusDays(1)
        endDayofWeek = now.with(DayOfWeek.SATURDAY)
    }

    fun initLayout(){
        if (mainActivity.placeResults.isEmpty()){

        }
        else{
        }
    }

    fun updateView(){
        val dayStr = "$startDayofWeek ~ $endDayofWeek"
        v.findViewById<TextView>(R.id.dateText).text = dayStr
        weekResults = mainActivity.workResults.where()
            .equalTo("placeId", selectedPlace!!.id)
            .greaterThanOrEqualTo("date", startDayofWeek.convertToDate())
            .lessThanOrEqualTo("date", endDayofWeek.convertToDate()).findAll()
        rViewAdapter = WorkListAdapter(weekResults, mainActivity.applicationContext, mainActivity.placeResults)
        workListView.adapter = rViewAdapter
        totalMoney = 0
        totalTime = 0f
        for (result in weekResults){
            totalMoney += ((result.timeDuring / 60.0) * result.place!!.payByHour).toInt()
            totalTime += (result.timeDuring / 60.0).toFloat()
        }
        v.findViewById<TextView>(R.id.totalMoneyText).text = totalMoney.toString() + "원"
        v.findViewById<TextView>(R.id.totalTimeText).text = "총 " + totalTime.toString() + "시간 근무"
    }




}