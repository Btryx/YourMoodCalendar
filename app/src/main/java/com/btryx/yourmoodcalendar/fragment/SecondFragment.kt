package com.btryx.yourmoodcalendar.fragment

import com.btryx.yourmoodcalendar.R
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.databinding.FragmentSecondBinding
import com.btryx.yourmoodcalendar.viewmodel.SecondFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.YearMonth
import java.util.Locale

@AndroidEntryPoint
class SecondFragment : DialogFragment() {

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

        setupMoodDropdown()
        setupDescriptionListener()
        setupSaveButton()
        observeMoodData()
    }

    private fun setupMoodDropdown() {
        val moodTypes = Mood.MoodType.values()
        val moodLabels = moodTypes.map {
            when (it) {
                Mood.MoodType.HAPPY -> "Happy"
                Mood.MoodType.SAD -> "Sad"
                Mood.MoodType.ANGRY -> "Angry"
                Mood.MoodType.CONFIDENT -> "Confident"
                Mood.MoodType.FINE -> "Fine"
                Mood.MoodType.BORED -> "Bored"
                Mood.MoodType.TIRED -> "Tired"
            }
        }

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.mood_dropdown_item,
            moodLabels
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.mood_dropdown_item, parent, false)

                val textView = view.findViewById<TextView>(R.id.text)
                val imageView = view.findViewById<ImageView>(R.id.icon)

                textView.text = moodLabels[position]
                imageView.setImageResource(when (moodTypes[position]) {
                    Mood.MoodType.HAPPY -> R.drawable.happy
                    Mood.MoodType.SAD -> R.drawable.sad
                    Mood.MoodType.ANGRY -> R.drawable.angry
                    Mood.MoodType.CONFIDENT -> R.drawable.confident
                    Mood.MoodType.FINE -> R.drawable.fine
                    Mood.MoodType.BORED -> R.drawable.bored
                    Mood.MoodType.TIRED -> R.drawable.tired
                })

                return view
            }
        }

        (binding.moodInputLayout.editText as? AutoCompleteTextView)?.let { autoCompleteTextView ->
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                viewModel.setMood(moodTypes[position])
            }
        }
    }

    private fun setupDescriptionListener() {
        binding.editTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { viewModel.setDescription(it) }
            }
        })
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateInput()) {
                viewModel.createMoodForToday {
                    (requireParentFragment() as? FirstFragment)
                        ?.viewModel
                        ?.fetchMoodsByMonth(YearMonth.now())
                    dismiss()
                }
            }
        }
    }

    private fun observeMoodData() {
        viewModel.getMoodForToday().observe(viewLifecycleOwner) { mood ->
            if (mood != null) {
                (binding.moodInputLayout.editText as? AutoCompleteTextView)?.setText(
                    mood.type.name.capitalize(), false
                )
                binding.editTextDescription.setText(mood.description)
            }
        }
    }

    private fun validateInput(): Boolean {
        val moodText = (binding.moodInputLayout.editText as? AutoCompleteTextView)?.text
        return when {
            moodText.isNullOrEmpty() -> {
                binding.moodInputLayout.error = "Please select a mood"
                false
            }
            else -> {
                binding.moodInputLayout.error = null
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}