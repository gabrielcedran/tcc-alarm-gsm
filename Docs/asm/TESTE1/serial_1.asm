; ******************************************************************************
;                         CONFIGURA��O DO MICROCONTROLADOR.
; ******************************************************************************
    LIST P=16F628A                      ;INFORMA O PIC UTILIZADO. 
    INCLUDE   <P16F628A.INC>            ;INCLUS�O DA BIBLIOTECA.
    __CONFIG _CP_OFF & _WDT_OFF & _PWRTE_OFF & _BODEN_OFF & _MCLRE_OFF   &   _HS_OSC   &   _LVP_OFF
    ERRORLEVEL -302	                    ;RETIRA MSG OPERA��O NO BANCO 1.

; ******************************************************************************
;                          DECLARA��O DE VARI�VEIS E CONSTANTES.
; ******************************************************************************
    
    CBLOCK 0X20             
        T1		            ;VARI�VEL AUXILIAR PARA TEMPORIZA��O
        T2		            ;"
		T3					;"
		AUX					;DADO RECEBIDO PELA SERIAL
		DADO_RX
		CTRL_BLOQUEIO		
    ENDC
			
	#DEFINE E_1			PORTA,0		;
	#DEFINE E_2			PORTA,1		;
	#DEFINE MONITOR		PORTB,0		;
	#DEFINE S_IGNICAO	PORTB,3		;
	#DEFINE S_PORTA		PORTB,4		;
	#DEFINE S_SIRENE	PORTB,5		;
	#DEFINE S_FAROIS	PORTB,6		;

; ******************************************************************************
;                          VETORES DE RESET E INTERRUP��O.
; ******************************************************************************

    ORG 0X00		                    ;A EXECU��O INICIA NESSE ENDERE�O.
    GOTO   INICIO	                ;VAI PARA A SUB-ROTINA IN�CIO. 
	
    ORG	0X04		                    ;VEM NESSE ENDERE�O COM A INTERRUP��O.
	RETFIE
   
; ******************************************************************************
;                                 ROTINAS GERAIS.
; ******************************************************************************
AUTO_TESTE							;PISCA O LEDS 2 VEZES	
	BSF		MONITOR					;LIGA
	CALL	TEMPO					;AGUARDA 200MS
	BCF		MONITOR					;DESLIGA
	CALL	TEMPO
	DECFSZ	AUX,F					;PISCA 5 VEZES
	GOTO	AUTO_TESTE
	GOTO	PRINCIPAL

PRINCIPAL
	BTFSS	PIR1,RCIF		;
	GOTO 	DECODIFICA		;
	BTFSS	E_1				;BOT�O						
	GOTO	DISPARO
	BTFSS	E_2				;BOT�O					
	GOTO	DISPARO	
	GOTO	PRINCIPAL

DISPARO
	MOVLW	'E'
	MOVWF	TXREG
	BTFSS	PIR1,TXIF
	GOTO	$-1
	CALL	TEMPO
	GOTO	PRINCIPAL

RECEBE						;TRATAMENTO DA INTERRUP��O
	;SALVA W
	MOVLW	.48				;ASCII PARA DECIMAL
	SUBWF	RCREG,W
	MOVWF	DADO_RX			;DECIMAL
	;RESTAURA W
	RETFIE
	
DECODIFICA
  ; 1-Ativar o alarme; 3-travar portas; 7-destravar portas; 5-obter posi��o atual; 9-desativar alarme
	CALL 	RECEBE
	MOVF	DADO_RX,W
	ADDWF	PCL,F
	GOTO	PRINCIPAL		;'0'
	GOTO	BLOQUEIO        ;'1'
	GOTO	PRINCIPAL		;'2'
	GOTO	TRAVA_PORTAS	;'3'
	GOTO	PRINCIPAL		;'4'
	GOTO  	PRINCIPAL       ;'5'
	GOTO	PRINCIPAL		;'6'
	GOTO	DESTRAVA_PORTAS ;'7'
	GOTO	PRINCIPAL		;'8'
	GOTO	DESBLOQUEIO 	;'9'

DESTRAVA_PORTAS
	BSF		S_PORTA				;ACIONA O BOT�O PARA DESTRAVAR AS PORTAS
	CALL	TEMPO
	BCF		S_PORTA
	CLRF	DADO_RX				;DADO_RX=0
	GOTO	PRINCIPAL
	
TRAVA_PORTAS
	BCF		S_PORTA				;ACIONA O BOT�O PARA DESTRAVAR AS PORTAS
	CALL	TEMPO
	BSF		S_PORTA
	CLRF	DADO_RX				;DADO_RX=0
	GOTO	PRINCIPAL

LOCALIZADOR
	MOVLW	.5
	MOVWF	AUX

LOCALIZADOR2
	BSF		S_FAROIS
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	BCF		S_FAROIS
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	CALL	TEMPO
	DECFSZ	AUX
	GOTO	LOCALIZADOR2
	CLRF	DADO_RX
	GOTO	PRINCIPAL

BLOQUEIO
	BSF		S_IGNICAO
	BSF		S_SIRENE
	BSF		S_FAROIS
	CALL	TEMPO
	CALL	TEMPO
	BCF		S_SIRENE
	BCF		S_FAROIS
	CALL	TEMPO
	CALL	TEMPO
	BTFSS	PIR1,RCIF					;
	GOTO 	DECODIFICA_DESBLOQUEIO		;
	GOTO	BLOQUEIO

DECODIFICA_DESBLOQUEIO
  ; 1-Ativar o alarme; 3-travar portas; 7-destravar portas; 5-obter posi��o atual; 9-desativar alarme
	CALL 	RECEBE
	MOVF	DADO_RX,W
	ADDWF	PCL,F
	GOTO	BLOQUEIO		;'0'
	GOTO	BLOQUEIO        ;'1'
	GOTO	BLOQUEIO		;'2'
	GOTO	BLOQUEIO		;'3'
	GOTO	BLOQUEIO		;'4'
	GOTO  	BLOQUEIO       	;'5'
	GOTO	BLOQUEIO		;'6'
	GOTO	BLOQUEIO 		;'7'
	GOTO	BLOQUEIO		;'8'
	GOTO	DESBLOQUEIO 	;'9'

DESBLOQUEIO	
	BCF		S_IGNICAO
	BCF		S_SIRENE
	BCF		S_FAROIS
	CLRF	DADO_RX
	GOTO	PRINCIPAL	


	

; ******************************************************************************
;                                 ROTINAS DE TEMPORIZA��O.
; ******************************************************************************
TEMPO
	MOVLW	.255						
    MOVWF	T2 			                ;INICIALIZA T2 
    MOVLW	.255
    MOVWF	T1                          ;INICIALIZA T1
    DECFSZ	T1, F			            ;DECREMENTA T1 E SALTA SE T1 = 0
    GOTO	$-1			                ;VOLTA UMA LINHA
    DECFSZ	T2, F						;DECREMENTA T2 E SALTA SE T2 = 0
    GOTO	$-5							;VOLTA CINCO LINHAS
    RETURN                              ;RETORNA DA CHAMADA CALL


; ******************************************************************************
;                               CONFIGURA��O INICIAL.
; ******************************************************************************
INICIO
    BSF		STATUS,RP0     		;VAI PARA O BANCO 1
	MOVLW	B'00000010'
	MOVWF	TRISB				;B,1= ENTRADA SERIAL / B,4= BOT�O TX / B7 - OK
	MOVLW	B'00000011'
	MOVWF	TRISA
	BSF		PIE1,RCIE			;HABILITA A INTERRUP��O DE RECEBIMENTO PELA SERIAL	
	BSF   	TXSTA, TXEN 		;HABILITA A TRANSMISS�O SERIAL
	BSF   	TXSTA, TRMT 		;LIMPA O REGISTRADOR DE TRANSMISS�O
	BSF   	TXSTA, BRGH 		;MODO ALTA VELOCIDADE
	MOVLW 	.76; .18 			;18 BAUD RATE PARA 38400 BPS
	MOVWF 	SPBRG 				;
    BCF		STATUS,RP0         	;VOLTA PARA O BANCO 0
	MOVLW	.7					;DESABILITA OS COMPARADORES ANAL�GICOS
	MOVWF	CMCON
	BSF 	RCSTA,SPEN 			;HABILITA A COMUNICA��O SERIAL
	BSF 	RCSTA,CREN 			;HABILITA A RECEP��O CONT�NUA
	BSF		INTCON,PEIE			;HABILITA A INTERRUP��O DOS PERIF�RICOS
	BSF		INTCON,GIE			;HABILITA A INTERRUP��O GERAL
	MOVLW	B'00000010'
	MOVWF	PORTB
	CLRF	PORTA
	CLRF	DADO_RX
	MOVLW	.5
	MOVWF	AUX	
    GOTO    AUTO_TESTE     	

    END
