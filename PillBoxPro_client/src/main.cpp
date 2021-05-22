#include <Arduino.h>
#include <PubSubClient.h>      //MQTT
#include <ESP8266WiFi.h>       //WIFI
#include <ArduinoHttpClient.h> //HTTP
#include <ArduinoJson.h>       //JSON
#include <Servo.h>             //SERVO
#include <Hash.h>              //SHA1 HASH
#include <Wire.h>
#include <LiquidCrystal_I2C.h> //LCD
#include <Time.h>              //TIME INTERNET
#include <Adafruit_Sensor.h>   //GYRO
#include <Adafruit_MPU6050.h>  //GYRO

#include <rest.h>
#include <servo_personal.h>
#include <hashMac.h>

const char *ssid = "MOVISTAR_072E";
const char *password = "X8Z3J2Jptc6vAkZYRsan";
const int portHttp = 8082;
const int portMqtt = 1883;
const byte hashLen = 20; /* 256-bit */
const byte macLen = 6;
String placaId = "";
byte mac[macLen];
byte hashMac[hashLen];

WiFiClient espWifiClient;
IPAddress server(192, 168, 1, 10);
//IPAddress server(10, 100, 24, 91);
PubSubClient mqttClient(espWifiClient);
HttpClient httpClient = HttpClient(espWifiClient, server, portHttp);
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
  placaId = hashToString(hashMac, hashLen);
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
  mpu.setAccelerometerRange(MPU6050_RANGE_8_G);
  Serial.print("Accelerometer range set to: ");
  switch (mpu.getAccelerometerRange())
  {
  case MPU6050_RANGE_2_G:
    Serial.println("+-2G");
    break;
  case MPU6050_RANGE_4_G:
    Serial.println("+-4G");
    break;
  case MPU6050_RANGE_8_G:
    Serial.println("+-8G");
    break;
  case MPU6050_RANGE_16_G:
    Serial.println("+-16G");
    break;
  }
  mpu.setGyroRange(MPU6050_RANGE_500_DEG);
  Serial.print("Gyro range set to: ");
  switch (mpu.getGyroRange())
  {
  case MPU6050_RANGE_250_DEG:
    Serial.println("+- 250 deg/s");
    break;
  case MPU6050_RANGE_500_DEG:
    Serial.println("+- 500 deg/s");
    break;
  case MPU6050_RANGE_1000_DEG:
    Serial.println("+- 1000 deg/s");
    break;
  case MPU6050_RANGE_2000_DEG:
    Serial.println("+- 2000 deg/s");
    break;
  }

  mpu.setFilterBandwidth(MPU6050_BAND_5_HZ);
  Serial.print("Filter bandwidth set to: ");
  switch (mpu.getFilterBandwidth())
  {
  case MPU6050_BAND_260_HZ:
    Serial.println("260 Hz");
    break;
  case MPU6050_BAND_184_HZ:
    Serial.println("184 Hz");
    break;
  case MPU6050_BAND_94_HZ:
    Serial.println("94 Hz");
    break;
  case MPU6050_BAND_44_HZ:
    Serial.println("44 Hz");
    break;
  case MPU6050_BAND_21_HZ:
    Serial.println("21 Hz");
    break;
  case MPU6050_BAND_10_HZ:
    Serial.println("10 Hz");
    break;
  case MPU6050_BAND_5_HZ:
    Serial.println("5 Hz");
    break;
  }
  Serial.println("");
  delay(100);
}

void testGyro()
{
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

  /* Print out the values */
  Serial.print("Acceleration X: ");
  Serial.print(a.acceleration.x);
  Serial.print(", Y: ");
  Serial.print(a.acceleration.y);
  Serial.print(", Z: ");
  Serial.print(a.acceleration.z);
  Serial.println(" m/s^2");

  Serial.print("Rotation X: ");
  Serial.print(g.gyro.x);
  Serial.print(", Y: ");
  Serial.print(g.gyro.y);
  Serial.print(", Z: ");
  Serial.print(g.gyro.z);
  Serial.println(" rad/s");

  Serial.print("Temperature: ");
  Serial.print(temp.temperature);
  Serial.println(" degC");

  Serial.println("");
}

void callbackMqtt(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  String rutaMsg = "placa/" + String(placaId) + "/move";
  String topicStr = String(topic);
  if (topicStr.equals(rutaMsg))
  {
    String res;
    for (unsigned int i = 0; i < length; i++)
    {
      res += (char)payload[i];
      //Serial.print((char)payload[i]);
    }
    const String value = "1";
    if (res.equals(value))
    {
      int readServo = servoRead();
      if (readServo >= 180)
      {
        servoWrite(0);
      }
      else
      {
        servoWrite(readServo + 45);
      }
      delay(1000);
      Serial.print("Servo: ");
      Serial.println(servoRead());
    }
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
      Serial.println("Subscrito a la ruta: " + rutaMsg);
      mqttClient.publish(rutaMsg.c_str(), "conectado");
      // ... and resubscribe
      rutaMsg += "#";
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
  registrarPlaca();
  servoSetup();
  mqttSetup();
  mqttConnect();
  //testServo();
  //checkI2CAddresses();
  //testLCD();
  //setupBuzzer();
  //setupGyro();
  delay(2000);
}

void loop()
{
  // put your main code here, to run repeatedly:
  /*if (!mqttClient.connected()) {
    reconnect();
  }*/
  //testServo();
  //testBuzzer();
  /*time_t now = time(nullptr);
  lcd.setCursor(1, 0);
  lcd.print("Hora actual");
  lcd.setCursor(1, 1);*/
  //lcd.print(hour(now)+":"+minute(now)+":"+second(now));

  /* Get new sensor events with the readings */

  //testGyro();
  //delay(1000);
  //loopMqtt();
  mqttClient.loop();
}