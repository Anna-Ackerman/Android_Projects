package stu.cn.ua.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import stu.cn.ua.helloworld.R
import stu.cn.ua.helloworld.databinding.FragmentOptionsBinding
import stu.cn.ua.helloworld.models.Student

class OptionsFragment : BaseFragment() {

    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        setupGroupsSpinner(savedInstanceState?.getString(KEY_GROUP))

        if (savedInstanceState == null) {
            getStudentArg()?.let { student ->
                binding.firstNameEditText.setText(student.firstName)
                binding.lastNameEditText.setText(student.lastName)
                val groupIndex = Student.GROUPS.indexOf(student.group)
                if (groupIndex != -1) {
                    binding.groupSpinner.setSelection(groupIndex)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_GROUP, binding.groupSpinner.selectedItem?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            getAppContract().cancel()
        }

        binding.doneButton.setOnClickListener {
            val student = Student(
                firstName = binding.firstNameEditText.text.toString(),
                lastName = binding.lastNameEditText.text.toString(),
                group = binding.groupSpinner.selectedItem.toString()
            )

            if (!student.isValid()) {
                Toast.makeText(
                    requireContext(),
                    R.string.empty_fields_error,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            getAppContract().publish(student)
            getAppContract().cancel()
        }
    }

    private fun setupGroupsSpinner(selectedGroup: String?) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Student.GROUPS
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.groupSpinner.adapter = adapter

        selectedGroup?.let {
            val index = Student.GROUPS.indexOf(it)
            if (index != -1) {
                binding.groupSpinner.setSelection(index)
            }
        }
    }

    private fun getStudentArg(): Student? {
        return arguments?.getParcelable(ARG_STUDENT)
    }

    companion object {
        private const val ARG_STUDENT = "STUDENT"
        private const val KEY_GROUP = "GROUP"

        fun newInstance(student: Student?): OptionsFragment {
            return OptionsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STUDENT, student)
                }
            }
        }
    }
}