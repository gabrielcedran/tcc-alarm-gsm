;PROJETO = PISCA LED.
;PIC = 16F628
;AUTOR = IVAIR TEIXEIRA
;DATA = 04/04/2008
; ***********************************************************************************
;                           CONFIGURA��O DO MICROCONTROLADOR.
; ***********************************************************************************
	LIST P=16F628				;PROCESSADOR UTILIZADO
	#INCLUDE <P16F628A.INC>		;INCLUS�O DA BIBLIOTECA
	ERRORLEVEL -302				;DESABILITA O WARNING DE OPERA��O NO BANCO 1
	__CONFIG _CP_OFF & _WDT_OFF & _PWRTE_OFF & _MCLRE_ON & _INTRC_OSC_NOCLKOUT & _LVP_OFF

; ***********************************************************************************
;                           DECLARA��O DAS VARI�VEIS E CONTANTES
; ***********************************************************************************

	CBLOCK 0X20
		T1						;VARI�VEL UTILIZADA NA TEMPORIZA��O
		T2						;" "
		AUX
	ENDC
	#DEFINE BTN1 PORTA,0         ;DEFINE QUE LED1 = PORTA,0

; ***********************************************************************************
;                            VETORES DE RESET E INTERRUP��O.
; ***********************************************************************************

	ORG 0X00                      ;NO RESET A EXECU��O INICIA NESSE ENDERE�O
	GOTO	INICIO                ;DESVIO INCONDICIONAL PARA A SUB-ROTINA IN�CIO. 	

	ORG 0X04                      ;VEM PARA ESSE ENDERE�O COM A INTERRUP��O.
	RETFIE                        ;RETORNA DA INTERRUP��O.

; ***********************************************************************************
;                                       ROTINAS GERAIS.
; ***********************************************************************************
INICIALIZA
	MOVLW	.10
	MOVWF	AUX
	GOTO	PRINCIPAL

PRINCIPAL
	MOVLW	B'00000001'             ;MOVE O VALOR 00000001 PARA O ACUMULADOR W
	MOVWF	PORTB                   ;MOVE O CONTE�DO DE W PARA O REGISTRADOR PORTB
	CALL	TEMPO                   ;CHAMA A ROTINA TEMPO
	MOVLW	B'00000010'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'00000100'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'00001000'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'00010000'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'00100000'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'01000000'
	MOVWF	PORTB
	CALL	TEMPO
	MOVLW	B'10000000'
	MOVWF	PORTB
	CALL	TEMPO
	BTFSS	BTN1
	GOTO	FIM
	DECFSZ	AUX, F
	GOTO	PRINCIPAL               ;VAI PARA A ROTINA PRINCIPAL

FIM
	NOP
	GOTO	FIM

; ***********************************************************************************
;                                   TEMPORIZA��O.
; ***********************************************************************************
TEMPO                           ;TEMPO DE 196 MILISSEGUNDOS
	MOVLW	.255                ;MOVE O VALOR 255 PARA W					
	MOVWF	T2					;INICIA T2
	MOVLW	.255
	MOVWF	T1					;INICIA T1
	DECFSZ	T1,F				;DECREMENTA T1 E SALTA A PR�XIMA LINHA SE T1=0
	GOTO	$-1                 ;T1 � DIFERENTE E 0, VOLTA UMA LINHA
	DECFSZ	T2,F                ;T1 E IGUAL A 0 CONTINUA ...
	GOTO	$-5
	RETURN                      ;RETORNA PARA A LINHA POSTERIOR A CHAMADA.

; ***********************************************************************************
;                            CONFIGURA��O INICIAL.
; ***********************************************************************************
INICIO
	BSF		STATUS,RP0			;VAI PARA O BANCO DE MEM�RIA 1
  	MOVLW   B'00000001'         
	MOVWF	TRISA               ;CONFIGURA PORTA  - 0=SAIDA (OUT) 1=ENTRADA (IN)
	MOVLW   B'00000000'			
	MOVWF	TRISB				;CONFIGURA PORTB
	BCF		STATUS,RP0			;VOLTA PARA O BANCO 0
	MOVLW	B'00000111'			;MOVE 00000111 PARA W
	MOVWF	CMCON				;DESABILITA OS COMPARADORES ANAL�GICOS
	CLRF	PORTA				;INICIALIZA O PORTA COM 0
	CLRF	PORTB
	CLRW				
	GOTO	INICIALIZA			;VAI PARA A ROTINA PRINCIPAL

	END
