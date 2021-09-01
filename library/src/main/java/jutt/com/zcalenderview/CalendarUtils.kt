package jutt.com.zcalenderview

import kotlin.jvm.JvmOverloads
import androidx.recyclerview.widget.RecyclerView
import jutt.com.zcalenderview.SimpleMonthAdapter
import jutt.com.zcalenderview.DatePickerController
import android.content.res.TypedArray
import jutt.com.zcalenderview.SimpleMonthView
import jutt.com.zcalenderview.ResizeAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import jutt.com.zcalenderview.SimpleMonthAdapter.SelectedDays
import jutt.com.zcalenderview.SimpleMonthAdapter.CalendarDay
import jutt.com.zcalenderview.ZCalenderView
import jutt.com.zcalenderview.R
import android.view.animation.Animation
import jutt.com.zcalenderview.SimpleMonthView.OnDayClickListener
import android.text.format.DateUtils
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.Paint.Align
import android.view.View.MeasureSpec
import android.view.MotionEvent
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.AbsListView
import java.lang.IllegalArgumentException
import java.util.*

object CalendarUtils {
    fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }
}