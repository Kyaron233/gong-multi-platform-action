package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky31.gongmultiplatform.data.repository.PublicDataRepositoryImpl
import com.sky31.gongmultiplatform.model.ClassroomData
import com.sky31.gongmultiplatform.network.repository.PublicRepositoryImpl
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.NetworkResult
import com.sky31.gongmultiplatform.util.codeToDataState
import com.sky31.gongmultiplatform.util.safeApiCallsSequential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ClassroomViewModel: ViewModel(), KoinComponent {
    private val publicDataRepository: PublicDataRepositoryImpl by inject()
    private val publicRepository: PublicRepositoryImpl by inject()

    private val _todayClassroomMap = MutableStateFlow<Map<String, List<ClassroomData.ClassroomInfo>>?>(null)
    val todayClassroomMap = _todayClassroomMap.asStateFlow()

    private val _tomorrowClassroomMap = MutableStateFlow<Map<String, List<ClassroomData.ClassroomInfo>>?>(null)
    val tomorrowClassroomMap = _tomorrowClassroomMap.asStateFlow()


    private val _todayDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val todayDate = _todayDate.asStateFlow()

    init {
        viewModelScope.launch {
            getTodayClassroomFromLocal()
            getTomorrowClassroomFromLocal()

            update()
        }
    }

    suspend fun update() {
        val results = safeApiCallsSequential(
            calls = listOf(
                { updateTodayClassroom() },
                { updateTomorrowClassroom() }
            )
        )
    }

    private suspend fun getTodayClassroomFromLocal() {
        publicDataRepository.getTodayClassroom()?.let {
            _todayClassroomMap.value = it.classrooms
        }
    }

    suspend fun updateTodayClassroom(): DataState {
        when(val result = publicRepository.getTodayClassroom()) {
            is NetworkResult.Success -> {
                publicDataRepository.updatePublicData(
                    todayClassroom = result.data
                )

                getTodayClassroomFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }

    private suspend fun getTomorrowClassroomFromLocal() {
        publicDataRepository.getTomorrowClassroom()?.let {
            _tomorrowClassroomMap.value = it.classrooms
        }
    }

    suspend fun updateTomorrowClassroom(): DataState {
        when(val result = publicRepository.getTomorrowClassroom()) {
            is NetworkResult.Success -> {
                publicDataRepository.updatePublicData(
                    tomorrowClassroom = result.data
                )

                getTomorrowClassroomFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }
}