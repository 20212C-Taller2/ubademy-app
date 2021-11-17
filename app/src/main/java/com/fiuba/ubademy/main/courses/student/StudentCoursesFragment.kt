package com.fiuba.ubademy.main.courses.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentStudentCoursesBinding
import com.fiuba.ubademy.main.courses.CourseAdapter
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class StudentCoursesFragment : Fragment() {

    private lateinit var viewModel: StudentCoursesViewModel
    private lateinit var binding: FragmentStudentCoursesBinding

    private lateinit var progressBar : ProgressBar
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(StudentCoursesViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_student_courses,
            container,
            false
        )

        binding.searchCourseButton.setOnClickListener {
            it.findNavController().navigate(StudentCoursesFragmentDirections.actionStudentCoursesFragmentToSearchCourseFragment())
        }

        progressBar = binding.root.findViewById(R.id.studentCoursesProgressBar)

        val adapter = CourseAdapter()

        viewModel.courses.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.studentCoursesViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.studentCoursesList.adapter = adapter

        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.studentCoursesList)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager
                    val size = viewModel.courses.value!!.size
                    if (llm.findLastVisibleItemPosition() > size - 5 && !loading) {
                        progressBar.visibility = View.VISIBLE
                        loading = true
                        lifecycleScope.launch {
                            val getCoursesStatus : GetCoursesStatus = viewModel.addCourses(size)
                            if (getCoursesStatus == GetCoursesStatus.FAIL)
                                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.INVISIBLE
                            loading = false
                        }
                    }
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            val getCoursesStatus : GetCoursesStatus = viewModel.getCourses()
            if (getCoursesStatus == GetCoursesStatus.FAIL)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            BusyFragment.hide()
        }
    }
}