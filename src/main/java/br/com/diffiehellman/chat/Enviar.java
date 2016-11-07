package br.com.diffiehellman.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

import br.com.diffiehellman.Default;

public class Enviar implements Runnable{
	
	private String host;
    private int port;
    private SecretKey desKey;    
    private boolean modoDebug;	
     
    Enviar(){}
     
    public Enviar(String host, int port, boolean modoDebug, SecretKey desKey){    
    	this.host = host;
        this.port = port;
        this.desKey = desKey;      
        this.modoDebug = modoDebug;
    }
           
	public void run(){ 				
		
    	if(!host.isEmpty()){	 
    		Socket socket;         
            try{                	
                // Cria socket        	
            	socket = new Socket(this.host, this.port); 
            	socket.setSoTimeout(10000000);//10 min
                
                new ClientThread(modoDebug, socket, desKey).run();
    		} catch (Exception ex){
                System.err.println("Erro ao iniciar Soket de comunicação: "+ex);
            }
    	}else{	
    		ServerSocket socket;         
            try{  
            	// Cria socket   
            	socket = new ServerSocket(this.port);            	            	            	            
            	while(true){
 
            		Socket cliente = socket.accept();  
            		cliente.setSoTimeout(10000000);//10 min
    	            
    	            new ClientThread(modoDebug, cliente, desKey).start();
            	}
    		} catch (Exception ex){
                System.err.println("Erro ao iniciar Soket de comunicação: "+ex);
            }
    	}
    }        

	// Passa a lidar com o socket
    static class ClientThread extends Thread {
    	
        private Socket socket;
        private SecretKey desKey;
        private int count = 0;    
        private boolean modoDebug;
         
        ClientThread(boolean modoDebug, Socket socket, SecretKey desKey){
        	this.modoDebug = modoDebug;
            this.socket = socket;
            this.desKey = desKey;             
        }
         
        public void run(){   
        	Default diffieHellman = new Default();    	
             
            try{     
            	String ipConexao = socket.getInetAddress().getHostAddress();
                System.out.println("\n\tConectado com "+ipConexao);
                System.out.println("Conectado! Digite seuas mensagens: \n");                                                
                
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());     

                //vetor iv fixo para modo de bloco CTR fixo
//                byte[] nonceBytes = new byte[] { 
//                	  (byte) 0x69, (byte) 0xdd, (byte) 0xa8, (byte) 0x45, 
//                	  (byte) 0x5c, (byte) 0x7d, (byte) 0xd4, (byte) 0x25, 
//                	  (byte) 0x4b, (byte) 0xf3, (byte) 0x53, (byte) 0xb7, 
//                	  (byte) 0x73, (byte) 0x30, (byte) 0x4e, (byte) 0xec }; //16 byts 
//                IvParameterSpec nonce = new IvParameterSpec(nonceBytes);                                                               
        
                byte[] cleartext;
                byte[] ciphertext;   
                byte[] ciphertextandnonce;
                               
                // thread para receber mensagens
                Receber r = new Receber(modoDebug, dIn, this.desKey, ipConexao);
                new Thread(r).start();                                  
                                
                // msgs do teclado
                Scanner teclado = new Scanner(System.in);               
                while (teclado.hasNextLine()) { 
                	// vetor iv aleatorio para modo de bloco CTR
                    SecureRandom random = new SecureRandom();
                    byte[] nonceBytes = new byte[16];
                    random.nextBytes(nonceBytes);
                    byte[] nonceBytesToCrypt = new byte[16];
                    System.arraycopy(nonceBytes, 0, nonceBytesToCrypt, 0, 15);
                                       
                    diffieHellman.printHex(modoDebug, "\nNonce original e enviado: ", nonceBytes);
                    
                    count++;    
                    //XOR entre count e 10° elemento da chave privada
                    nonceBytesToCrypt[15] = (byte) ((byte) (count & 0xff) ^ this.desKey.getEncoded()[10]);                                                        
                    IvParameterSpec nonce = new IvParameterSpec(nonceBytesToCrypt);                                  
                                          
                    Cipher aliceCipher = Cipher.getInstance("AES/CTR/PKCS5Padding");                                                                                                                                         
                    aliceCipher.init(Cipher.ENCRYPT_MODE, this.desKey, nonce);                                         
                    
                	cleartext = teclado.nextLine().getBytes("UTF-8");                	
                	String encoded = DatatypeConverter.printBase64Binary(cleartext);                   	
                	ciphertext = aliceCipher.doFinal(encoded.getBytes());
                	                
                	diffieHellman.printHex(modoDebug, "\nNonce codificado: ", nonce.getIV());                                                                        	                
                	diffieHellman.printHex(modoDebug, "\nMensagem SEM nonce:", ciphertext);                  	
                	
                	ciphertextandnonce = new byte[ciphertext.length + nonceBytes.length];
                	System.arraycopy(ciphertext, 0, ciphertextandnonce, 0, ciphertext.length);
                	System.arraycopy(nonceBytes, 0, ciphertextandnonce, ciphertext.length, nonceBytes.length);
                	                             	                	                	
                	diffieHellman.printHex(modoDebug, "\nMensagem COM nonce: ", ciphertextandnonce);                	
                	System.out.println("--------------------------------------");
                	
	            	dOut.writeInt(ciphertextandnonce.length); // Escreve comprimento da mensagem            	
	     		    dOut.write(ciphertextandnonce);	     	  // Envia mensagem    	     		    

                }    
                //Finaliza leitura de teclado
                teclado.close();        	
				
            } catch (Exception ex) {
                System.err.println("Erro em Enviar: "+ex);
            } finally{
            	try {    
            		//Finaliza objetos
            		socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				} catch (IOException e) {
					System.err.println("Erro ao finalizar os objetos em enviar: "+e);
				}            	
            }            
        }                       
    }
}
