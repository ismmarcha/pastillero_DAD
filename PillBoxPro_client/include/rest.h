#include <Arduino.h>
#include <ArduinoJson.h>
#include <ArduinoHttpClient.h>

String doGet(HttpClient httpClient, String uri, String body);
String doPost(HttpClient httpClient, String uri, String body);
String doPut(HttpClient httpClient, String uri, String body);
String doDelete(HttpClient httpClient, String uri, String body);