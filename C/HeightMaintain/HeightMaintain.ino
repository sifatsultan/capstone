/*-----( Import needed libraries )-----*/
#include <NewPing.h>
#include <Servo.h>

/*-----( Declare Constants and Pin Numbers )-----*/
Servo esc;
int throttlePin = 0;
Servo motor1;
Servo motor2;
Servo motor3;
Servo motor4;
int DistanceRequired;
double motorValues[4];
#define  TRIGGER_PIN  11
#define  ECHO_PIN     10
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters).

int CurrentDistanceInCm;

/*-----( Declare objects )-----*/
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.

void setup()
{
  
  // Initializing motors to some speed near 93%
  motorValues[0] = 100;
  motorValues[1] = 100;
  motorValues[2] = 100;
  motorValues[3] = 100;
  
  // Setting up ESCs and programming them to run at max speed 106 (pulling ~3.5Amps)
  setupMotorsAtPins(9,10,11,12);
  
  // Setting up Serial with Mobile at 115.2 Kbps
  Serial.begin(115200);
  Serial.print("Finshed Setup\n");
}

int getDistanceInCm(){
  String distanceString = "NNN";
  // data string format "Motor 1 Percentage + Motor 2 Percentage + Motor 3 Percentage + Motor 4 Percentage"
  dataString = getSerialString();
  if(dataString == ""){
    // No data received, Do Nothing, just reset the datastring
    dataString = "NNN";
    return -1;
  }
  else{
    // Data was received, so convert it and send back 
    return stringToNumber(dataString);
  }
}

void loop()
{
  
  DistanceRequired = getDistanceInCm();
  delay(70);// Wait 100ms between pings (about 10 pings/sec). 29ms should be the shortest delay between pings.
  CurrentDistanceInCm = sonar.ping_cm();
  if(CurrentDistanceInCm < DistanceRequired){
    // Current height is low, Speed it Up!
    motor1.write(motorValues[0] + 1);
    motor2.write(motorValues[1] + 1);
    motor3.write(motorValues[2] + 1);
    motor4.write(motorValues[3] + 1);
  }
  else if (CurrentDistanceInCm > DistanceRequired) {
    // Current height is high, Speed it Down!
    motor1.write(motorValues[0] - 1);
    motor2.write(motorValues[1] - 1);
    motor3.write(motorValues[2] - 1);
    motor4.write(motorValues[3] - 1);
  }
  else{
    // Current height is perfect, Don't Change it
  }
  
}

int stringToNumber(String thisString) {
  int i, value, length;
  length = thisString.length();
  char blah[(length+1)];
  for(i=0; i<length; i++) {
    blah[i] = thisString.charAt(i);
  }
  blah[i]=0;
  value = atoi(blah);
  return value;
}

String getSerialString() {
  String inputString = "";           // a string to hold incoming data
  boolean stringComplete = false;    // whether the string is complete
  while (Serial.available()) {
    do{
      // get the new byte:
      for(int i=0; i<15; i++){
      char inChar = (char)Serial.read();
      // add it to the inputString:
      inputString += inChar;
      // if the incoming character is a newline, set a flag
      // so the main loop can do something about it:
      if (inChar == '\n') {
        stringComplete = true;
      } 
      }

    }while(stringComplete = false);
    return inputString;
  }
}

void setupMotorsAtPins(int motorPin1, int motorPin2, int motorPin3, int motorPin4){
  // Attaching Motors to all pins
  motor1.attach(motorPin1);
  motor2.attach(motorPin2);
  motor3.attach(motorPin3);
  motor4.attach(motorPin4);
  // Programming ESCs for maximum values
  motor1.write(143);
  motor2.write(143);
  motor3.write(143);
  motor4.write(143);
  delay(4000);
  // Programming ESCs for minimum values
  motor1.write(30);
  motor2.write(30);
  motor3.write(30);
  motor4.write(30);
  delay(4000);
  // Setting to normal speed initially
  motor1.write(59);
  motor2.write(59);
  motor3.write(59);
  motor4.write(59);
  delay(4000);
}
