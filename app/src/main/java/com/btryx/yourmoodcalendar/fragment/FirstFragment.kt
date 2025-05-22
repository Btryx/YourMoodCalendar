package com.btryx.yourmoodcalendar.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.btryx.yourmoodcalendar.R
import com.btryx.yourmoodcalendar.databinding.Calendar2CalendarDayBinding
import com.btryx.yourmoodcalendar.databinding.FragmentFirstBinding
import com.btryx.yourmoodcalendar.viewmodel.FirstFragmentViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val monthCalendarView: CalendarView get() = binding.calendar
    private val viewModel: FirstFragmentViewModel by viewModels()

    private var happyDays: List<LocalDate> = emptyList()
    private var sadDays: List<LocalDate> = emptyList()
    private var angryDays: List<LocalDate> = emptyList()
    private var confidentDays: List<LocalDate> = emptyList()
    private var fineDays: List<LocalDate> = emptyList()
    private var boredDays: List<LocalDate> = emptyList()
    private var stressedDays: List<LocalDate> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10) // todo, first month of usage
        val endMonth = currentMonth.plusMonths(0)

        viewModel.calendarData.observe(viewLifecycleOwner) {
            if (it.happyDays != null && it.sadDays != null && it.angryDays != null && it.confidentDays != null
                && it.fineDays != null && it.boredDays != null && it.stressedDays != null) {
                this.happyDays = it.happyDays
                this.sadDays = it.sadDays
                this.angryDays = it.angryDays
                this.confidentDays = it.confidentDays
                this.fineDays = it.fineDays
                this.stressedDays = it.stressedDays
                this.boredDays = it.boredDays
                setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
            }
        }
        viewModel.fetchMoodsByMonth(currentMonth)
    }


    private fun DayOfWeek.displayText(uppercase: Boolean = false, narrow: Boolean = false): String {
        val style = if (narrow) TextStyle.NARROW else TextStyle.SHORT
        return getDisplayName(style, Locale.ENGLISH).let { value ->
            if (uppercase) value.uppercase(Locale.ENGLISH) else value
        }
    }

    private fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.ENGLISH)
    }

    private fun TextView.setTextColorRes(@ColorRes color: Int) =
        setTextColor(context.getColorCompat(color))


    private fun Context.getColorCompat(@ColorRes color: Int) =
        ContextCompat.getColor(this, color)

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = Calendar2CalendarDayBinding.bind(view).dayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
            }
        }
        monthCalendarView.monthScrollListener = { updateTitle() }
        monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = null
        if (isSelectable) {
            when {
                happyDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.happy)
                }
                sadDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.sad)
                }
                angryDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.angry)
                }
                confidentDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.confident)
                }
                fineDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.fine)
                }
                boredDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.bored)
                }
                stressedDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.stressed)
                }
                else -> {
                    textView.text = date.dayOfMonth.toString()
                    textView.setBackgroundResource(R.drawable.nothing)
                }
            }
        } else {
            textView.text = null
            textView.setBackgroundResource(android.R.color.transparent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text = month.month.displayText(short = false)
    }

    private fun dateClicked(date: LocalDate) {
        val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(date.toString())
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
