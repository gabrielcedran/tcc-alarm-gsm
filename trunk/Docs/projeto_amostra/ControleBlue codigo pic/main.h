#include <16F877A.h>
#device *=16
#device adc=8

#FUSES NOWDT                    //No Watch Dog Timer
#FUSES XT                       //XT Osc with CLKOUT
#FUSES PUT                    // Power Up Timer
#FUSES PROTECT                //Code  protected from reading
#FUSES NODEBUG                  //No Debug mode for ICD
#FUSES BROWNOUT               //Brownout reset
#FUSES NOLVP                    //No low voltage prgming, B3(PIC16) or B5(PIC18) used for I/O
#FUSES CPD                    //No EE protection
#FUSES NOWRT                    //Program memory not write protected

#use delay(clock=4915200)
#use rs232(baud=9600,parity=N,xmit=PIN_C6,rcv=PIN_C7,bits=8)

