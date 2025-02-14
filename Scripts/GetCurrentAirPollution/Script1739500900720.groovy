import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper as JsonSlurper

String pollutionUrl = 'http://api.openweathermap.org/data/2.5/air_pollution?lat=-6.3035&lon=106.7469&appid=fd62756a7312a96b27b13b6fcd69cfe6'

def response

try {
    // Mengirim permintaan GET ke API langsung
    response = WS.sendRequest(findTestObject('AirPollutionRequest'))
    
    // Verifikasi status kode jika response is not null
    if (response != null) {
        WS.verifyResponseStatusCode(response, 200)
        // Pengecekan response body
        def jsonResponse = new JsonSlurper().parseText(response.getResponseBodyContent())
        assert jsonResponse != null

        // Verifikasi JSON Schema untuk memastikan Verifikasi Koordinat (Lat/Lon) Jakarta Selatan
        WS.verifyElementPropertyValue(response, 'city.coord.lat', -6.3035)
        WS.verifyElementPropertyValue(response, 'city.coord.lon', 106.7469)

        // Verifikasi JSON Schema
        WS.verifyElementPropertyValue(response, 'list[0].main.aqi', 1) // Memastikan AQI (Air Quality Index) ada
        assert jsonResponse.list[0].components.co != null // Memastikan kadar CO ada
        assert jsonResponse.list[0].components.pm10 != null // Memastikan kadar PM10 ada
        assert jsonResponse.list[0].components.no2 != null // Memastikan kadar NO2 ada
    } else {
        println("Response is null. API request failed.")
    }
} catch (Exception e) {
    println("Error occurred: " + e.getMessage())
    e.printStackTrace()
}
