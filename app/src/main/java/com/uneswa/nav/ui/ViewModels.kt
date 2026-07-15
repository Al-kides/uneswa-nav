package com.uneswa.nav.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uneswa.nav.data.Location
import com.uneswa.nav.data.LocationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeVM(private val repo: LocationRepo) : ViewModel() {

    private val _q = MutableStateFlow("")
    private val _results = MutableStateFlow(repo.all.toList())

    val q       = _q.asStateFlow()
    val results = _results.asStateFlow()

    fun onSearch(q: String) {
        _q.update { q }
        _results.update { repo.search(q).toList() }
    }
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
        cls.isAssignableFrom(HomeVM::class.java)       -> HomeVM(repo) as T
        cls.isAssignableFrom(DirectionsVM::class.java) -> DirectionsVM(repo, locId) as T
        else -> throw IllegalArgumentException("Unknown VM: ${cls.name}")
    }
}
