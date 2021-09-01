package jutt.com.zcalenderview

import android.annotation.SuppressLint
import android.content.Context
import kotlin.jvm.JvmOverloads
import androidx.recyclerview.widget.RecyclerView
import jutt.com.zcalenderview.SimpleMonthAdapter
import jutt.com.zcalenderview.DatePickerController
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
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
import android.text.format.Time
import android.view.View.MeasureSpec
import android.view.MotionEvent
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.lang.StringBuilder
import java.security.InvalidParameterException
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class SimpleMonthView(context: Context, typedArray: TypedArray?) : View(context) {
    var mPadding = 0
    private val mDayOfWeekTypeface: String
    private val mMonthTitleTypeface: String
    var mMonthDayLabelPaint: Paint? = null
    var mMonthNumPaint: Paint? = null
    var mMonthInfoPaint: Paint? = null
    var mMonthTitlePaint: Paint? = null
    var mSelectedCirclePaint: Paint? = null
    var mCurrentCirclePaint: Paint? = null
    var mCurrentDayTextColor: Int
    var mMonthTextColor: Int
    var mDayTextColor: Int
    var mMonthDayLabelTextColor: Int
    var mDayNumColor: Int
    var mMonthTitleBGColor: Int
    var mPreviousDayColor: Int
    var mSelectedDaysColor: Int
    var mWeekendsColor: Int
    var mCurrentDayColor: Int
    private val mStringBuilder: StringBuilder
    var mHasToday = false
    var mIsPrev = false
    var mSelectedBeginDay = -1
    var mSelectedBeginMonth = -1
    var mSelectedBeginYear = -1
    var mToday = -1
    var mWeekStart = 1
    var mNumDays = 7
    var mNumCells = mNumDays
    private var mDayOfWeekStart = 0
    var mMonth = 0
    var mDrawRect: Boolean
    var mRowHeight = DEFAULT_HEIGHT
    var mWidth = 0
    var mYear = 0
    private val today: Time
    private val mCalendar: Calendar
    private val mDayLabelCalendar: Calendar
    private val isPrevDayEnabled: Boolean
    private var shouldShowMonthInfo = false
    private var alphaStartTime: Long = -1
    private val FRAMES_PER_SECOND = 60
    private val ALPHA_DURATION: Long = 400
    private var currentDraggingAlpha = 0
    private var currentNormalAlpha = 0
    private var mNumRows = DEFAULT_NUM_ROWS
    private val eventSymbols: MutableMap<Int, Int> = HashMap()
    private val mDateFormatSymbols = DateFormatSymbols()
    private var mOnDayClickListener: OnDayClickListener? = null
    private var mEventCirclePaint: Paint? = null
    private var mDividerPaint: Paint? = null

    private fun calculateNumRows(): Int {
        val offset = findDayOffset()
        val dividend = (offset + mNumCells) / mNumDays
        val remainder = (offset + mNumCells) % mNumDays
        return dividend + if (remainder > 0) 1 else 0
    }

    @SuppressLint("SimpleDateFormat")
    private fun drawMonthDayLabels(canvas: Canvas) {
        val y = MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE / 2
        val dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2)
        for (i in 0 until mNumDays) {
            if (i == 0 || i == mNumDays - 1) {
                mMonthDayLabelPaint!!.color = mWeekendsColor
            } else {
                mMonthDayLabelPaint!!.color = mMonthDayLabelTextColor
            }
            val calendarDay = (i + mWeekStart) % mNumDays
            val x = (2 * i + 1) * dayWidthHalf + mPadding
            mDayLabelCalendar[Calendar.DAY_OF_WEEK] = calendarDay
            val dateFormat = SimpleDateFormat("EEEEE")
            val dayLabelText = dateFormat.format(mDayLabelCalendar.time)
            canvas.drawText(dayLabelText, x.toFloat(), y.toFloat(), mMonthDayLabelPaint!!)
        }
    }

    private fun drawMonthTitle(canvas: Canvas) {
        val paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays)
        val x = paddingDay * (findDayOffset() * 2 + 1) + mPadding
        val y =
            (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE / 5 * 2
        val stringBuilder = StringBuilder(monthString.lowercase(Locale.getDefault()))
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder[0]))
        canvas.drawText(
            stringBuilder.toString().uppercase(Locale.getDefault()),
            x.toFloat(),
            (y - 10).toFloat(),
            mMonthTitlePaint!!
        )
    }

    private fun findDayOffset(): Int {
        return ((if (mDayOfWeekStart < mWeekStart) mDayOfWeekStart + mNumDays else mDayOfWeekStart)
                - mWeekStart)
    }

    private val monthAndYearString: String
        get() {
            val flags =
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NO_MONTH_DAY
            mStringBuilder.setLength(0)
            val millis = mCalendar.timeInMillis
            return DateUtils.formatDateRange(context, millis, millis, flags)
        }
    private val monthString: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val dateFormat = SimpleDateFormat("MMM")
            return dateFormat.format(mCalendar.time)
        }

    private fun onDayClick(calendarDay: CalendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled || !(calendarDay.month == today.month && calendarDay.year == today.year && calendarDay.day < today.monthDay))) {
            mOnDayClickListener!!.onDayClick(this, calendarDay)
        }
    }

    private fun sameDay(monthDay: Int, time: Time): Boolean {
        return mYear == time.year && mMonth == time.month && monthDay == time.monthDay
    }

    private fun prevDay(monthDay: Int, time: Time): Boolean {
        return mYear < time.year || mYear == time.year && mMonth < time.month || mMonth == time.month && monthDay < time.monthDay
    }

    /**
     * draw dots to show events
     * @param x x pos of date number
     * @param y y pos of date number
     * @param count
     * @param canvas
     */
    private fun drawDots(x: Int, y: Int, count: Int, canvas: Canvas) {
        when (count) {
            1 -> canvas.drawCircle(
                x.toFloat(),
                (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                mEventCirclePaint!!
            )
            2 -> {
                canvas.drawCircle(
                    (x - EVENT_DOTS_CIRCLE_SIZE * 8 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
                canvas.drawCircle(
                    (x + EVENT_DOTS_CIRCLE_SIZE * 8 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
            }
            3 -> {
                canvas.drawCircle(
                    (x - EVENT_DOTS_CIRCLE_SIZE * 16 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
                canvas.drawCircle(
                    (x + EVENT_DOTS_CIRCLE_SIZE * 16 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
                canvas.drawCircle(
                    x.toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
            }
            else -> {
                canvas.drawCircle(
                    (x - EVENT_DOTS_CIRCLE_SIZE * 16 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
                canvas.drawCircle(
                    (x + EVENT_DOTS_CIRCLE_SIZE * 16 / 5).toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
                canvas.drawCircle(
                    x.toFloat(),
                    (y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5 + 20).toFloat(),
                    EVENT_DOTS_CIRCLE_SIZE.toFloat(),
                    mEventCirclePaint!!
                )
            }
        }
    }

    private fun drawMonthNums(canvas: Canvas) {
        var y =
            (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE
        val paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays)
        var dayOffset = findDayOffset()
        var day = 1
        var line_y =
            (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE / 5 * 2
        while (day <= mNumCells) {
            val x = paddingDay * (1 + dayOffset * 2) + mPadding
            canvas.drawLine(
                (x - 40).toFloat(),
                (line_y - 2).toFloat(),
                mWidth.toFloat(),
                (line_y - 2).toFloat(),
                mDividerPaint!!
            )
            if (mHasToday && mToday == day) {
                canvas.drawCircle(
                    x.toFloat(),
                    (y - MINI_DAY_NUMBER_TEXT_SIZE / 3).toFloat(),
                    DAY_SELECTED_CIRCLE_SIZE.toFloat(),
                    mCurrentCirclePaint!!
                )
            }
            if (day == 1) {
                drawMonthTitle(canvas)
            }
            val dotsCount = eventSymbols[day]
            if (dotsCount != null && dotsCount > 0) {
                drawDots(x, y, dotsCount, canvas)
            }
            if (mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) {
                if (mDrawRect) {
                    val rectF = RectF(
                        (x - DAY_SELECTED_CIRCLE_SIZE).toFloat(),
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3 - DAY_SELECTED_CIRCLE_SIZE).toFloat(),
                        (x + DAY_SELECTED_CIRCLE_SIZE).toFloat(),
                        (y - MINI_DAY_NUMBER_TEXT_SIZE / 3 + DAY_SELECTED_CIRCLE_SIZE).toFloat()
                    )
                    canvas.drawRoundRect(rectF, 10.0f, 10.0f, mSelectedCirclePaint!!)
                } else canvas.drawCircle(
                    x.toFloat(),
                    (y - MINI_DAY_NUMBER_TEXT_SIZE / 3).toFloat(),
                    DAY_SELECTED_CIRCLE_SIZE.toFloat(),
                    mSelectedCirclePaint!!
                )
            }
            if (dayOffset == 0 || dayOffset == mNumDays - 1) {
                mMonthNumPaint!!.color = mWeekendsColor
            } else {
                mMonthNumPaint!!.color = mDayNumColor
            }
            if (mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) mMonthNumPaint!!.color =
                mMonthTitleBGColor
            if (!isPrevDayEnabled && prevDay(
                    day,
                    today
                ) && today.month == mMonth && today.year == mYear
            ) {
                mMonthNumPaint!!.color = mPreviousDayColor
                mMonthNumPaint!!.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
            } else if (mHasToday && mToday == day) {
                mMonthNumPaint!!.color = mCurrentDayTextColor
            }
            if (shouldShowMonthInfo) {
                mMonthNumPaint!!.alpha = DRAGING_ALPHA
            } else {
                mMonthNumPaint!!.alpha = NORMAL_ALPHA
            }
            canvas.drawText(String.format("%d", day), x.toFloat(), y.toFloat(), mMonthNumPaint!!)
            dayOffset++
            if (dayOffset == mNumDays) {
                dayOffset = 0
                y += mRowHeight
                line_y += mRowHeight
            }
            day++
        }
    }

    private fun drawMonthInfo(canvas: Canvas) {
        val x = (mWidth + 2 * mPadding) / 2
        val y = (mRowHeight * 3.2).toInt()
        canvas.drawText(monthAndYearString, x.toFloat(), y.toFloat(), mMonthInfoPaint!!)
    }

    private fun getDayFromLocation(x: Float, y: Float): CalendarDay? {
        val padding = mPadding
        if (x < padding || x > mWidth - mPadding) {
            return null
        }
        val yDay = (y - MONTH_HEADER_SIZE).toInt() / mRowHeight
        val day =
            1 + (((x - padding) * mNumDays / (mWidth - padding - mPadding)).toInt() - findDayOffset()) + yDay * mNumDays
        return if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(
                mMonth,
                mYear
            ) < day || day < 1
        ) null else CalendarDay(mYear, mMonth, day)
    }

    private fun initView() {
        mMonthTitlePaint = Paint()
        mMonthTitlePaint!!.isFakeBoldText = true
        mMonthTitlePaint!!.isAntiAlias = true
        mMonthTitlePaint!!.textSize = MONTH_LABEL_TEXT_SIZE.toFloat()
        mMonthTitlePaint!!.typeface = Typeface.create(mMonthTitleTypeface, Typeface.NORMAL)
        mMonthTitlePaint!!.color = mMonthTextColor
        mMonthTitlePaint!!.textAlign = Align.CENTER
        mMonthTitlePaint!!.style = Paint.Style.FILL
        mMonthInfoPaint = Paint()
        mMonthInfoPaint!!.isFakeBoldText = true
        mMonthInfoPaint!!.isAntiAlias = true
        mMonthInfoPaint!!.textSize = MONTH_INFO_TEXT_SIZE.toFloat()
        mMonthInfoPaint!!.typeface = Typeface.create(mMonthTitleTypeface, Typeface.NORMAL)
        mMonthInfoPaint!!.color = mDayNumColor
        mMonthInfoPaint!!.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        mMonthInfoPaint!!.textAlign = Align.CENTER
        mMonthInfoPaint!!.alpha = 0
        mMonthInfoPaint!!.style = Paint.Style.FILL
        mSelectedCirclePaint = Paint()
        mSelectedCirclePaint!!.isFakeBoldText = true
        mSelectedCirclePaint!!.isAntiAlias = true
        mSelectedCirclePaint!!.color = mSelectedDaysColor
        mSelectedCirclePaint!!.textAlign = Align.CENTER
        mSelectedCirclePaint!!.style = Paint.Style.FILL
        mCurrentCirclePaint = Paint()
        mCurrentCirclePaint!!.isFakeBoldText = true
        mCurrentCirclePaint!!.isAntiAlias = true
        mCurrentCirclePaint!!.color = mCurrentDayColor
        mCurrentCirclePaint!!.textAlign = Align.CENTER
        mCurrentCirclePaint!!.style = Paint.Style.FILL
        mEventCirclePaint = Paint()
        mEventCirclePaint!!.isFakeBoldText = true
        mEventCirclePaint!!.isAntiAlias = true
        mEventCirclePaint!!.color = mMonthTextColor
        mEventCirclePaint!!.textAlign = Align.CENTER
        mEventCirclePaint!!.style = Paint.Style.FILL
        mMonthDayLabelPaint = Paint()
        mMonthDayLabelPaint!!.isAntiAlias = true
        mMonthDayLabelPaint!!.textSize = MONTH_DAY_LABEL_TEXT_SIZE.toFloat()
        mMonthDayLabelPaint!!.color = mMonthDayLabelTextColor
        mMonthDayLabelPaint!!.typeface = Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL)
        mMonthDayLabelPaint!!.style = Paint.Style.FILL
        mMonthDayLabelPaint!!.textAlign = Align.CENTER
        mMonthDayLabelPaint!!.isFakeBoldText = true
        mMonthNumPaint = Paint()
        mMonthNumPaint!!.isAntiAlias = true
        mMonthNumPaint!!.textSize = MINI_DAY_NUMBER_TEXT_SIZE.toFloat()
        mMonthNumPaint!!.style = Paint.Style.FILL
        mMonthNumPaint!!.textAlign = Align.CENTER
        mMonthNumPaint!!.isFakeBoldText = false
        mDividerPaint = Paint()
        mDividerPaint!!.isAntiAlias = true
        mDividerPaint!!.style = Paint.Style.FILL
        mDividerPaint!!.color = mWeekendsColor
        mDividerPaint!!.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        calculateAlpha()
        drawMonthNums(canvas)
        drawMonthInfo(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            mRowHeight * mNumRows + MONTH_HEADER_SIZE + PADDING_BOTTOM
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val calendarDay = getDayFromLocation(event.x, event.y)
            calendarDay?.let { onDayClick(it) }
        }
        return true
    }

    fun reuse() {
        mNumRows = DEFAULT_NUM_ROWS
        eventSymbols.clear()
        requestLayout()
    }

    fun setMonthParams(params: HashMap<String?, Int?>) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw InvalidParameterException("You must specify month and year for this view")
        }
        tag = params
        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params[VIEW_PARAMS_HEIGHT]!!
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params[VIEW_PARAMS_SELECTED_BEGIN_DAY]!!
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params[VIEW_PARAMS_SELECTED_BEGIN_MONTH]!!
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params[VIEW_PARAMS_SELECTED_BEGIN_YEAR]!!
        }
        mMonth = params[VIEW_PARAMS_MONTH]!!
        mYear = params[VIEW_PARAMS_YEAR]!!
        mHasToday = false
        mToday = -1
        mCalendar[Calendar.MONTH] = mMonth
        mCalendar[Calendar.YEAR] = mYear
        mCalendar[Calendar.DAY_OF_MONTH] = 1
        mDayOfWeekStart = mCalendar[Calendar.DAY_OF_WEEK]
        mWeekStart = if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            params[VIEW_PARAMS_WEEK_START]!!
        } else {
            mCalendar.firstDayOfWeek
        }
        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear)
        for (i in 0 until mNumCells) {
            val day = i + 1
            if (sameDay(day, today)) {
                mHasToday = true
                mToday = day
            }
            mIsPrev = prevDay(day, today)
        }
        mNumRows = calculateNumRows()
    }

    fun showMothInfo(show: Boolean) {
        if (shouldShowMonthInfo != show) {
            shouldShowMonthInfo = show
            alphaStartTime = System.currentTimeMillis()
        }
    }

    private fun calculateAlpha() {
        val elapsedTime = System.currentTimeMillis() - alphaStartTime
        val alphaChange = ((NORMAL_ALPHA - 0) * elapsedTime / ALPHA_DURATION).toInt()
        currentDraggingAlpha = NORMAL_ALPHA - alphaChange
        if (currentDraggingAlpha < 0 || alphaStartTime == -1L) {
            currentDraggingAlpha = 0
        }
        currentNormalAlpha = alphaChange
        if (currentNormalAlpha > NORMAL_ALPHA) {
            currentNormalAlpha = NORMAL_ALPHA
        }
        if (shouldShowMonthInfo) {
            mMonthInfoPaint!!.alpha = currentNormalAlpha
            mMonthTitlePaint!!.alpha = DRAGING_ALPHA
            mSelectedCirclePaint!!.alpha = DRAGING_ALPHA
            mCurrentCirclePaint!!.alpha = DRAGING_ALPHA
            mMonthDayLabelPaint!!.alpha = DRAGING_ALPHA
        } else {
            mMonthInfoPaint!!.alpha = currentDraggingAlpha
            mMonthTitlePaint!!.alpha = NORMAL_ALPHA
            mSelectedCirclePaint!!.alpha = NORMAL_ALPHA
            mCurrentCirclePaint!!.alpha = NORMAL_ALPHA
            mMonthDayLabelPaint!!.alpha = NORMAL_ALPHA
        }
        if (elapsedTime < ALPHA_DURATION) {
            this.postInvalidateDelayed((1000 / FRAMES_PER_SECOND).toLong())
        }
    }

    fun setEventSymbols(symbols: HashMap<CalendarDay, Int>) {
        eventSymbols.clear()
        for ((key, value) in symbols) {
            eventSymbols[key.day] = value
        }
    }

    fun setOnDayClickListener(onDayClickListener: OnDayClickListener?) {
        mOnDayClickListener = onDayClickListener
    }

    interface OnDayClickListener {
        fun onDayClick(simpleMonthView: SimpleMonthView?, calendarDay: CalendarDay?)
    }

    companion object {
        const val VIEW_PARAMS_HEIGHT = "height"
        const val VIEW_PARAMS_MONTH = "month"
        const val VIEW_PARAMS_YEAR = "year"
        const val VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day"
        const val VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day"
        const val VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month"
        const val VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month"
        const val VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year"
        const val VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year"
        const val VIEW_PARAMS_WEEK_START = "week_start"
        private const val DRAGING_ALPHA = 82
        private const val NORMAL_ALPHA = 255
        var DEFAULT_HEIGHT = 70
        const val DEFAULT_NUM_ROWS = 6

        var DAY_SELECTED_CIRCLE_SIZE: Int = 0
        var EVENT_DOTS_CIRCLE_SIZE: Int = 0
        var DAY_SEPARATOR_WIDTH = 1
        var MINI_DAY_NUMBER_TEXT_SIZE: Int = 0
        var MIN_HEIGHT = 10
        var MONTH_DAY_LABEL_TEXT_SIZE: Int = 0
        var MONTH_HEADER_SIZE: Int = 0
        var MONTH_LABEL_TEXT_SIZE: Int = 0
        var MONTH_INFO_TEXT_SIZE: Int = 0
        var PADDING_BOTTOM: Int = 0
    }

    init {
        val resources = context.resources
        mDayLabelCalendar = Calendar.getInstance()
        mCalendar = Calendar.getInstance()
        today = Time(Time.getCurrentTimezone())
        today.setToNow()
        mDayOfWeekTypeface = resources.getString(R.string.sans_serif)
        mMonthTitleTypeface = resources.getString(R.string.sans_serif)
        mCurrentDayColor = typedArray!!.getColor(
            R.styleable.ZCalenderView_colorCurrentDayCircle,
            ContextCompat.getColor(context,R.color.current_day_background)
        )
        mCurrentDayTextColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorCurrentDayText,
            ContextCompat.getColor(context,R.color.normal_day)
        )
        mMonthTextColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorMonthName,
            ContextCompat.getColor(context,R.color.normal_month)
        )
        mMonthDayLabelTextColor = resources.getColor(R.color.month_day_label_text)
        mDayTextColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorDayName,
            ContextCompat.getColor(context,R.color.normal_day)
        )
        mDayNumColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorNormalDay,
            ContextCompat.getColor(context,R.color.normal_day)
        )
        mPreviousDayColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorPreviousDay,
            ContextCompat.getColor(context,R.color.normal_day)
        )
        mSelectedDaysColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorSelectedDayBackground,
            ContextCompat.getColor(context,R.color.selected_day_background)
        )
        mMonthTitleBGColor = typedArray.getColor(
            R.styleable.ZCalenderView_colorSelectedDayText,
            ContextCompat.getColor(context,R.color.selected_day_text)
        )
        mWeekendsColor = ContextCompat.getColor(context,R.color.weekends_day)
        mDrawRect = typedArray.getBoolean(R.styleable.ZCalenderView_drawRoundRect, false)
        //mCurrentDayColor = ContextCompat.getColor(context,R.color.current_day_background);
        mStringBuilder = StringBuilder(50)
        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(
            R.styleable.ZCalenderView_textSizeDay,
            resources.getDimensionPixelSize(R.dimen.text_size_day)
        )
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(
            R.styleable.ZCalenderView_textSizeMonth,
            resources.getDimensionPixelSize(R.dimen.text_size_month)
        )
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(
            R.styleable.ZCalenderView_textSizeDayName,
            resources.getDimensionPixelSize(R.dimen.text_size_day_name)
        )
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(
            R.styleable.ZCalenderView_headerMonthHeight,
            resources.getDimensionPixelOffset(R.dimen.header_month_height)
        )
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(
            R.styleable.ZCalenderView_selectedDayRadius,
            resources.getDimensionPixelOffset(R.dimen.selected_day_radius)
        )
        EVENT_DOTS_CIRCLE_SIZE = resources.getDimensionPixelSize(R.dimen.event_dots_radius)
        MONTH_INFO_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_info_text_size)
        PADDING_BOTTOM =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                .toInt()
        mRowHeight = (typedArray.getDimensionPixelSize(
            R.styleable.ZCalenderView_calendarHeight,
            resources.getDimensionPixelOffset(R.dimen.calendar_height)
        ) - MONTH_HEADER_SIZE) / 6
        isPrevDayEnabled = typedArray.getBoolean(R.styleable.ZCalenderView_enablePreviousDay, true)
        initView()
    }
}