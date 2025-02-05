#define MODE_ONE 4
#define GND_FOR_BUTTON_ONE 5

//#define MODE_TWO 7
//#define GND_FOR_BUTTON_TWO 6

int led = 8;
void setup() {
  pinMode(led, OUTPUT);
  // put your setup code here, to run once:
  pinMode(MODE_ONE, INPUT_PULLUP);
  //pinMode(MODE_TWO, INPUT_PULLUP);
  // ИНИЦИАЛИЗИРУЕМ ПИНЫ ДЛЯ ЗЕМЛИ
  pinMode(GND_FOR_BUTTON_ONE, OUTPUT);
  //pinMode(GND_FOR_BUTTON_TWO, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
   digitalWrite(GND_FOR_BUTTON_ONE, LOW);
   //digitalWrite(GND_FOR_BUTTON_TWO, LOW);

   // кнопки для переключения мода:
   int sensorMODE_1 = digitalRead(MODE_ONE);
   //int sensorMODE_2 = digitalRead(MODE_TWO);

   if (sensorMODE_1 == HIGH /*&& sensorMODE_2 == HIGH */){
    digitalWrite(led, HIGH);

}

else {

digitalWrite(led, LOW);

}
   }

