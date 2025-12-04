package com.example.bakewise

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.bakewise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ask permission for notifications on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("navigate_to_step", false) == true) {
            val recipeId = intent.getIntExtra("recipeId", -1)
            val stepIndex = intent.getIntExtra("stepIndex", -1)
            val scheduleName = intent.getStringExtra("scheduleName")
            
            if (scheduleName != null) {
                CurrentBakeSession.scheduleName = scheduleName
            }

            if (recipeId != -1 && stepIndex != -1) {
                // Navigate to the specific step
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                val navController = navHostFragment?.navController

                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", stepIndex)
                }
                
                // We might need to pop everything and start fresh or navigate from current destination
                // Ideally, navigate to the step fragment
                navController?.navigate(R.id.recipeStepFragment, bundle)
            }
        }
    }
}
