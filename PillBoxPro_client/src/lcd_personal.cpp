#include <Arduino.h>
#include <LiquidCrystal_I2C.h> //LCD

LiquidCrystal_I2C lcd(0x27, 20, 4);

void setupLCD(){
  lcd.init();
  lcd.backlight();
}

void writeLCD(String text, int8_t column, int8_t row){
  lcd.setCursor(column, row);
  lcd.print(text);
}

void testLCD()
{
  writeLCD("Hora actual", 1, 0);
  writeLCD("Prueba", 1, 1);
}