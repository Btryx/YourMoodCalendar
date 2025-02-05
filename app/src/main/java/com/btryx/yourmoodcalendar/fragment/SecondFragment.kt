package com.btryx.yourmoodcalendar.fragment

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.databinding.FragmentSecondBinding
import com.btryx.yourmoodcalendar.viewmodel.SecondFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SecondFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup mood dropdown
        val moodTypes = Mood.MoodType.values()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            moodTypes.map { it.name }
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerMood.adapter = adapter

        // Listen to mood selection
        binding.spinnerMood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setMood(moodTypes[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listen to description changes
        binding.editTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { viewModel.setDescription(it) }
            }
        })

        viewModel.getMoodForToday().observe(viewLifecycleOwner) { mood ->
            if (mood != null) {
                val moodPosition = Mood.MoodType.entries.toTypedArray().indexOfFirst { it == mood.type }
                binding.spinnerMood.setSelection(moodPosition)
                binding.editTextDescription.setText(mood.description)
            }
        }

        // Save button click listener
        binding.buttonSave.setOnClickListener {
            if (validateInput()) {
                viewModel.createMoodForToday {
                    requireActivity().runOnUiThread {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        return when {
            binding.spinnerMood.selectedItemPosition == AdapterView.INVALID_POSITION -> {
                Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}