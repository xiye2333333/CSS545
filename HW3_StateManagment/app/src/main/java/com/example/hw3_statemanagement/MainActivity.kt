package com.example.hw3_statemanagement

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity



class MainActivity : ComponentActivity() {

    private var counter: Int = 0
    private lateinit var counterTextView: TextView
    private lateinit var incrementButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        counterTextView = findViewById(R.id.counterTextView)
        incrementButton = findViewById(R.id.incrementButton)

        // Restore state if available
        savedInstanceState?.let {
            counter = it.getInt("counter_key", 0)
            println("Restored counter value: $counter")
        }

        // Update the UI
        updateCounterText()

        incrementButton.setOnClickListener {
            counter++
            updateCounterText()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the counter value
        outState.putInt("counter_key", counter)
        println("onSaveInstanceState")
    }

    private fun updateCounterText() {
        counterTextView.text = "Counter: $counter"
        println("updateCounterText")
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        counter = savedInstanceState.getInt("counter_key", 0)
//        println("onRestoreInstanceState")
//    }
//
//    override fun onStart() {
//        super.onStart()
//        println("onStart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        println("onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        println("onPause")
//    }
}