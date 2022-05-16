package io.ionic.googlemapstest

import com.capacitorjs.plugins.googlemaps.CapacitorGoogleMaps
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleMapsUnitTests {
    @Test
    fun exampleTest() {
        val plugin = CapacitorGoogleMaps()

        val valueOf = plugin.echo("Hello world!")
        assertEquals("Hello world!", valueOf)
    }
}