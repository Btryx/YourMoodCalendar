package com.btryx.yourmoodcalendar.fragment

import com.btryx.yourmoodcalendar.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.databinding.FragmentSecondBinding
import com.btryx.yourmoodcalendar.utils.TimeUtils
import com.btryx.yourmoodcalendar.viewmodel.SecondFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SecondFragmentViewModel by viewModels()
    private val args: SecondFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)
    }

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
        val moodTypes = Mood.MoodType.entries.toTypedArray()
        val moodLabels = moodTypes.map { it.toLabel() }

        if (TimeUtils.localDateToString(args.selectedDate) != TimeUtils.localDateToString(LocalDate.now())) {
            binding.moodInputLayout.isEnabled = false
        } else {
            binding.moodInputLayout.isEnabled = true
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
                imageView.setImageResource(moodTypes[position].toDrawableResource())

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
        if (TimeUtils.localDateToString(args.selectedDate) != TimeUtils.localDateToString(LocalDate.now())) {
            binding.editTextDescription.isEnabled = false
        } else {
            binding.editTextDescription.isEnabled = true
            binding.editTextDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    s?.toString()?.let { viewModel.setDescription(it) }
                }
            })
        }
    }

    private fun setupSaveButton() {
        if (TimeUtils.localDateToString(args.selectedDate) != TimeUtils.localDateToString(LocalDate.now())) {
            binding.dialogTitle.text = TimeUtils.localDateToPrettyString(args.selectedDate)
            binding.buttonSave.visibility = View.GONE
        } else {
            binding.buttonSave.setOnClickListener {
                val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
                binding.buttonSave.startAnimation(scaleAnimation)
                scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        if (validateInput()) {
                            viewModel.createMoodForToday {
                                requireActivity().runOnUiThread {
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun observeMoodData() {
        viewModel.getMoodForDate(args.selectedDate).observe(viewLifecycleOwner) { mood ->
            if (mood != null) {
                (binding.moodInputLayout.editText as? AutoCompleteTextView)?.setText(
                    mood.type.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }, false
                )
                binding.editTextDescription.setText(mood.description)
                viewModel.setMood(mood.type)
                viewModel.setDescription(mood.description)
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

    //Gets the drawable resource for each emotion. Format: MoodType.EMOTION -> drawable.emotion
    fun Mood.MoodType.toDrawableResource(): Int {
        val drawableName = name.lowercase()

        return try {
            R.drawable::class.java.getField(drawableName).getInt(null)
        } catch (e: Exception) {
            R.drawable.fine // Default
        }
    }

    //Gets the label for each emotion. Format: MoodType.EMOTION -> Emotion
    private fun Mood.MoodType.toLabel(): String {
        val drawableName = name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        return try {
            drawableName
        } catch (e: Exception) {
            "Fine" // Default
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}