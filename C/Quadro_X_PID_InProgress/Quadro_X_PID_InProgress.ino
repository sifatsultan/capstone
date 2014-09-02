#include <Servo.h>
Servo esc;
int throttlePin = 0;
Servo Thrust;
Servo Elevation;
Servo Rudder;
Servo Aileron;
int greenled = A2;
int redled = A0;
int yellowled = A1;
byte AileronValue;
byte ElevationValue;
byte ThrustValue;
byte RudderValue;
int CurrentState = 0;
int signalVoltage = 0;

/*--- PID ---*/
#include <PID_v1.h>

//Define Variables we'll be connecting to
double Setpoint, Input, Output;

//Specify the links and initial tuning parameters
PID myPID(&Input, &Output, &Setpoint,2,5,1, DIRECT);

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
int big_counter = 0;
int small_counter = 0;
int rx_counter = 0;
int CurrentValue;
char commands[6];

void setup()
{
  // SET LEDs as Outputs
  pinMode(greenled, OUTPUT);
  pinMode(redled, OUTPUT);
  pinMode(yellowled, OUTPUT);
  // Turn OFF all LEDs for now
  digitalWrite(yellowled, HIGH);
  digitalWrite(redled, HIGH);
  digitalWrite(greenled, HIGH);
  
  // Setting up Aileron, Elevation, Thrust, Rudder
  setupVariables(9,10,11,6);
  Setpoint = 100;
  //turn the PID on
  myPID.SetMode(AUTOMATIC);
  
  // Setting up Serial with Mobile at 115.2 Kbps
  Serial.begin(1200);
  Serial.print("Finshed Setup\n");
  delay(5000);
  
}

void loop()
{  
  switch(CurrentState){
      case 0: 
      {
        // ---- Idle State ---- //
        // Setting to 0 speed if not at 0 speed
        if((Aileron.read() != 0) && (Elevation.read() != 0) && (Thrust.read() != 67) && (Rudder.read() != 0)){
          Aileron.write(0);
          Elevation.write(0);
          Thrust.write(67);
          Rudder.write(0);
          Serial.println("Idle State Entered.");
          digitalWrite(greenled, LOW);
        }
         break;
      }
      case 1: 
      {
        // ---- Slow Speed State ---- //
        // Setting to slow speed if not at slow speed
        if((Aileron.read() != 0) && (Elevation.read() != 0) && (Thrust.read() != 67) && (Rudder.read() != 0)){
          Aileron.write(0);
          Elevation.write(0);
          Thrust.write(67);
          Rudder.write(0);
          Serial.println("Slow Speed State Entered.");
        }
        break;
      }
      case 2: 
      {
        // ---- Free Hand Guesture Control State ---- //
        // Setting to whatever speeds received from ground station
        if((Aileron.read() != AileronValue) && (Elevation.read() != ElevationValue) && (Thrust.read() != ThrustValue) && (Rudder.read() != RudderValue)){
          Aileron.write(AileronValue);
          Elevation.write(ElevationValue);
          Thrust.write(ThrustValue);
          Rudder.write(RudderValue);
          Serial.println("Free Hand Guesture Control State Entered.");
        }
          break;
      }
      case 3: 
      {
        // ---- Altitude Hold State ---- //
        // Initializing PID Altitude Hold
        Input = DistanceCm;
        // signalVoltage = analogRead(A0);
        // Input = map(signalVoltage,0,1023,0,100);
        myPID.Compute();
        int OutputToThrust = map(Output,0,255,62,124);
        Thrust.write(OutputToThrust);
        Serial.println("Hold State Entered.");
        Serial.print("Input:");
        Serial.print(Input);
        Serial.print(" Thrust:");
        Serial.println(OutputToThrust);
        delay(30);
        break;
      }
    }
     
}


void killyourself(){
  digitalWrite(redled, LOW);
  CurrentValue = Thrust.read();
  CurrentValue--;
  Thrust.write(CurrentValue);
  delay(50);
  digitalWrite(redled, HIGH);
}

void doSonarStuff(){
        digitalWrite(redled, LOW);
        DistanceCm = sonar.ping_cm();
        Serial.print("Ping: ");
        Serial.println(DistanceCm);
        digitalWrite(redled, HIGH);
}

void serialEvent(){
  while (Serial.available()) {
      char commands[7];
      delay(5);
      Serial.readBytes(commands,7);
      if(commands[0] == commands[6]){
        // Valid command detected
          if((char)commands[1] == 'a'){
            // Idle State
            CurrentState = 0;
          }
          else if((char)commands[1] == 'b'){
            // Slow Speed State
            CurrentState = 1;
          }
          else if((char)commands[1] == 'c'){
            // Free Hand Guesture Control State
            CurrentState = 2;
            AileronValue = commands[2];
            ElevationValue = commands[3];
            ThrustValue = commands[4];
            RudderValue = commands[5];
          }
          else if((char)commands[1] == 'd'){
            // Altitude Hold State
            CurrentState = 3;
            // PID shit goes here
          }
          else {
            // Idle State
            CurrentState = 0;
          }
      }
      else{
        Serial.println("Wrong Command!");
      }
  } 
}

void getSerialCommandArray(){
  while (Serial.available()) {
      char commands[7];
      delay(5);
      Serial.readBytes(commands,7);
      if(commands[0] == commands[6]){
        // Valid command detected
          if((char)commands[1] == 'b'){
            // Slow Speed State
            CurrentState = 1;
          }
          else if((char)commands[1] == 'c'){
            // Free Hand Guesture Control State
            CurrentState = 2;
            AileronValue = commands[2];
            ElevationValue = commands[3];
            ThrustValue = commands[4];
            RudderValue = commands[5];
          }
          else if((char)commands[1] == 'd'){
            // Altitude Hold State
            CurrentState = 3;
            // PID shit goes here
          }
          else {
            // Idle State
            CurrentState = 0;
          }
      }
  } 
}

void doSerialStuff(){
  while (Serial.available()) {
      char commands[6];
      delay(5);
      Serial.readBytes(commands,6);
      Aileron.write(commands[1]);
      Elevation.write(commands[2]);
      Thrust.write(commands[3]);
      Rudder.write(commands[4]);
  } 
}


void setupVariables(int motorPin1, int motorPin2, int motorPin3, int motorPin4){
  // Turn ON all LEDs
  digitalWrite(redled, LOW);
  digitalWrite(yellowled, LOW);
  digitalWrite(greenled, LOW);
  /* I changed this on Saturday
  // Attaching Motors to all pins
  Aileron.attach(motorPin1);
  Elevation.attach(motorPin2);
  Thrust.attach(motorPin3);
  Rudder.attach(motorPin4);
  */
  // Setting up the Controller Board and Arming it up
  // Moving throttle to down and rudder to right
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
  delay(4000);
  // Setting to very slow initially
  Aileron.write(0);
  Elevation.write(0);
  Thrust.write(67);
  Rudder.write(0);
  // Turn off all LEDs
  digitalWrite(redled, HIGH);
  digitalWrite(yellowled, HIGH);
  digitalWrite(greenled, HIGH);
}

