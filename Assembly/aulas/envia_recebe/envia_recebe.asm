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
        T1                 			;T1 NO ENDEREÇO 0X20
		T2
        AUX		                
    ENDC
   		V1 EQU 0X25
		V2 EQU 0X26
		V3 EQU 0X27

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
INICIALIZA
	MOVLW 0X25
	MOVWF FSR
	GOTO PRINCIPAL

PRINCIPAL
	BTFSS	PIR1,RCIF
	GOTO	$-1
	CALL	RX_SERIAL
	CALL	TX_SERIAL
	CALL 	CONVERTE
	CALL	SALVA
	GOTO 	PRINCIPAL

RX_SERIAL
	MOVF	RCREG,W
	MOVWF	AUX
	RETURN

TX_SERIAL
	MOVF	AUX,W
	MOVWF	TXREG
	BTFSS	PIR1,TXIF
	GOTO	$-1
	RETURN

CONVERTE
	MOVLW	.48
	SUBWF 	AUX,F
	RETURN

SALVA
	MOVLW	0X25
	MOVWF	FSR
	MOVF	AUX,W
	MOVWF	INDF
	INCF	FSR
	RETURN

; ******************************************************************************
;                                 ROTINAS DE TEMPORIZAÇÃO.
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

; ******************************************************************************
;                               CONFIGURAÇÃO INICIAL.
; ******************************************************************************
INICIO
   	BSF STATUS,RP0 ;MUDA PARA O BANCO 1
	CLRF PORTA ;PORTA TODO SAÍDA
	MOVLW B'00000010'
	MOVWF TRISB ;PORTB,1 ENTRADA, RESTANTE SAÍDA
	BSF TXSTA, TXEN ;HABILITA A TRANSMISSÃO SERIAL
	BSF TXSTA, TRMT ;LIMPA O REGISTRADOR DE TRANSMISSÃO
	BSF TXSTA, BRGH ;MODO ALTA VELOCIDADE
	MOVLW .25 ;BAUD RATE PARA 9.600 BPS
	MOVWF SPBRG ;
	BCF STATUS,RP0 ;VOLTA PARA O BANCO 0
	MOVLW B'00000111' ;DESABILITA OS...
	MOVWF CMCON ;... COMPARADORES ANALÓGICOS
	BSF RCSTA,SPEN ;HABILITA A COMUNICAÇÃO SERIAL
	BSF RCSTA,CREN ;HABILITA A RECEPÇÃO CONTÍNUA
	CLRF    PORTA		            ;INICIALIZA PORTA COM 0
    CLRF    PORTB				
    GOTO    INICIALIZA				;VAI PARA A ROTINA PRINCIPAL	
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

