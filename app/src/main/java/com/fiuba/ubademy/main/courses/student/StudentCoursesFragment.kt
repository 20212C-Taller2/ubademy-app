package com.fiuba.ubademy.main.courses.student

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentStudentCoursesBinding
import com.fiuba.ubademy.network.model.Course
import com.fiuba.ubademy.main.courses.CourseAdapter

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
            // TODO: go to search screen
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

                val llm = recyclerView.layoutManager as LinearLayoutManager
                if (llm.findLastVisibleItemPosition() > viewModel.courses.value!!.size - 5 && !loading) {
                    progressBar.visibility = View.VISIBLE
                    loading = true
                    val lastItem = viewModel.courses.value!!.last()
                    val list = mutableListOf<Course>()
                    list.add(Course(lastItem.id + 1, "Test ${lastItem.id + 1}", "Test ${lastItem.id + 1}"))
                    list.add(Course(lastItem.id + 2, "Test ${lastItem.id + 2}", "Test ${lastItem.id + 2}"))
                    list.add(Course(lastItem.id + 3, "Test ${lastItem.id + 3}", "Test ${lastItem.id + 3}"))
                    list.add(Course(lastItem.id + 4, "Test ${lastItem.id + 4}", "Test ${lastItem.id + 4}"))
                    list.add(Course(lastItem.id + 5, "Test ${lastItem.id + 5}", "Test ${lastItem.id + 5}"))
                    list.add(Course(lastItem.id + 6, "Test ${lastItem.id + 6}", "Test ${lastItem.id + 6}"))
                    list.add(Course(lastItem.id + 7, "Test ${lastItem.id + 7}", "Test ${lastItem.id + 7}"))
                    list.add(Course(lastItem.id + 8, "Test ${lastItem.id + 8}", "Test ${lastItem.id + 8}"))
                    list.add(Course(lastItem.id + 9, "Test ${lastItem.id + 9}", "Test ${lastItem.id + 9}"))
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressBar.visibility = View.INVISIBLE
                        loading = false
                        viewModel.courses.value = viewModel.courses.value?.plus(list)
                    }, 1000)
                }
            }
        })

        return binding.root
    }
}