package infinitnoob.com.samplezcalenderview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import jutt.com.zcalenderview.DatePickerController;
import jutt.com.zcalenderview.SimpleMonthAdapter;
import jutt.com.zcalenderview.ZCalenderView;

public class MainActivity extends AppCompatActivity  implements DatePickerController{

    private ZCalenderView calenderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calenderView = (ZCalenderView) findViewById(R.id.calendar_view);

        // on click of specific date
        calenderView.setController(this);

        // define if you want to show 1 month for this view only and vertical scroll is enable in this case
        calenderView.setEnableHeightResize(false);

        // events require hashmap
        HashMap<SimpleMonthAdapter.CalendarDay,Integer> eventsMap = new HashMap<>();

        // this date is basically 1st august 2017 and it will show 1 event dot under this day
        eventsMap.put(new SimpleMonthAdapter.CalendarDay(2017,7,20),1);

        calenderView.setEventsHashMap(eventsMap);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Toast.makeText(this, "y:"+year+" , m:"+month+" , d:"+day, Toast.LENGTH_SHORT).show();
    }
}
