#include <Arduino.h>
#include <PubSubClient.h>      //MQTT
#include <ESP8266WiFi.h>       //WIFI
#include <ArduinoHttpClient.h> //HTTP
#include <ArduinoJson.h>       //JSON
#include <TimeLib.h>           //TIME INTERNET
#include <StringSplitter.h>    //

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

WiFiClient espWifiClient1;
WiFiClient espWifiClient2;
IPAddress server(192, 168, 1, 10);
PubSubClient mqttClient(espWifiClient1);
HttpClient httpClient = HttpClient(espWifiClient2, server, portHttp);
DynamicJsonDocument listaDosis(4096);
DynamicJsonDocument siguienteDosis(256);

void comprobarSiguienteDosis();

void setupTime()
{
  configTime(2 * 3600, 0, "pool.ntp.org", "time.nist.gov");
  Serial.println("\nWaiting for time");
  while (!time(nullptr))
  {
    Serial.print(".");
    delay(1000);
  }
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
      mqttClient.subscribe(String(rutaMsg + "/statusGiro").c_str());
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
  String rutaStatusGiro = "placa/" + String(placaId) + "/statusGiro";
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
  if (topicStr.equals(rutaStatusGiro))
  {
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
    }
    giroInicial = res.toInt();
    servo1Write(giroInicial);
  }
}

void mqttSetup()
{
  mqttClient.setServer(server, portMqtt);
  mqttClient.setCallback(callbackMqtt);
}

void mostrarHora()
{
  time_t now = time(nullptr);
  struct tm *timeinfo;
  time(&now);
  timeinfo = localtime(&now);
  int hora = timeinfo->tm_hour;
  int minuto = timeinfo->tm_min;
  int segundo = timeinfo->tm_sec;
  String horaStr = "";
  String minutoStr = "";
  String segundoStr = "";
  if (hora < 10)
  {
    horaStr = "0" + String(hora);
  }
  else
  {
    horaStr = String(hora);
  }
  if (minuto < 10)
  {
    minutoStr = "0" + String(minuto);
  }
  else
  {
    minutoStr = String(minuto);
  }
  if (segundo < 10)
  {
    segundoStr = "0" + String(segundo);
  }
  else
  {
    segundoStr = String(segundo);
  }
  writeLCD(horaStr + ":" + minutoStr + ":" + segundoStr, 1, 1);
}

void comprBuzzer()
{
  if (banderaBuzzer == true)
  {
    long tiempoPasado = millis() - tiempoBuzzer;
    if (tiempoPasado >= 10000)
    {
      banderaBuzzer = false;
      buzzerOff();
    }
    else
    {
      if ((tiempoPasado / 1000) % 2 == 0)
      {
        buzzerOn();
      }
      else
      {
        buzzerOff();
      }
    }
  }
}

void comprobarSiguienteDosis()
{
  //NECESITO CAMBIAR LA FECHA ACTUAL A 00:00 PARA PODER AÑADIR BIEN LA FECHA, MIRAR DETENIDAMENTE SI SE MIDE EN SEGUNDOS O MILI
  long menorTiempo = time(nullptr) + 3600 * 24 * 8;
  DynamicJsonDocument dosisMasCercana(256);
  for (JsonObject elem : listaDosis.as<JsonArray>())
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
    time_t fechaActual = time(nullptr);
    struct tm *fechaActualInfo;
    time(&fechaActual);
    fechaActualInfo = localtime(&fechaActual);
    int hora = fechaActualInfo->tm_hour;
    int minuto = fechaActualInfo->tm_min;
    int segundo = fechaActualInfo->tm_sec;
    //int dayW = fechaActualInfo->tm_wday;
    time_t fechaActualBase = fechaActual - (hora * 3600 + minuto * 60) - segundo; //Reseteamos la fecha actual a las 00:00 para facilitar calculos
    int diaWActual = fechaActualInfo->tm_wday;
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
    hora = fechaApiInfo->tm_hour;
    minuto = fechaApiInfo->tm_min;
    //dayW = fechaApiInfo->tm_wday;
    //int fechaDia = fechaApiInfo->tm_mday;
    /*Serial.println("Fecha base");
    Serial.println(String(hora) + ":" + String(minuto) + " W: " + String(dayW) + " DIA MES: " + String(fechaDia));
    Serial.println("Fecha API");
    Serial.println(String(horaApi) + ":" + String(minutoApi) + " W: " + String(dia_semana));*/
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
  /*Serial.println("DOSIS MAS CERCANA");
  Serial.println(String(dia_semana_dosis) + "->" + String(hora_inicio_dosis));*/
  siguienteDosis["dia_semana"] = dia_semana_dosis;
  siguienteDosis["hora_inicio"] = hora_inicio_dosis;
  siguienteDosis["segundosFecha"] = fecha_dosis;
  writeLCD(String(dia_semana_dosis) + "->" + hora_inicio_dosis, 1, 3);
}

void comprobarSiTocaDosis()
{
  time_t fechaSiguienteDosis = siguienteDosis["segundosFecha"];
  time_t fechaActualSiToca = time(nullptr);
  if (fechaActualSiToca >= fechaSiguienteDosis)
  {
    String rutaStatusGiro = "placa/" + String(placaId) + "/statusGiro";
    Serial.println("Ha llegado la hora");
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
    mqttClient.publish(rutaStatusGiro.c_str(), String(readServo).c_str(), true);
    comprobarSiguienteDosis();
    banderaBuzzer = true;
    tiempoBuzzer = millis();
    buzzerOn();
  }
}

void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);

  setupWifi();
  buzzerSetup();
  checkI2CAddresses();
  mqttSetup();
  mpuSetup();
  setupLCD();

  writeLCD("Hora actual:", 1, 0);
  writeLCD("Hora dosis: ", 1, 2);
  mqttConnect();
  mqttClient.loop();
  servoSetup(giroInicial, 0);
  mpuTest();
  registrarPlaca();
  obtenerCitas();
  comprobarSiguienteDosis();
  delay(2000);
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

  mostrarHora();

  comprBuzzer();

  comprobarSiTocaDosis();
}