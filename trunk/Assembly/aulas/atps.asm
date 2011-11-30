;CÓDIGO BASE
;PIC16F628A
;OSCILADOR INTERNO 4MHZ
;PROF. IVAIR TEIXERA

; ******************************************************************************
;                         CONFIGURAÇÃO DO MICROCONTROLADOR.
; ******************************************************************************
    LIST 		P=16F628A                 ;INFORMA O PIC UTILIZADO. 
    INCLUDE   	<P16F628A.INC>            ;INCLUSÃO DA BIBLIOTECA.
    __CONFIG 	_CP_OFF & _WDT_OFF & _PWRTE_OFF & _BODEN_OFF & _MCLRE_OFF   &   _INTRC_OSC_NOCLKOUT   &   _LVP_OFF 
		;ATENÇÃO O CONFIG TEM QUE SER EM UMA ÚNICA LINHA.
    ERRORLEVEL 	-302	                  ;RETIRA MSG OPERAÇÃO NO BANCO 1.

; ******************************************************************************
;                          DECLARAÇÃO DE VARIÁVEIS E CONSTANTES.
; ******************************************************************************
    
    CBLOCK 0X20                		;0X20 É O INÍCIO DA MEMÓRIA RAM
        T1		                    ;T1 NO ENDEREÇO 0X20
        T2		                    ;T2 NO ENDEREÇO 0X21
        T3
    ENDC
    #DEFINE 	BTN_LIGA PORTA,0       ;DEFINE QUE BTN1 = PORTA,0
    #DEFINE 	LED_CRZ PORTB,5        ;DEFINE QUE BTN1 = PORTA,0
    #DEFINE 	LED_FUN PORTB,6        ;DEFINE QUE BTN1 = PORTA,0
    #DEFINE 	RELE PORTB,7      	    ;DEFINE QUE BTN1 = PORTA,0
; ******************************************************************************
;                          VETORES DE RESET E INTERRUPÇÃO.
; ******************************************************************************

    ORG 0X00		     			;A EXECUÇÃO INICIA NESSE ENDEREÇO.
    GOTO	   INICIO	            ;VAI PARA A SUB-ROTINA INÍCIO. 
	
    ORG	0X04		                ;VEM NESSE ENDEREÇO COM A INTERRUPÇÃO.
    RETFIE		                    ;RETORNA DA INTERRUPÇÃO.

; ******************************************************************************
;                                 ROTINAS GERAIS.
; ******************************************************************************
AGUARDA
	BTFSC BTN_LIGA
	GOTO LIGA
	GOTO AGUARDA

LIGA
	BSF	LED_CRZ
	BSF	 LED_FUN
	BSF	 RELE
	CALL 	TEMPO
	BCF	LED_CRZ
	CALL 	TEMPO
	CALL 	TEMPO
	CALL 	TEMPO
	CALL 	TEMPO	
	BCF	RELE
	BCF	LED_FUN
	GOTO 	AGUARDA
    

; ******************************************************************************
;                                 ROTINAS DE TEMPORIZAÇÃO.
; ******************************************************************************

TEMPO  
    MOVLW		.12
    MOVWF		T3 			  ;INICIALIZA T2 COM 255                            		 ;TEMPO DE 196MS
    MOVLW		.166
    MOVWF	T2 			           	 ;INICIALIZA T2 COM 255
    MOVLW	.166
    MOVWF	T1                      		;INICIALIZA T1 COM 255
    DECFSZ	T1, F			        ;DECREMENTA T1 E SALTA SE T1 = 0
    GOTO	$-1			           	 ;VOLTA UMA LINHA
    DECFSZ	T2, F
    GOTO	$-5
   DECFSZ	T2, F
    GOTO	$-9
    RETURN				            ;RETORNA DA CHAMADA CALL

; ******************************************************************************
;                               CONFIGURAÇÃO INICIAL.
; ******************************************************************************
INICIO
    BSF     STATUS,RP0			   		 ;VAI PARA O BANCO 1
    MOVLW   B'00000000'				;0=SAÍDA    1=ENTRADA
    MOVWF   TRISA				    		;PORTA,0 ENTRADA RESTANTE SAÍDA
    MOVLW   B'00000000'				;0=SAÍDA    1=ENTRADA
    MOVWF   TRISB				   		 ;PORTB TODO SAÍDA
    BCF     STATUS,RP0		       		 ;VOLTA PARA O BANCO 0
    MOVLW   B'00000111'		      		  ;MOVE 00000111 PARA W
    MOVWF   CMCON                   			;DESABILITA OS COMPARADORES ANALÓGICOS
    CLRF    PORTA		            			;INICIALIZA PORTA COM 0
    CLRF    PORTB				
    GOTO    AGUARDA					;VAI PARA A ROTINA PRINCIPAL	

    END

                    	;**********IMPORTANTE****************

;Sempre Configure antes o TRISA e TRISB (0=Saida, 1=Entrada)

;Não salve o arquivo em pastas cujo "tamanho" do caminho completo
;seja maior que 62 caracteres( não salve em Meus Documentos ou Desktop)

;Para compilar: F10

;Para visualizar as saídas:
	;*Wiew -> Watch.
	;*Nessa janela é possível inserir e visualizar o valor de todos os 
	;registradores (Add SFR)ou variáveis (Add Symbol) do PIC.
	;*Click com o botão direito sobre o registrador e em “properties...” 
	;*Escolha o formato da exibição mais conveniente (binário, decimal...).

;Para simular: Debugger ->Select tools -> MPLab SIM 
	;F5=Interrompe a execução    
	;F6=reset 
	;F7=passo-a-passo entrando nas chamadas “CALL”
	;F8=passo-a-passo sem estrar nas chamadas “CALL”
	;F9=Executa direto até o primeiro "Breakpoint"

;Stimulus (Simula o acionamento de uma entrada do PIC):
	;*Debugger -> Stimulus -> New Workbook -> Asynch
	;*Em “Pin/SFR” escolha o “PORT” desejado, e em “Action” escolha 
	;“Toggle” (inverter).
	;*Para inverter o nível digital (0 ou 1) pressione o botão “Fire”.
	;*Obs: F10 ->  F6  -> F8 -> F8... até passar pela inicialização do 
	;PORTA, após isso “click” em “Fire”, selecione novamente a janela 
	;de código e pressione novamente F8 para atualizar a janela Watch.

;StopWatch (permite verificar o tempo da rotina) :
	;*Debugger -> StopWatch.
	;*Debugger -> Settings -> Processor Frequency = 4.
	;*Nessa janela é possível visualizar o tempo de execução de uma 
	;determinada rotina.
	;*Colocar um breakpoint (2 clicks) na primeira INTRUÇÃO e outro na 
	;última INTRUÇÃO da rotina (Não funciona no NOME da rotina). 
	;*Pressionar F9 para o código ser executado até o primeiro breakpoint,
	;zerar o StopWatch e pressionar novamente F9 e verificar o tempo.

