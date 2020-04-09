package com.example.tubestatus.models

enum class TubeLineColours(
    val id: String,
    val backgroundColour: String,
    val whiteForegroundColour: Boolean = true
) {
    BAKERLOO("bakerloo", "#894e24"),
    CENTRAL("central", "#dc241f"),
    CIRCLE("circle", "#ffce00", false),
    DISTRICT("district", "#007229"),
    DLR("dlr", "#00afad"),
    HAMMERSMITH_AND_CITY("hammersmith-city", "#d799af", false),
    JUBILEE("jubilee", "#6a7278"),
    LONDON_OVERGROUND("london-overground", "#e86a10"),
    METROPOLITAN("metropolitan", "#751056"),
    NORTHERN("northern", "#000000"),
    PICCADILLY("piccadilly", "#0019a8"),
    TFL_RAIL("tfl-rail", "#0019a8", true),
    VICTORIA("victoria", "#00a0e2", false),
    WATERLOO_AND_CITY("waterloo-city", "#76d0bd", false),
}