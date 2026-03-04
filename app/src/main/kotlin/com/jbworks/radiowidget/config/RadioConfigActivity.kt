package com.jbworks.radiowidget.config

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jbworks.radiowidget.R
import com.jbworks.radiowidget.model.RadioPreferences
import com.jbworks.radiowidget.model.RadioStation
import com.jbworks.radiowidget.model.StationManager
import com.jbworks.radiowidget.widget.RadioWidget

/**
 * Configuration activity for selecting radio station when adding widget
 */
class RadioConfigActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var selectedStationId: String? = null
    private lateinit var adapter: StationAdapter

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionRationale()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        // Set result to CANCELED initially
        setResult(RESULT_CANCELED)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Get widget ID from intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If invalid widget ID, finish
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.stations_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StationAdapter(StationManager.stations) { station ->
            selectedStationId = station.id
        }
        recyclerView.adapter = adapter

        // Set default selection
        selectedStationId = StationManager.getDefaultStation().id
        adapter.setSelectedStation(selectedStationId!!)

        // Set up save button
        findViewById<Button>(R.id.save_button).setOnClickListener {
            saveConfiguration()
        }

        // Set up cancel button
        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            finish()
        }
    }

    private fun saveConfiguration() {
        selectedStationId?.let { stationId ->
            // Save station preference
            RadioPreferences.setWidgetStation(this, appWidgetId, stationId)

            // Update widget
            val appWidgetManager = AppWidgetManager.getInstance(this)
            RadioWidget.updateAllWidgets(this)

            // Return success
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required_title)
            .setMessage(R.string.permission_required_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Adapter for station selection
     */
    private class StationAdapter(
        private val stations: List<RadioStation>,
        private val onStationSelected: (RadioStation) -> Unit
    ) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

        private var selectedStationId: String = ""

        fun setSelectedStation(stationId: String) {
            selectedStationId = stationId
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.station_item, parent, false)
            return StationViewHolder(view)
        }

        override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
            val station = stations[position]
            holder.bind(station, station.id == selectedStationId) {
                selectedStationId = station.id
                notifyDataSetChanged()
                onStationSelected(station)
            }
        }

        override fun getItemCount() = stations.size

        class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val radioButton: RadioButton = itemView.findViewById(R.id.station_radio_button)
            private val nameText: TextView = itemView.findViewById(R.id.station_name_text)
            private val frequencyText: TextView = itemView.findViewById(R.id.station_frequency_text)

            fun bind(station: RadioStation, isSelected: Boolean, onClick: () -> Unit) {
                nameText.text = station.name
                frequencyText.text = station.frequency
                radioButton.isChecked = isSelected

                itemView.setOnClickListener {
                    onClick()
                }
                radioButton.setOnClickListener {
                    onClick()
                }
            }
        }
    }
}
