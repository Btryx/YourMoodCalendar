package com.btryx.yourmoodcalendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btryx.yourmoodcalendar.database.entities.Mood
import com.btryx.yourmoodcalendar.database.repository.MoodRepository
import com.btryx.yourmoodcalendar.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SecondFragmentViewModel @Inject constructor(private val moodRepository: MoodRepository): ViewModel()  {
    private val _mood = MutableLiveData<Mood.MoodType>()
    val mood: LiveData<Mood.MoodType> get() = _mood

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description


    fun setMood(mood: Mood.MoodType) {
        _mood.value = mood
    }

    fun setDescription(desc: String) {
        _description.value = desc
    }

    fun createMoodForToday(onCompletion: (() -> Unit)? = null) {
        val today = LocalDate.now()
        val date = TimeUtils.localDateToString(today)
        val mood = Mood(
            day = date,
            description = description.value ?: "",
            type = mood.value ?: Mood.MoodType.FINE // Set a default if null ( shouldn't happen)
        )

        viewModelScope.launch {
            moodRepository.insertMood(mood)
            onCompletion?.invoke()
        }
    }

    fun getMoodForToday(): MutableLiveData<Mood> {
        val result = MutableLiveData<Mood>()
        val today = LocalDate.now()
        val date = TimeUtils.localDateToString(today)

        viewModelScope.launch {
            val mood = moodRepository.getMoodByDate(date)
            result.postValue(mood)
        }
        return result
    }
}