package jutt.com.zcalenderview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import jutt.com.zcalenderview.SimpleMonthView.OnDayClickListener
import java.io.Serializable
import java.util.*

open class SimpleMonthAdapter(
    context: Context,
    datePickerController: DatePickerController?,
    private val typedArray: TypedArray
) : RecyclerView.Adapter<SimpleMonthAdapter.ViewHolder>(), OnDayClickListener {
    private val mContext: Context
    private val mController: DatePickerController?
    private val calendar: Calendar = Calendar.getInstance()
    val selectedDays: SelectedDays<CalendarDay?>
    private val firstMonth: Int = typedArray.getInt(R.styleable.ZCalenderView_firstMonth, 0)
    private val lastMonth: Int = typedArray.getInt(R.styleable.ZCalenderView_lastMonth, 11)
    private var isDragging = false
    private var countMap: HashMap<CalendarDay?, Int?>? = null
    private val monthCountMap = HashMap<CalendarMonth, HashMap<CalendarDay, Int>>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val simpleMonthView = SimpleMonthView(mContext, typedArray)
        return ViewHolder(simpleMonthView, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val v = viewHolder.simpleMonthView
        val drawingParams = HashMap<String?, Int?>()
        val month: Int = (firstMonth + position % MONTHS_IN_YEAR) % MONTHS_IN_YEAR
        val year: Int = position / MONTHS_IN_YEAR - 1 + calendar[Calendar.YEAR]
        var selectedFirstDay = -1
        var selectedLastDay = -1
        var selectedFirstMonth = -1
        var selectedLastMonth = -1
        var selectedFirstYear = -1
        var selectedLastYear = -1
        if (selectedDays.first != null) {
            selectedFirstDay = selectedDays.first!!.day
            selectedFirstMonth = selectedDays.first!!.month
            selectedFirstYear = selectedDays.first!!.year
        }
        if (selectedDays.last != null) {
            selectedLastDay = selectedDays.last!!.day
            selectedLastMonth = selectedDays.last!!.month
            selectedLastYear = selectedDays.last!!.year
        }
        v.reuse()
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_YEAR] = selectedFirstYear
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_YEAR] = selectedLastYear
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_MONTH] =
            selectedFirstMonth
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_MONTH] = selectedLastMonth
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_DAY] = selectedFirstDay
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_DAY] = selectedLastDay
        drawingParams[SimpleMonthView.VIEW_PARAMS_YEAR] = year
        drawingParams[SimpleMonthView.VIEW_PARAMS_MONTH] = month
        drawingParams[SimpleMonthView.VIEW_PARAMS_WEEK_START] = calendar.firstDayOfWeek
        v.setMonthParams(drawingParams)
        v.showMothInfo(isDragging)
        val calendarMonth = CalendarMonth(year, month)
        if (monthCountMap.containsKey(calendarMonth)) {
            v.setEventSymbols(monthCountMap[calendarMonth]!!)
        }
        v.invalidate()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return 3 * MONTHS_IN_YEAR
    }

    class ViewHolder(itemView: View, onDayClickListener: OnDayClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val simpleMonthView: SimpleMonthView = itemView as SimpleMonthView

        init {
            simpleMonthView.layoutParams = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            simpleMonthView.isClickable = true
            simpleMonthView.setOnDayClickListener(onDayClickListener)
        }
    }

    private fun init() {
        if (typedArray!!.getBoolean(
                R.styleable.ZCalenderView_currentDaySelected,
                false
            )
        ) onDayTapped(
            CalendarDay(
                System.currentTimeMillis()
            )
        )
    }

    override fun onDayClick(simpleMonthView: SimpleMonthView?, calendarDay: CalendarDay?) {
        calendarDay?.let { onDayTapped(it) }
    }

    private fun onDayTapped(calendarDay: CalendarDay) {
        mController!!.onDayOfMonthSelected(calendarDay.year, calendarDay.month + 1, calendarDay.day)
        setSelectedDay(calendarDay)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedDay(calendarDay: CalendarDay?) {
        selectedDays.setFirst(calendarDay)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDragging(isDragging: Boolean) {
        this.isDragging = isDragging
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCountMap(countMap: HashMap<CalendarDay?, Int?>) {
        this.countMap = countMap
        monthCountMap.clear()
        val it: Iterator<*> = countMap.entries.iterator()
        while (it.hasNext()) {
            val nextElement = it.next() as Map.Entry<*, *>
            val calendarMonth = CalendarMonth(nextElement.key as CalendarDay)
            if (monthCountMap.containsKey(calendarMonth)) {
                val tempMap = monthCountMap[calendarMonth]!!
                tempMap[nextElement.key as CalendarDay] = nextElement.value as Int
            } else {
                val tempMap = HashMap<CalendarDay, Int>()
                tempMap[nextElement.key as CalendarDay] = nextElement.value as Int
                monthCountMap[calendarMonth] = tempMap
            }
        }
        notifyDataSetChanged()
    }

    class CalendarMonth : Serializable {
        private val calendar = Calendar.getInstance()
        var month: Int
        var year: Int

        constructor(day: CalendarDay) {
            month = day.month
            year = day.year
            calendar[Calendar.MONTH] = day.month
            calendar[Calendar.YEAR] = day.year
        }

        constructor(year: Int, month: Int) {
            this.year = year
            this.month = month
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = year
        }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("{ year: ")
            stringBuilder.append(year)
            stringBuilder.append(" month: ")
            stringBuilder.append(month)
            stringBuilder.append(" }")
            return stringBuilder.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other is CalendarMonth) {
                if (other.year == year && other.month == month) { return true }
            }
            return false
        }

        override fun hashCode(): Int {
            return calendar.hashCode() + month * 37 + year
        }
    }

    class CalendarDay : Serializable {
        private var calendar = Calendar.getInstance()
        var day = 0
        var month = 0
        var year = 0

        constructor() {
            setTime(System.currentTimeMillis())
        }

        constructor(year: Int, month: Int, day: Int) {
            setDay(year, month, day)
        }

        constructor(timeInMillis: Long) {
            setTime(timeInMillis)
        }

        constructor(calendar: Calendar) {
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
        }

        private fun setTime(timeInMillis: Long) {
            calendar.timeInMillis = timeInMillis
            month = calendar[Calendar.MONTH]
            year = calendar[Calendar.YEAR]
            day = calendar[Calendar.DAY_OF_MONTH]
        }

        fun set(calendarDay: CalendarDay) {
            year = calendarDay.year
            month = calendarDay.month
            day = calendarDay.day
        }

        fun setDay(year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
        }

        val date: Date
            get() {
                calendar[year, month] = day
                return calendar.time
            }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("{ year: ")
            stringBuilder.append(year)
            stringBuilder.append(", month: ")
            stringBuilder.append(month)
            stringBuilder.append(", day: ")
            stringBuilder.append(day)
            stringBuilder.append(" }")
            return stringBuilder.toString()
        }

        override fun equals(o: Any?): Boolean {
            if (o is CalendarDay) {
                if (o.year == year && o.month == month && o.day == day) {
                    return true
                }
            }
            return false
        }

        override fun hashCode(): Int {
            return calendar.hashCode() + 17 * day + 31 * month + year
        }

        companion object {
            private const val serialVersionUID = -5456695978688356202L
        }
    }

    class SelectedDays<K> : Serializable {
        var first: K? = null
            private set
        var last: K? = null
            private set

        fun setFirst(first: K) {
            this.first = first
        }

        fun setLast(last: K) {
            this.last = last
        }

        companion object {
            private const val serialVersionUID = 3942549765282708376L
        }
    }

    companion object {
        protected const val MONTHS_IN_YEAR = 12
    }

    init {
        selectedDays = SelectedDays()
        mContext = context
        mController = datePickerController
        init()
    }
}