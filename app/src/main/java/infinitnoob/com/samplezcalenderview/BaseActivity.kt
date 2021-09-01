package infinitnoob.com.samplezcalenderview

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        onReady()
    }

    /**
     * Here we may bind our observers to LiveData if some.
     * This method will be executed after parent [onCreate] method
     */
    protected open fun onReady() {
        //Optional
    }

    protected fun <T, LD : LiveData<T>> observeNullable(liveData: LD, onChanged: (T?) -> Unit) {
        liveData.observe(this, {
            onChanged(it)
        })
    }

    protected fun <T, LD : LiveData<T>> observe(liveData: LD, onChanged: (T) -> Unit) {
        liveData.observe(this, {
            it?.let(onChanged)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}