#include <Arduino.h>
#include <Hash.h> //SHA1 HASH

String hashToString(byte *hash, byte hashLenIn);

byte *doHashMac(uint8_t *macIn, byte *hashOut);

bool checkHashMac(String hashIn1, String hashIn2);

String macToStr(const uint8_t *mac);