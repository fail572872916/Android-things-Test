void setup() {
  // put your setup code here, to run once:
   Serial.begin(9600);
  Serial.println("Uart Initialized. Now commucation begins...");
}

void loop() {
   while (Serial.available() > 0)
  {
    String msg = Serial.readString();
    Serial.println(msg);
  }
}

