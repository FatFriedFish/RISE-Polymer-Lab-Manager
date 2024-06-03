package edu.sefs.risepolylab

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import edu.sefs.risepolylab.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.updateUser()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_searchinv, R.id.nav_editinv, R.id.nav_manage
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //log in
        AuthInit(viewModel, signInLauncher)

        //set the nav_header contents
        viewModel.observeUserName().observe(this){
            navView.getHeaderView(0).findViewById<TextView>(R.id.header_username).text = it
        }
        viewModel.observeEmail().observe(this){
            navView.getHeaderView(0).findViewById<TextView>(R.id.header_useremail).text = it
        }


        //hide the manage tabs for users, only show for admin
        viewModel.observeUid().observe(this){
            if (it != "rd3vKzA9UOObC3whvZpDlkpyvRI2"){
                Log.d("Not Admin", it)
                hideForUser()
            }else{
                Log.d("Admin", it)
                showForAdmin()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_settings ->{
                viewModel.signOut()
                AuthInit(viewModel, signInLauncher)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun hideForUser(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.findItem(R.id.nav_manage).isVisible = false
    }
    private fun showForAdmin(){
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.findItem(R.id.nav_manage).isVisible = true
    }
}