;SEQUENCIAL DE LEDS
;RODRIGO DOS REIS ROCHA

;****************************************************************
;* 				CONFIGURAÇÃO DO MICROCONTROLADOR                *
;****************************************************************

	LIST P = 16F628A    ;INFORMA O PIC UTILIZADO
	INCLUDE <P16F628A.INC>     ;INCLUSÃO DA BIBLIOTECA
	__CONFIG _CP_OFF & _WDT_OFF & _PWRTE_OFF & _BODEN_OFF & _MCLRE_ON & _INTRC_OSC_NOCLKOUT &_LVP_OFF 	
    ERRORLEVEL -302    ;RETIRA MSG OPERAÇÃO BANCO1.
 
	
;****************************************************************
;* 				Declaração de Variaveis e Constantes            *
;****************************************************************

	CBLOCK 0X20			;0X20 É O INICIO DA MEMORIA RAM
		T1					;T1 NO ENDEREÇO 0X20
		T2 					;T2 NO ENDEREÇO 0X21
    ENDC
	#DEFINE S1 PORTA,0	;DEFINE QUE S1 = PORTA,0

;****************************************************************
;* 				VETORES DE RESET E INTERRUPÇÃO                  *
;****************************************************************
	ORG 0X00			;A EXECUÇÃO INICIA NESTE ENDEREÇO
	GOTO	INICIO  	;VAI PARA A SUB ROTINA INICIO
	ORG 0X04 			;VEM NESSE ENDEREÇO COM A INTERRUPÇÃO
	RETFIE 				;RETORNA DA INTERRUPÇAO 
	
;****************************************************************
;* 			             ROTINAS GERAIS 			            *
;****************************************************************
SEQUENCIAL
	MOVLW B'00000001'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO
	
	MOVLW B'00000010'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'00000100'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'00001000'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'00010000'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'00100000'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'01000000'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

	MOVLW B'10000000'	;MOVE 00000001 PARA W
	MOVWF PORTB			;MOVE PARA O PORT B
	CALL TEMPO			;CHAMA A ROTINA TEMPO

;; ********************************************************
;                       ROTINAS DE TEMPORIZAÇÃO.          *
; *********************************************************
TEMPO
	MOVLW .200
	MOVWF T2 	;INICIALIZA T2 COM 255
	MOVLW .255
	MOVWF T1 	;INICIALIZA T1 COM 255
	DECFSZ T1, F ;DECREMENTA T1 E SALTA SE =0
	GOTO $-1 	 ; VOLTA UMA LINHA
	DECFSZ T2, F
	GOTO $-5
	RETURN 		;RETORNA DA CHAMADA CALL

; **********************************************************
;                       CONFIGURAÇÃO INICIAL.              *
; **********************************************************
INICIO
	BSF STATUS,RP0 	;VAI PARA O BANCO 1
	MOVLW B'00000001'
	MOVWF TRISA 	;PORTA,0 ENTRADA
	CLRF TRISB 		;PORTB TODO SAÍDA
	BCF STATUS,RP0  ;VOLTA PARA O BANCO 0
	MOVLW B'00000111' ;MOVE 00000111 PARA W
	MOVWF CMCON 	  ;DESABILITA OS COMP ANALÓGICOS
	CLRF PORTA 		  ;INICIALIZA PORTA COM 0
	CLRF PORTB
	GOTO SEQUENCIAL

	END




	