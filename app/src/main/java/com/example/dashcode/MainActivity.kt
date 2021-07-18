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
        //Disable default title on action bar (it's on left side)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        val navController = this.findNavController(R.id.nav_host_fragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    //Inflate action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    // Handle clicking of action bar items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return NavigationUI.onNavDestinationSelected(item, require)
        return when(item.itemId) {
            //TODO: implement settings
            R.id.settingsFragment -> {
//                findNavController(R.id.settingsFragment).navigate(R.id.action_mainFragment_to_settingsFragment)
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