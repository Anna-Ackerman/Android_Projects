package stu.cn.ua.helloworld.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import stu.cn.ua.helloworld.BuildConfig
import stu.cn.ua.helloworld.R
import stu.cn.ua.helloworld.databinding.FragmentMenuBinding
import stu.cn.ua.helloworld.models.Student

class MenuFragment : BaseFragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var student: Student? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerListener(Student::class.java) { student ->
            this.student = student
            updateView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            student = it.getParcelable(KEY_STUDENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optionsButton.setOnClickListener {
            getAppContract().toOptionsScreen(this, student)
        }

        binding.quitButton.setOnClickListener {
            getAppContract().cancel()
        }

        binding.getVariantButton.setOnClickListener {
            student?.let {
                getAppContract().toResultsScreen(this, it)
            }
        }

        binding.versionTextView.text = getString(
            R.string.app_version,
            BuildConfig.VERSION_NAME
        )

        updateView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_STUDENT, student)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView() {
        _binding?.getVariantButton?.isEnabled = student?.isValid() == true
    }

    companion object {
        private const val KEY_STUDENT = "STUDENT"
    }
}