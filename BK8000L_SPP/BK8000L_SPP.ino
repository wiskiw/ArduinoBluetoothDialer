#include <AltSoftSerial.h>

#define LED_PIN A1

AltSoftSerial altSerial;

//AltSoftSerial rxPin = D8
//AltSoftSerial txPin = D9

String line;

void setup() {
    Serial.begin(9600); // opens serial port, sets data rate to 9600 bps
    while (!Serial);

    altSerial.begin(9600);
    
    pinMode(LED_PIN, OUTPUT);
  
    delay(1500);
    altSerial.print("APT+SPP8888\r\n");
    
    Serial.println("ARD:Ready");
}

void loop() {

    char c;

    if (Serial.available()) {
        c = Serial.read();
        altSerial.print(c);
    }
    if (altSerial.available()) {
        c = altSerial.read();
        //Serial.print(c);
        
        if(c != '\n'){
            line += c;
        } else {
            // end of line
        
            Serial.print("BK8000L:");
            Serial.println(line);         
            checkCmd(line);
            
            line = ""; 
        }
    }
}

void checkCmd(String cmd){
    if(cmd.startsWith("APR+1")){
        digitalWrite(LED_PIN, HIGH);
        
    } else if(cmd.startsWith("APR+0")) {
        digitalWrite(LED_PIN, LOW);
    }
}
