#include <Servo.h>

Servo servo;

void servoSetup()
{
  servo.attach(2); //Pin D4
  servo.write(0);
}

void servoTest()
{
  for (int i = 0; i < 180; i++)
  {
    servo.write(i);
    Serial.println(i);
    delay(100);
  }
}

void servoWrite(int angle){
    servo.write(angle);
}

int servoRead(){
    return servo.read();
}