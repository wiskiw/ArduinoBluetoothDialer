#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#include <AltSoftSerial.h>



#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels

// SCL - A5
// SDA - A4
// Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
#define OLED_RESET -1 // Reset pin # (or -1 if sharing Arduino reset pin)

// пин ввода цифры подключает
// подключается через внешний pulldown резистор
#define PIN_DIALER_NUMBERS 2

// пин "замка" ввода цифры
// подключается через внешний pulldown резистор
#define PIN_DIALER_LOCKER A1

#define DEBOUNCER_DIALER_MILLISECONDS 80
#define DEBOUNCER_DIALER_LOCKER_MILLISECONDS 80

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

#define LED_PIN A1

AltSoftSerial altSerial;
//AltSoftSerial rxPin = D8
//AltSoftSerial txPin = D9
String btLineBuffer;

// таймеры компенсации дребезга
volatile unsigned long debouncerDialerTimer = 0;
unsigned long debouncerDialerLockerTimer = 0;

// счетчик размыканий контактов набирателя
volatile unsigned char dialerIntrruptCounter = 0; // счетчик вода цифр в интерапторе
byte dialerCounter = 0;

bool isDialingInProgress = false;


void setup() {
  Serial.begin(9600);
  while (!Serial);
  Serial.println("Serial online!");

 
  // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) { // Address 0x3D for 128x64
    Serial.println(F("SSD1306 allocation failed"));
    for(;;); // Don't proceed, loop forever
  }

  attachInterrupt(digitalPinToInterrupt(PIN_DIALER_NUMBERS), onDialerIntrrupt, FALLING);
  pinMode(PIN_DIALER_LOCKER, INPUT);

  pinMode(LED_PIN, OUTPUT);

  // bluetooth init
  altSerial.begin(9600);
  delay(1500);
  altSerial.print("APT+SPP8888\r\n");
  Serial.println("ARD:Ready");

  printReady();
}

void printReady(){
  display.clearDisplay();
  display.setTextSize(2);             // Normal 1:1 pixel scale
  display.setTextColor(SSD1306_WHITE);        // Draw white text
  display.setCursor(32,26);             // Start at top-left corner
  display.println(F("READY!"));
  display.display();
}

void onDialerIntrrupt() {
  bool debouncerDialerCounterLocked = millis() - debouncerDialerTimer < DEBOUNCER_DIALER_MILLISECONDS;

  // компенсация дребезга ввода цифр
  if (!debouncerDialerCounterLocked) {
    debouncerDialerTimer = millis();

    dialerIntrruptCounter++;
  }
}

void loop() {
  dialerLoop();
  bluetoothLoop();
}

void bluetoothLoop(){
    char c;

    if (Serial.available()) {
        c = Serial.read();
        altSerial.print(c);
    }
    if (altSerial.available()) {
        c = altSerial.read();
        //Serial.print(c);
        
        if(c != '\n'){
            btLineBuffer += c;
        } else {
            // end of line
            Serial.print("BK8000L:");
            Serial.println(btLineBuffer);         
            checkCmd(btLineBuffer);
            
            btLineBuffer = ""; 
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

void dialerLoop() {
  bool debouncerDialerLockerLocked = millis() - debouncerDialerLockerTimer < DEBOUNCER_DIALER_LOCKER_MILLISECONDS;
  
  // компенсация дребезга замка ввода
  if (!debouncerDialerLockerLocked) {
        debouncerDialerLockerTimer = millis();

        bool isDialingInProgressUpdated = digitalRead(PIN_DIALER_LOCKER);
        byte dialerCounterUpdated = getCurrentDialerIntrruptCounter();

        // значение счетчика было увеличено
        bool isDialerCounterIncreased = dialerCounter < dialerCounterUpdated;
        
        // замок перешел с состояния "разблокированный" в "заблокированный"        
        bool isDialingFinished = isDialingInProgress && !isDialingInProgressUpdated;
        
        if (isDialerCounterIncreased || (dialerCounterUpdated > 0 && isDialingFinished)){           
            onDialerInput(!isDialingFinished, dialerCounterUpdated);       
        }

        // ввод закончен
        if (isDialingFinished){           
            resetCurrentDialerIntrruptCounter();
        }

        isDialingInProgress = isDialingInProgressUpdated;
        dialerCounter = dialerCounterUpdated;
   }
}

void onDialerInput(bool isInProgress, byte dialerCounter){    
    byte dialerValue = dialerCounter >= 10 ? 0 : dialerCounter;
    printValue(!isInProgress, dialerValue);
    
//    if (isInputInProgress) {
//        // ввод закончен           
//    } else {
//        // ввод начат
//    }
}

byte getCurrentDialerIntrruptCounter() {
    noInterrupts();
    char dialerCounter = dialerIntrruptCounter;
    interrupts();
    return dialerCounter;
}

void resetCurrentDialerIntrruptCounter() {
    noInterrupts();
    dialerIntrruptCounter = 0;
    interrupts();
}

void printValue(bool isFinal, byte value) {
    if(isFinal) Serial.print("[F]");
    Serial.print("input value: ");
    Serial.println(value);


    display.clearDisplay();
    
    display.setTextSize(1);             // Normal 1:1 pixel scale
    display.setTextColor(SSD1306_WHITE);        // Draw white text
    display.setCursor(0,0);             // Start at top-left corner
    display.println(F("value: "));
    
    display.setCursor(56,24);             // Start at top-left corner
    display.setTextSize(4);             // Normal 1:1 pixel scale
    display.println(value);

    if(isFinal){
        display.fillRect(80, 48, 4, 4, WHITE);
    }
    
    display.display();
}
