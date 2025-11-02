package stu.cn.ua.helloworld.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import stu.cn.ua.helloworld.interfaces.AppContract
import stu.cn.ua.helloworld.interfaces.ResponseListener

open class BaseFragment : Fragment() {

    private var appContract: AppContract? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContract = context as? AppContract
    }

    override fun onDetach() {
        super.onDetach()
        appContract?.unregisterListeners(this)
        appContract = null
    }

    protected fun getAppContract(): AppContract {
        return appContract ?: throw IllegalStateException("AppContract is null")
    }

    protected fun <T> registerListener(clazz: Class<T>, listener: ResponseListener<T>) {
        getAppContract().registerListener(this, clazz, listener)
    }
}