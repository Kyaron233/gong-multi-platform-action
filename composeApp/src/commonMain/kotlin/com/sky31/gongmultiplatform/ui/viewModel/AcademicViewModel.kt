package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky31.gongmultiplatform.data.repository.AcademicDataRepositoryImpl
import com.sky31.gongmultiplatform.model.RankData
import com.sky31.gongmultiplatform.model.ScoreData
import com.sky31.gongmultiplatform.network.repository.AcademicRepositoryImpl
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.NetworkResult
import com.sky31.gongmultiplatform.util.codeToDataState
import com.sky31.gongmultiplatform.util.safeApiCallsSequential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AcademicViewModel: ViewModel(), KoinComponent {
    private val academicDataRepository: AcademicDataRepositoryImpl by inject()
    private val academicRepository: AcademicRepositoryImpl by inject()

    private val _majorScore = MutableStateFlow<ScoreData?>(null)
    val majorScore = _majorScore.asStateFlow()

    private val _minorScore = MutableStateFlow<ScoreData?>(null)
    val minorScore = _minorScore.asStateFlow()

    private val _compulsoryRank = MutableStateFlow<RankData?>(null)
    val compulsoryRank = _compulsoryRank.asStateFlow()

    private val _totalRank = MutableStateFlow<RankData?>(null)
    val totalRank = _totalRank.asStateFlow()

    private val majorAcademicInfoState = MutableStateFlow<DataState>(DataState.Uninitialized)
    private val minorAcademicInfoState = MutableStateFlow<DataState>(DataState.Uninitialized)
    private val compulsoryRankState = MutableStateFlow<DataState>(DataState.Uninitialized)
    private val totalRankState = MutableStateFlow<DataState>(DataState.Uninitialized)

    init {
        viewModelScope.launch {
            getMajorScoreFromLocal()
            getMinorScoreFromLocal()
            getTotalRankFromLocal()
            getCompulsoryRankFromLocal()
        }
    }

    suspend fun update() {
        val results = safeApiCallsSequential(
            calls = listOf(
                { updateMajorAcademicInfo() },
                { updateMinorAcademicInfo() },
                { updateTotalRank() },
                { updateCompulsoryRank() }
            )
        )
    }

    private suspend fun getMajorScoreFromLocal() {
        academicDataRepository.getMajorScore()?.let {
            _majorScore.value = it
        }
    }

    suspend fun updateMajorAcademicInfo(): DataState {
        when(val result = academicRepository.getMajorAcademicInfo()) {
            is NetworkResult.Success -> {
                academicDataRepository.updateAcademicData(
                    majorScore = result.data
                )

                getMajorScoreFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }

    private suspend fun getMinorScoreFromLocal() {
        academicDataRepository.getMinorScore()?.let {
            _minorScore.value = it
        }
    }

    suspend fun updateMinorAcademicInfo(): DataState {
        when(val result = academicRepository.getMinorAcademicInfo()) {
            is NetworkResult.Success -> {
                academicDataRepository.updateAcademicData(
                    minorScore = result.data
                )

                getMinorScoreFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }

    private suspend fun getTotalRankFromLocal() {
        academicDataRepository.getTotalRank()?.let {
            _totalRank.value = it
        }
    }

    suspend fun updateTotalRank(): DataState {
        when(val result = academicRepository.getTotalRank()) {
            is NetworkResult.Success -> {
                academicDataRepository.updateAcademicData(
                    totalRank = result.data
                )

                getTotalRankFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }

    private suspend fun getCompulsoryRankFromLocal() {
        academicDataRepository.getCompulsoryRank()?.let {
            _compulsoryRank.value = it
        }
    }

    suspend fun updateCompulsoryRank(): DataState {
        when(val result = academicRepository.getCompulsoryRank()) {
            is NetworkResult.Success -> {
                academicDataRepository.updateAcademicData(
                    compulsoryRank = result.data
                )

                getCompulsoryRankFromLocal()

                return DataState.Newest
            }

            is NetworkResult.Error -> {
                return codeToDataState(result.code)
            }
        }
    }
}