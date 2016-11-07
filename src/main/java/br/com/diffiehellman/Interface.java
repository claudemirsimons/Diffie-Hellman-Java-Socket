package br.com.diffiehellman;

import java.security.KeyPair;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

import br.com.diffiehellman.exception.InteracaoException;

public interface Interface {
	
	DHParameterSpec geradorParametrosDH(int dhTamanho, String modo, byte[] chavePubKeyEncRecebida) throws InteracaoException;
	KeyPair criaParChave(DHParameterSpec dhSkipParamSpec) throws InteracaoException;
	KeyAgreement criaInicializaObjAcordo(KeyPair kPair) throws InteracaoException;
	byte[] segredoCompartilhado(byte[] chavePubKeyEncRecebida,  KeyAgreement KeyAgree) throws InteracaoException;
	SecretKey criaChaveSecreta(byte[] sharedSecret, int tamanho) throws InteracaoException;
	void meuIP() throws InteracaoException;
	void printHex(boolean ativar, String mensagem, byte[] b);

}
