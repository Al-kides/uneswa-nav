package com.uneswa.nav.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uneswa.nav.data.Location
import com.uneswa.nav.data.LocationRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class HomeVM(private val repo: LocationRepo) : ViewModel() {
    private val _q = MutableStateFlow("")
    val q = _q.asStateFlow()

    @OptIn(FlowPreview::class)
    val results = _q
        .debounce(300)
        .map { query ->
            withContext(Dispatchers.Default) {
                repo.search(query).toList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repo.all.toList())

    fun onSearch(query: String) {
        _q.value = query
    }
}

class LaptopRecommenderVM : ViewModel() {
    var selectedFaculty by mutableStateOf<String?>(null)
    var selectedProgramme by mutableStateOf<String?>(null)
    var showLayman by mutableStateOf(false)
}

class DirectionsVM(repo: LocationRepo, locId: String) : ViewModel() {
    val loc = repo.byId(locId)
    private val _idx = MutableStateFlow(0)
    val idx = _idx.asStateFlow()
    fun pick(i: Int) { _idx.update { i } }
}

class VMFactory(
    private val repo: LocationRepo,
    private val locId: String = ""
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(cls: Class<T>): T = when {
        cls.isAssignableFrom(HomeVM::class.java) -> HomeVM(repo) as T
        cls.isAssignableFrom(DirectionsVM::class.java) -> DirectionsVM(repo, locId) as T
        cls.isAssignableFrom(LaptopRecommenderVM::class.java) -> LaptopRecommenderVM() as T
        else -> throw IllegalArgumentException("Unknown VM: ${cls.name}")
    }
}
