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
import android.view.View
import android.view.ViewGroup
import android.view.animation.Transformation
import android.widget.AbsListView

/**
 * Created by jgzhu on 2/2/15.
 */
class ResizeAnimation(var view: View, val targetHeight: Int) : Animation() {
    val startHeight: Int
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

    init {
        startHeight = view.height
    }
}