package konkuk.yeonj.paymanager.ui.work

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import konkuk.yeonj.paymanager.*
import konkuk.yeonj.paymanager.data.Place
import konkuk.yeonj.paymanager.data.Work
import konkuk.yeonj.paymanager.ui.calendar.AddWorkActivity
import konkuk.yeonj.paymanager.widget.dialog.CalendarDialog
import konkuk.yeonj.paymanager.widget.dialog.ListDialog
import kotlinx.android.synthetic.main.fragment_work.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class WorkFragment : Fragment() {
    lateinit var v:View
    private lateinit var startDay: LocalDate
    private lateinit var endDay: LocalDate
    private var isMonthType = true
    private lateinit var mainActivity: MainActivity
    private lateinit var weekResults: RealmResults<Work>
    private lateinit var rViewAdapter: WorkListAdapter
    private var totalTime = 0
    private var totalMoney = 0
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
            updateView()
            initWorkList()
            initPlaceFilter()
            initDateHeader()
        }
        else {
            defaultText.visibility = View.VISIBLE
        }
    }

    private fun initWorkList() {
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

        workListView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context, RecyclerView.VERTICAL, false
        )
    }

    //상단 장소 필터
    private fun initPlaceFilter(){
        var list = arrayListOf<String>()
        list.add("전체")
        for(i in mainActivity.placeResults){
            list.add(i.name)
        }
        placeFilterButton.setOnClickListener {
            var dialog = ListDialog.Builder(requireContext(), list).create()
            dialog.itemClickListener = object : ListDialog.OnItemClickListener{
                override fun onItemClick(position: Int) {
                    if (position == 0){ //전체 선택
                        selectedPlace = null
                        placeFilterButton.background.colorFilter = requireContext().getColor(R.color.colorPrimaryDark).toColorFilter()
                    }
                    else{
                        selectedPlace = mainActivity.placeResults[position - 1]!!
                        placeFilterButton.background.colorFilter = selectedPlace!!.color!!.toColorRes(
                            requireContext()
                        ).toColorFilter()
                    }
                    placeFilterButton.text = list[position]
                    updateView()
                    dialog.dismissDialog()
                }
            }
            dialog.show()
        }
    }

    private fun initDateHeader(){
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
                    if (dialog.getStartDate() != null && dialog.getEndDate() != null) {
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

    fun updateView(){
        dateText.text = periodToString(startDay, endDay)
        if (selectedPlace == null){
            weekResults = mainActivity.workResults.where()
                .greaterThanOrEqualTo("date", startDay.convertToDate())
                .lessThanOrEqualTo("date", endDay.convertToDate()).findAll().sort("date")
        }
        else{
            weekResults = mainActivity.workResults.where()
                .equalTo("placeId", selectedPlace!!.id)
                .greaterThanOrEqualTo("date", startDay.convertToDate())
                .lessThanOrEqualTo("date", endDay.convertToDate()).findAll().sort("date")
        }

        rViewAdapter = WorkListAdapter(
            weekResults,
            mainActivity.applicationContext,
            mainActivity.placeResults
        )
        workListView.adapter = rViewAdapter
        totalMoney = 0
        totalTime = 0


        if(selectedPlace != null && selectedPlace!!.vacPay){ //주휴 수당 적용
            var isNext = false
            var timeInWeek = 0
            val cal = Calendar.getInstance()
            for (result in weekResults) {
                totalMoney += calTotalPay(
                    result.timeStart,
                    result.timeEnd,
                    result.overTime,
                    result.breakTime,
                    result.place!!.payByHour
                )
                totalTime += result.timeEnd - result.timeStart - result.breakTime
                cal.time = result.date
                if(cal.get(Calendar.DAY_OF_WEEK) == 1 && !isNext){
                    //일요일
                    totalTime += timeInWeek / 5
                    timeInWeek = result.timeEnd - result.timeStart - result.breakTime
                }
                else{
                    timeInWeek += result.timeEnd - result.timeStart - result.breakTime
                }
                Log.d("mytag", timeInWeek.toString())
            }
        }
        else{
            for (result in weekResults) {
                totalMoney += calTotalPay(
                    result.timeStart,
                    result.timeEnd,
                    result.overTime,
                    result.breakTime,
                    result.place!!.payByHour
                )
                totalTime += result.timeEnd - result.timeStart - result.breakTime
            }
        }


        totalMoneyText.text = totalMoney.moneyToString()
        totalTimeText.text = "총 " + totalTime.minToString() + " 근무"
    }
}