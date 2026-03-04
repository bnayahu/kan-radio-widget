package com.jbworks.radiowidget.model

/**
 * Data class representing a radio station
 */
data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val streamUrl: String
)
