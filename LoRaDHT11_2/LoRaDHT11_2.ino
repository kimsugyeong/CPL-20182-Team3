#include <SNIPE.h>

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

int LED = 3;
boolean onoff=HIGH;

void setup() {
  ATSerial.begin(115200);

  // put your setup code here, to run once:
  while (ATSerial.read() >= 0) {
    pinMode(LED, OUTPUT);
  }
  while (!ATSerial);

  DebugSerial.begin(115200);
}

void loop() {
  delay(2000);
  int h = dht.readHumidity();
  int t = dht.readTemperature();
  String str = "b1 " + String(t) + " " + String(h);

  if (SNIPE.lora_send(str))
  {
    digitalWrite(LED, onoff);
    onoff=(!onoff);
    
    DebugSerial.println("send success");

    String op=SNIPE.lora_recv();

    if(op=="On"){
      digitalWrite(LED, HIGH);
    }

    if(op=="Off"){
      digitalWrite(LED, LOW);
    }
    
  }


  /*
    DebugSerial.println(ver);

    if (ver == "PING" )
    {
    DebugSerial.println("recv success");
    DebugSerial.println(SNIPE.lora_getRssi());
    DebugSerial.println(SNIPE.lora_getSnr());

    if(SNIPE.lora_send("PONG"))
    {
      DebugSerial.println("send success");
    }
    else
    {
      DebugSerial.println("send fail");
      delay(500);
    }
    }
  */
  //delay(1000);
}
