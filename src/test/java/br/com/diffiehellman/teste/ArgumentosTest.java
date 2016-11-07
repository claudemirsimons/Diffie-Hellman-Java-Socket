package br.com.diffiehellman.teste;


public class ArgumentosTest {
/*
	@Test
	public void deveValidarArgumentosPassados() throws ParseException {
		String[] argsDoUsuario = getArgumentos(true);
		Argumentos args = Argumentos.get(argsDoUsuario);
		Assert.assertEquals(2, args.getArgs().length);
	}

	@Test(expected = ParseException.class)
	public void deveInvalidarArgumentosPassadosErroneamente()
			throws ParseException {
		String[] argsDoUsuario = getArgumentos(false);
		Argumentos.get(argsDoUsuario);
	}

	@Test(expected = ParseException.class)
	public void deveInvalidarCasoFalteAlgumArgumento() throws ParseException {
		String[] argsDoUsuarioIncompletos = new String[] {
				"-ip", "127.0.0.1"};

		Argumentos.get(argsDoUsuarioIncompletos);
	}

	@Test
	public void deveRetornarArgumentos() throws ParseException {
		String[] argsDoUsuario = getArgumentos(true);
		Argumentos args = Argumentos.get(argsDoUsuario);
		Assert.assertEquals("127.0.0.1",
				args.getArgValue(Argumento.IP));		
	}

	private String[] getArgumentos(boolean validos) {
		final String argTemplate = (validos) ? "-%s" : "-invalido%s";
		String[] argsDoUsuario = new String[] {
				String.format(argTemplate,
						Argumento.IP.getNome()),
				"127.0.0.1",			
				String.format(argTemplate, Argumento.PORTA.getNome()),
				"9999"};
		return argsDoUsuario;
	}
*/
}
