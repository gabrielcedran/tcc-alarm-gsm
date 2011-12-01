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
		DADO_RX				;DADO RECEBIDO PELA SERIAL
		FILTRO
    ENDC

    #DEFINE S1 		PORTB,0     ;SAÍDA 1
								;PORTB1 = RX SERIAL
								;PORTB2 = TX SERIAL				
	#DEFINE S2		PORTB,3		;SAÍDA 2
	#DEFINE BTN		PORTB,4		;ENTRADA
	#DEFINE CON_OK	PORTB,7		;CONEXAO_OK

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
MONITOR_PIC							;PISCA O LEDS 2 VEZES	
	BSF		S1						;LIGA
	CALL	TEMPO_1S				;AGUARDA 1 SEGUNDO
	BCF		S1						;DESLIGA
	BSF		S2					
	CALL	TEMPO_1S			
	BCF		S2
	BCF		PIR1,RCIF				;LIMPA O BUFFER DE RECEPÇÃO	

PRINCIPAL
	BTFSS	BTN						
	CALL	BOTAO
	BTFSS	PIR1,RCIF
	GOTO	PRINCIPAL
	CALL	RECEBE
	CALL	ACIONA
	GOTO	PRINCIPAL

BOTAO
	MOVLW	'1'
	CALL	TRANSMITE
	CALL	TEMPO_1S
	RETURN

TRANSMITE
	MOVWF	TXREG
	BTFSS	PIR1,TXIF
	GOTO	$-1
	CALL	TEMPO_10MS
	RETURN

RECEBE
	MOVF	RCREG,W
	MOVWF	DADO_RX
	RETURN

ACIONA
	MOVLW	'1'
	XORWF	DADO_RX,W
	BTFSC	STATUS,Z
	GOTO	LIGA_S1
	MOVLW	'2'
	XORWF	DADO_RX,W
	BTFSC	STATUS,Z
	GOTO	DESL_S1
	MOVLW	'3'
	XORWF	DADO_RX,W
	BTFSC	STATUS,Z
	GOTO	LIGA_S2
	MOVLW	'4'
	XORWF	DADO_RX,W
	BTFSC	STATUS,Z
	GOTO	DESL_S2
	RETURN

LIGA_S1
	BSF		S1
	RETURN

DESL_S1
	BCF		S1
	RETURN

LIGA_S2
	BSF		S2
	RETURN

DESL_S2
	BCF		S2
	RETURN				


	

; ******************************************************************************
;                                 ROTINAS DE TEMPORIZAÇÃO.
; ******************************************************************************
TEMPO_1S
	MOVLW	.36						
    MOVWF	T3 			                ;INICIALIZA T2
	MOVLW	.166						
    MOVWF	T2 			                ;INICIALIZA T2 
    MOVLW	.166
    MOVWF	T1                          ;INICIALIZA T1
    DECFSZ	T1, F			            ;DECREMENTA T1 E SALTA SE T1 = 0
    GOTO	$-1			                ;VOLTA UMA LINHA
    DECFSZ	T2, F						;DECREMENTA T2 E SALTA SE T2 = 0
    GOTO	$-5							;VOLTA CINCO LINHAS
    DECFSZ	T3, F						;DECREMENTA T2 E SALTA SE T2 = 0
    GOTO	$-9							;VOLTA CINCO LINHAS
    RETURN                              ;RETORNA DA CHAMADA CALL

TEMPO_10MS								;TEMPO DE 10 MS
	MOVLW	.50						
    MOVWF	T2 			                ;INICIALIZA T2 
    MOVLW	.199
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
	BSF		PORTB,2	
    BSF		STATUS,RP0     		;VAI PARA O BANCO 1
	MOVLW	B'10010010'
	MOVWF	TRISB				;B,1= ENTRADA SERIAL / B,4= BOTÃO TX / B7 - OK
	CLRF	TRISA
	BSF   	TXSTA, TXEN 		;HABILITA A TRANSMISSÃO SERIAL
	BSF   	TXSTA, TRMT 		;LIMPA O REGISTRADOR DE TRANSMISSÃO
	BSF   	TXSTA, BRGH 		;MODO ALTA VELOCIDADE
	MOVLW 	.76; .18 				;18 BAUD RATE PARA 38400 BPS
	MOVWF 	SPBRG 				;
    BCF		STATUS,RP0         	;VOLTA PARA O BANCO 0
	MOVLW	.7					;DESABILITA OS COMPARADORES ANALÓGICOS
	MOVWF	CMCON
	BSF 	RCSTA,SPEN 			;HABILITA A COMUNICAÇÃO SERIAL
	BSF 	RCSTA,CREN 			;HABILITA A RECEPÇÃO CONTÍNUA
    GOTO    MONITOR_PIC     	

    END
