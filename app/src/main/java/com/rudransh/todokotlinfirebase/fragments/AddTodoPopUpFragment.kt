package com.rudransh.todokotlinfirebase.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.rudransh.todokotlinfirebase.R
import com.rudransh.todokotlinfirebase.databinding.FragmentAddTodoPopUpBinding

class AddTodoPopUpFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTodoPopUpBinding
    private lateinit var listener: DialogNextButtonClickListener

    fun setListener(listener: DialogNextButtonClickListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTodoPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerEvents()
    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()) {
                listener.onSaveTask(todoTask,binding.todoEt)
            } else {
                Toast.makeText(
                    context,
                    "Please type some task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }

    interface DialogNextButtonClickListener{
        fun onSaveTask(todo: String,todoEt : TextInputEditText)
    }
}