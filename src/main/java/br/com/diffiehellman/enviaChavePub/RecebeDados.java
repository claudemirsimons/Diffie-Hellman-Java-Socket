package br.com.diffiehellman.enviaChavePub;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

public class RecebeDados { 
	
    private ServerSocket servidor;        
    private Socket cliente;
    private DataInputStream dIn;
    private String ipConectado;

	public RecebeDados(){}
	
	public String getIpConectado(){
		return this.ipConectado;
	}
    
    public byte[] getRecebeDados(int porta, String modo, boolean modoDebug){
    	return recebe(porta, modo, modoDebug);
    }

    private byte[] recebe(int porta, String modo, boolean modoDebug){   
    	 byte[] message = null;
    	
    	 try {      		
	        servidor = new ServerSocket(porta);	        
	        if (modo.equals("r")){
	        	System.out.println("Aguardando conexao...");
	        } else{
        		System.out.println("Conectando...");
	        }
	        
	        if (modoDebug)
	        	System.out.println("\nPorta "+porta+" aberta!");   	        	       
	        
	        cliente = servidor.accept();	   
	        this.ipConectado = cliente.getInetAddress().getHostAddress();
        	System.out.println("Nova conexao com " + ipConectado);
        	
        	dIn = new DataInputStream(cliente.getInputStream());        	        	

        	int length = dIn.readInt(); //ler comprimento da mensagem recebida
        	if(length>0) {
        	    message = new byte[length];
        	    dIn.readFully(message, 0, message.length); //ler a mensagem
        	} 
        	
        	//Finaliza objetos
        	cliente.shutdownInput();
        	cliente.close();             
            servidor.close(); 
        	
        	String value = new String(message);                                                          	 	   	 	   
            return DatatypeConverter.parseBase64Binary(value);	            
        } catch (IOException e) {
            System.err.println("Erro no RecebeDados: "+e);
            return null;
        }    	
		                  
    }       
}
