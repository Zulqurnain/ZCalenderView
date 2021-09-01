package infinitnoob.com.samplezcalenderview

import android.view.LayoutInflater
import android.widget.Toast
import infinitnoob.com.samplezcalenderview.databinding.ActivityMainBinding
import jutt.com.zcalenderview.DatePickerController
import jutt.com.zcalenderview.SimpleMonthAdapter.CalendarDay
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(), DatePickerController {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun onReady() {
        with(binding) {
            // on click of specific date
            calendarView.controller = this@MainActivity

            // define if you want to show 1 month for this view only and vertical scroll is enable in this case
            calendarView.setEnableHeightResize(false)

            // events require hashmap
            val eventsMap = HashMap<CalendarDay?, Int?>()

            // this date is basically 1st august 2017 and it will show 1 event dot under this day
            eventsMap[CalendarDay(2017, 7, 20)] = 1
            calendarView.setEventsHashMap(eventsMap)
        }

    }

    override fun onDayOfMonthSelected(year: Int, month: Int, day: Int) {
        Toast.makeText(this, "y:$year , m:$month , d:$day", Toast.LENGTH_SHORT).show()
    }
}