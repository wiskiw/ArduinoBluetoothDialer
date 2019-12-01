#include "BK8000L.h"
#include <SoftwareSerial.h> //if using SW, with HW no need for this

#define LED_PIN A1

SoftwareSerial sfSerial(7, 6); //rxPin, txPin

String str;

void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600); // opens serial port, sets data rate to 9600 bps
  while (!Serial);

  sfSerial.begin(9600); // opens serial port, sets data rate to 9600 bps
  while (!sfSerial);
  // set the data rate for the SoftwareSerial port

  pinMode(LED_PIN, OUTPUT);
  
  Serial.println("ARD:Ready");

  delay(3000);
  sfSerial.println("APT+SPP8888");
}

void loop() {
  if (sfSerial.available() > 0) {
    str = sfSerial.readString(); // read the incoming byte:
    Serial.print("BK8000L: ");
    Serial.println(str);
    checkCmd(str);
  }
}

void checkCmd(String cmd){
    if(cmd.startsWith("APR+1")){
        digitalWrite(LED_PIN, HIGH);
        
    } else if(cmd.startsWith("APR+0")) {
        digitalWrite(LED_PIN, LOW);
    }
}
