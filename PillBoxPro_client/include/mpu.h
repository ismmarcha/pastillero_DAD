#include <Arduino.h>
#include <Adafruit_Sensor.h>  //GYRO
#include <Adafruit_MPU6050.h> //GYRO

void mpuSetup();

void mpuTest();

sensors_event_t mpuTemperatura();

sensors_event_t mpuGyro();

sensors_event_t mpuAccel();