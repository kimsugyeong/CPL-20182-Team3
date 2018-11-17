#include <SNIPE.h>

#include <SoftwareSerial.h>
#include "SNIPE.h"

#define TXpin 11
#define RXpin 10
#define ATSerial Serial

String lora_app_key = "00 00 00 00 00 00 00 00 11 11 11 11 11 11 11 11";  //16byte hex key

SoftwareSerial DebugSerial(RXpin, TXpin);
SNIPE SNIPE(ATSerial);

int solenoidPin = 4;    //This is the output pin on the Arduino we are using

void setup() {

  ATSerial.begin(115200);
  pinMode(solenoidPin, OUTPUT);           //Sets the pin as an output

  while (ATSerial.read() >= 0) {

  }
  while (!ATSerial);

  DebugSerial.begin(115200);
}

void loop() {

  String str = SNIPE.lora_recv();
  DebugSerial.println(str);

  delay(500);
  
  if (isNumeric(str)) {
    int time = str.toInt();
    digitalWrite(solenoidPin, HIGH);    //Switch Solenoid ON
    delay(time * 1000);
    digitalWrite(solenoidPin, LOW);
  }
}

boolean isNumeric(String str) {
  unsigned int stringLength = str.length();

  if (stringLength == 0) {
    return false;
  }

  boolean seenDecimal = false;

  for (unsigned int i = 0; i < stringLength; i++) {
    if (isDigit(str.charAt(i))) {
      continue;
    }

    if (str.charAt(i) == '.') {
      if (seenDecimal) {
        return false;
      }
      seenDecimal = true;
      continue;
    }
    return false;
  }
  return true;
}
