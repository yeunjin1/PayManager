package konkuk.yeonj.paymanager.ui.work

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.ui.calendar.AddWorkActivity
import konkuk.yeonj.paymanager.widget.dialog.CalendarDialog
import konkuk.yeonj.paymanager.widget.dialog.CustomDialog
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_work.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
class WorkFragment : Fragment() {
    lateinit var v:View
    private lateinit var startDay: LocalDate
    private lateinit var endDay: LocalDate
    private var isMonthType = true
    private lateinit var mainActivity: MainActivity
    private lateinit var weekResults: RealmResults<Work>
    private lateinit var rViewAdapter: WorkListAdapter
    private var totalTime = 0f
    private var totalMoney = 0
    lateinit var spinnerAdapter: ArrayAdapter<String>
    var selectedPlace: Place? = null
    private val now = LocalDate.now()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_work, container, false)
        mainActivity = activity as MainActivity
        startDay = now.toFirstDayOfMonth()
        endDay = now.toLastDayOfMonth()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mainActivity.placeResults.isNotEmpty()) {
//            selectedPlace = mainActivity.placeResults[0]
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
            spinnerAdapter.add("전체")
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
                    if (position == 0){ //전체 선택
                        selectedPlace = null
                    }
                    else{
                        selectedPlace = mainActivity.placeResults[position - 1]!!
                    }
                    var color = R.color.darkGray
                    when(selectedPlace?.color){
                        0-> color = R.color.red
                        1-> color = R.color.orange
                        2-> color = R.color.green
                        3-> color = R.color.blue
                        4-> color = R.color.purple
                    }
                    (parent?.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    placeName.background.colorFilter = context!!.getColor(color).toColorFilter()
                    updateView()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            rightButton.setOnClickListener {
                if(isMonthType){
                    startDay = startDay.plusMonths(1)
                    endDay = startDay.toLastDayOfMonth()
                }
                else{
                    val period = ChronoUnit.DAYS.between(startDay, endDay) + 1
                    endDay = endDay.plusDays(period)
                    startDay = startDay.plusDays(period)
                }
                updateView()
            }

            leftButton.setOnClickListener {
                if(isMonthType){
                    startDay = startDay.minusMonths(1)
                    endDay = startDay.toLastDayOfMonth()
                }
                else{
                    val period = ChronoUnit.DAYS.between(startDay, endDay) + 1
                    endDay = endDay.minusDays(period)
                    startDay = startDay.minusDays(period)
                }
                updateView()
            }

            dateText.setOnClickListener {
                var dialog = CalendarDialog.Builder(requireContext()).create()
                dialog.setButtons(
                    {// 선택
                        isMonthType = false
                        if(dialog.getStartDate() != null && dialog.getEndDate() != null){
                            startDay = dialog.getStartDate()!!
                            endDay = dialog.getEndDate()!!
                            updateView()
                        }
                        dialog.dismissDialog()
                    },
                    { // 일주일
                        isMonthType = false
                        startDay = now.toFirstDayOfWeek()
                        endDay = now.toLastDayOfWeek()
                        updateView()
                        dialog.dismissDialog()
                    },
                    {// 한달
                        isMonthType = true
                        startDay = now.toFirstDayOfMonth()
                        endDay = now.toLastDayOfMonth()
                        updateView()
                        dialog.dismissDialog()
                    }
                )
                dialog.show()
            }

        }
        else{
            defaultText.visibility = View.VISIBLE
        }
    }


    fun updateView(){
        v.findViewById<TextView>(R.id.dateText).text = periodToString(startDay, endDay)
        if (selectedPlace == null){
            weekResults = mainActivity.workResults.where()
                .greaterThanOrEqualTo("date", startDay.convertToDate())
                .lessThanOrEqualTo("date", endDay.convertToDate()).findAll()
        }
        else{
            weekResults = mainActivity.workResults.where()
                .equalTo("placeId", selectedPlace!!.id)
                .greaterThanOrEqualTo("date", startDay.convertToDate())
                .lessThanOrEqualTo("date", endDay.convertToDate()).findAll()
        }

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