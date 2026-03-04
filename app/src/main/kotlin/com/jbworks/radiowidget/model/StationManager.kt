package com.jbworks.radiowidget.model

/**
 * Manages the list of available radio stations
 */
object StationManager {
    private const val BASE_URL = "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/radio"

    private fun buildStreamUrl(stationPath: String): String {
        return "$BASE_URL/$stationPath/live.livx/playlist.m3u8?renditions"
    }

    val stations = listOf(
        RadioStation(
            id = "kan_reshet_bet",
            name = "כאן ב",
            frequency = "95.0 FM",
            streamUrl = buildStreamUrl("kan_reshet_bet")
        ),
        RadioStation(
            id = "kan_gimmel",
            name = "כאן גימל",
            frequency = "105.5 FM",
            streamUrl = buildStreamUrl("kan_gimmel")
        ),
        RadioStation(
            id = "kan_88",
            name = "כאן 88",
            frequency = "88.5 FM",
            streamUrl = buildStreamUrl("kan_88")
        ),
        RadioStation(
            id = "kan_moreshet",
            name = "כאן מורשת",
            frequency = "100.7 FM",
            streamUrl = buildStreamUrl("kan_moreshet")
        ),
        RadioStation(
            id = "kan_kol_hamusica",
            name = "כאן קול המוזיקה",
            frequency = "98.5 FM",
            streamUrl = buildStreamUrl("kan_kol_hamusica")
        ),
        RadioStation(
            id = "makan",
            name = "מכאן",
            frequency = "99.3 FM",
            streamUrl = buildStreamUrl("makan")
        ),
        RadioStation(
            id = "kan_tarbut",
            name = "כאן תרבות",
            frequency = "104.9 FM",
            streamUrl = buildStreamUrl("kan_tarbut")
        ),
        RadioStation(
            id = "kan_reka",
            name = "כאן רקע",
            frequency = "101.8 FM",
            streamUrl = buildStreamUrl("kan_reka")
        )
    )

    /**
     * Find a station by its ID
     */
    fun getStationById(id: String?): RadioStation? {
        return stations.find { it.id == id }
    }

    /**
     * Get the first station (default)
     */
    fun getDefaultStation(): RadioStation = stations.first()
}
