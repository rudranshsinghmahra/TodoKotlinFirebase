package com.rudransh.todokotlinfirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rudransh.todokotlinfirebase.databinding.FragmentHomeBinding
import com.rudransh.todokotlinfirebase.utils.TodoAdapter
import com.rudransh.todokotlinfirebase.utils.TodoData

class HomeFragment : Fragment(), AddTodoPopUpFragment.DialogNextButtonClickListener,
    TodoAdapter.TodoAdapterClickInterface {
    private lateinit var navControl: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popUpFragment: AddTodoPopUpFragment
    private lateinit var adapter: TodoAdapter
    private lateinit var todoList: MutableList<TodoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                todoList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todo = taskSnapshot.key?.let {
                        TodoData(it, taskSnapshot.value.toString())
                    }

                    if (todo != null) {
                        todoList.add(todo)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener {
            popUpFragment = AddTodoPopUpFragment()
            popUpFragment.setListener(this)
            popUpFragment.show(
                childFragmentManager,
                "AddTodoPopUpFragment"
            )
        }
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        todoList = mutableListOf()
        adapter = TodoAdapter(todoList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseReference.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    context,
                    "Todo saved successfully !!",
                    Toast.LENGTH_SHORT
                ).show()
                todoEt.text = null
            } else {
                Toast.makeText(
                    context,
                    it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            popUpFragment.dismiss()
        }
    }

    override fun onDeleteButtonClicked(todoData: TodoData) {
        databaseReference.child(todoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    context,
                    "Deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onEditButtonClicked(todoData: TodoData) {
        TODO("Not yet implemented")
    }
}