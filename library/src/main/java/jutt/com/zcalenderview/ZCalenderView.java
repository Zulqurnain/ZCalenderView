package jutt.com.zcalenderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.HashMap;

public class ZCalenderView extends RecyclerView
{
    public static final String TAG = ZCalenderView.class.getSimpleName();

    protected Context mContext;
	protected SimpleMonthAdapter mAdapter;
	private DatePickerController mController;
    protected int mCurrentScrollState = 0;
	protected long mPreviousScrollPosition;
	protected int mPreviousScrollState = 0;
    private boolean isSingleMonthHeightAdjust;
    private TypedArray typedArray;
    private OnScrollListener onScrollListener;

    public ZCalenderView(Context context)
    {
        this(context, null);
    }

    public ZCalenderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ZCalenderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        if (!isInEditMode())
        {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZCalenderView);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            this.setOverScrollMode(OVER_SCROLL_NEVER);
            init(context);
        }
    }

    public void setController(DatePickerController mController)
    {
        this.mController = mController;
        setUpAdapter();
        setAdapter(mAdapter);

        scrollToPosition(12 + Calendar.getInstance().get(Calendar.MONTH));

    }

    public void scrollToToday() {
        smoothScrollToPosition(12 + Calendar.getInstance().get(Calendar.MONTH));
        adjustHeight();
    }


	public void init(Context paramContext) {
        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final SimpleMonthView child = (SimpleMonthView) recyclerView.getChildAt(0);
                if (child == null) {
                    return;
                }

                mPreviousScrollPosition = dy;
                mPreviousScrollState = mCurrentScrollState;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_DRAGGING) {
                    mAdapter.setDragging(true);
                } else if (newState == SCROLL_STATE_IDLE) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setDragging(false);
                        }
                    }, 150);


                    View firstChild = getChildAt(0);
                    float offset = getY() - firstChild.getY();
                    if (offset > firstChild.getMeasuredHeight() / 2) {
                        stopScroll();
                        smoothScrollBy(0, (int) (getChildAt(1).getY() - getY()) + 70);
                    } else {
                        stopScroll();
                        smoothScrollBy(0, -(int) offset + 70);
                    }

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isSingleMonthHeightAdjust) {
                                int firstItemHeight = getChildAt(0).getMeasuredHeight();
                                if (getMeasuredHeight() != firstItemHeight) {
                                    ResizeAnimation resizeAnimation = new ResizeAnimation(ZCalenderView.this, firstItemHeight);
                                    resizeAnimation.setDuration(300);
                                    startAnimation(resizeAnimation);
                                }
                            }
                        }
                    }, 400);
                }
            }
        };

        setLayoutManager(new LinearLayoutManager(paramContext));
		mContext = paramContext;
		setUpListView();

    }


	protected void setUpAdapter() {
		if (mAdapter == null) {
			mAdapter = new SimpleMonthAdapter(getContext(), mController, typedArray);
        }
		mAdapter.notifyDataSetChanged();
	}

    public void setEnableHeightResize(boolean b){
        isSingleMonthHeightAdjust = b;
    }

    public void adjustHeight() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getChildAt(0) != null && isSingleMonthHeightAdjust) {
                    int firstItemHeight = getChildAt(0).getMeasuredHeight();
                    ResizeAnimation resizeAnimation = new ResizeAnimation(ZCalenderView.this, firstItemHeight);
                    resizeAnimation.setDuration(300);
                    startAnimation(resizeAnimation);
                }
            }
        }, 200);
    }


    protected void setUpListView() {
		setVerticalScrollBarEnabled(false);
		setOnScrollListener(onScrollListener);
		setFadingEdgeLength(0);
	}

    public SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> getSelectedDays()
    {
        return mAdapter.getSelectedDays();
    }

    public void clearSelectedDays() {
        mAdapter.setSelectedDay(null);
    }

    /**
     * setting Hashmap for events in calender
     * for example:
     * eventsMap.put(new SimpleMonthAdapter.CalendarDay(2017,7,20),1); // will set event circle at date 1 , august , 2017
     * Note: second parameter here is number of event circles like in this case its 1 , it can be from 1 - 3
     * @param countMap
     */
    public void setEventsHashMap(HashMap<SimpleMonthAdapter.CalendarDay, Integer> countMap) {
        mAdapter.setCountMap(countMap);
        this.invalidate();
    }

    protected DatePickerController getController()
    {
        return mController;
    }

    protected TypedArray getTypedArray()
    {
        return typedArray;
    }
}