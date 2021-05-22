#include <Arduino.h>
#include <Hash.h> //SHA1 HASH

String hashToString(byte *hash, byte hashLenIn)
{
    String result;
    char buffer[20];
    for (uint16_t i = 0; i < hashLenIn; i++)
    {
        sprintf(buffer, "%02x", hash[i]);
        result += buffer;
    }
    return result;
}

byte *doHashMac(uint8_t *macIn, byte *hashOut)
{
    sha1((char *)macIn, &hashOut[0]);
    return hashOut;
}

bool checkHashMac(String hashIn1, String hashIn2)
{
    if (hashIn1.equals(hashIn2))
    {
        return true;
    }
    return false;
}

String macToStr(const uint8_t *mac)
{
  String result;
  for (int i = 0; i < 6; ++i)
  {
    result += String(mac[i], 16);
    if (i < 5)
      result += ':';
  }
  return result;
}