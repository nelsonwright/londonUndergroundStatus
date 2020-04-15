package uk.co.nelsonwright.londonundergroundstatus.models

const val DARK_BLUE = "#113b92"
const val WHITE = "#ffffff"

enum class TubeLineColours(
    val id: String,
    val backgroundColour: String,
    val whiteForegroundColour: Boolean = true
) {
    BAKERLOO("bakerloo", "#ae6118"),
    CENTRAL("central", "#e41f1f"),
    CIRCLE("circle", "#f8d42d", false),
    DISTRICT("district", "#007229"),
    DLR("dlr", "#00bbb4"),
    HAMMERSMITH_AND_CITY("hammersmith-city", "#e899a8", false),
    JUBILEE("jubilee", "#686e72"),
    LONDON_OVERGROUND("london-overground", "#f86c00"),
    METROPOLITAN("metropolitan", "#893267"),
    NORTHERN("northern", "#000000"),
    PICCADILLY("piccadilly", "#0450a1"),
    TFL_RAIL("tfl-rail", "#0019a8"),
    TRAM("tram", "#66cc00", false),
    VICTORIA("victoria", "#009fe0"),
    WATERLOO_AND_CITY("waterloo-city", "#70c3ce", false),
}