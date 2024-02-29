package com.prafull.cleanteams

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.prafull.cleanteams.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * Just using a quick concept hence saving in Shared Preference; else a DB would be better choice.
     */
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("SP", Context.MODE_PRIVATE)

        binding.apply {
            input1.setText(sharedPreferences.getString("title", ""))
            input2.setText(sharedPreferences.getString("keyword", ""))
            input1.addTextChangedListener({_,_,_,_ -> },{newText,_,_,_ ->
                newText?.let {
                    sharedPreferences.edit().putString("title", it.toString()).apply()
                }
            })

            input2.addTextChangedListener({_,_,_,_ -> },{newText,_,_,_ ->
                newText?.let {
                    sharedPreferences.edit().putString("keyword", it.toString()).apply()
                }
            })

            fab.setOnClickListener {
                val lastValue = sharedPreferences.getBoolean("active", false)
                sharedPreferences.edit().putBoolean("active", lastValue.not()).apply()
                checkStatusAndUpdateFabIcon()
                var message = ""
                if(lastValue) {
                    message = "Filtering Stopped"
                } else {
                    message = "Filtering Started"
                }
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
        checkStatusAndUpdateFabIcon()
    }

    private fun checkStatusAndUpdateFabIcon() {
        if(sharedPreferences.getBoolean("active", false)) {
            binding.fab.setImageDrawable(resources.getDrawable(R.drawable.pause))
        } else {
            binding.fab.setImageDrawable(resources.getDrawable(R.drawable.play_arrow_black_24dp))
        }
    }
}
