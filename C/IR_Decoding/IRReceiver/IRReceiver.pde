/* 

 Demonstration of the use of the interrupt-driven infra-red remote-
 control receiver code lovingly presented to you, dear dear Arduino
 community, in this the month of November in the year 2007.

 Regards, Charlie.

 See Header for hedification.
 
*/

#include <IR.h>

void setup()
{
  Serial.begin(38400);

  // IR receiver hardware is on pin2.
  //  
  IR::initialise(0);
}

void loop()
{
  // Look Ma, no blocking!
  //
  if (IR::queueIsEmpty())
  {
    // Do something more interesting
  }
  else
  {
    Serial.println("____");

    IR_COMMAND_TYPE code;
    while (IR::queueRead(code))
    {
      Serial.println(code, DEC);
    }
  }
}
