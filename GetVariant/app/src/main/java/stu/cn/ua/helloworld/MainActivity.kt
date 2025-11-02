package stu.cn.ua.helloworld

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import stu.cn.ua.helloworld.fragments.MenuFragment
import stu.cn.ua.helloworld.fragments.OptionsFragment
import stu.cn.ua.helloworld.fragments.ResultsFragment
import stu.cn.ua.helloworld.interfaces.AppContract
import stu.cn.ua.helloworld.interfaces.ResponseListener
import stu.cn.ua.helloworld.models.Student
import java.util.UUID

class MainActivity : AppCompatActivity(), AppContract {

    private val listeners = mutableMapOf<String, MutableList<ListenerInfo<*>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            launchFragment(null, MenuFragment())
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun toOptionsScreen(target: Fragment, student: Student?) {
        launchFragment(target, OptionsFragment.newInstance(student))
    }

    override fun toResultsScreen(target: Fragment, student: Student) {
        launchFragment(target, ResultsFragment.newInstance(student))
    }

    override fun cancel() {
        val count = supportFragmentManager.backStackEntryCount
        if (count <= 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun <T> publish(results: T) {
        val currentFragment = getCurrentFragment() ?: run {
            Log.e(TAG, "Can't find the current fragment")
            return
        }

        val targetFragment = currentFragment.targetFragment ?: run {
            Log.e(TAG, "Fragment $currentFragment doesn't have a target")
            return
        }

        val tag = targetFragment.tag ?: run {
            Log.e(TAG, "Target fragment exists but doesn't have a tag: $targetFragment")
            return
        }

        listeners[tag]?.let { listenerList ->
            // Преобразуем T в Any перед передачей
            listenerList.firstOrNull { it.tryPublish(results as Any) }
        }
    }

    override fun <T> registerListener(
        fragment: Fragment,
        clazz: Class<T>,
        listener: ResponseListener<T>
    ) {
        val tag = fragment.tag ?: run {
            Log.e(TAG, "Fragment '$fragment' doesn't have a tag")
            return
        }

        val listenerList = listeners.getOrPut(tag) { mutableListOf() }
        listenerList.add(ListenerInfo(clazz, listener))
    }

    override fun unregisterListeners(fragment: Fragment) {
        val tag = fragment.tag ?: run {
            Log.e(TAG, "Fragment '$fragment' doesn't have a tag")
            return
        }
        listeners.remove(tag)
    }

    private fun launchFragment(target: Fragment?, fragment: Fragment) {
        target?.let {
            fragment.setTargetFragment(it, 0)
        }

        val tag = UUID.randomUUID().toString()
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragmentContainer)
    }

    private data class ListenerInfo<T>(
        val clazz: Class<T>,
        val listener: ResponseListener<T>
    ) {
        @Suppress("UNCHECKED_CAST")
        fun tryPublish(result: Any): Boolean {
            return if (clazz.isInstance(result)) {
                listener.onResults(result as T)
                true
            } else {
                false
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}