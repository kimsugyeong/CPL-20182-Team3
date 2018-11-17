#include <SNIPE.h>

#include <SoftwareSerial.h>
#include "SNIPE.h"

#define TXpin 11
#define RXpin 10
#define ATSerial Serial

String lora_app_key = "00 00 00 00 00 00 00 00 11 11 11 11 11 11 11 11";  //16byte hex key

boolean onoff = HIGH;

SoftwareSerial DebugSerial(RXpin, TXpin);
SNIPE SNIPE(ATSerial);

void setup() {
  ATSerial.begin(115200);

  pinMode(LED_BUILTIN, OUTPUT);

  // put your setup code here, to run once:
  while (ATSerial.read() >= 0) {

  }
  while (!ATSerial);

  DebugSerial.begin(115200);
}

void loop() {
  String ver = SNIPE.lora_recv();
  delay(1000);
  
  if (Serial.available() > 0) {
    String ver = Serial.readString();

    if (ver == "On" || ver == "Off") {
      digitalWrite(LED_BUILTIN, onoff);
      onoff = (!onoff);

      SNIPE.lora_send(ver);
    }
  }


}
