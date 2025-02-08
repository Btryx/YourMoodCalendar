package com.btryx.yourmoodcalendar.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.kizitonwose.calendar.core.CalendarMonth
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
    private var tiredDays: List<LocalDate> = emptyList()

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
                && it.fineDays != null && it.boredDays != null && it.tiredDays != null) {
                this.happyDays = it.happyDays
                this.sadDays = it.sadDays
                this.angryDays = it.angryDays
                this.confidentDays = it.confidentDays
                this.fineDays = it.fineDays
                this.tiredDays = it.tiredDays
                this.boredDays = it.boredDays
                setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
                setupMoodCountView(currentMonth)
                Log.d("calendarData", "Happy days : ${this.happyDays}")
            }
        }


        binding.fab.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
            binding.fab.startAnimation(scaleAnimation)
            scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(LocalDate.now())
                    findNavController().navigate(action)
                }
            })
        }

        viewModel.fetchMoods()
    }

    private fun setupMoodCountView(month : YearMonth) {
        //todo: it might be faster to just get it from the database, check this later
        val moods = listOf(
            Triple(this.happyDays.count { YearMonth.from(it).equals(month); }, binding.happyCardView, binding.happyCountText),
            Triple(this.fineDays.count{ YearMonth.from(it).equals(month); }, binding.fineCardView, binding.fineCountText),
            Triple(this.sadDays.count{ YearMonth.from(it).equals(month); }, binding.sadCardView, binding.sadCountText),
            Triple(this.boredDays.count{ YearMonth.from(it).equals(month); }, binding.boredCardView, binding.boredCountText),
            Triple(this.tiredDays.count{ YearMonth.from(it).equals(month); }, binding.tiredCardView, binding.tiredCountText),
            Triple(this.angryDays.count{ YearMonth.from(it).equals(month); }, binding.angryCardView, binding.angryCountText),
            Triple(this.confidentDays.count{ YearMonth.from(it).equals(month); }, binding.confidentCardView, binding.confidentCountText)
        )

        for ((count, cardView, countText) in moods) {
            if (count > 0) {
                cardView.visibility = View.VISIBLE
                countText.text = getString(R.string.days, count.toString(), if (count == 1) "day" else "days")
            } else {
                cardView.visibility = View.GONE
            }
        }
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
                        dateClicked(date = day.date, textView)

                    }
                }
            }
        }
        Log.d("setupMonthCalendar", "In setupMonthCalendar")
        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
            }
        }
        monthCalendarView.monthScrollListener = { monthScroll(it) }
        monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun monthScroll(calendarMonth: CalendarMonth) {
        setupMoodCountView(calendarMonth.yearMonth)
        updateTitle()
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        Log.d("bindDate", "In binddate")
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
                tiredDays.contains(date) -> {
                    textView.setBackgroundResource(R.drawable.tired)
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

    private fun dateClicked(date: LocalDate, textView: TextView) {
        viewModel.getMoodForDate(date).observe(viewLifecycleOwner) { mood ->
            if (mood != null) {
                val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
                textView.startAnimation(scaleAnimation)
                scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        //todo: send the month of the date to secondFragment, so we come back to right month
                        val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(date)
                        findNavController().navigate(action)
                    }
                })
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
