#include <Servo.h>

Servo servo1;
Servo servo2;

void servoSetup()
{
  servo1.attach(2); //Pin D4
  servo1.write(0);

  servo2.attach(3); //DEBO COMPROBARLO
  servo2.write(0);
}

void servoTest()
{
  for (int i = 0; i < 180; i++)
  {
    servo1.write(i);
    Serial.println(i);
    delay(100);
  }
}

void servo1Write(int angle){
    servo1.write(angle);
}

int servo1Read(){
    return servo1.read();
}

void servo2Write(int angle){
    servo2.write(angle);
}

int servo2Read(){
    return servo2.read();
}