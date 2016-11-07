package br.com.diffiehellman;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.diffiehellman.exception.InteracaoException;

public class Default implements Interface{	
	
	private PublicKey pubKey;
	
	public PublicKey getPubKey(){
		return pubKey;
	}
	
	@Override
	public DHParameterSpec geradorParametrosDH(int dhTamanho, String modo, byte[] chavePubKeyEncRecebida) throws InteracaoException{
		
		DHParameterSpec dhSkipParamSpec;
		
		try{
			if(modo.equals("r")){			
				KeyFactory keyFac = KeyFactory.getInstance("DH");
		        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(chavePubKeyEncRecebida);
		        PublicKey pubKey = keyFac.generatePublic(x509KeySpec);  
		        
		        this.pubKey = pubKey;
		        
		        return ((DHPublicKey) pubKey).getParams();							
			}else if(modo.equals("c")){
			
		        if (dhTamanho == 2048) {
		            // Alguma autoridade central cria novos parâmetros DH
		            System.out.println("Criando parâmetros Diffie-Hellman (Chave mais forte) ...");
		            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
		            paramGen.init(2048);//( Disponivel em Java 8 )
		            AlgorithmParameters params = paramGen.generateParameters();
		            dhSkipParamSpec =
		                    (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
		        } else if (dhTamanho == 1024){
		            // usar alguns parâmetros, DH padrão pré-gerados
		            System.out.println("Usando parâmetros de SKIP Diffie-Hellman");
		            dhSkipParamSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
		        } else{
		        	throw new InteracaoException(
							"Parametro 'modoGerador' incorreto");
		        }
		        
		        return dhSkipParamSpec;				
			}	
			else{
				throw new InteracaoException(
						"Parametro 'MODO' incorreto");								
			}
		} catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar parametros DH: "
							+ e.getMessage());
		}
               			
	}
	
	@Override
	public KeyPair criaParChave(DHParameterSpec dhSkipParamSpec) throws InteracaoException {
		try{		    
	        KeyPairGenerator kPairGen = KeyPairGenerator.getInstance("DH");
	        kPairGen.initialize(dhSkipParamSpec);
	        KeyPair kPair = kPairGen.generateKeyPair();	       	
	                	        
			return kPair;							        
		} catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar criar par de chaves: "
							+ e.getMessage());
		}
	}
	
	@Override
	public KeyAgreement criaInicializaObjAcordo(KeyPair kPair) throws InteracaoException {
		try{
			// Alice cria e inicializa objeto de acordo chave DH	        
	        KeyAgreement KeyAgree = KeyAgreement.getInstance("DH");
	        KeyAgree.init(kPair.getPrivate());
	        
			return KeyAgree;
		} catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar criar e inicializar objeto de acordo: "
							+ e.getMessage());
		}
	}
	
	@Override
	public byte[] segredoCompartilhado(byte[] chavePubKeyEncRecebida,  KeyAgreement KeyAgree) throws InteracaoException {
		try{								
				       	        
			KeyFactory keyFac = KeyFactory.getInstance("DH");
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(chavePubKeyEncRecebida);
	        PublicKey pubKey = keyFac.generatePublic(x509KeySpec);
	        //fase 1             
	        KeyAgree.doPhase(pubKey, true);   
	        //byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();    
	        
			return KeyAgree.generateSecret();
		} catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar gerar segredo compartilhado: "
							+ e.getMessage());
		}
	}
	
	@Override
	public SecretKey criaChaveSecreta(byte[] sharedSecret, int tamanho) throws InteracaoException {
		try{
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256"); 
			byte[] bkey = Arrays.copyOf(sha256.digest(sharedSecret), tamanho / Byte.SIZE);
									             
//	        SecretKey aliceDesKey = aliceKeyAgree.generateSecret("AES"); //para 256bits direto  
	    	
			return( new SecretKeySpec(bkey, "AES"));
		} catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar criar chave secreta com MessageDigest: "
							+ e.getMessage());
		}
	}
	
	@Override
	public void meuIP() throws InteracaoException{
		try {
//			InetAddress inet = InetAddress.getLocalHost();
//			InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
//		    if (ips  != null ) {
//		    	for (int i = 0; i < ips.length; i++) {
//		    		System.out.println(ips[i]);
//		    	}
//		    }	
			System.out.println("IP local: "+InetAddress.getLocalHost().getHostAddress());
	    } catch (Exception e) {
			throw new InteracaoException(
					"Ocorreu um erro ao tentar consultar ip local: "
							+ e.getMessage());
		}
		  
	}
	
	@Override
	public void printHex(boolean ativar, String mensagem, byte[] b) {
	   	if(ativar){
	   		System.out.println(mensagem);
	        if (b == null) {
	            System.out.println ("(null)");
	        } else {
		        for (int i = 0; i < b.length ; ++i) {
		            if (i % 16 == 0) {
		                System.out.print (Integer.toHexString ((i & 0xFFFF) | 0x10000).substring(1,5) + " - ");
		            }
		            System.out.print (Integer.toHexString((b[i]&0xFF) | 0x100).substring(1,3) + " ");
		            if (i % 16 == 15 || i == b.length - 1){
		                int j;
		                for (j = 16 - i % 16; j > 1; --j)
		                    System.out.print ("   ");
			                System.out.print (" - ");
			                int start = (i / 16) * 16;
			                int end = (b.length < i + 1) ? b.length : (i + 1);
		                for (j = start; j < end; ++j)
		                    if (b[j] >= 32 && b[j] <= 126)
		                        System.out.print ((char)b[j]);
		                    else
		                        System.out.print (".");
		                System.out.println ();
		            }
		        }
	        System.out.println("bits: "+b.length*8);
	        }
	   	}
	}        
		 
   // Os valores do módulo Diffie-Hellman de 1024 bits usados por SKIP
   private static final byte skip1024ModulusBytes[] = {
       (byte) 0xF4, (byte) 0x88, (byte) 0xFD, (byte) 0x58,
       (byte) 0x4E, (byte) 0x49, (byte) 0xDB, (byte) 0xCD,
       (byte) 0x20, (byte) 0xB4, (byte) 0x9D, (byte) 0xE4,
       (byte) 0x91, (byte) 0x07, (byte) 0x36, (byte) 0x6B,
       (byte) 0x33, (byte) 0x6C, (byte) 0x38, (byte) 0x0D,
       (byte) 0x45, (byte) 0x1D, (byte) 0x0F, (byte) 0x7C,
       (byte) 0x88, (byte) 0xB3, (byte) 0x1C, (byte) 0x7C,
       (byte) 0x5B, (byte) 0x2D, (byte) 0x8E, (byte) 0xF6,
       (byte) 0xF3, (byte) 0xC9, (byte) 0x23, (byte) 0xC0,
       (byte) 0x43, (byte) 0xF0, (byte) 0xA5, (byte) 0x5B,
       (byte) 0x18, (byte) 0x8D, (byte) 0x8E, (byte) 0xBB,
       (byte) 0x55, (byte) 0x8C, (byte) 0xB8, (byte) 0x5D,
       (byte) 0x38, (byte) 0xD3, (byte) 0x34, (byte) 0xFD,
       (byte) 0x7C, (byte) 0x17, (byte) 0x57, (byte) 0x43,
       (byte) 0xA3, (byte) 0x1D, (byte) 0x18, (byte) 0x6C,
       (byte) 0xDE, (byte) 0x33, (byte) 0x21, (byte) 0x2C,
       (byte) 0xB5, (byte) 0x2A, (byte) 0xFF, (byte) 0x3C,
       (byte) 0xE1, (byte) 0xB1, (byte) 0x29, (byte) 0x40,
       (byte) 0x18, (byte) 0x11, (byte) 0x8D, (byte) 0x7C,
       (byte) 0x84, (byte) 0xA7, (byte) 0x0A, (byte) 0x72,
       (byte) 0xD6, (byte) 0x86, (byte) 0xC4, (byte) 0x03,
       (byte) 0x19, (byte) 0xC8, (byte) 0x07, (byte) 0x29,
       (byte) 0x7A, (byte) 0xCA, (byte) 0x95, (byte) 0x0C,
       (byte) 0xD9, (byte) 0x96, (byte) 0x9F, (byte) 0xAB,
       (byte) 0xD0, (byte) 0x0A, (byte) 0x50, (byte) 0x9B,
       (byte) 0x02, (byte) 0x46, (byte) 0xD3, (byte) 0x08,
       (byte) 0x3D, (byte) 0x66, (byte) 0xA4, (byte) 0x5D,
       (byte) 0x41, (byte) 0x9F, (byte) 0x9C, (byte) 0x7C,
       (byte) 0xBD, (byte) 0x89, (byte) 0x4B, (byte) 0x22,
       (byte) 0x19, (byte) 0x26, (byte) 0xBA, (byte) 0xAB,
       (byte) 0xA2, (byte) 0x5E, (byte) 0xC3, (byte) 0x55,
       (byte) 0xE9, (byte) 0x2F, (byte) 0x78, (byte) 0xC7
   };
       
   // O SKIP módulo 1024 bit  
   private static final BigInteger skip1024Modulus =
           new BigInteger(1, skip1024ModulusBytes);

   // A base utilizada com o SKIP módulo 1024 bit   
   private static final BigInteger skip1024Base = BigInteger.valueOf(2);

}
