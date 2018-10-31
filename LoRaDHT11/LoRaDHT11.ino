#include <SoftwareSerial.h>
#include "SNIPE.h"

#define TXpin 11
#define RXpin 10
#define ATSerial Serial

#include "DHT.h"
#define DHTPIN 7
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

String lora_app_key = "00 00 00 00 00 00 00 00 11 11 11 11 11 11 11 11";  //16byte hex key

SoftwareSerial DebugSerial(RXpin, TXpin);
SNIPE SNIPE(ATSerial);

void setup() {
  ATSerial.begin(115200);

  // put your setup code here, to run once:
  while (ATSerial.read() >= 0) {}
  while (!ATSerial);

  DebugSerial.begin(115200);
  DebugSerial.println("SNIPE LoRa PingPong Test");
}

void loop() {
  delay(2000);
  int h = dht.readHumidity();
  int t = dht.readTemperature();
  String str = "b1 " + String(t) + " " + String(h);

  if (SNIPE.lora_send(str))
  {
    DebugSerial.println("send success");
  }
}
