;C�DIGO BASE
;PIC16F628A
;OSCILADOR INTERNO 4MHZ
;PROF. IVAIR TEIXERA

; ******************************************************************************
;                         CONFIGURA��O DO MICROCONTROLADOR.
; ******************************************************************************
    LIST 		P=16F628A                 ;INFORMA O PIC UTILIZADO. 
    INCLUDE   	<P16F628A.INC>            ;INCLUS�O DA BIBLIOTECA.
    __CONFIG 	_CP_OFF & _WDT_OFF & _PWRTE_OFF & _BODEN_OFF & _MCLRE_OFF   &   _INTRC_OSC_NOCLKOUT   &   _LVP_OFF 
		;ATEN��O O CONFIG TEM QUE SER EM UMA �NICA LINHA.
    ERRORLEVEL 	-302	                  ;RETIRA MSG OPERA��O NO BANCO 1.

; ******************************************************************************
;                          DECLARA��O DE VARI�VEIS E CONSTANTES.
; ******************************************************************************
    
    CBLOCK 0X20                		;0X20 � O IN�CIO DA MEM�RIA RAM
        T1		                    ;T1 NO ENDERE�O 0X20
        T2		                    ;T2 NO ENDERE�O 0X21
		T3
    ENDC
    #DEFINE 	S2 	 PORTA,0        ;DEFINE QUE S2 = PORTA,0
	#DEFINE 	S1	 PORTA,3        ;DEFINE QUE S2 = PORTA,3
	#DEFINE 	VM1  PORTB,1        ;DEFINE QUE S2 = PORTB,1
	#DEFINE 	AM1	 PORTB,2        ;DEFINE QUE S2 = PORTB,2
	#DEFINE 	VD1  PORTB,3        ;DEFINE QUE S2 = PORTB,3
	#DEFINE 	VM2  PORTB,6        ;DEFINE QUE S2 = PORTB,6
	#DEFINE 	AM2	 PORTB,5        ;DEFINE QUE S2 = PORTA,5
	#DEFINE 	VD2  PORTB,4        ;DEFINE QUE S2 = PORTA,4
	
	
	

; ******************************************************************************
;                          VETORES DE RESET E INTERRUP��O.
; ******************************************************************************

    ORG 0X00		     			;A EXECU��O INICIA NESSE ENDERE�O.
    GOTO	   INICIO	            ;VAI PARA A SUB-ROTINA IN�CIO. 
	
    ORG	0X04		                ;VEM NESSE ENDERE�O COM A INTERRUP��O.
    RETFIE		                    ;RETORNA DA INTERRUP��O.

; ******************************************************************************
;                                 ROTINAS GERAIS.
; ******************************************************************************
PISTA1_ABERTA
	BSF VD1
	BCF AM1
	BCF VM1
	BSF VM2
	BCF AM2
	BCF VD2
    BTFSC S2
	GOTO PISTA1_ABERTA
	CALL	TEMPO_2S
	CALL	TEMPO_2S
	CALL	TEMPO_2S	
	GOTO PISTA1_ATENCAO

PISTA1_ATENCAO
	BCF VD1
	BSF AM1
	BCF VM1
	BSF VM2
	BCF AM2
	BCF VD2
    CALL TEMPO_2S
	GOTO PISTA2_ABERTA

PISTA2_ABERTA
	BCF 	VD1
	BCF 	AM1
	BSF 	VM1
	BCF 	VM2
	BCF 	AM2
	BSF 	VD2
	BTFSC 	S1
	GOTO  	PISTA2_ABERTA
	CALL	TEMPO_2S
	CALL	TEMPO_2S
	CALL	TEMPO_2S	
	GOTO 	PISTA2_ATENCAO

PISTA2_ATENCAO
	BCF VD1
	BCF AM1
	BSF VM1
	BCF VM2
	BSF AM2
	BCF VD2
    CALL TEMPO_2S
	GOTO PISTA1_ABERTA
	

; ******************************************************************************
;                                 ROTINAS DE TEMPORIZA��O.
; ******************************************************************************
TEMPO                               ;TEMPO DE 196MS
    MOVLW	.255
    MOVWF	T2 			            ;INICIALIZA T2 COM 255
    MOVLW	.255
    MOVWF	T1                      ;INICIALIZA T1 COM 255
    DECFSZ	T1, F			        ;DECREMENTA T1 E SALTA SE T1 = 0
    GOTO	$-1			            ;VOLTA UMA LINHA
    DECFSZ	T2, F
    GOTO	$-5
    RETURN				            ;RETORNA DA CHAMADA CALL

TEMPO_2S                               ;TEMPO DE 196MS
    MOVLW   .24
    MOVWF   T3 
    MOVLW	.166
    MOVWF	T2 			            ;INICIALIZA T2 COM 255
    MOVLW	.166
    MOVWF	T1                      ;INICIALIZA T1 COM 255
    DECFSZ	T1, F			        ;DECREMENTA T1 E SALTA SE T1 = 0
    GOTO	$-1			            ;VOLTA UMA LINHA
    DECFSZ	T2, F
    GOTO	$-5
	DECFSZ  T3, F
	GOTO    $-9
    RETURN				            ;RETORNA DA CHAMADA CALL

; ******************************************************************************
;                               CONFIGURA��O INICIAL.
; ******************************************************************************
INICIO
    BSF     STATUS,RP0			    ;VAI PARA O BANCO 1
    MOVLW   B'00001001'				;0=SA�DA    1=ENTRADA
    MOVWF   TRISA				    ;PORTA,0 ENTRADA RESTANTE SA�DA
    MOVLW   B'00000000'				;0=SA�DA    1=ENTRADA
    MOVWF   TRISB				    ;PORTB TODO SA�DA
    BCF     STATUS,RP0		        ;VOLTA PARA O BANCO 0
    MOVLW   B'00000111'		        ;MOVE 00000111 PARA W
    MOVWF   CMCON                   ;DESABILITA OS COMPARADORES ANAL�GICOS
    CLRF    PORTA		            ;INICIALIZA PORTA COM 0
    CLRF    PORTB				
    GOTO    PISTA1_ABERTA			;VAI PARA A ROTINA PRINCIPAL	

    END

                    	;**********IMPORTANTE****************

;Sempre Configure antes o TRISA e TRISB (0=Saida, 1=Entrada)

;N�o salve o arquivo em pastas cujo "tamanho" do caminho completo
;seja maior que 62 caracteres( n�o salve em Meus Documentos ou Desktop)

;Para compilar: F10

;Para visualizar as sa�das:
	;*Wiew -> Watch.
	;*Nessa janela � poss�vel inserir e visualizar o valor de todos os 
	;registradores (Add SFR)ou vari�veis (Add Symbol) do PIC.
	;*Click com o bot�o direito sobre o registrador e em �properties...� 
	;*Escolha o formato da exibi��o mais conveniente (bin�rio, decimal...).

;Para simular: Debugger ->Select tools -> MPLab SIM 
	;F5=Interrompe a execu��o    
	;F6=reset 
	;F7=passo-a-passo entrando nas chamadas �CALL�
	;F8=passo-a-passo sem estrar nas chamadas �CALL�
	;F9=Executa direto at� o primeiro "Breakpoint"

;Stimulus (Simula o acionamento de uma entrada do PIC):
	;*Debugger -> Stimulus -> New Workbook -> Asynch
	;*Em �Pin/SFR� escolha o �PORT� desejado, e em �Action� escolha 
	;�Toggle� (inverter).
	;*Para inverter o n�vel digital (0 ou 1) pressione o bot�o �Fire�.
	;*Obs: F10 ->  F6  -> F8 -> F8... at� passar pela inicializa��o do 
	;PORTA, ap�s isso �click� em �Fire�, selecione novamente a janela 
	;de c�digo e pressione novamente F8 para atualizar a janela Watch.

;StopWatch (permite verificar o tempo da rotina) :
	;*Debugger -> StopWatch.
	;*Debugger -> Settings -> Processor Frequency = 4.
	;*Nessa janela � poss�vel visualizar o tempo de execu��o de uma 
	;determinada rotina.
	;*Colocar um breakpoint (2 clicks) na primeira INTRU��O e outro na 
	;�ltima INTRU��O da rotina (N�o funciona no NOME da rotina). 
	;*Pressionar F9 para o c�digo ser executado at� o primeiro breakpoint,
	;zerar o StopWatch e pressionar novamente F9 e verificar o tempo.

