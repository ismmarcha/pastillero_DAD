#include <Arduino.h>
#include <PubSubClient.h>
#include <Ethernet.h>
#include <ESP8266WiFi.h>
#include <ArduinoHttpClient.h>
#include <ArduinoJson.h>
#include <Servo.h>
#include <Hash.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Time.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_MPU6050.h>

#include <rest.h>

const char *ssid = "MOVISTAR_072E";
const char *password = "X8Z3J2Jptc6vAkZYRsan";
const int portHttp = 8082;
const int portMqtt = 1883;
String placaId = "";
const byte hashLen = 20; /* 256-bit */
const byte macLen = 6;
byte mac[macLen];
byte hashMac[hashLen];

WiFiClient espWifiClient;
IPAddress server(192, 168, 1, 10);
PubSubClient mqttClient(espWifiClient);
HttpClient httpClient = HttpClient(espWifiClient, server, portHttp);
Servo servo;
LiquidCrystal_I2C lcd(0x27, 20, 4);
Adafruit_MPU6050 mpu;

String macToStr(const uint8_t *mac)
{
  String result;
  for (int i = 0; i < 6; ++i)
  {
    result += String(mac[i], 16);
    if (i < 5)
      result += ':';
  }
  return result;
}

String hashToString(const byte *hash)
{
  String result;
  char buffer[20];
  for (uint16_t i = 0; i < hashLen; i++)
  {
    sprintf(buffer, "%02x", hash[i]);
    result += buffer;
  }
  return result;
}

byte *doHashMac(uint8_t *macIn, byte *hashOut)
{
  sha1((char *)macIn, &hashOut[0]);
  return hashOut;
}

bool checkHashMac(String hashIn)
{
  if ((hashToString(hashMac)).equals(hashIn))
  {
    return true;
  }
  return false;
}

void setupTime()
{
  configTime(3 * 3600, 0, "pool.ntp.org", "time.nist.gov");
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
  placaId = hashToString(hashMac);
  setupTime();
  //byte * h = (byte*)"E38Df4AABA2A8949353A35CD3E3516A83AB1073276788B65F6321E65CB74";
  //checkHashMac(mac);
  //Serial.println("placaid " + placaId);
}

void restTest()
{
  DynamicJsonDocument bodyGet(1024), bodyPost(1024), bodyPut(1024), bodyDelete(1024);
  String bodyGetData = "", bodyPostData = "", bodyPutData = "", bodyDeleteData = "";
  bodyGet[String("id_pastillero")] = "192R5T";
  serializeJson(bodyGet, bodyGetData);
  String resGet = doGet(httpClient, "/api/pastilleros/getPastilleroId", bodyGetData);
  delay(2500);
  Serial.println("resGet: " + resGet);
  bodyPost[String("id_pastillero")] = placaId;
  bodyPost[String("alias")] = "placa_manlorhid";
  serializeJson(bodyPost, bodyPostData);
  String resPost = doPost(httpClient, "/api/pastilleros/addPastillero", bodyPostData);
  delay(2500);
  Serial.println("posRes: " + resPost);
  bodyPut[String("id_pastillero")] = placaId;
  bodyPut[String("alias")] = "placa_manlorhid_editada";
  serializeJson(bodyPut, bodyPutData);
  String resPut = doPut(httpClient, "/api/pastilleros/editPastillero", bodyPutData);
  delay(2500);
  Serial.println("putRes: " + resPut);
  bodyDelete[String("id_pastillero")] = placaId;
  serializeJson(bodyDelete, bodyDeleteData);
  String resDelete = doDelete(httpClient, "/api/pastilleros", bodyDeleteData);
  Serial.println("deleteRes: " + resDelete);
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

void setupServo()
{
  servo.attach(2); //Pin D4
  servo.write(0);
}

void testServo()
{
  for (int i = 0; i < 180; i++)
  {
    servo.write(i);
    Serial.println(i);
    delay(100);
  }
}

void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (unsigned int i = 0; i < length; i++)
  {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void reconnect()
{
  // Loop until we're reconnected
  while (!mqttClient.connected())
  {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    const char *placaIdChar = placaId.c_str();
    if (mqttClient.connect(placaIdChar, "admin1", "123456"))
    {
      Serial.println("connected");
      // Once connected, publish an announcement...
      String rutaMsg = "placa/" + placaId+"/status";
      mqttClient.publish(rutaMsg.c_str(), "conectado");
      // ... and resubscribe
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

void checkI2CAddresses()
{
  while (!Serial)
  {
  } // Waiting for serial connection

  Serial.println();
  Serial.println("Start I2C scanner ...");
  Serial.print("\r\n");
  byte count = 0;

  Wire.begin();
  for (byte i = 8; i < 120; i++)
  {
    Wire.beginTransmission(i);
    if (Wire.endTransmission() == 0)
    {
      Serial.print("Found I2C Device: ");
      Serial.print(" (0x");
      Serial.print(i, HEX);
      Serial.println(")");
      count++;
      delay(1);
    }
  }
  Serial.print("\r\n");
  Serial.println("Finish I2C scanner");
  Serial.print("Found ");
  Serial.print(count, HEX);
  Serial.println(" Device(s).");
}

void testLCD()
{
  lcd.init();

  lcd.backlight();
  lcd.setCursor(1, 0);
  lcd.print("Hora actual");
  lcd.setCursor(1, 1);
  lcd.print("Prueba");
}

void setupBuzzer()
{
  pinMode(14, OUTPUT); //D5
  digitalWrite(14, LOW);
}

void testBuzzer()
{
  digitalWrite(14, HIGH);
  delay(1000);
  digitalWrite(14, LOW);
  delay(1000);
}

void setupGyro()
{
  if (!mpu.begin())
  {
    Serial.println("Failed to find MPU6050 chip");
    while (1)
    {
      delay(10);
    }
  }
  Serial.println("MPU6050 Found!");
}

void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);
  mqttClient.setServer(server, portMqtt);
  mqttClient.setCallback(callback);
  setupWifi();
  registrarPlaca();
  reconnect();
  //setupServo();
  //testServo();
  //checkI2CAddresses();
  //testLCD();
  //setupBuzzer();
  //setupGyro
  delay(2000);
}

void loop()
{
  // put your main code here, to run repeatedly:
  /*if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop();*/
  //testBuzzer();
  /*time_t now = time(nullptr);
  lcd.setCursor(1, 0);
  lcd.print("Hora actual");
  lcd.setCursor(1, 1);*/
  //lcd.print(hour(now)+":"+minute(now)+":"+second(now));
  delay(1000);
}