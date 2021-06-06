#include <Arduino.h>
#include <Wire.h>

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

String intToStringDiaSemana(int dw)
{
    String dws = "";
    switch (dw)
    {
    case 0:
        dws = "Domingo";
        break;
    case 1:
        dws = "Lunes";
        break;
    case 2:
        dws = "Martes";
        break;
    case 3:
        dws = "Miercoles";
        break;
    case 4:
        dws = "Jueves";
        break;
    case 5:
        dws = "Viernes";
        break;
    case 6:
        dws = "Sabado";
        break;

    default:
        dws = "NaN";
        break;
    }
    return dws;
}