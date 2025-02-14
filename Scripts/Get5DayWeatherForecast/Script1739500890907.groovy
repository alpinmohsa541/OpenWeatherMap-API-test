import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper

// URL API untuk ramalan cuaca
String weatherUrl = 'https://api.openweathermap.org/data/2.5/forecast?lat=-6.3035&lon=106.7469&appid=fd62756a7312a96b27b13b6fcd69cfe6'

// Mengirim permintaan GET ke API
def response = WS.sendRequestAndVerify(findTestObject('Object Repository/WeatherForecastRequest', [('url') : weatherUrl]))

// Verifikasi status kode
WS.verifyResponseStatusCode(response, 200)

// Pengecekan response body
def jsonResponse = new JsonSlurper().parseText(response.getResponseBodyContent())
assert jsonResponse != null

// Verifikasi JSON Schema untuk memastikan Verifikasi Koordinat (Lat/Lon) Jakarta Selatan
WS.verifyElementPropertyValue(response, 'city.coord.lat', -6.3035) 
WS.verifyElementPropertyValue(response, 'city.coord.lon', 106.7469)

WS.verifyElementPropertyValue(response, 'list.size()', 40) // Verifikasi jumlah data ramalan (5 hari * 8 data per hari)

// Menambahkan validasi lain jika diperlukan
assert jsonResponse.list[0].main.temp != null // Memastikan suhu ada
assert jsonResponse.list[0].weather[0].description != null // Memastikan deskripsi cuaca ada
// Memastikan bahwa response body adalah JSON yang valid
assert response.getResponseBodyContent().startsWith('{') && response.getResponseBodyContent().endsWith('}')
// Validasi data hujan jika ada
if (jsonResponse.list[0].rain != null) {
	assert jsonResponse.list[0].rain.'3h' instanceof Number // Pastikan ada data hujan dalam 3 jam terakhir
} else {
	assert jsonResponse.list[0].rain == null // Pastikan rain null jika tidak ada hujan
}
// Memastikan response body tidak null atau kosong
assert response.getResponseBodyContent() != null
assert response.getResponseBodyContent().trim().length() > 0
