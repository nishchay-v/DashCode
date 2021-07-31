package com.example.dashcode.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.dashcode.database.getDatabase
import com.example.dashcode.repository.ContestsRepository
import com.example.dashcode.repository.AccountsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)

    private val accountsRepository = AccountsRepository(database)

    private val contestsRepository = ContestsRepository(database)

    val userAccounts = accountsRepository.accounts

    val cList = contestsRepository.contests

    val foundUser = accountsRepository.foundAccount

    // Load Contest List whenever app starts
    init {
        viewModelScope.launch {
            Log.i("MainViewModel", "Refreshing Contests Repo")
            contestsRepository.refreshUpcomingContests()
        }
    }

    // Change value when Add button is pressed to trigger popup
    private val _showPopup = MutableLiveData<Boolean>()
    val showPopup: LiveData<Boolean>
        get() = _showPopup

    private var contestListItemPosition: Int = 0

    fun onPlatformSelected(position: Int) {
        contestListItemPosition = position
    }

    fun addUser(handle: String) {
        when(contestListItemPosition) {
            1 -> addCodeforcesUser(handle)
            2 -> addCodechefUser(handle)
            3 -> addPlatformUser("leetcode.com", handle.toLowerCase(Locale.ROOT))
            4 -> addPlatformUser("atcoder.jp", handle.toLowerCase(Locale.ROOT))
            else -> Log.i("MainViewModel", "Please select a platform")
        }
    }

    private fun addCodeforcesUser(handle: String) {
        viewModelScope.launch {
            Log.i("MainViewModel", "Adding User using repository")
            accountsRepository.addCodeForcesAccount(handle)
        }
    }

    private fun addCodechefUser(handle: String) {
        viewModelScope.launch {
            Log.i("ChartViewModel", "Adding User using repository")
            accountsRepository.addCodeChefAccount(handle)
        }
    }

    private fun addPlatformUser(platform: String, handle: String) {
        viewModelScope.launch {
            accountsRepository.addClistAccount(platform, handle)
        }
    }

    fun onAddButtonClicked() {
        _showPopup.value = true
    }

    fun onUserAdded() {
        _showPopup.value = false
        accountsRepository.onAddUserComplete()
    }

    private val _navigateToSettings = MutableLiveData<Boolean>()
    val navigateToSettings: LiveData<Boolean>
        get() = _navigateToSettings

    fun onSettingsPressed() {
        _navigateToSettings.value = true
    }

    fun onNavigateToSettingsComplete() {
        _navigateToSettings.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}