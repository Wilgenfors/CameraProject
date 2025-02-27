//#define MODE_ONE 4
//#define GND_FOR_BUTTON_ONE 5

int gndPin = 4;
int signalPin = 2;
int led = 8;

void setup() {
  pinMode(led, OUTPUT);
  pinMode(gndPin, OUTPUT);
  digitalWrite(gndPin, LOW);
  pinMode(signalPin, INPUT_PULLUP);
  //Serial.begin(9600);
}

void loop() {

  // сохраняем информацию, полученную от кнопки, в переменную:
  int sensorVal = digitalRead(signalPin);
  // Когда кнопка не нажата, Arduino считывает это как HIGH, а если нажата, то как LOW.
  if (sensorVal == LOW) {    // Если кнопка нажата,
    // то светодиод будет гореть,
    digitalWrite(8, HIGH);
  } else {
    digitalWrite(8, LOW);
  }
}
