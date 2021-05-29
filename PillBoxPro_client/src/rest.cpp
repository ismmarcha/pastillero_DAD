#include <Arduino.h>
#include <ArduinoJson.h>
#include <ArduinoHttpClient.h>

String doGet(HttpClient httpClient, String uri, String bodyData)
{
  DynamicJsonDocument body(1024), resGet(1024);
  //doc[String("id_pastillero")] = "192R5T";
  String resData = "";
  //serializeJson(doc, bodyData);
  Serial.println(bodyData);
  Serial.println("making GET request");
  httpClient.beginRequest();
  httpClient.get(uri);
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCodeGet = httpClient.responseStatusCode();
  String responseGet = httpClient.responseBody();

  Serial.print("GET Status code: ");
  Serial.println(statusCodeGet);
  Serial.print("GET Response: ");
  Serial.println(responseGet);
  resGet[String("statusCode")] = statusCodeGet;
  resGet["response"] = responseGet;
  JsonArray p = resGet["response"];
  serializeJson(resGet, resData);
  Serial.print("GET Data: ");
  Serial.println(p);
  return resData;
}

String doPost(HttpClient httpClient, String uri, String bodyData)
{
  DynamicJsonDocument bodyPost(1024), resPost(1024);
  String resData = "";
  Serial.println(bodyData);
  Serial.println("making POST request");
  httpClient.beginRequest();
  httpClient.post(uri);
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCodePost = httpClient.responseStatusCode();
  String responsePost = httpClient.responseBody();

  Serial.print("POST Status code: ");
  Serial.println(statusCodePost);
  Serial.print("POST Response: ");
  Serial.println(responsePost);
  resPost[String("statusCode")] = statusCodePost;
  resPost[String("response")] = responsePost;
  serializeJson(resPost, resData);
  return resData;
}

String doPut(HttpClient httpClient, String uri, String bodyData)
{
  DynamicJsonDocument body(1024), res(1024);
  String resData = "";
  Serial.println(bodyData);
  Serial.println("making PUT request");
  httpClient.beginRequest();
  httpClient.put(uri);
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCodePut = httpClient.responseStatusCode();
  String responsePut = httpClient.responseBody();

  Serial.print("PUT Status code: ");
  Serial.println(statusCodePut);
  Serial.print("PUT Response: ");
  Serial.println(responsePut);
  res[String("statusCode")] = statusCodePut;
  res[String("response")] = responsePut;
  serializeJson(res, resData);
  return resData;
}

String doDelete(HttpClient httpClient, String uri, String bodyData)
{
  DynamicJsonDocument body(1024), res(1024);
  String resData = "";
  Serial.println(bodyData);
  Serial.println("making DELETE request");
  httpClient.beginRequest();
  httpClient.del(uri);
  httpClient.sendHeader("Content-Type", "application/json");
  httpClient.sendHeader("Content-Length", bodyData.length());
  httpClient.sendHeader("X-Custom-Header", "custom-header-value");
  httpClient.beginBody();
  httpClient.print(bodyData);
  httpClient.endRequest();

  // read the status code and body of the response
  int statusCode = httpClient.responseStatusCode();
  String response = httpClient.responseBody();

  Serial.print("DELETE Status code: ");
  Serial.println(statusCode);
  Serial.print("DELETE Response: ");
  Serial.println(response);
  res[String("statusCode")] = statusCode;
  res[String("response")] = response;
  serializeJson(res, resData);
  return resData;
}

/*
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
*/