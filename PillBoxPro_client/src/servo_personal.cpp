#include <Servo.h>

Servo servo1;
Servo servo2;

void servoSetup(int giroInicial1, int giroInicial2)
{
  servo1.attach(2); //Pin D4
  servo1.write(giroInicial1);

  servo2.attach(0); //D3 DEBO COMPROBARLO
  servo2.write(giroInicial2);
}

void servoTest()
{
 // for (int i = 0; i < 180; i++)
  //{
    servo1.write(0);
    //Serial.println(i);
    //delay(100);
 // }
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