#include <Servo.h>
 
Servo esc;
int throttlePin = 0;
Servo Thrust;
Servo Elevation;
Servo Rudder;
Servo Aileron;
int greenled = A0;
int redled = A2;
int yellowled = A1;
int AileronValue;
int ElevationValue;
int ThrustValue;
int RudderValue;

/*-----( Import needed libraries )-----*/
#include <NewPing.h>
/*-----( Declare Constants and Pin Numbers )-----*/
#define  TRIGGER_PIN  A3
#define  ECHO_PIN     2
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters).
                         //Maximum sensor distance is rated at 400-500cm.
/*-----( Declare objects )-----*/
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.
/*-----( Declare Variables )-----*/
int DistanceCm;
int frequency_counter = 1;

void setup()
{
  // SET LEDs as Outputs
  pinMode(greenled, OUTPUT);
  pinMode(redled, OUTPUT);
  pinMode(yellowled, OUTPUT);
  // Turn off all LEDs for now
  digitalWrite(yellowled, HIGH);
  digitalWrite(redled, HIGH);
  digitalWrite(greenled, HIGH);
  
  // Setting up Aileron, Elevation, Thrust, Rudder
  setupVariables(9,10,11,6);
  
  // Setting up Serial with Mobile at 115.2 Kbps
  Serial.begin(9600);
  Serial.print("Finshed Setup\n");
  
  
}

void getVariables(){
  // Let me set up a protocol for communication
  // This is a typical string sent from the mobile
  // b symbolizes the begining of the string and e for ending of the string
  // The motor values go from 100 to 900, so 500 is the middle value.
  // Order of Command Aileron -> Elevation -> Thrust -> Rudder
  // b400400400400e
  // Let me setup the initial Value
  String dataString = "b500500100500e";
  // data string format "Motor 1 Percentage + Motor 2 Percentage + Motor 3 Percentage + Motor 4 Percentage"
  dataString = getSerialString();
  if (dataString == ""){
    // Do Nothing
  }
  else if(dataString == "stopstopstopst"){
      Aileron.write(0);
      Elevation.write(0);
      Thrust.write(0);
      Rudder.write(0);
      Serial.print("I Stopped \n");  
  }
  else{
    // Check if it is a valid command begining with b and ending with e
    if(dataString.substring(0,1) == "b" && dataString.substring(13,14) == "e"){
      // Substring the data and indicate by Yellow LED
      Serial.print("I am in, Received: \n"); 
      Serial.print(dataString);
      digitalWrite(yellowled, LOW);
      AileronValue = (stringToNumber(dataString.substring(1,4)));
      ElevationValue = (stringToNumber(dataString.substring(4,7)));
      ThrustValue = (stringToNumber(dataString.substring(7,10)));
      RudderValue = (stringToNumber(dataString.substring(10,13)));
      setValues(AileronValue,ElevationValue,ThrustValue,RudderValue);
      digitalWrite(yellowled, HIGH);
    }
    else{
      // Nothing was received
      Serial.print("Something was wrong in the string received.\n");
      Serial.println(dataString);
      Serial.flush();
    }
  }
}

void loop()
{
    // Making the frequency of the program about 50 Hz
    delay(20);
    // Wait 100ms between pings (about 10 pings/sec). 29ms should be the shortest delay between pings.
    if(frequency_counter == 50){
      digitalWrite(redled, LOW);
      DistanceCm = sonar.ping_cm();
      Serial.print("Ping: ");
      Serial.println(DistanceCm);
      digitalWrite(redled, HIGH);
      frequency_counter = 0;
    }
    frequency_counter++;
   getVariables();
  
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
      delay(10);
      // get the new byte.
      // 15 charecters means this loop should run 15 times
      for(int i =0; i<14; i++){
        char inChar = (char)Serial.read();
        // add it to the inputString:
        inputString += inChar;
      }
    return inputString;
  }
}

void setupVariables(int motorPin1, int motorPin2, int motorPin3, int motorPin4){
  // Turn on the red LED
  digitalWrite(redled, LOW);
  // Attaching Motors to all pins
  Aileron.attach(motorPin1);
  Elevation.attach(motorPin2);
  Thrust.attach(motorPin3);
  Rudder.attach(motorPin4);
  // Setting up the Controller Board and Arming it up
  // Moving throttle to down and rudder to right
  Aileron.write(180);
  Elevation.write(0);
  Thrust.write(0);
  Rudder.write(90);
  digitalWrite(redled, LOW);
  digitalWrite(yellowled, LOW);
  digitalWrite(greenled, LOW);
  delay(4000);
  // Setting to normal initially
  Aileron.write(90);
  Elevation.write(90);
  Thrust.write(90);
  Rudder.write(90);
  // Turn off the red LED
  digitalWrite(redled, HIGH);
  digitalWrite(yellowled, HIGH);
  digitalWrite(greenled, HIGH);
}

void setValues(int motorVal1, int motorVal2, int motorVal3, int motorVal4){
  // Check if all motor values passed are within the range
  digitalWrite(greenled, LOW);
  if ((motorVal1 <= 900) && (motorVal1 >= 100) && (motorVal2 <= 900) && (motorVal2 >= 100) && (motorVal4 <= 900) && (motorVal4 >= 100)){
    // Scaling and setting the values
    // Aileron
    Aileron.write((int)(((motorVal1-100.0)/800.0)*180.0));
    // Elevation
    Elevation.write((int)(((motorVal2-100.0)/800.0)*180.0));
    // Thrust
    Thrust.write((int)(((motorVal3-100.0)/800.0)*180.0));
    // Rudder
    Rudder.write((int)(((motorVal4-100.0)/800.0)*180.0));
  }
  else{
    //Serial.print("Something wrong in values passed to the motors. Check if all the values passed are within the range.\n");
  }
  digitalWrite(greenled, HIGH);
}

