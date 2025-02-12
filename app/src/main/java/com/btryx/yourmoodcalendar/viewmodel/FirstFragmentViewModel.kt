package com.btryx.yourmoodcalendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.database.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class FirstFragmentViewModel @Inject constructor(private val moodRepository: MoodRepository) : ViewModel() {

    private val _happyDays = MutableLiveData<List<LocalDate>>()
    val happyDays: LiveData<List<LocalDate>> = _happyDays

    private val _sadDays = MutableLiveData<List<LocalDate>>()
    val sadDays: LiveData<List<LocalDate>> = _sadDays

    private val _angryDays = MutableLiveData<List<LocalDate>>()
    val angryDays: LiveData<List<LocalDate>> = _angryDays

    private val _confidentDays = MutableLiveData<List<LocalDate>>()
    val confidentDays: LiveData<List<LocalDate>> = _confidentDays

    private val _boredDays = MutableLiveData<List<LocalDate>>()
    val boredDays: LiveData<List<LocalDate>> = _boredDays

    private val _tiredDays = MutableLiveData<List<LocalDate>>()
    val tiredDays: LiveData<List<LocalDate>> = _tiredDays

    private val _fineDays = MutableLiveData<List<LocalDate>>()
    val fineDays: LiveData<List<LocalDate>> = _fineDays

    val calendarData = MediatorLiveData<CalendarData>().apply {
        addSource(_happyDays) { happy ->
            value = CalendarData(happy, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, _tiredDays.value,_boredDays.value )
        }
        addSource(_sadDays) { sad ->
            value = CalendarData(_happyDays.value, sad, _angryDays.value, _confidentDays.value, _fineDays.value, _tiredDays.value, _boredDays.value )
        }
        addSource(_angryDays) { angry ->
            value = CalendarData(_happyDays.value, _sadDays.value, angry, _confidentDays.value, _fineDays.value, _tiredDays.value, _boredDays.value )
        }
        addSource(_confidentDays) { confident ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, confident, _fineDays.value, _tiredDays.value, _boredDays.value )
        }
        addSource(_fineDays) { fine ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, fine, _tiredDays.value, _boredDays.value )
        }
        addSource(_tiredDays) { tired ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, tired, _boredDays.value )
        }
        addSource(_boredDays) { bored ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, _tiredDays.value, bored )
        }
    }


    fun fetchMoodsByMonth(month: YearMonth) {
        viewModelScope.launch {
            val monthString = if (month.monthValue < 10) "0${month.monthValue}" else month.monthValue.toString()
            val dates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.HAPPY)
            val happyDates = dates.map { LocalDate.parse(it.day) }
            _happyDays.postValue(happyDates)

            val sadDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.SAD)
            val sadDays = sadDates.map { LocalDate.parse(it.day) }
            _sadDays.postValue(sadDays)

            val angryDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.ANGRY)
            val angryDays = angryDates.map { LocalDate.parse(it.day) }
            _angryDays.postValue(angryDays)

            val boredDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.BORED)
            val boredDays = boredDates.map { LocalDate.parse(it.day) }
            _boredDays.postValue(boredDays)

            val fineDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.FINE)
            val fineDays = fineDates.map { LocalDate.parse(it.day) }
            _fineDays.postValue(fineDays)

            val tiredDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.TIRED)
            val tiredDays = tiredDates.map { LocalDate.parse(it.day) }
            _tiredDays.postValue(tiredDays)

            val confidentDates = moodRepository.getMoodsByMonthAndType(monthString, Mood.MoodType.CONFIDENT)
            val confidentDays = confidentDates.map { LocalDate.parse(it.day) }
            _confidentDays.postValue(confidentDays)
        }
    }
}

data class CalendarData(
    val happyDays: List<LocalDate>?,
    val sadDays: List<LocalDate>?,
    val angryDays: List<LocalDate>?,
    val confidentDays: List<LocalDate>?,
    val fineDays: List<LocalDate>?,
    val tiredDays: List<LocalDate>?,
    val boredDays: List<LocalDate>?,
)