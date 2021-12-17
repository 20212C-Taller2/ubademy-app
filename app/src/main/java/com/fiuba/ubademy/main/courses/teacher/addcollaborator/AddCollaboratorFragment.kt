package com.fiuba.ubademy.main.courses.teacher.addcollaborator

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentAddCollaboratorBinding
import com.fiuba.ubademy.main.courses.GetUsersStatus
import com.fiuba.ubademy.main.courses.UserAdapter
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class AddCollaboratorFragment : Fragment() {

    private lateinit var viewModel: AddCollaboratorViewModel
    private lateinit var binding: FragmentAddCollaboratorBinding

    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(AddCollaboratorViewModel::class.java)

        val courseId = AddCollaboratorFragmentArgs.fromBundle(requireArguments()).courseId
        val collaboratorIds = AddCollaboratorFragmentArgs.fromBundle(requireArguments()).collaboratorIds

        viewModel.courseId.value = courseId
        viewModel.collaboratorIds.value = collaboratorIds.toList()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_collaborator,
            container,
            false
        )

        progressBar = binding.root.findViewById(R.id.addCollaboratorProgressBar)

        val adapter = UserAdapter()

        viewModel.filteredUsers.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        adapter.onUserItemClick = {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            builder.setTitle(getString(R.string.confirm_add_collaborator, it.displayName))

            builder.setCancelable(true)

            builder.setPositiveButton(R.string.yes) { _, _ ->
                BusyFragment.show(parentFragmentManager)
                lifecycleScope.launch {
                    val addCollaboratorStatus = viewModel.addCollaborator(it.id)
                    BusyFragment.hide()
                    if (addCollaboratorStatus == AddCollaboratorStatus.SUCCESS) {
                        viewModel.collaboratorIds.postValue(viewModel.collaboratorIds.value!!.plus(listOf(it.id)))
                        Toast.makeText(context, getString(R.string.add_collaborator_succeeded, it.displayName), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, R.string.add_collaborator_failed, Toast.LENGTH_LONG).show()
                    }
                }
            }

            builder.setNegativeButton(R.string.no) { _, _ ->
            }

            builder.show()
        }

        binding.addCollaboratorViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.usersList.adapter = adapter

        recyclerView = binding.root.findViewById(R.id.usersList)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager
                    val size = viewModel.filteredUsers.value!!.size
                    if (llm.findLastVisibleItemPosition() > size - 5 && !loading) {
                        progressBar.visibility = View.VISIBLE
                        loading = true
                        lifecycleScope.launch {
                            val getUsersStatus : GetUsersStatus = viewModel.addUsers()
                            if (getUsersStatus == GetUsersStatus.FAIL)
                                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                            if (getUsersStatus == GetUsersStatus.NOT_FOUND)
                                Toast.makeText(context, R.string.there_is_no_more_users, Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.INVISIBLE
                            loading = false
                        }
                    }
                }
            }
        })

        lifecycleScope.launch { getUsers() }

        return binding.root
    }

    private fun getUsers() {
        if (!loading) {
            loading = true
            BusyFragment.show(parentFragmentManager)
            lifecycleScope.launch {
                val getUsersStatus : GetUsersStatus = viewModel.getUsers()
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