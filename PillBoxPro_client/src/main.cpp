#include <Arduino.h>
#include <PubSubClient.h>      //MQTT
#include <ESP8266WiFi.h>       //WIFI
#include <ArduinoHttpClient.h> //HTTP
#include <ArduinoJson.h>       //JSON
#include <TimeLib.h>           //TIME INTERNET
#include <StringSplitter.h>    //
#include <NTPClient.h>
#include <WiFiUdp.h>

#include <rest.h>
#include <servo_personal.h>
#include <mpu.h>
#include <hashMac.h>
#include <utils.h>
#include <buzzer.h>
#include <lcd_personal.h>

const char *ssid = "MOVISTAR_072E";
const char *password = "X8Z3J2Jptc6vAkZYRsan";
const int portHttp = 8083;
const int portMqtt = 1883;
const byte hashLen = 20; /* 256-bit */
const byte macLen = 6;
String placaId = "";
byte mac[macLen];
byte hashMac[hashLen];
long tiempoBuzzer = 0;
bool banderaBuzzer = false;
int giroInicial = 0;
float accelXAnterior = 0;

WiFiClient espWifiClient1;
WiFiClient espWifiClient2;
IPAddress server(192, 168, 1, 10);
PubSubClient mqttClient(espWifiClient1);
HttpClient httpClient = HttpClient(espWifiClient2, server, portHttp);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "europe.pool.ntp.org", 2 * 3600, 60000);
DynamicJsonDocument siguienteDosis(256);
DynamicJsonDocument docListaDosis(2048);
JsonArray listaDosis = docListaDosis.as<JsonArray>();

void comprobarSiguienteDosis();

void setupTime()
{
  timeClient.begin();
}

void setupWifi()
{
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  WiFi.macAddress(mac);
  doHashMac(mac, hashMac);
  placaId = hashToString(hashMac, hashLen);
  setupTime();
  //byte * h = (byte*)"E38Df4AABA2A8949353A35CD3E3516A83AB1073276788B65F6321E65CB74";
  //checkHashMac(mac);
  //Serial.println("placaid " + placaId);
}

void registrarPlaca()
{
  DynamicJsonDocument bodyGet(1024), resGet(1024), responseGet(1024);
  String bodyGetData = "";
  bodyGet[String("id_pastillero")] = placaId;
  serializeJson(bodyGet, bodyGetData);
  String resGetData = doGet(httpClient, "/api/pastilleros/getPastilleroId", bodyGetData);
  deserializeJson(resGet, resGetData);
  int statusCode = resGet[String("statusCode")];
  String responseData = resGet[String("response")];
  delay(1000);
  Serial.println(responseData);
  if (responseData.equals("{}"))
  {
    Serial.println("Placa no registrada");
    DynamicJsonDocument bodyPost(1024), resPost(1024);
    String bodyPostData = "";
    bodyPost[String("id_pastillero")] = placaId;
    bodyPost[String("alias")] = "cliente-" + placaId;
    serializeJson(bodyPost, bodyPostData);
    String resPostData = doPost(httpClient, "/api/pastilleros/addPastillero", bodyPostData);
    deserializeJson(resPost, resPostData);
    statusCode = resPost[String("statusCode")];
    String responsePostData = resPost[String("response")];
    if (statusCode == 200)
    {
      Serial.println("Placa registrada correctamente");
    }
    else
    {
      Serial.println("ERROR: " + responsePostData);
    }
  }
  else
  {
    Serial.println("Placa ya registrada");
  }
}

void obtenerCitas()
{
  DynamicJsonDocument bodyGet(2048), resGet(2048), responseGet(2048), dosisPlaca(2048), dosisInd(256);
  String bodyGetData = "";
  bodyGet[String("id_pastillero")] = placaId;
  serializeJson(bodyGet, bodyGetData);
  String resGetData = doGet(httpClient, "/api/dosis/getDosisPorPastillero", bodyGetData);
  deserializeJson(resGet, resGetData);
  resGetData = resGet["response"].as<String>();
  deserializeJson(dosisPlaca, resGetData);
  listaDosis = dosisPlaca[placaId].as<JsonArray>();
}

void mqttConnect()
{
  // Loop until we're reconnected
  while (!mqttClient.connected())
  {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (mqttClient.connect(placaId.c_str(), "admin1", "123456"))
    {
      Serial.println("connected");
      String rutaMsg = "placa/" + String(placaId);
      mqttClient.publish(rutaMsg.c_str(), "");
      mqttClient.subscribe(String(rutaMsg + "/actuDosis").c_str());
      mqttClient.subscribe(String(rutaMsg + "/statusGiro1").c_str());
      mqttClient.subscribe(String(rutaMsg + "/statusGiro2").c_str());
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void callbackMqtt(char *topic, byte *payload, unsigned int length)
{
  Serial.print(F("Message arrived ["));
  Serial.print(topic);
  Serial.print(F("] "));
  String rutaActuDosis = "placa/" + String(placaId) + "/actuDosis";
  String rutaStatusGiro1 = "placa/" + String(placaId) + "/statusGiro1";
  String rutaStatusGiro2 = "placa/" + String(placaId) + "/statusGiro2";
  String topicStr = String(topic);
  String res;
  if (topicStr.equals(rutaActuDosis))
  {
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
    }
    if (res.equals("1"))
    {
      obtenerCitas();
      Serial.println("Actu Dosis = 0");
      String ruta = rutaActuDosis;
      mqttClient.publish(ruta.c_str(), "0");
      comprobarSiguienteDosis();
    }
  }
  if (topicStr.equals(rutaStatusGiro1))
  {
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
    }
    giroInicial = res.toInt();
    servo1Write(giroInicial);
  }

  if (topicStr.equals(rutaStatusGiro2))
  {
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
    }
    servo2Write(res.toInt());
  }
}

void mqttSetup()
{
  mqttClient.setServer(server, portMqtt);
  mqttClient.setCallback(callbackMqtt);
}

void mostrarHora()
{
  timeClient.update();
  int horaMostrar = timeClient.getHours();
  int minutoMostrar = timeClient.getMinutes();
  int segundoMostrar = timeClient.getSeconds();
  String horaStr = "";
  String minutoStr = "";
  String segundoStr = "";
  if (horaMostrar < 10)
  {
    horaStr = "0" + String(horaMostrar);
  }
  else
  {
    horaStr = String(horaMostrar);
  }
  if (minutoMostrar < 10)
  {
    minutoStr = "0" + String(minutoMostrar);
  }
  else
  {
    minutoStr = String(minutoMostrar);
  }
  if (segundoMostrar < 10)
  {
    segundoStr = "0" + String(segundoMostrar);
  }
  else
  {
    segundoStr = String(segundoMostrar);
  }
  writeLCD(horaStr + ":" + minutoStr + ":" + segundoStr, 1, 1);
}

bool movimientoMpu()
{
  float accelXActual = mpuAccel().acceleration.v[0];

  if (abs(accelXActual) > 3)
  {
    return true;
  }
  else
  {
    return false;
  }
}

void comprBuzzer()
{
  if (banderaBuzzer == true)
  {
    long tiempoPasado = millis() - tiempoBuzzer;
    if ((tiempoPasado / 1000) % 2 == 0)
    {
      buzzerOn();
    }
    else
    {
      buzzerOff();
    }

    if (movimientoMpu())
    {
      banderaBuzzer = false;
      buzzerOff();
    }
  }
}

void comprobarSiguienteDosis()
{
  DynamicJsonDocument dosisMasCercana(256);
  long menorTiempo = timeClient.getEpochTime() + 3600 * 24 * 31;
  if (listaDosis.size() == 0)
  {
    dosisMasCercana["segundosFecha"] = -1;
  }
  for (JsonObject elem : listaDosis)
  {
    int dia_semana = elem["dia_semana"];           // 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 3, 4, 5, 6, 1, 2, 2, 6, 1, ...
    const char *hora_inicio = elem["hora_inicio"]; // "10:00", "10:00", "10:00", "10:00", "10:00", "10:00", ...
    StringSplitter *splitter = new StringSplitter(hora_inicio, ':', 2);
    int horaApi = 00;
    int minutoApi = 00;
    int dias_restantes = 7;
    long secondsToNextDay = 0;
    horaApi = (splitter->getItemAtIndex(0)).toInt();
    minutoApi = (splitter->getItemAtIndex(1)).toInt();
    secondsToNextDay = 3600 * horaApi + 60 * minutoApi;
    time_t fechaActual = timeClient.getEpochTime();
    int horaAct = timeClient.getHours();
    int minutoAct = timeClient.getMinutes();
    int segundoAct = timeClient.getSeconds();
    //int dayW = fechaActualInfo->tm_wday;
    time_t fechaActualBase = fechaActual - (horaAct * 3600 + minutoAct * 60) - segundoAct; //Reseteamos la fecha actual a las 00:00 para facilitar calculos
    int diaWActual = timeClient.getDay();
    if (diaWActual <= dia_semana)
    {
      dias_restantes = dia_semana - diaWActual;
    }
    else
    {
      dias_restantes = 7 - (diaWActual - dia_semana);
    }
    secondsToNextDay += 24 * dias_restantes * 3600;
    time_t fechaApi = fechaActualBase + secondsToNextDay;
    if (fechaApi <= fechaActual)
    {
      fechaApi += 3600 * 24 * 7;
    }
    struct tm *fechaApiInfo = localtime(&fechaApi);
    horaAct = fechaApiInfo->tm_hour;
    minutoAct = fechaApiInfo->tm_min;
    //dayW = fechaApiInfo->tm_wday;
    //int fechaDia = fechaApiInfo->tm_mday;
    Serial.println("Fecha API");
    Serial.println(String(horaApi) + ":" + String(minutoApi) + " W: " + String(dia_semana));
    if (fechaApi <= menorTiempo)
    {
      menorTiempo = fechaApi;
      dosisMasCercana["dia_semana"] = dia_semana;
      dosisMasCercana["hora_inicio"] = hora_inicio;
      dosisMasCercana["segundosFecha"] = fechaApi;
      //Serial.println("MENOR QUE ANTES");
    }
  }
  int dia_semana_dosis = dosisMasCercana["dia_semana"];
  String hora_inicio_dosis = dosisMasCercana["hora_inicio"];
  time_t fecha_dosis = dosisMasCercana["segundosFecha"];
  siguienteDosis["dia_semana"] = dia_semana_dosis;
  siguienteDosis["hora_inicio"] = hora_inicio_dosis;
  siguienteDosis["segundosFecha"] = fecha_dosis;
  clearLCDLine(3);
  writeLCD(intToStringDiaSemana(dia_semana_dosis) + "->" + hora_inicio_dosis, 1, 3);
}

void comprobarSiTocaDosis()
{
  time_t fechaSiguienteDosis = siguienteDosis["segundosFecha"];
  time_t fechaActualSiToca = timeClient.getEpochTime();
  if (fechaActualSiToca >= fechaSiguienteDosis && fechaSiguienteDosis > 0)
  {
    Serial.println("Ha llegado la hora");
    String rutaStatusGiro1 = "placa/" + String(placaId) + "/statusGiro1";
    String rutaStatusGiro2 = "placa/" + String(placaId) + "/statusGiro2";

    int readServo = servo1Read();
    if (readServo >= 180)
    {
      readServo = 0;
      //servo1Write(0);
    }
    else
    {
      readServo += 45;
      //servo1Write(readServo + 45);
    }
    mqttClient.publish(rutaStatusGiro1.c_str(), String(readServo).c_str(), true);
    comprobarSiguienteDosis();
    banderaBuzzer = true;
    tiempoBuzzer = millis();
    buzzerOn();
  }
}

void mostrarTemperatura()
{
  sensors_event_t temp;
  temp = mpuTemperatura();
  writeLCD(String(temp.temperature), 13, 0);
}

void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);

  setupLCD();
  writeLCD("PILLBOX PRO", 1, 0);
  writeLCD("CARGANDO...", 1, 2);
  setupWifi();
  delay(1000);
  setupTime();
  buzzerSetup();
  checkI2CAddresses();
  mqttSetup();
  mpuSetup();

  mqttConnect();
  servoSetup(0, 0);
  mpuTest();
  registrarPlaca();
  obtenerCitas();
  clearLCDLine(0);
  clearLCDLine(2);
  writeLCD("Hora actual:", 1, 0);
  writeLCD("Hora dosis: ", 1, 2);
  mostrarHora();
  comprobarSiguienteDosis();
}

void loop()
{
  // put your main code here, to run repeatedly:
  if (WiFi.status() != WL_CONNECTED)
  {
    ESP.reset();
  }

  if (!mqttClient.connected())
  {
    mqttConnect();
  }
  mqttClient.loop();

  //mostrarHora();

  //mostrarTemperatura();

  comprBuzzer();

  comprobarSiTocaDosis();

}