package com.fiuba.ubademy.main.courses.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentStudentsBinding
import com.fiuba.ubademy.main.courses.GetUsersStatus
import com.fiuba.ubademy.main.courses.UserAdapter
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class StudentsFragment : Fragment() {

    private lateinit var viewModel: StudentsViewModel
    private lateinit var binding: FragmentStudentsBinding

    private lateinit var recyclerView : RecyclerView
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(StudentsViewModel::class.java)

        val studentIds = StudentsFragmentArgs.fromBundle(requireArguments()).studentIds
        viewModel.studentIds.value = studentIds.toList()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_students,
            container,
            false
        )

        val adapter = UserAdapter()

        viewModel.filteredStudents.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        adapter.onUserItemClick = {
            findNavController().navigate(StudentsFragmentDirections.actionStudentsFragmentToViewPublicProfileFragment(it))
        }

        binding.studentsList.adapter = adapter

        binding.studentsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.root.findViewById(R.id.studentsList)

        lifecycleScope.launch { getStudents() }

        return binding.root
    }

    private fun getStudents() {
        if (!loading) {
            loading = true
            BusyFragment.show(parentFragmentManager)
            lifecycleScope.launch {
                val getUsersStatus : GetUsersStatus = viewModel.getStudents()
                if (getUsersStatus == GetUsersStatus.FAIL)
                    Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                if (getUsersStatus == GetUsersStatus.NOT_FOUND)
                    Toast.makeText(context, R.string.there_is_no_users, Toast.LENGTH_LONG).show()
                recyclerView.smoothScrollToPosition(0)
                BusyFragment.hide()
                loading = false
            }
        }
    }
}