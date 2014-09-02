#include <Servo.h>
 
Servo esc;
int throttlePin = 0;
Servo motor1;
Servo motor2;
Servo motor3;
Servo motor4;
double motorValues[4];
int greenled = A0;
int redled = A2;
int yellowled = A1;
int CurrentSpeed = 59;
boolean goingUp = true;

/*-----( Import needed libraries )-----*/
#include <NewPing.h>
/*-----( Declare Constants and Pin Numbers )-----*/
#define  TRIGGER_PIN  11
#define  ECHO_PIN     10
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters).
                         //Maximum sensor distance is rated at 400-500cm.
/*-----( Declare objects )-----*/
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.
/*-----( Declare Variables )-----*/
int DistanceIn;
int DistanceCm;

void setup()
{
  pinMode(greenled, OUTPUT);
  pinMode(redled, OUTPUT);
  pinMode(yellowled, OUTPUT);
  digitalWrite(yellowled, HIGH);
  digitalWrite(redled, HIGH);
  digitalWrite(greenled, HIGH);
  // Initializing motors to some speed near 93%
  motorValues[0] = 100;
  motorValues[1] = 100;
  motorValues[2] = 100;
  motorValues[3] = 100;
  
  // Setting up ESCs and programming them to run at max speed 106 (pulling ~3.5Amps)
  setupMotorsAtPins(9,10,11,6);
  
  // Setting up Serial with Mobile at 115.2 Kbps
  Serial.begin(115200);
  Serial.print("Finshed Setup\n");
}


void getMotorSpeeds(){
  String dataString = "0N0+0N0+0N0+0N0";
  // data string format "Motor 1 Percentage + Motor 2 Percentage + Motor 3 Percentage + Motor 4 Percentage"
  dataString = getSerialString();
  if(dataString == ""){
    dataString = "000+000+000+000";
  }
  else{
    if(dataString.substring(3,4) == "+" && dataString.substring(7,8) == "+" && dataString.substring(11,12) == "+"){
      // Substringing the data
      digitalWrite(yellowled, LOW);
      motorValues[0] = ((((stringToNumber(dataString.substring(0,3)))-100)/100.0)*76.0)+30;
      Serial.print("data 1: "+dataString.substring(0,3)+"\n");
      motorValues[1] = (((stringToNumber(dataString.substring(4,7))-100)/100.0)*76.0)+30;
      Serial.print("data 2: "+dataString.substring(4,7)+"\n");
      motorValues[2] = (((stringToNumber(dataString.substring(8,11))-100)/100.0)*76.0)+30;
      Serial.print("data 3: "+dataString.substring(8,11)+"\n");
      motorValues[3] = (((stringToNumber(dataString.substring(12,15))-100)/100.0)*76.0)+30;
      Serial.print("data 4: "+dataString.substring(12,15)+"\n");
      Serial.print("Motor 1 Value: ");
      Serial.print(motorValues[0],DEC);
      Serial.print("\nMotor 2 Value: ");
      Serial.print(motorValues[1],DEC);
      Serial.print("\nMotor 3 Value: ");
      Serial.print(motorValues[2],DEC);
      Serial.print("\nMotor 4 Value: ");
      Serial.print(motorValues[3],DEC);
      Serial.print("\n");
      motor1.write(motorValues[0]);
      motor2.write(motorValues[1]);
      motor3.write(motorValues[2]);
      motor4.write(motorValues[3]);
      digitalWrite(yellowled, HIGH);
    }
    else{
      Serial.print("Don't Bullshit with me. -_-\n");
    }
    
  }
}

void loop()
{
 writeToAllMotors(35);
 Serial.println("Hi from Shit!");
 DistanceCm = sonar.ping_cm();
 Serial.print("Distance in Cm: ");
 Serial.print(DistanceCm);
 
 delay(3000);
 digitalWrite(redled, LOW);
 digitalWrite(yellowled, LOW);
  
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
  digitalWrite(greenled, LOW);
  motor1.write(143);
  motor2.write(143);
  motor3.write(143);
  motor4.write(143);
  delay(4000);
  digitalWrite(greenled, HIGH);
  // Programming ESCs for minimum values
  digitalWrite(yellowled, LOW);
  motor1.write(30);
  motor2.write(30);
  motor3.write(30);
  motor4.write(30);
  delay(4000);
  digitalWrite(yellowled, HIGH);
  // Setting to normal speed initially
  motor1.write(59);
  motor2.write(59);
  motor3.write(59);
  motor4.write(59);
  digitalWrite(redled, LOW);
}

void writeToAllMotors(int value){
  // Writing a value to all ESCs
  digitalWrite(greenled, LOW);
  motor1.write(value);
  motor2.write(value);
  motor3.write(value);
  motor4.write(value);
  digitalWrite(greenled, HIGH);
}

