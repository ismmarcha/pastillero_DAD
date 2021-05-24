#include <Arduino.h>
#include <PubSubClient.h>      //MQTT
#include <ESP8266WiFi.h>       //WIFI
#include <ArduinoHttpClient.h> //HTTP
#include <ArduinoJson.h>       //JSON
#include <Time.h>              //TIME INTERNET

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

WiFiClient espWifiClient;
IPAddress server(192, 168, 1, 10);
PubSubClient mqttClient(espWifiClient);
HttpClient httpClient = HttpClient(espWifiClient, server, portHttp);

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

void callbackMqtt(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  String rutaMove = "placa/" + String(placaId) + "/move";
  String rutaNextDosis = "placa/" + String(placaId) + "/nextDosis";
  String topicStr = String(topic);
  String res;
  if (topicStr.equals(rutaMove))
  {
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
      //Serial.print((char)payload[i]);
    }
    const String value = "1";
    if (res.equals(value))
    {
      int readServo = servo1Read();
      if (readServo >= 180)
      {
        servo1Write(0);
      }
      else
      {
        servo1Write(readServo + 45);
      }
      Serial.print("Servo1: ");
      Serial.println(servo1Read());
    }
  }
  if (topicStr.equals(rutaNextDosis))
  {
    DynamicJsonDocument jsonNextDosis(1024);
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
    }
    deserializeJson(jsonNextDosis, res);
    String dia = jsonNextDosis[String("dia")];
    String hora = jsonNextDosis[String("hora")];
    writeLCD(dia+"->"+hora, 1,3);
  }
  Serial.println();
}

void mqttSetup()
{
  mqttClient.setServer(server, portMqtt);
  mqttClient.setCallback(callbackMqtt);
}

void mqttConnect()
{
  // Loop until we're reconnected
  while (!mqttClient.connected())
  {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    //const char *placaIdChar = placaIdMqtt;
    if (mqttClient.connect(placaId.c_str(), "admin1", "123456"))
    {
      Serial.println("connected");
      // Once connected, publish an announcement...
      String rutaMsg = "placa/" + String(placaId) + "/";
      mqttClient.publish(rutaMsg.c_str(), "conectado");
      // ... and resubscribe
      rutaMsg += "#";
      Serial.println("Subscrito a la ruta: " + rutaMsg);
      mqttClient.subscribe(rutaMsg.c_str());
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);

  setupWifi();
  buzzerSetup();
  servoSetup();
  checkI2CAddresses();
  mqttSetup();
  mpuSetup();
  setupLCD();

  writeLCD("Hora actual:", 1, 0);
  writeLCD("Hora dosis: ", 1, 2);
  //testLCD();
  mqttConnect();
  //servoTest();
  mpuTest();
  registrarPlaca();
  delay(2000);
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
void loop()
{
  // put your main code here, to run repeatedly:
  if (!mqttClient.connected())
  {
    mqttConnect();
  }
  //testServo();
  //testBuzzer();
  mostrarHora();
  //Serial.println(hora);
  //lcd.print(hour(now)+":"+minute(now)+":"+second(now));

  /* Get new sensor events with the readings */

  //testGyro();
  //delay(1000);
  //loopMqtt();
  mqttClient.loop();
}