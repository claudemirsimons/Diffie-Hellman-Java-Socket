package br.com.diffiehellman.enviaChavePub;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

public class EnviaDados {	
	
	private DataOutputStream dOut;

	public EnviaDados(String ip, int port, boolean modoDebug, byte[] valores){
		envia(ip, port, modoDebug, valores);
	}

	private void envia(String ip, int port, boolean modoDebug, byte[] valores){
	    Socket socket = null;	    		
	    
	    try {
	    	socket = new Socket(ip, port);
	    	if (modoDebug)
				System.out.println("\nEnviando Dados...");
			
	    	String encoded = DatatypeConverter.printBase64Binary(valores);
	    	dOut = new DataOutputStream(socket.getOutputStream());

		    dOut.writeInt(encoded.getBytes().length); // Escreve comprimento da mensagem
		    dOut.write(encoded.getBytes());           // Envia mensagem
		    
		    if (modoDebug)
		    	System.out.println("Dados Enviado!");	
			
			//Finaliza objetos
			socket.shutdownOutput();
			socket.close();		
			
	    } catch(IOException ex ){
	    	System.err.println("Erro no EnviaDados: "+ex);
	    }
	}
}
