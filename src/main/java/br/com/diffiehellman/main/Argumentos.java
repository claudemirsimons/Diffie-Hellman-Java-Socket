package br.com.diffiehellman.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Argumentos {

	private static boolean WITH_ARG = true;
	private CommandLine cmd;

	private Argumentos(CommandLine cmd) {
		this.cmd = cmd;
	}

	public static Argumentos get(String[] args) throws ParseException {
		Options disponiveis = getDisponiveis();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(disponiveis, args);
		if (cmd.hasOption("h") || cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(
					"java -jar DH-VXXX.jar",
					disponiveis);
			System.exit(0);
		}

		validaQueTodosForamInformados(disponiveis, cmd);
		Argumentos argumentos = new Argumentos(cmd);
		return argumentos;
	}

	private static void validaQueTodosForamInformados(Options disponiveis,
			CommandLine cmd) throws ParseException {
		final int qtdOptionsAjuda = 2;
		final int opcionais = 4;
		if (((disponiveis.getOptions().size() - (qtdOptionsAjuda + opcionais)) > cmd.getOptions().length)) {
			throw new ParseException("Devem ser informados todos os argumentos obrigatorios");
		}		

	}

	private static Options getDisponiveis() {
		Options argumentos = new Options();
		for (Argumento arg : Argumento.values()) {
			argumentos.addOption(arg.getNome(), arg.isTemArgumento(),
					arg.getDescricao());
		}
		// configura opções de ajuda
		final String mensagemAjuda = "Use este argumento para ajuda no uso do processo";
		argumentos.addOption(new Option("h", mensagemAjuda));
		argumentos.addOption(new Option("help", mensagemAjuda));
		return argumentos;
	}

	public String getArgValue(Argumento arg) {
		return this.cmd.getOptionValue(arg.getNome());
	}

	public Option[] getArgs() {
		return this.cmd.getOptions();
	}

	public static enum Argumento {
		MODO        ("modo"   , WITH_ARG, "(Obrigatorio) Recebe ou conecta a alguem(r = recebe, c = conecta)"), 
		IP			("ip"     , WITH_ARG, "(Obrigatorio/Opcional) ip do cliente, opcional em caso 'modo = r'"), 
		PORTA       ("porta"  , WITH_ARG, "(Obrigatorio) porta para conexao"),
		DEBUG       ("debug"  , WITH_ARG, "(Opcional) modo debug (true ou false), caso nao informado padrao e 'false'"),
		DH          ("dh"     , WITH_ARG, 
				"(Opcional) tamanho de bits para criptografia DiffieHellman(1024 ou 2048), caso nao informado padrao e '1024', "
				+ "para maior que o padrao usar Java 8"),
		AES         ("aes"    , WITH_ARG, 
				"(Opcional) tamanho de bits para criptografia AES(128, 192 ou 256), caso nao informado padrao e '128', "
				+ "para valores maior que padrao precisa substituir o arquivo de autorizacao da JDK");
		
		private final String nome;
		private final boolean temArgumento;
		private final String descricao;

		private Argumento(String nome, boolean temArgumento, String descricao) {
			this.nome = nome;
			this.temArgumento = temArgumento;
			this.descricao = descricao;
		}

		public String getNome() {
			return nome;
		}

		public boolean isTemArgumento() {
			return temArgumento;
		}

		public String getDescricao() {
			return descricao;
		}
	}

}
