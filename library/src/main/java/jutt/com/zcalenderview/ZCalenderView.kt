package jutt.com.zcalenderview

import android.annotation.SuppressLint
import android.content.Context
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
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.view.MotionEvent
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.AbsListView
import java.util.*

@SuppressLint("Recycle")
class ZCalenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    private var mContext: Context? = null
    var mAdapter: SimpleMonthAdapter? = null
    private var mController: DatePickerController? = null
    var mCurrentScrollState = 0
    var mPreviousScrollPosition: Long = 0
    var mPreviousScrollState = 0
    private var isSingleMonthHeightAdjust = false
    var typedArray: TypedArray? = null
    private var scrollListener: OnScrollListener? = null

    fun scrollToToday() {
        smoothScrollToPosition(12 + Calendar.getInstance()[Calendar.MONTH])
        adjustHeight()
    }

    private fun init(paramContext: Context?) {
        scrollListener = object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                val child = recyclerView.getChildAt(0) as SimpleMonthView
                mPreviousScrollPosition = dy.toLong()
                mPreviousScrollState = mCurrentScrollState
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    mAdapter?.setDragging(true)
                } else if (newState == SCROLL_STATE_IDLE) {
                    postDelayed({ mAdapter?.setDragging(false) }, 150)
                    val firstChild = getChildAt(0)
                    val offset = y - firstChild.y
                    if (offset > firstChild.measuredHeight / 2) {
                        stopScroll()
                        smoothScrollBy(0, (getChildAt(1).y - y).toInt() + 70)
                    } else {
                        stopScroll()
                        smoothScrollBy(0, (-offset).toInt() + 70)
                    }
                    postDelayed({
                        if (isSingleMonthHeightAdjust) {
                            val firstItemHeight = getChildAt(0).measuredHeight
                            if (measuredHeight != firstItemHeight) {
                                val resizeAnimation =
                                    ResizeAnimation(this@ZCalenderView, firstItemHeight)
                                resizeAnimation.duration = 300
                                startAnimation(resizeAnimation)
                            }
                        }
                    }, 400)
                }
            }
        }
        layoutManager = LinearLayoutManager(paramContext)
        mContext = paramContext
        setUpListView()
    }

    private fun setUpAdapter() {
        if (mAdapter == null && typedArray != null) {
            mAdapter = SimpleMonthAdapter(
                context,
                mController,
                typedArray!!
            )
        }
        adapter = mAdapter
    }

    fun setEnableHeightResize(b: Boolean) {
        isSingleMonthHeightAdjust = b
    }

    private fun adjustHeight() {
        postDelayed({
            if (getChildAt(0) != null && isSingleMonthHeightAdjust) {
                val firstItemHeight = getChildAt(0).measuredHeight
                val resizeAnimation = ResizeAnimation(this@ZCalenderView, firstItemHeight)
                resizeAnimation.duration = 300
                startAnimation(resizeAnimation)
            }
        }, 200)
    }

    private fun setUpListView() {
        isVerticalScrollBarEnabled = false
        scrollListener?.let { addOnScrollListener(it) }
        setFadingEdgeLength(0)
    }

    val selectedDays: SelectedDays<CalendarDay?>?
        get() = mAdapter?.selectedDays

    fun clearSelectedDays() {
        mAdapter?.setSelectedDay(null)
    }

    /**
     * setting Hashmap for events in calender
     * for example:
     * eventsMap.put(new SimpleMonthAdapter.CalendarDay(2017,7,20),1); // will set event circle at date 1 , august , 2017
     * Note: second parameter here is number of event circles like in this case its 1 , it can be from 1 - 3
     * @param countMap
     */
    fun setEventsHashMap(countMap: HashMap<CalendarDay?, Int?>) {
        mAdapter?.setCountMap(countMap)
        this.invalidate()
    }

    var controller: DatePickerController?
        get() = mController
        set(mController) {
            this.mController = mController
            setUpAdapter()
            adapter = mAdapter
            scrollToPosition(12 + Calendar.getInstance()[Calendar.MONTH])
        }

    init {
        if (!isInEditMode) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZCalenderView)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            this.overScrollMode = OVER_SCROLL_NEVER
            init(context)
        }
    }
}