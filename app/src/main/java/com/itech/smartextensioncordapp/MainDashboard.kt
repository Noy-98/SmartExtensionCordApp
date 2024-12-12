package com.itech.smartextensioncordapp

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainDashboard : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var tempValue: TextView
    private lateinit var switchControl: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("SmartExtensionCordDevice")

        // Initialize UI components
        tempValue = findViewById(R.id.tempValue)
        switchControl = findViewById(R.id.switch_control)
        // Listen for temperature changes in Firebase
        database.child("Sensor").child("temperature").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get temperature value from Firebase
                val temperature = snapshot.getValue(Double::class.java)
                if (temperature != null) {
                    tempValue.text = String.format("%.1f", temperature)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                tempValue.text = "Error"
            }
        })

        // Listen for changes to the relay state in Firebase and update the switch UI
        database.child("Controls").child("channelRelayState").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(Int::class.java)
                switchControl.isChecked = state == 1
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })

        // Add listener to Switch to update Firebase on toggle
        switchControl.setOnCheckedChangeListener { _, isChecked ->
            val newValue = if (isChecked) 1 else 0
            database.child("Controls").child("channelRelayState").setValue(newValue)
        }

    }
}