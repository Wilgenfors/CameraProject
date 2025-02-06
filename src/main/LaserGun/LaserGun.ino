#define LASER 8
#define BUTTON 2
#define MODE 9
bool state = 0, ledState = 0;
int gndPin = 6;
int signalPin = 7;
int delayNumber = 2000;

void setup() {
  pinMode(LASER, OUTPUT);  // инициализируем Pin выход
  pinMode(BUTTON, INPUT_PULLUP);
  pinMode(gndPin, OUTPUT);
  digitalWrite(gndPin, LOW);
  pinMode(signalPin, INPUT_PULLUP);
  Serial.begin(9600);
}

void loop() {
  int sensorVal2 = digitalRead(signalPin);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (sensorVal2 == LOW) {    // Если кнопка нажата,
    delayNumber = 200;  // то светодиод будет гореть,
    digitalWrite(13, HIGH);
  } else {
    delayNumber = 2000;
    digitalWrite(13, LOW);
  }

  digitalWrite(LASER, LOW);

  // сохраняем информацию, полученную от кнопки, в переменную:
  int sensorVal = digitalRead(BUTTON);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (state == 0) {
    if (sensorVal == LOW) {  // Если кнопка нажата,
      digitalWrite(LASER, HIGH);
      Serial.print("sensorVal2 = ");
      Serial.println(sensorVal2);
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
