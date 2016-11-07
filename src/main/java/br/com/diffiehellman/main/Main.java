package br.com.diffiehellman.main;

public class Main {

	private static final int COD_SUCESSO = 0;
	private static final int COD_ERRO_VALIDACAO = -1;

	/*
	 * Este programa executa o protocolo de acordo de chave Diffie-Hellman entre 2 partes: 
	 * Alice e Bob.
	 * Por padrão, são usados parâmetros pré-configurados 
	 * (módulo e base de geradores primo de 1024 bits usado por SKIP).
	 * Também inicia um chat usando criptografia AES utilizando a chave gerada por Diffie-Hellman
	 * Por padrão, são usados parâmetros pré-configurados~
	 * (Criptografia AES 128 bits)
	 */
	public static void main(String[] args) {
		Argumentos argumentos = null;					
				
		try {
			argumentos = Argumentos.get(args);
			Orquestrador orq = new Orquestrador();
			orq.run(argumentos);	
		} catch (Exception e) {
			System.err.println("Ocorreu um erro ao parsear os argumentos: "
					+ e.getMessage());
			System.exit(COD_ERRO_VALIDACAO);
		}
		
		System.exit(COD_SUCESSO);
	}
}
