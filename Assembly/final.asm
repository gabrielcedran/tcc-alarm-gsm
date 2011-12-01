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
		DADO        
    ENDC
   		V1 EQU 0X25
		V2 EQU 0X26
		V3 EQU 0X27
	#DEFINE S_IGNICAO	PORTB,3		;
	#DEFINE S_PORTA		PORTB,4		;
	#DEFINE S_SIRENE	PORTB,5		;
	#DEFINE S_FAROIS	PORTB,6		;

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
	BCF S_IGNICAO
	BSF S_PORTA
	BCF S_SIRENE
	BCF S_FAROIS
	GOTO PRINCIPAL

PRINCIPAL
	BTFSS	PIR1,RCIF
	GOTO	$-1
	CALL	RX_SERIAL
	CALL	TX_SERIAL
	CALL 	CONVERTE
;	CALL	SALVA
	GOTO 	DECODIFICA
	GOTO 	PRINCIPAL

RX_SERIAL
	MOVF	RCREG,W
	MOVWF 	DADO
	MOVF	DADO,W
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

ATIVA_ALARME
	BCF  S_IGNICAO
	BSF  S_FAROIS
	BSF  S_SIRENE
	BSF	 S_PORTA
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	BCF  S_FAROIS
	BCF  S_SIRENE
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	BTFSS PIR1, RCIF ;testa se chegou caractere
	GOTO ATIVA_ALARME
	CALL	RX_SERIAL
	CALL	TX_SERIAL
	CALL 	CONVERTE
	GOTO DECODIFICA_ALARME

DESATIVA_ALARME
	BSF  S_IGNICAO
	BCF  S_FAROIS
	BCF  S_SIRENE
	BCF	 S_PORTA
	GOTO PRINCIPAL

ATIVA_LOCALIZADOR
	BSF	 S_FAROIS
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	BCF  S_FAROIS
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	CALL TEMPO
	BTFSS PIR1, RCIF ;testa se chegou caractere
	GOTO ATIVA_LOCALIZADOR
	CALL	RX_SERIAL
	CALL	TX_SERIAL
	CALL 	CONVERTE
	GOTO DECODIFICA_LOCALIZADOR

DESATIVA_LOCALIZADOR
	BCF  S_FAROIS
	GOTO PRINCIPAL


TRAVA_PORTAS
	BCF  S_IGNICAO
	BCF  S_FAROIS
	BCF  S_SIRENE
	BSF	 S_PORTA
	GOTO PRINCIPAL

DESTRAVA_PORTAS
	BSF  S_IGNICAO
	BCF  S_FAROIS
	BCF  S_SIRENE
	BCF	 S_PORTA
	GOTO PRINCIPAL

DECODIFICA
  	; 1-Ativar o alarme; 2-localizador; 3-travar portas; 5-obter posição atual; 6 - cancelar localizador 7-destravar portas;  9-desativar alarme
	MOVF	AUX,W
	ADDWF	PCL,F
	GOTO	PRINCIPAL				;'0'
	GOTO	ATIVA_ALARME 		   		;'1'
	GOTO 	ATIVA_LOCALIZADOR		;'2'
	GOTO	TRAVA_PORTAS			;'3'
	GOTO	PRINCIPAL				;'4'
	GOTO  	PRINCIPAL       		;'5'
	GOTO	DESATIVA_LOCALIZADOR	;'6'
	GOTO	DESTRAVA_PORTAS 		;'7'
	GOTO	PRINCIPAL				;'8'
	GOTO	DESATIVA_ALARME			;'9'

DECODIFICA_ALARME
  	; 1-Ativar o alarme; 2-localizador; 3-travar portas; 5-obter posição atual; 6 - cancelar localizador 7-destravar portas;  9-desativar alarme
	MOVF	AUX,W
	ADDWF	PCL,F
	GOTO	ATIVA_ALARME				;'0'
	GOTO	ATIVA_ALARME 		   		;'1'
	GOTO 	ATIVA_ALARME		;'2'
	GOTO	ATIVA_ALARME			;'3'
	GOTO	ATIVA_ALARME				;'4'
	GOTO  	ATIVA_ALARME	;'5'
	GOTO	ATIVA_ALARME	;'6'
	GOTO	ATIVA_ALARME 		;'7'
	GOTO	ATIVA_ALARME				;'8'
	GOTO	DESATIVA_ALARME		;'9'	

DECODIFICA_LOCALIZADOR
  	; 1-Ativar o alarme; 2-localizador; 3-travar portas; 5-obter posição atual; 6 - cancelar localizador 7-destravar portas;  9-desativar alarme
	MOVF	AUX,W
	ADDWF	PCL,F
	GOTO	ATIVA_LOCALIZADOR				;'0'
	GOTO	ATIVA_ALARME 		   		;'1'
	GOTO 	ATIVA_LOCALIZADOR		;'2'
	GOTO	TRAVA_PORTAS			;'3'
	GOTO	ATIVA_LOCALIZADOR				;'4'
	GOTO  	ATIVA_LOCALIZADOR	;'5'
	GOTO	DESATIVA_LOCALIZADOR	;'6'
	GOTO	DESTRAVA_PORTAS 		;'7'
	GOTO	ATIVA_LOCALIZADOR				;'8'
	GOTO	DESATIVA_ALARME		;'9'

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

