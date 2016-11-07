package br.com.diffiehellman.chat;

import java.io.DataInputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

import br.com.diffiehellman.Default;


public class Receber implements Runnable{
	
	private DataInputStream dIn;
	private byte[] message = null;	
	private SecretKey desKey;	
	private int count = 0;
	private boolean modoDebug;
	private String ipConexao;
    
    Receber(boolean modoDebug, DataInputStream dIn, SecretKey desKey, String ipConexao){
        this.dIn = dIn;    
        this.desKey = desKey;  
        this.modoDebug = modoDebug;
        this.ipConexao = ipConexao;
    }
	
	public void run(){
		Default diffieHellman = new Default();		
		
		try {
			while (true) {					 
				//vetor iv para modo de bloco CTR fixo
//				byte[] ivBytes = new byte[] { (byte) 0x69, (byte) 0xdd, (byte) 0xa8,
//	                        (byte) 0x45, (byte) 0x5c, (byte) 0x7d, (byte) 0xd4,
//	                        (byte) 0x25, (byte) 0x4b, (byte) 0xf3, (byte) 0x53,
//	                        (byte) 0xb7, (byte) 0x73, (byte) 0x30, (byte) 0x4e, (byte) 0xec };
//                IvParameterSpec iv = new IvParameterSpec(ivBytes);
				count++;
				
				int length = dIn.readInt();							
				message = new byte[length];
				
				dIn.readFully(message, 0, message.length); // read the message				
				
				byte[] iv = Arrays.copyOfRange(message, length-16, length);	
				diffieHellman.printHex(modoDebug, "\nNonce recebido: ", iv);
				iv[15] = (byte) ((byte) (count & 0xff) ^ this.desKey.getEncoded()[10]);				
				IvParameterSpec nonce = new IvParameterSpec(iv);
								
				diffieHellman.printHex(modoDebug, "\nNonce codificado: ", nonce.getIV());			
				
				byte[] ciphertextandnonce = Arrays.copyOfRange(message, 0, length-16);
								
				diffieHellman.printHex(modoDebug, "\nMensagem SEM nonce: ", ciphertextandnonce);	
				diffieHellman.printHex(modoDebug, "\nMensagem COM nonce: ", message);
							
				Cipher aliceCipher = Cipher.getInstance("AES/CTR/PKCS5Padding");												                                
		        aliceCipher.init(Cipher.DECRYPT_MODE, this.desKey, nonce);
								 	    		 	    
		 	    byte[] recovered = aliceCipher.doFinal(ciphertextandnonce);
		 	    String value = new String(recovered, "UTF-8");  
		 	    byte[] decoded = DatatypeConverter.parseBase64Binary(value);
		 	    
		        System.out.println("\nMensagem recebida:\n"+new String(decoded, "UTF-8"));
		        System.out.println("--------------------------------------");

			}
			
		} catch (Exception e) {				
			System.out.println(ipConexao+" saiu da conversa!");	
			System.exit(-2);
		} 		
	}		   	 	
}
