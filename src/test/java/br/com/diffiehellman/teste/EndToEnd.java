package br.com.diffiehellman.teste;

import org.junit.Test;

import br.com.diffiehellman.main.Argumentos;
import br.com.diffiehellman.main.Argumentos.Argumento;
import br.com.diffiehellman.main.Orquestrador;


/**
 * 
 * O teste a seguir é realizado no ambiente de homologação com os dados
 * providos no mesmo
 * 
 */
public class EndToEnd {

	@Test(timeout = 5 * 1000 * 60)
	public void chatDHRecebe() throws Exception {	
		final String argTemplate = "-%s";

		String[] args = new String[] {
				argTemplate,
					Argumento.MODO.getNome(),
					"r",					
					Argumento.PORTA.getNome(),
					"9999"};

		Orquestrador orq = new Orquestrador();
		orq.run(Argumentos.get(args));
		
	}
	
	@Test(timeout = 5 * 1000 * 60)
	public void chatDHConecta() throws Exception {	
		final String argTemplate = "-%s";

		String[] args = new String[] {
				argTemplate,
					Argumento.MODO.getNome(),
					"c",
					Argumento.IP.getNome(),
					"127.0.0.1",
					Argumento.PORTA.getNome(),
					"9999"};

		Orquestrador orq = new Orquestrador();
		orq.run(Argumentos.get(args));
		
	}
	
	@Test(timeout = 5 * 1000 * 60)
	public void chatDHTodosArgsConecta() throws Exception {	
		final String argTemplate = "-%s";

		String[] args = new String[] {
				argTemplate,
					Argumento.MODO.getNome(),
					"c",
					Argumento.IP.getNome(),
					"127.0.0.1",
					Argumento.PORTA.getNome(),
					"9999",
					Argumento.DEBUG.getNome(),
					"true",
					Argumento.DH.getNome(),
					"1024",					
					Argumento.AES.getNome(),
					"128"};

		Orquestrador orq = new Orquestrador();
		orq.run(Argumentos.get(args));
		
	}
	
	@Test(timeout = 5 * 1000 * 60)
	public void chatDHTodosArgsRecebe() throws Exception {	
		final String argTemplate = "-%s";

		String[] args = new String[] {
				argTemplate,
					Argumento.MODO.getNome(),
					"r",					
					Argumento.PORTA.getNome(),
					"9999",
					Argumento.DEBUG.getNome(),
					"true",								
					Argumento.AES.getNome(),
					"128"};

		Orquestrador orq = new Orquestrador();
		orq.run(Argumentos.get(args));
		
	}

}
