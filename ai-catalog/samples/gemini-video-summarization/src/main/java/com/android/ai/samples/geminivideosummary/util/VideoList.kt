package com.android.ai.samples.geminivideosummary.util

import android.net.Uri

/**
 * Class containing a list of hardcoded video URIs and their titles.
 */
class VideoList {
    val videos = listOf(
        VideoItem(
            "Big Buck Bunny",
            Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        ), VideoItem(
            "Android Spotlight Week (Shorts video)",
            Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/shorts_android_developers/shorts_10.mp4")
        ), VideoItem(
            "Rio De Janerio",
            Uri.parse("gs://cloud-samples-data/generative-ai/video/rio_de_janeiro_beyond_the_map_rio.mp4")
        ),
        VideoItem(
            "Youtube Link (On Device Watch Next with Google TV)",
            Uri.parse("https://www.youtube.com/watch?v=QFMIP5GOo70")
        ),
        VideoItem(
            "Tears of Steel",
            Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4")
        ), VideoItem(
            "For Bigger Blazes",
            Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
        ), VideoItem(
            "For Bigger Escape",
            Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4")
        )
    )
}

/**
 * Data class to represent a video item with a title and URI.
 */
data class VideoItem(val title: String, val uri: Uri)