package infinitnoob.com.samplezcalenderview

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import jutt.com.zcalenderview.ZCalenderView
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ZCalenderView::class,
    MainActivity::class
)
class ExampleAndroidTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }


    @Test
    fun doSomething_useful_with_your_life() {
        // prepare

        // act

        //assertion
        assertEquals(1,1)
    }
}