package br.com.ucsal.olimpiadas;

import br.com.ucsal.olimpiadas.domain.*;
import br.com.ucsal.olimpiadas.repository.InMemoryRepository;
import br.com.ucsal.olimpiadas.repository.Repository;
import br.com.ucsal.olimpiadas.service.ProvaService;
import br.com.ucsal.olimpiadas.service.ProvaServiceImpl;
import br.com.ucsal.olimpiadas.service.QuestaoService;
import br.com.ucsal.olimpiadas.service.QuestaoServiceImpl;

import java.util.Scanner;

public class App {

	// Repositórios
	private final Repository<Participante> participanteRepo = new InMemoryRepository<>();
	private final Repository<Prova> provaRepo = new InMemoryRepository<>();
	private final Repository<Questao> questaoRepo = new InMemoryRepository<>();
	private final Repository<Tentativa> tentativaRepo = new InMemoryRepository<>();

	// Serviços
	private final QuestaoService questaoService = new QuestaoServiceImpl();
	private final ProvaService provaService = new ProvaServiceImpl(questaoRepo);

	private static long proximoParticipanteId = 1;
	private static long proximaProvaId = 1;
	private static long proximaQuestaoId = 1;
	private static long proximaTentativaId = 1;

	private final Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		new App().iniciar();
	}

	private void iniciar() {
		seed();
		menuPrincipal();
	}

	private void menuPrincipal() {
		while (true) {
			System.out.println("\n=== OLIMPÍADA DE QUESTÕES (V1 - SOLID) ===");
			System.out.println("1) Cadastrar participante");
			System.out.println("2) Cadastrar prova");
			System.out.println("3) Cadastrar questão (A–E) em uma prova");
			System.out.println("4) Aplicar prova");
			System.out.println("5) Listar tentativas");
			System.out.println("0) Sair");
			System.out.print("> ");

			switch (in.nextLine().trim()) {
				case "1" -> cadastrarParticipante();
				case "2" -> cadastrarProva();
				case "3" -> cadastrarQuestao();
				case "4" -> aplicarProva();
				case "5" -> listarTentativas();
				case "0" -> {
					System.out.println("tchau");
					return;
				}
				default -> System.out.println("opção inválida");
			}
		}
	}

	private void cadastrarParticipante() {
		System.out.print("Nome: ");
		var nome = in.nextLine();

		System.out.print("Email (opcional): ");
		var email = in.nextLine();

		if (nome == null || nome.isBlank()) {
			System.out.println("nome inválido");
			return;
		}

		var p = new Participante();
		p.setId(proximoParticipanteId++);
		p.setNome(nome);
		if (!email.isBlank()) p.setEmail(email);

		participanteRepo.salvar(p);
		System.out.println("Participante cadastrado: " + p.getId());
	}

	private void cadastrarProva() {
		System.out.print("Título da prova: ");
		var titulo = in.nextLine();

		if (titulo == null || titulo.isBlank()) {
			System.out.println("título inválido");
			return;
		}

		var prova = new Prova();
		prova.setId(proximaProvaId++);
		prova.setTitulo(titulo);

		provaRepo.salvar(prova);
		System.out.println("Prova criada: " + prova.getId());
	}

	private void cadastrarQuestao() {
		if (provaRepo.listarTodos().isEmpty()) {
			System.out.println("não há provas cadastradas");
			return;
		}

		var provaId = escolherProva();
		if (provaId == null) return;

		System.out.println("Enunciado:");
		var enunciado = in.nextLine();

		var alternativas = new String[5];
		for (int i = 0; i < 5; i++) {
			char letra = (char) ('A' + i);
			System.out.print("Alternativa " + letra + ": ");
			alternativas[i] = letra + ") " + in.nextLine();
		}

		System.out.print("Alternativa correta (A–E): ");
		char correta;
		try {
			correta = Questao.normalizar(in.nextLine().trim().charAt(0));
		} catch (Exception e) {
			System.out.println("alternativa inválida");
			return;
		}

		var q = new Questao();
		q.setId(proximaQuestaoId++);
		q.setProvaId(provaId);
		q.setEnunciado(enunciado);
		q.setAlternativas(alternativas);
		q.setAlternativaCorreta(correta);

		questaoRepo.salvar(q);
		System.out.println("Questão cadastrada: " + q.getId() + " (na prova " + provaId + ")");
	}

	private void aplicarProva() {
		if (participanteRepo.listarTodos().isEmpty()) {
			System.out.println("cadastre participantes primeiro");
			return;
		}
		if (provaRepo.listarTodos().isEmpty()) {
			System.out.println("cadastre provas primeiro");
			return;
		}

		var participanteId = escolherParticipante();
		if (participanteId == null) return;

		var provaId = escolherProva();
		if (provaId == null) return;

		var questoesDaProva = provaService.buscarQuestoesDaProva(provaId);

		if (questoesDaProva.isEmpty()) {
			System.out.println("esta prova não possui questões cadastradas");
			return;
		}

		var tentativa = new Tentativa();
		tentativa.setId(proximaTentativaId++);
		tentativa.setParticipanteId(participanteId);
		tentativa.setProvaId(provaId);

		System.out.println("\n--- Início da Prova ---");

		for (var q : questoesDaProva) {
			System.out.println("\nQuestão #" + q.getId());
			System.out.println(q.getEnunciado());

			if (q.getFenInicial() != null && !q.getFenInicial().isBlank()) {
				System.out.println("Posição inicial:");
				imprimirTabuleiroFen(q.getFenInicial());
			}

			for (var alt : q.getAlternativas()) {
				System.out.println(alt);
			}

			System.out.print("Sua resposta (A–E): ");
			char marcada = 'X';
			try {
				marcada = Questao.normalizar(in.nextLine().trim().charAt(0));
			} catch (Exception e) {
				System.out.println("resposta inválida (marcando como errada)");
			}

			var r = new Resposta();
			r.setQuestaoId(q.getId());
			r.setAlternativaMarcada(marcada);
			r.setCorreta(questaoService.isRespostaCorreta(q, marcada));

			tentativa.getRespostas().add(r);
		}

		tentativaRepo.salvar(tentativa);

		int nota = calcularNota(tentativa);
		System.out.println("\n--- Fim da Prova ---");
		System.out.println("Nota (acertos): " + nota + " / " + tentativa.getRespostas().size());
	}

	private int calcularNota(Tentativa tentativa) {
		int acertos = 0;
		for (var r : tentativa.getRespostas()) {
			if (r.isCorreta()) acertos++;
		}
		return acertos;
	}

	private void listarTentativas() {
		System.out.println("\n--- Tentativas ---");
		for (var t : tentativaRepo.listarTodos()) {
			System.out.printf("#%d | participante=%d | prova=%d | nota=%d/%d%n",
					t.getId(),                    // long
					t.getParticipanteId(),        // long
					t.getProvaId(),               // long
					calcularNota(t),              // int
					t.getRespostas().size());     // int
		}
	}

	private Long escolherParticipante() {
		System.out.println("\nParticipantes:");
		for (var p : participanteRepo.listarTodos()) {
			System.out.printf("  %d) %s%n", p.getId(), p.getNome());
		}
		System.out.print("Escolha o id do participante: ");

		try {
			long id = Long.parseLong(in.nextLine().trim());
			boolean existe = participanteRepo.listarTodos().stream()
					.anyMatch(p -> p.getId() == id);
			if (!existe) {
				System.out.println("id inválido");
				return null;
			}
			return id;
		} catch (Exception e) {
			System.out.println("entrada inválida");
			return null;
		}
	}

	private Long escolherProva() {
		System.out.println("\nProvas:");
		for (var p : provaRepo.listarTodos()) {
			System.out.printf("  %d) %s%n", p.getId(), p.getTitulo());
		}
		System.out.print("Escolha o id da prova: ");

		try {
			long id = Long.parseLong(in.nextLine());
			boolean existe = provaRepo.listarTodos().stream().anyMatch(p -> p.getId() == id);
			if (!existe) {
				System.out.println("id inválido");
				return null;
			}
			return id;
		} catch (Exception e) {
			System.out.println("entrada inválida");
			return null;
		}
	}

	private void imprimirTabuleiroFen(String fen) {
		String parteTabuleiro = fen.split(" ")[0];
		String[] ranks = parteTabuleiro.split("/");

		System.out.println();
		System.out.println("    a b c d e f g h");
		System.out.println("   -----------------");

		for (int r = 0; r < 8; r++) {
			String rank = ranks[r];
			System.out.print((8 - r) + " | ");

			for (char c : rank.toCharArray()) {
				if (Character.isDigit(c)) {
					int vazios = c - '0';
					for (int i = 0; i < vazios; i++) {
						System.out.print(". ");
					}
				} else {
					System.out.print(c + " ");
				}
			}
			System.out.println("| " + (8 - r));
		}

		System.out.println("   -----------------");
		System.out.println("    a b c d e f g h");
		System.out.println();
	}

	private void seed() {
		var prova = new Prova();
		prova.setId(proximaProvaId++);
		prova.setTitulo("Olimpíada 2026 • Nível 1 • Prova A");
		provaRepo.salvar(prova);

		var q1 = new Questao();
		q1.setId(proximaQuestaoId++);
		q1.setProvaId(prova.getId());
		q1.setEnunciado("""
                Questão 1 — Mate em 1.
                É a vez das brancas.
                Encontre o lance que dá mate imediatamente.
                """);
		q1.setFenInicial("6k1/5ppp/8/8/8/7Q/6PP/6K1 w - - 0 1");
		q1.setAlternativas(new String[]{"A) Qh7#", "B) Qf5#", "C) Qc8#", "D) Qh8#", "E) Qe6#"});
		q1.setAlternativaCorreta('C');

		questaoRepo.salvar(q1);
	}
}