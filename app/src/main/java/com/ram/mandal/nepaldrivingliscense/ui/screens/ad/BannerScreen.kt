package com.ram.mandal.nepaldrivingliscense.ui.screens.ad

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.ram.mandal.nepaldrivingliscense.BuildConfig
import com.ram.mandal.nepaldrivingliscense.R
import com.ram.mandal.nepaldrivingliscense.ui.theme.DigitalNepaliToolsTheme

/**
 * Created by Ram Mandal on 24/08/2025
 * @System: Apple M1 Pro
 */

/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



@Composable
fun BannerScreen(modifier: Modifier = Modifier) {

    val TAG = "BannerScreen"
    val context = LocalContext.current
    // [START create_ad_view]
    // Each BannerScreen gets its own AdView
    val displayMetrics = LocalContext.current.resources.displayMetrics

    val adView = remember {
        AdView(context).apply {
            adUnitId = if (BuildConfig.DEBUG)
                context.getString(R.string.banner_add_test)
            else
                context.getString(R.string.banner_add)

            //[Calculate width of the screen]
            val density = displayMetrics.density
            //[-10 make sure not to get cut off]
            val adWidth = (displayMetrics.widthPixels / density).toInt() - 10
            //[End of calculate width size]

            // [START set_ad_size]
            // Set the adaptive banner ad size with a given width.
            val adSize =
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    adWidth
                )
            setAdSize(adSize)
            // [END set_ad_size]
        }
    }


    // [START banner_screen]
    // Place the ad view at the bottom of the screen.
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            BannerAd(adView, modifier)
        }
    }
    // [END banner_screen]
    // [END create_ad_view]

    // [START add_listener]
    // [Optional] Set an AdListener to receive callbacks for various ad events.
    adView.adListener =
        object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad was loaded.")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner ad failed to load: ${error.message}")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Banner ad recorded an impression.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "Banner ad was clicked.")
            }
        }
    // [END add_listener]

    // Prevent loading the AdView if the app is in preview mode.
    if (!LocalInspectionMode.current) {
        // [START load_ad]
        // Create an AdRequest and load the ad.
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // [END load_ad]
    }

    // [START dispose_ad]
    DisposableEffect(Unit) {
        // Destroy the AdView to prevent memory leaks when the screen is disposed.
        onDispose {
            Log.v("GoogleAd", "Disposing")
            adView.destroy()
            Log.v("GoogleAd", "Disposed")
        }
    }
    // [END dispose_ad]
}

@Preview(apiLevel = 33)
@Composable
private fun BannerScreenPreview() {
    DigitalNepaliToolsTheme {
        Surface(color = MaterialTheme.colorScheme.background) { BannerScreen() }
    }
}