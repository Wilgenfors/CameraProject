#define LASER 8 // Пин для лазера
#define BUTTON 2// Пин для лазера

bool state = 0, ledState = 0;
int gndPin = 6; // Земля для кнопки переключения режима стрельбы
int gndPin_2 = 4; // Земля для лазера
int signalPin = 7; // Пин для режима стрельбы
int delayNumber = 2000;

void setup() {
  pinMode(LASER, OUTPUT);  // инициализируем Pin выход
  pinMode(BUTTON, INPUT_PULLUP);
  pinMode(gndPin, OUTPUT);
  digitalWrite(gndPin, LOW);
  pinMode(gndPin_2, OUTPUT);
   digitalWrite(gndPin_2, LOW);
  pinMode(signalPin, INPUT_PULLUP);
  Serial.begin(9600);
}

void loop() {
  int sensorVal2 = digitalRead(signalPin);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (sensorVal2 == LOW) {    // Если кнопка нажата,
    delayNumber = 200;  // то светодиод будет гореть,
  } else {
    delayNumber = 2000;
  }

  digitalWrite(LASER, LOW);

  // сохраняем информацию, полученную от кнопки, в переменную:
  int sensorVal = digitalRead(BUTTON);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (state == 0) {
    if (sensorVal == LOW) {  // Если кнопка нажата,
      digitalWrite(LASER, HIGH);
      delay(delayNumber);  // то светодиод будет гореть,
      state = 1;
    } else {
      digitalWrite(LASER, LOW);  //а если нет – то не будет
    }
  }
  if (sensorVal == HIGH) {
    state = 0;
  }
}
