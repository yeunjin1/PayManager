package konkuk.yeonj.paymanager.ui.work

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.realm.Realm
import konkuk.yeonj.paymanager.MainActivity
import konkuk.yeonj.paymanager.R
import kotlinx.android.synthetic.main.fragment_work.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class WorkFragment : Fragment() {
    lateinit var v:View
    private lateinit var startDayofWeek: LocalDate
    private lateinit var endDayofWeek: LocalDate
    private lateinit var mainActivity: MainActivity


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
    }

    fun initDay(){
        val now = LocalDate.now()
        startDayofWeek = now.with(DayOfWeek.MONDAY).minusDays(1)
        endDayofWeek = now.with(DayOfWeek.FRIDAY)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = "카페"
        val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
        dateText.text = dayStr

        rightButton.setOnClickListener {
            endDayofWeek = endDayofWeek.plusDays(7)
            startDayofWeek = startDayofWeek.plusDays(7)
            val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
            dateText.text = dayStr
            val weekResult = mainActivity.workResults.where().greaterThanOrEqualTo("date", Date.from(startDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).lessThanOrEqualTo("date", Date.from(endDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).findAll()
            Log.d("mytag", weekResult.toString())

        }

        leftButton.setOnClickListener {
            endDayofWeek = endDayofWeek.minusDays(7)
            startDayofWeek = startDayofWeek.minusDays(7)
            val dayStr = startDayofWeek.toString() + " ~ " + endDayofWeek.toString()
            dateText.text = dayStr
            val weekResult = mainActivity.workResults.where().greaterThanOrEqualTo("date", Date.from(startDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).lessThanOrEqualTo("date", Date.from(endDayofWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())).findAll()
            Log.d("mytag", weekResult.toString())

        }
    }
}