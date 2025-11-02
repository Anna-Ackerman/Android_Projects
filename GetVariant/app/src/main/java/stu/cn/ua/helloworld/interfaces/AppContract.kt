package stu.cn.ua.helloworld.interfaces

import androidx.fragment.app.Fragment
import stu.cn.ua.helloworld.models.Student

interface AppContract {
    fun toOptionsScreen(target: Fragment, student: Student?)
    fun toResultsScreen(target: Fragment, student: Student)
    fun cancel()
    fun <T> publish(data: T)
    fun <T> registerListener(fragment: Fragment, clazz: Class<T>, listener: ResponseListener<T>)
    fun unregisterListeners(fragment: Fragment)
}

fun interface ResponseListener<T> {
    fun onResults(results: T)
}