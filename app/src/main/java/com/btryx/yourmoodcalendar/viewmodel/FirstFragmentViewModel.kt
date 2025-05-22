package com.btryx.yourmoodcalendar.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.database.repository.MoodRepository
import com.btryx.yourmoodcalendar.utils.TimeUtils
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

    private val _stressedDays = MutableLiveData<List<LocalDate>>()
    val stressedDays: LiveData<List<LocalDate>> = _stressedDays

    private val _fineDays = MutableLiveData<List<LocalDate>>()
    val fineDays: LiveData<List<LocalDate>> = _fineDays

    val calendarData = MediatorLiveData<CalendarData>().apply {
        addSource(_happyDays) { happy ->
            value = CalendarData(happy, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, _stressedDays.value,_boredDays.value )
        }
        addSource(_sadDays) { sad ->
            value = CalendarData(_happyDays.value, sad, _angryDays.value, _confidentDays.value, _fineDays.value, _stressedDays.value, _boredDays.value )
        }
        addSource(_angryDays) { angry ->
            value = CalendarData(_happyDays.value, _sadDays.value, angry, _confidentDays.value, _fineDays.value, _stressedDays.value, _boredDays.value )
        }
        addSource(_confidentDays) { confident ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, confident, _fineDays.value, _stressedDays.value, _boredDays.value )
        }
        addSource(_fineDays) { fine ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, fine, _stressedDays.value, _boredDays.value )
        }
        addSource(_stressedDays) { stressed ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, stressed, _boredDays.value )
        }
        addSource(_boredDays) { bored ->
            value = CalendarData(_happyDays.value, _sadDays.value, _angryDays.value, _confidentDays.value, _fineDays.value, _stressedDays.value, bored )
        }
    }


    fun fetchMoods() {
        Log.d("fetchMoodsByMonth", "In fetchMoodsByMonth")
        viewModelScope.launch {
            val dates = moodRepository.getMoodsByType(Mood.MoodType.HAPPY)
            val happyDates = dates.map { LocalDate.parse(it.day) }
            _happyDays.postValue(happyDates)

            val sadDates = moodRepository.getMoodsByType(Mood.MoodType.SAD)
            val sadDays = sadDates.map { LocalDate.parse(it.day) }
            _sadDays.postValue(sadDays)

            val angryDates = moodRepository.getMoodsByType(Mood.MoodType.ANGRY)
            val angryDays = angryDates.map { LocalDate.parse(it.day) }
            _angryDays.postValue(angryDays)

            val boredDates = moodRepository.getMoodsByType(Mood.MoodType.BORED)
            val boredDays = boredDates.map { LocalDate.parse(it.day) }
            _boredDays.postValue(boredDays)

            val fineDates = moodRepository.getMoodsByType(Mood.MoodType.FINE)
            val fineDays = fineDates.map { LocalDate.parse(it.day) }
            _fineDays.postValue(fineDays)

            val tiredDates = moodRepository.getMoodsByType(Mood.MoodType.STRESSED)
            val tiredDays = tiredDates.map { LocalDate.parse(it.day) }
            _stressedDays.postValue(tiredDays)

            val confidentDates = moodRepository.getMoodsByType(Mood.MoodType.CONFIDENT)
            val confidentDays = confidentDates.map { LocalDate.parse(it.day) }
            _confidentDays.postValue(confidentDays)
        }
    }

    fun getMoodForDate(day: LocalDate): LiveData<Mood?> {
        val result = MutableLiveData<Mood>()
        val date = TimeUtils.localDateToString(day)

        viewModelScope.launch {
            val mood = moodRepository.getMoodByDate(date)
            result.postValue(mood)
        }
        return result
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