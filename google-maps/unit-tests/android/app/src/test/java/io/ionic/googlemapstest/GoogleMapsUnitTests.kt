package io.ionic.googlemapstest

import androidx.appcompat.app.AppCompatActivity
import com.capacitorjs.plugins.googlemaps.CapacitorGoogleMaps
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleMapsUnitTests {
    lateinit var activity: AppCompatActivity;

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.javaObjectType).create().get()
    }

    @Test
    fun exampleTest() {
        val plugin = CapacitorGoogleMaps()

        val valueOf = plugin.echo("Hello world!")
        assertEquals("Hello world!", valueOf)
    }
}