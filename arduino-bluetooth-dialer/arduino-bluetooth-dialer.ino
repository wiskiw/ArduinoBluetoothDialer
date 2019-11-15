// пин ввода цифры подключает
// подключается черзе внешний pullup резистор
#define PIN_DIALER_NUMBERS 2 

// пин "замка" ввода цифры
// подключается черзе внешний pullup резистор
#define PIN_DIALER_LOCKER A1 

#define DEBOUNCER_DIALER_MILLISECONDS 80
#define DEBOUNCER_DIALER_LOCKER_MILLISECONDS 40

volatile unsigned long debouncerDilerTimer = 0;
volatile unsigned char dialerIntrruptCounter = 0; // счетчик вода цифр

unsigned long debouncerDilerLockerTimer = 0;
bool isDialerLockedPreview = false;

void setup() {
  Serial.begin(9600);

  attachInterrupt(digitalPinToInterrupt(PIN_DIALER_NUMBERS), onDialerIntrrupt, FALLING);
  pinMode(PIN_DIALER_LOCKER, INPUT);
}

void onDialerIntrrupt() {
  bool debouncerDialerCounterLocked = millis() - debouncerDilerTimer < DEBOUNCER_DIALER_MILLISECONDS;
  
 // компенсация дребезга ввода цифр
  if (!debouncerDialerCounterLocked){
	debouncerDilerTimer = millis();
	
    dialerIntrruptCounter++;  
  } 
}

void loop() {
  // main loop
  dialerLoop();
}

void dialerLoop(){
	bool debouncerDialerLockerLocked = millis() - debouncerDilerLockerTimer < DEBOUNCER_DIALER_LOCKER_MILLISECONDS;
	
	// компенсация дребезга замка ввода
	if (!debouncerDialerLockerLocked){
		debouncerDilerLockerTimer = millis();
		
		bool isDialerLocked = digitalRead(PIN_DIALER_LOCKER);
		//  Serial.print("isDialerLocked: ");
		//  Serial.println(isDialerLocked);
		
		if (isDialerLocked != isDialerLockedPreview){
			isDialerLockedPreview = isDialerLocked;
			onDilerLockerStateChanged(isDialerLocked)
		} 
	}
}

void onDilerLockerStateChanged(bool isLocked){
	char dialerCounter = getCurrentDialerIntrruptCounter();
	char dialerValue = dialerCounter >= 10 ? 0 : dialerCounter;
	printValue(isLocked, dialerValue);

	if(isLocked){
		// ввод закончен
		resetCurrentDialerIntrruptCounter();
	} else {
		// ввод начат  
	}
}

char getCurrentDialerIntrruptCounter() {
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

void printValue(bool isFinal, unsigned char value){
	Serial.print("input value: ");
	Serial.println(value);
}

