package br.com.diffiehellman.main;

import java.security.KeyPair;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

import br.com.diffiehellman.Default;
import br.com.diffiehellman.chat.Enviar;
import br.com.diffiehellman.enviaChavePub.EnviaDados;
import br.com.diffiehellman.enviaChavePub.RecebeDados;
import br.com.diffiehellman.exception.InteracaoException;
import br.com.diffiehellman.main.Argumentos.Argumento;

public class Orquestrador {
	
	private static final int COD_ERRO_TECNICO = -2;
	
	private boolean modoDebug = false;
	private int aesTamanho = 128;
	private int dhTamanho = 1024;

	public Orquestrador() {}
	
	public void run(Argumentos argumentos) throws Exception {
		
		final String modo = argumentos.getArgValue(Argumento.MODO);
		final String ip = argumentos.getArgValue(Argumento.IP);
		final int porta = Integer.parseInt(argumentos.getArgValue(Argumento.PORTA));	
		modoDebug = Boolean.parseBoolean(argumentos.getArgValue(Argumento.DEBUG));
		dhTamanho = argumentos.getArgValue(Argumento.DH) == null ? dhTamanho : Integer.parseInt(argumentos.getArgValue(Argumento.DH));
		aesTamanho = argumentos.getArgValue(Argumento.AES) == null ? aesTamanho : Integer.parseInt(argumentos.getArgValue(Argumento.AES));			
		
		Default diffieHellman = new Default();
		try{		
			if(modo.equals("r")){
				//Obtem IP da maquina		
				diffieHellman.meuIP();
				
				RecebeDados recebeDados = new RecebeDados();
				//recebe chave em formato codificado de Alice
				byte[] chavePubKeyEncRecebida = recebeDados.getRecebeDados(porta, modo, modoDebug);
				diffieHellman.printHex(modoDebug, "\nChave publica recebida:", chavePubKeyEncRecebida);
					
				//Bob instancia uma chave pública DH do material de chave codificada.
				//Bob recebe os parametros DH associados a chave pulica de Alice
				DHParameterSpec dhSkipParamSpec = diffieHellman.geradorParametrosDH(dhTamanho, modo, chavePubKeyEncRecebida);				
				//Bob usa os mesmos parametros para criar seu par de chaves
				KeyPair kPair = diffieHellman.criaParChave(dhSkipParamSpec);				
				//Bob cria e inicializa o objeto Key de acordo DH
				KeyAgreement KeyAgree = diffieHellman.criaInicializaObjAcordo(kPair);
				
				// Bob codifica sua chave pública
		        byte[] bobPubKeyEnc = kPair.getPublic().getEncoded();
		        diffieHellman.printHex(modoDebug, "\nChave publica criada:", bobPubKeyEnc);
		        
		        //envia para Alice
		        String ipRecebido = recebeDados.getIpConectado();		        
		        new EnviaDados(ipRecebido, porta, modoDebug, bobPubKeyEnc); 
		        
		        //Bob usa a chave pública de Alice para a primeira fase (e única) de sua versão do protocolo DH.
		        KeyAgree.doPhase(diffieHellman.getPubKey(), true);
		        
		        //Gera segredo compartilhado
		        byte[] segredoCompartilhado = KeyAgree.generateSecret();
		        diffieHellman.printHex(modoDebug, "\nBob secret:", segredoCompartilhado);
		        
		        //Gera chave simetrica AES
		        SecretKey bobDesKey = diffieHellman.criaChaveSecreta(segredoCompartilhado, aesTamanho);                   	             	        	       
		        diffieHellman.printHex(modoDebug, "\nChave Simetrica AES:", bobDesKey.getEncoded()); 
		        
		        //Conexão com outra ponta(Alice)		        
		        Enviar e = new Enviar(ipRecebido, porta, modoDebug, bobDesKey);		        
		        e.run();  		       

			}else if(modo.equals("c")){
				//Cria parametros DH
				DHParameterSpec dhSkipParamSpec = diffieHellman.geradorParametrosDH(dhTamanho, modo, null);
				//Alice cria par de chaves usando os parametros gerados a cima
				KeyPair kPair = diffieHellman.criaParChave(dhSkipParamSpec);
				//Alice cria e inicialiaza o objeto de acordo
				KeyAgreement KeyAgree = diffieHellman.criaInicializaObjAcordo(kPair);
							
		        // Alice codifica sua chave pública	
		        byte[] pubKeyEnc = kPair.getPublic().getEncoded();	        	    
		        diffieHellman.printHex(modoDebug, "\nChave publica criada:", pubKeyEnc);	        
		        
		        //envia para Bob
		        new EnviaDados(ip, porta, modoDebug, pubKeyEnc);
		        
		        //Alice recebe chave publica de Bob
		        byte[] chavePubKeyEncRecebida = new RecebeDados().getRecebeDados(porta, modo, modoDebug);	        	       
		        diffieHellman.printHex(modoDebug, "\nChave publica recebida:", chavePubKeyEncRecebida);
		        		        
		        //Alice usa a chave pública de Bob para a primeira fase (e única) de sua versão do protocolo DH.
				//Antes que ela possa fazê-lo, ela tem que instanciar uma chave pública DH de material de chave codificada de Bob.
		        //Gera segredo compartilhado
		        byte[] segredoCompartilhado = diffieHellman.segredoCompartilhado(chavePubKeyEncRecebida, KeyAgree);	        	         
		        diffieHellman.printHex(modoDebug, "\nAlice secret:", segredoCompartilhado);
		        
		        //Gera chave simetrica AES
		        SecretKey aliceDesKey = diffieHellman.criaChaveSecreta(segredoCompartilhado, aesTamanho);                   	             	        	       
		        diffieHellman.printHex(modoDebug, "\nChave Simetrica AES:", aliceDesKey.getEncoded()); 
		        
		        //Conexão com outra ponta(Bob)
		        Enviar e = new Enviar("", porta, modoDebug, aliceDesKey);		       
		        e.run();			        			
			
			} 
		} catch (InteracaoException e) {
			System.err
					.println("Ocorreu um erro durante a execucao do Orquestrador: "
							+ e.getMessage());
			System.exit(COD_ERRO_TECNICO);
		}
	}		   
}
