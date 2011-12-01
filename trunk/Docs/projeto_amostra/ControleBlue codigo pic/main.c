/***************************************
Controle remoto Bluetooth para Android 

Autor:  Arnaldo Jos� Macari  
email:  arnaldomac@terra.com.br

PIC16F877A, Cristal de 4.9152Mhz


****************************************/

#include "main.h"

#include "LCD.C"

#define RS_tam_max_buffer_in  90  //  tamanho m�ximo de um comando





int         RS_tamanho_buffer;         // marca quantos bytes tem no buffer
BYTE        RS_Buffer_in[RS_tam_max_buffer_in]; //buffer para receber comandos 
int1        RS_ok;         //  flag que indica que h� um comando v�lido para ser interpretado
BYTE        RS_serial;     //  byte recebido da serial
int1        Bt1;           //flags que armazenam qual bot�o foi pressionado
int1        Bt2;
int1        Bt3;
int1        Bt4;
int1        st_led1;       //flags que armazenam se o led ta ligado
int1        st_led2;
int1        st_led3;
int1        st_led4;


void clear_botao(void);    // paga o status dos bot�es
void limpa_serial(void);   // limpa os dados da serial e prepara para uma nova recep��o


#int_RB
void  RB_isr(void) 
{
   if (!input(PIN_B4)) Bt1 = true;  // marca qual bot�o foi pressionado
   if (!input(PIN_B5)) Bt2 = true; 
   if (!input(PIN_B6)) Bt3 = true;
   if (!input(PIN_B7)) Bt4 = true;
   clear_interrupt(INT_RB);         // limpa interrup��o de teclado
}



#int_RDA
void  RDA_isr(void) 
{
   RS_serial = getc();          //l� byte da serial
   clear_interrupt(INT_EXT);    //limpa interrup��o de serial

   if (RS_serial == '$')        // verifica se � o caracter de in�cio de transmiss�o
   { 
      RS_tamanho_buffer=0;      // volta o buffer para a posi��o inicial
   }
   else                          
   {                            //  se n�o for in�cio de transmiss�o 
      if ( RS_serial == 13)     //  verifica se � o caracter de fim de transmiss�o
      {
           RS_Buffer_in[RS_tamanho_buffer]=0;      //marca o fim da string com 0          
           RS_ok = true;         // seta a flag que indica buffer com um comando v�lido  
      }
      else 
      {                          //  se n�o for nem in�cio nem fim de transmiss�o.
         if (RS_tamanho_buffer < RS_tam_max_buffer_in)  //  verifica se tem espa�o no buffer
         {
             RS_Buffer_in[RS_tamanho_buffer]= RS_serial; // grava caracter no buffer
             RS_tamanho_buffer++;                        // incrementa a posi��o do buffer
         };
      }
   };         
}



void main()
{

   setup_psp(PSP_DISABLED);    //  desliga v�rios hardwares n�o usados
   setup_spi(SPI_SS_DISABLED);
   setup_timer_0(RTCC_INTERNAL|RTCC_DIV_1);
   setup_timer_1(T1_DISABLED);
   setup_timer_2(T1_DISABLED,1023,1);
   setup_comparator(NC_NC_NC_NC);   
   setup_ccp1(CCP_OFF);
   setup_ccp2(CCP_OFF);   
   setup_vref(FALSE);

   enable_interrupts(INT_RB);  //  liga int de teclado
   enable_interrupts(INT_RDA); //  liga int de byte serial recebido
   enable_interrupts(GLOBAL);  //  ativa as int acima
   lcd_init();       // inicializa LCD
   RS_ok=FALSE;      // inicializa RS_ok
   clear_botao();    // limpa status dos bot�es

   printf(lcd_putc,"\f   ANDROID REMOTE     ");
  
   // TODO: USER CODE!!
   while(true)   //  loop infinito 
   {    
      if (Bt1)   //  se bot�o 1 foi pressionado
      { 
         st_led1 = !st_led1;  //  inverte o marcador de status do led
         output_bit( PIN_B0, st_led1);     // envia novo status para led              
         putc('$');        // envia comando  (ver protocolo no final do c�digo)
         putc('1');
         putc('1');
         if (st_led1) { putc('1');} else { putc('0');};
         putc(13);  
         clear_botao();      // limpa os status dos bot�es           
      };
      
      if (Bt2)   //  idem ao anterior
      {
         st_led2 = !st_led2;
         output_bit( PIN_B1, st_led2);
         putc('$');
         putc('1');
         putc('2');
         if (st_led2) { putc('1');} else { putc('0');};
         putc(13); 
         clear_botao();
      };  
      
      if (Bt3)  //  idem ao anterior
      {
         st_led3 = !st_led3;
         output_bit( PIN_B2, st_led3);
         putc('$');
         putc('1');
         putc('3');
         if (st_led3) { putc('1'); } else { putc('0'); };
         putc(13); 
         clear_botao();
      };      
      
      if ( Bt4)  //  idem ao anterior
      { 
         st_led4 = !st_led4;
         output_bit( PIN_B3, st_led4);
         putc('$');
         putc('1');
         putc('4');
         if (st_led4) { putc('1');} else { putc('0');};
         putc(13); 
         clear_botao();
      };    
      
      
      if (RS_ok)   //  se h� um comando v�lido no buffer
      {                         
        switch ( RS_Buffer_in[0])  //  testa o primeiro caracter
        {
            case '1':  // se for   '1'   � comando para os leds 
               
               switch (RS_Buffer_in[1])  //  testa o segundo caracter 
                {                         
                  case '1':   //  se for 1, o comando � para o led 1    
                    if ( RS_Buffer_in[2] == '1')   //testa  o terceiro caracter 
                      {                             // se for um � para ligar
                        output_bit( PIN_B0, true); //  liga o led1
                        st_led1 = true;            //  seta o marcador de estado do led 1
                      }
                      else
                      {                            //  se for 0 
                        output_bit( PIN_B0, false);//  desliga led1
                        st_led1 = false;           //  limpa o marcador de estado do led 1
                      };                                                                             
                  break;   
                                           
                  case '2':   //  idem para led 2
                    if ( RS_Buffer_in[2] == '1') 
                      { 
                        output_bit( PIN_B1, true);
                        st_led2 = true;
                      }
                      else
                      {
                        output_bit( PIN_B1, false);
                        st_led2 = false;
                      };                                                                             
                  break; 
                                           
                  case '3':    //  idem para led 3
                    if ( RS_Buffer_in[2] == '1') 
                      { 
                        output_bit( PIN_B2, true);
                        st_led3 = true;
                      }
                      else
                      {
                        output_bit( PIN_B2, false);
                        st_led3 = false;
                      };                                                                             
                  break; 
                                           
                  case '4':    //  idem para led 4
                    if ( RS_Buffer_in[2] == '1') 
                      { 
                        output_bit( PIN_B3, true);
                        st_led4 = true;
                      }
                      else
                      {
                        output_bit( PIN_B3, false);
                        st_led4 = false;
                      };                                                                             
                  break; 
                }                                           
            break;    
            
            case '2':  //   se o primeiro caracter for 2 � comando de LCD
                  switch (RS_Buffer_in[1])
                  {
                        case '1': //  se o segundo for 1 � comando de escrita
                           printf(lcd_putc,"\f%s",RS_Buffer_in+2); 
                        break;
                        
                        case '2': //   se o segundo caracter for 2
                        //   reservado para  outras fun��es de lcd
                        break;
                  };
            break;           
        }
        limpa_serial();
      }   
    }  
}



void clear_botao(void)  //  limpa o status dos bot�es
{
   Bt1=false;
   Bt2=false;
   Bt3=false;
   Bt4=false;
}

void limpa_serial(void)  //  limpa buffer de entrada serial 
{
  RS_ok = FALSE;        //   desmarca a flag  de comando v�lido
  RS_tamanho_buffer=0;  //  zera o tamanho do buffer
  RS_Buffer_in[0]=0;    //  masca o inicio do buffer como zero 

}




/*  PROTOCOLO 

Para garantir uma comunica��o segura e f�cil de ser expandida 
para muitas outras fun��es foi usado o seguinte protocolo 
de 5 bytes:

$  -> indica inicio de transmiss�o
1  -> indica que o comando � para os leds (  2  para lcd) 
1  -> indica qual led recebera o comando ( s�o 4 leds: 1,2,3,4)
1  -> indica se � para ligar ou desligar o LED ( 1 para ligar  e 0 para desligar) 
13 ->  ou '\r'  indica fim de transmiss�o

Ent�o.... 

Para ligar o led 3 fica:      $ 1 3 1 13
Para desligar o led 3 fica:   $ 1 3 0 13 

Para o LCD

$  -> indica inicio de transmiss�o
2  -> indica que o comando � para o LCD 
1  -> indica o que � para ser feito,  1 � para escrever no LCD 
TEXTO  ->  texto a ser escrito
13 ->  ou '\r'  indica fim de transmiss�o


Para escrever Android no LCD fica     $ 2 1 Android  13

*/


