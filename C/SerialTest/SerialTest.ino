void setup() {
  // initialize serial:
  Serial.begin(1200);
}

void serialEvent() {
  while (Serial.available()) {
    char commands[1];
    delay(5);
    Serial.readBytes(commands,1);
    if(commands[0] == 'a'){
      Serial.println("Got In...");
    }
  }
}

void loop() {
  
}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */


