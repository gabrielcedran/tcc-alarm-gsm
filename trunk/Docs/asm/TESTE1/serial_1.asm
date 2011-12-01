; ******************************************************************************
;                         CONFIGURAÇÃO DO MICROCONTROLADOR.
; ******************************************************************************
    LIST P=16F628A                      ;INFORMA O PIC UTILIZADO. 
    INCLUDE   <P16F628A.INC>            ;INCLUSÃO DA BIBLIOTECA.
    __CONFIG _CP_OFF & _WDT_OFF & _PWRTE_OFF & _BODEN_OFF & _MCLRE_OFF   &   _HS_OSC   &   _LVP_OFF
    ERRORLEVEL -302	                    ;RETIRA MSG OPERAÇÃO NO BANCO 1.

; ******************************************************************************
;                          DECLARAÇÃO DE VARIÁVEIS E CONSTANTES.
; ******************************************************************************
    
    CBLOCK 0X20             
        T1		            ;VARIÁVEL AUXILIAR PARA TEMPORIZAÇÃO
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
;                          VETORES DE RESET E INTERRUPÇÃO.
; ******************************************************************************

    ORG 0X00		                    ;A EXECUÇÃO INICIA NESSE ENDEREÇO.
    GOTO   INICIO	                ;VAI PARA A SUB-ROTINA INÍCIO. 
	
    ORG	0X04		                    ;VEM NESSE ENDEREÇO COM A INTERRUPÇÃO.
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
	BTFSS	E_1				;BOTÃO						
	GOTO	DISPARO
	BTFSS	E_2				;BOTÃO					
	GOTO	DISPARO	
	GOTO	PRINCIPAL

DISPARO
	MOVLW	'E'
	MOVWF	TXREG
	BTFSS	PIR1,TXIF
	GOTO	$-1
	CALL	TEMPO
	GOTO	PRINCIPAL

RECEBE						;TRATAMENTO DA INTERRUPÇÃO
	;SALVA W
	MOVLW	.48				;ASCII PARA DECIMAL
	SUBWF	RCREG,W
	MOVWF	DADO_RX			;DECIMAL
	;RESTAURA W
	RETFIE
	
DECODIFICA
  ; 1-Ativar o alarme; 3-travar portas; 7-destravar portas; 5-obter posição atual; 9-desativar alarme
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
	BSF		S_PORTA				;ACIONA O BOTÃO PARA DESTRAVAR AS PORTAS
	CALL	TEMPO
	BCF		S_PORTA
	CLRF	DADO_RX				;DADO_RX=0
	GOTO	PRINCIPAL
	
TRAVA_PORTAS
	BCF		S_PORTA				;ACIONA O BOTÃO PARA DESTRAVAR AS PORTAS
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
  ; 1-Ativar o alarme; 3-travar portas; 7-destravar portas; 5-obter posição atual; 9-desativar alarme
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
;                                 ROTINAS DE TEMPORIZAÇÃO.
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
;                               CONFIGURAÇÃO INICIAL.
; ******************************************************************************
INICIO
    BSF		STATUS,RP0     		;VAI PARA O BANCO 1
	MOVLW	B'00000010'
	MOVWF	TRISB				;B,1= ENTRADA SERIAL / B,4= BOTÃO TX / B7 - OK
	MOVLW	B'00000011'
	MOVWF	TRISA
	BSF		PIE1,RCIE			;HABILITA A INTERRUPÇÃO DE RECEBIMENTO PELA SERIAL	
	BSF   	TXSTA, TXEN 		;HABILITA A TRANSMISSÃO SERIAL
	BSF   	TXSTA, TRMT 		;LIMPA O REGISTRADOR DE TRANSMISSÃO
	BSF   	TXSTA, BRGH 		;MODO ALTA VELOCIDADE
	MOVLW 	.76; .18 			;18 BAUD RATE PARA 38400 BPS
	MOVWF 	SPBRG 				;
    BCF		STATUS,RP0         	;VOLTA PARA O BANCO 0
	MOVLW	.7					;DESABILITA OS COMPARADORES ANALÓGICOS
	MOVWF	CMCON
	BSF 	RCSTA,SPEN 			;HABILITA A COMUNICAÇÃO SERIAL
	BSF 	RCSTA,CREN 			;HABILITA A RECEPÇÃO CONTÍNUA
	BSF		INTCON,PEIE			;HABILITA A INTERRUPÇÃO DOS PERIFÉRICOS
	BSF		INTCON,GIE			;HABILITA A INTERRUPÇÃO GERAL
	MOVLW	B'00000010'
	MOVWF	PORTB
	CLRF	PORTA
	CLRF	DADO_RX
	MOVLW	.5
	MOVWF	AUX	
    GOTO    AUTO_TESTE     	

    END
