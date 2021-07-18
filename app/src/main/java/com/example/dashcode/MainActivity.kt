package com.example.dashcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.dashcode.ui.main.MainFragment
import com.example.dashcode.ui.main.MainViewModel
import com.example.dashcode.ui.main.MainViewModelFactory


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    //Inflate action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    // Handle clicking of action bar items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settingsFragment -> {
                viewModel.onSettingsPressed()
                true
            }
            R.id.add_button -> {
                viewModel.onAddButtonClicked()
                true
            }
            else -> false
        }
    }
}