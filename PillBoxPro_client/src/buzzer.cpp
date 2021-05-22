#include <Arduino.h>

void buzzerSetup()
{
  pinMode(14, OUTPUT); //D5
  digitalWrite(14, LOW);
}

void buzzerTest()
{
  digitalWrite(14, HIGH);
  delay(1000);
  digitalWrite(14, LOW);
  delay(1000);
}
