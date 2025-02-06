#define MODE_ONE 4
#define GND_FOR_BUTTON_ONE 5
int gndPin = 6;
int signalPin = 7;
//#define MODE_TWO 7
//#define GND_FOR_BUTTON_TWO 6

int led = 13;
void setup() {
  pinMode(led, OUTPUT);
  pinMode(gndPin, OUTPUT);
  digitalWrite(gndPin, LOW);
  // put your setup code here, to run once:
  pinMode(signalPin, INPUT_PULLUP);
  //pinMode(MODE_TWO, INPUT_PULLUP);
  // ИНИЦИАЛИЗИРУЕМ ПИНЫ ДЛЯ ЗЕМЛИ
  // pinMode(GND_FOR_BUTTON_ONE, OUTPUT);
  //pinMode(GND_FOR_BUTTON_TWO, OUTPUT);
}

void loop() {

  // сохраняем информацию, полученную от кнопки, в переменную:
  int sensorVal = digitalRead(signalPin);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (sensorVal == LOW) {    // Если кнопка нажата,
    digitalWrite(13, HIGH);  // то светодиод будет гореть,
  } else {
    digitalWrite(13, LOW);  //а если нет – то не будет
  }
  // put your main code here, to run repeatedly:
  // digitalWrite(GND_FOR_BUTTON_ONE, LOW);
  // //digitalWrite(GND_FOR_BUTTON_TWO, LOW);

  // // кнопки для переключения мода:
  // int sensorMODE_1 = digitalRead(MODE_ONE);
  // //int sensorMODE_2 = digitalRead(MODE_TWO);

  // if (sensorMODE_1 == HIGH /*&& sensorMODE_2 == HIGH */) {
  //   digitalWrite(led, HIGH);

  // }

  // else {

  //   digitalWrite(led, LOW);
  // }
}
