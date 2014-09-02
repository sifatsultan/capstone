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
int big_counter = 0;
int small_counter = 0;
int rx_counter = 0;
int CurrentValue;
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
  Serial.begin(1200);
  Serial.print("Finshed Setup\n");
  delay(5000);
}


void loop()
{  
    // doSonarStuff();
    doSerialStuff();  
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
  if(big_counter == 99999){
      if(small_counter == 99999){
        digitalWrite(redled, LOW);
        DistanceCm = sonar.ping_cm();
        Serial.print("Ping: ");
        Serial.println(DistanceCm);
        digitalWrite(redled, HIGH);
        small_counter = 0;
      }
      small_counter++;
      big_counter = 0;
    }
    big_counter++;
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
  // Attaching Motors to all pins
  Aileron.attach(motorPin1);
  Elevation.attach(motorPin2);
  Thrust.attach(motorPin3);
  Rudder.attach(motorPin4);
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
  // Setting to normal initially
  Aileron.write(90);
  Elevation.write(90);
  Thrust.write(70);
  Rudder.write(90);
  // Turn off all LEDs
  digitalWrite(redled, HIGH);
  digitalWrite(yellowled, HIGH);
  digitalWrite(greenled, HIGH);
}

