## About
This repo includes all the codes that were written during the capstone project which inlcudes
* Android Code
* Arduino Code
* C# Code


## Arduino


The Atmega328P in our PCB reads the data in the serial format and decides which mode to enter and consequently what values to forward to controller board. Any data received in the usb triggers the void serialEvent() function and this is where we take the packet, hold it and then analyze it as to what mode to go. The following analysis is taking place inside the void serialEvent().

```
while(Serial.available()){
  char commands[7];
  
  delay(5);
  Serial.readBytes(commands, 7);
  
  if(commands

}
```
