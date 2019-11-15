// пин ввода цифры подключает
// подключается через внешний pulldown резистор
#define PIN_DIALER_NUMBERS 2

// пин "замка" ввода цифры
// подключается через внешний pulldown резистор
#define PIN_DIALER_LOCKER A1

#define DEBOUNCER_DIALER_MILLISECONDS 80
#define DEBOUNCER_DIALER_LOCKER_MILLISECONDS 80

// таймеры компенсации дребезга
volatile unsigned long debouncerDialerTimer = 0;
unsigned long debouncerDialerLockerTimer = 0;

// счетчик размыканий контактов набирателя
volatile unsigned char dialerIntrruptCounter = 0; // счетчик вода цифр в интерапторе
byte dialerCounter = 0;

bool isDialingInProgress = false;


void setup() {
  Serial.begin(9600);

  attachInterrupt(digitalPinToInterrupt(PIN_DIALER_NUMBERS), onDialerIntrrupt, FALLING);
  pinMode(PIN_DIALER_LOCKER, INPUT);
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
  // main loop
  dialerLoop();
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
}
