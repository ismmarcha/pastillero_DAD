#include <Arduino.h>
#include <PubSubClient.h>
#include <Ethernet.h>
#include <ESP8266WiFi.h>
#include <ArduinoHttpClient.h>
#include <ArduinoJson.h>

#include <rest.h>

const char* ssid = "ADAMO-9634";
const char* password = "D3DUFAFZJTT35S";
const int portHttp = 8081;
const int portMqtt = 1883;
String placaId = "";

WiFiClient espWifiClient;
IPAddress server(192, 168, 1, 10);
PubSubClient mqttClient(espWifiClient);
HttpClient httpClient = HttpClient(espWifiClient, server, portHttp);

String macToStr(const uint8_t* mac)
{
  String result;
  for (int i = 0; i < 6; ++i) {
    result += String(mac[i], 16);
    if (i < 5)
      result += ':';
  }
  return result;
}

void setup_wifi(){
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  uint8_t mac[6];
  WiFi.macAddress(mac);
  placaId += macToStr(mac);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

<<<<<<< HEAD
void getTest(){
  DynamicJsonDocument doc(1024);
  doc[String("id_pastillero")] = "192R5T";
  String bodyData = "";
  serializeJson(doc, bodyData);
  Serial.println(bodyData);
  Serial.println("making GET request");
  httpClient.beginRequest();
  httpClient.get("/api/pastilleros/getPastilleroId");
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCode = httpClient.responseStatusCode();
  String response = httpClient.responseBody();

  Serial.print("GET Status code: ");
  Serial.println(statusCode);
  Serial.print("GET Response: ");
  Serial.println(response);

  Serial.println("Wait five seconds");
  delay(5000);
}

void postTest(){
  DynamicJsonDocument doc(1024);
  doc[String("id_pastillero")] = "193R5T";
  doc[String("alias")] = "papá";
  String bodyData = "";
  serializeJson(doc, bodyData);
  Serial.println(bodyData);
  Serial.println("making POST request ADD PASTILLERO");
  httpClient.beginRequest();
  httpClient.post("/api/pastilleros/addPastillero");
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCode = httpClient.responseStatusCode();
  String response = httpClient.responseBody();

  Serial.print("POST Status code: ");
  Serial.println(statusCode);
  Serial.print("POST Response: ");
  Serial.println(response);

  Serial.println("Wait five seconds");
  delay(5000);
=======
void restTest(){
  DynamicJsonDocument bodyGet(1024), bodyPost(1024), bodyPut(1024), bodyDelete(1024);
  String bodyGetData = "", bodyPostData = "", bodyPutData = "", bodyDeleteData = "";
  bodyGet[String("id_pastillero")] = "192R5T";
  serializeJson(bodyGet, bodyGetData);
  String resGet = doGet(httpClient, "/api/pastilleros/getPastilleroId", bodyGetData);
  Serial.println("resGet: "+resGet);
  bodyPost[String("id_pastillero")] = placaId;
  bodyPost[String("alias")] = "placa_manlorhid";
  serializeJson(bodyPost, bodyPostData);
  String resPost = doPost(httpClient, "/api/pastilleros/addPastillero", bodyPostData);
  Serial.println("posRes: "+resPost);
  bodyPut[String("id_pastillero")] = placaId;
  bodyPut[String("alias")] = "placa_manlorhid_editada";
  serializeJson(bodyPut, bodyPutData);
  String resPut = doPut(httpClient, "/api/pastilleros/editPastillero", bodyPutData);
  Serial.println("putRes: "+resPut);
  bodyDelete[String("id_pastillero")] = placaId;
  serializeJson(bodyDelete, bodyDeleteData);
  String resDelete = doDelete(httpClient, "/api/pastilleros", bodyDeleteData);
  Serial.println("deleteRes: "+resDelete);
>>>>>>> 26f8ea2f4b36c1878da8e4f2d9193545053eadfd
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (unsigned int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void reconnect() {
  // Loop until we're reconnected
  while (!mqttClient.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (mqttClient.connect("arduinoClient", "admin1", "123456")) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      mqttClient.publish("outTopic","hello world");
      // ... and resubscribe
      mqttClient.subscribe("inTopic");
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  mqttClient.setServer(server, portMqtt);
  mqttClient.setCallback(callback);
  setup_wifi();
  restTest();
  delay(1500);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop();
}