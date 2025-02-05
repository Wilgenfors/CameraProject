#define LASER 8
#define BUTTON 2
#define MODE 9
bool state = 0, ledState = 0;

void setup() {
  pinMode(LASER, OUTPUT);  // инициализируем Pin выход
  pinMode(BUTTON, INPUT_PULLUP);
}

void loop() {


  digitalWrite(LASER, LOW);

  // сохраняем информацию, полученную от кнопки, в переменную:
  int sensorVal = digitalRead(BUTTON);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (state == 0) {
    if (sensorVal == LOW) {  // Если кнопка нажата,
      digitalWrite(LASER, HIGH);
      delay(2000);  // то светодиод будет гореть,
      state = 1;
    } else {
      digitalWrite(LASER, LOW);  //а если нет – то не будет
    }
  }
  if (sensorVal == HIGH) {
    state = 0;
  }
}
