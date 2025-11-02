package stu.cn.ua.helloworld.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import stu.cn.ua.helloworld.R
import stu.cn.ua.helloworld.databinding.FragmentResultsBinding
import stu.cn.ua.helloworld.models.Student

class ResultsFragment : BaseFragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val student = getStudent()

        binding.firstNameTextView.text = student.firstName
        binding.lastNameTextView.text = student.lastName
        binding.groupTextView.text = student.group

        binding.doneButton.setOnClickListener {
            getAppContract().cancel()
        }

        binding.tryAgainButton.setOnClickListener {
            fetchVariant(student)
        }

        fetchVariant(student)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchVariant(student: Student) {
        toPendingState()

        try {
            // Власний алгоритм генерації варіанту
            val variant = calculateVariant(
                student.firstName,
                student.lastName,
                student.group,
                Student.MAX_VARIANT
            )
            toSuccessState(variant)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            toErrorState(e.message ?: getString(R.string.error))
        }
    }

    /**
     * Розраховує варіант на основі даних студента
     * Алгоритм: використовує hashCode всіх даних для генерації стабільного варіанту
     */
    private fun calculateVariant(
        firstName: String,
        lastName: String,
        group: String,
        maxVariant: Int
    ): Int {
        val combined = "$firstName$lastName$group"
        val hash = combined.hashCode()
        // Перетворюємо hash в діапазон [1, maxVariant]
        return (hash % maxVariant).let { if (it <= 0) it + maxVariant else it }
    }

    private fun toPendingState() {
        binding.progress.visibility = View.VISIBLE
        binding.resultsTable.visibility = View.INVISIBLE
        binding.doneButton.visibility = View.INVISIBLE
        binding.tryAgainButton.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun toSuccessState(variant: Int) {
        binding.doneButton.visibility = View.VISIBLE
        binding.tryAgainButton.visibility = View.GONE
        binding.resultsTable.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.variantTextView.text = variant.toString()
    }

    private fun toErrorState(errorMessage: String) {
        binding.doneButton.visibility = View.INVISIBLE
        binding.tryAgainButton.visibility = View.VISIBLE
        binding.resultsTable.visibility = View.INVISIBLE
        binding.progress.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = errorMessage
    }

    private fun getStudent(): Student {
        return arguments?.getParcelable(ARG_STUDENT)
            ?: throw IllegalStateException("Student argument is required")
    }

    companion object {
        private const val ARG_STUDENT = "STUDENT"
        private val TAG = ResultsFragment::class.java.simpleName

        fun newInstance(student: Student): ResultsFragment {
            return ResultsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STUDENT, student)
                }
            }
        }
    }
}