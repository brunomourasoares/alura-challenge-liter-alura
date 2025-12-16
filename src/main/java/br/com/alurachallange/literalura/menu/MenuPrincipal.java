package br.com.alurachallange.literalura.menu;

import br.com.alurachallange.literalura.dto.RespostaAPI;
import br.com.alurachallange.literalura.exception.NotFoundException;
import br.com.alurachallange.literalura.model.Livro;
import br.com.alurachallange.literalura.model.Autor;
import br.com.alurachallange.literalura.service.LivroService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MenuPrincipal {

    private final Scanner entradaDeDados = new Scanner(System.in);
    private final LivroService livroService;

    public MenuPrincipal(LivroService livroService) {
        this.livroService = livroService;
    }

    public void exibirMenu() {

        System.out.println(
                """
                        
                        ----- MENU PRINCIPAL -----
                        1- Buscar livro pelo titulo
                        2- Listar livros registrados
                        3- Listar autores registrados
                        4- Listar autores vivos em um determinado ano
                        5- Listar livros em um determinado idioma
                        6- Gerar estatísticas (extra)
                        7- Top 10 livros mais baixados (extra)
                        8- Buscar autor por nome (extra)
                        0- Sair
                        """
        );

        System.out.print("Escolha o número de sua opção: ");
        String escolhaStr = entradaDeDados.nextLine();

        int escolha;

        try {
            escolha = Integer.parseInt(escolhaStr);
            if (escolha < 0 || escolha > 8) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("\nOpção inválida. Tente novamente.");
            exibirMenu();
            return;
        }

        switch (escolha) {
            case 1:
                System.out.print("Insira o nome do livro que você deseja procurar: ");
                String nomeDoLivro = entradaDeDados.nextLine();

                while (nomeDoLivro.isEmpty()) {
                    System.out.print("\nNome do livro vazio.\nInsira o nome do livro que você deseja procurar: ");
                    nomeDoLivro = entradaDeDados.nextLine();
                }

                List<Livro> listaDeLivros = livroService.procurarLivrosNoBancoDeDados(nomeDoLivro);

                if (listaDeLivros.isEmpty()) {
                    System.out.println("\nLivro não encontrado no banco de dados local.\nBuscando na API do Gutendex...");

                    try {
                        String nomeDoLivroParaAPI = nomeDoLivro.replace(" ", "+");
                        RespostaAPI respostaAPI = livroService.buscarLivrosNaAPI(nomeDoLivroParaAPI);

                        if (respostaAPI.encontrados() == 0) {
                            throw new NotFoundException("\nLivro não encontrado na API");
                        }

                        listaDeLivros = respostaAPI.resultados();

                        livroService.salvarTodosOsLivros(listaDeLivros);
                    } catch (NotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }

                prepararListaLivros(nomeDoLivro, listaDeLivros);

                exibirMenu();
                break;
            case 2:
                List<Livro> livrosCadastrados = livroService.listarTodosOsLivrosCadastrados();

                if (livrosCadastrados.isEmpty()) {
                    System.out.println("\nNenhum livro cadastrado.");
                } else {
                    livrosCadastrados.forEach(this::exibirLivro);
                }

                exibirMenu();
                break;
            case 3:
                List<Livro> livrosParaAutores = livroService.listarTodosOsLivrosCadastrados();

                if (livrosParaAutores.isEmpty()) {
                    System.out.println("\nNenhum autor cadastrado.");
                } else {
                    Map<String, Autor> autoresMap = new HashMap<>();
                    Map<String, List<Livro>> livrosPorAutor = new HashMap<>();

                    for (Livro l : livrosParaAutores) {
                        for (Autor a : l.getAutores()) {
                            autoresMap.putIfAbsent(a.getNome(), a);
                            livrosPorAutor.computeIfAbsent(a.getNome(), k -> new ArrayList<>()).add(l);
                        }
                    }

                    livrosPorAutor.keySet().stream().sorted().forEach(nome ->
                            exibirAutor(autoresMap.get(nome), livrosPorAutor.get(nome))
                    );
                }

                exibirMenu();
                break;
            case 4:
                System.out.print("Insira o ano que deseja pesquisar: ");
                String anoStr = entradaDeDados.nextLine();

                int ano;
                while (true) {
                    try {
                        ano = Integer.parseInt(anoStr);
                        if (ano > 0 && ano <= LocalDate.now().getYear()) {
                            break;
                        }
                        System.out.print("\nAno inválido.\nInsira o ano que deseja pesquisar: ");
                        anoStr = entradaDeDados.nextLine();
                    } catch (NumberFormatException e) {
                        System.out.print("\nAno inválido.\nInsira o ano que deseja pesquisar: ");
                        anoStr = entradaDeDados.nextLine();
                    }
                }

                List<Livro> livrosParaAutoresPorAno = livroService.listarTodosOsLivrosCadastrados();

                if (livrosParaAutoresPorAno.isEmpty()) {
                    System.out.println("\nNenhum autor cadastrado.");
                    exibirMenu();
                    break;
                }

                Map<String, Autor> autoresMap = new HashMap<>();
                Map<String, List<Livro>> livrosPorAutor = new HashMap<>();

                for (Livro l : livrosParaAutoresPorAno) {
                    for (Autor a : l.getAutores()) {
                        autoresMap.putIfAbsent(a.getNome(), a);
                        livrosPorAutor.computeIfAbsent(a.getNome(), k -> new ArrayList<>()).add(l);
                    }
                }

                int finalAno = ano;
                List<String> autoresVivos = autoresMap
                        .values()
                        .stream()
                        .filter(a -> a.getAnoDeNascimento() <= finalAno && a.getAnoDeFalecimento() >= finalAno)
                        .map(Autor::getNome)
                        .sorted()
                        .toList();

                if (autoresVivos.isEmpty()) {
                    System.out.println("\nNenhum autor encontrado vivo em " + ano + ".");
                } else {
                    System.out.println("\nAutores vivos em " + ano + ":");
                    for (String nome : autoresVivos) {
                        exibirAutor(autoresMap.get(nome), livrosPorAutor.get(nome));
                    }
                }

                exibirMenu();
                break;
            case 5:
                System.out.print("Insira o idioma que deseja pesquisar: ");
                String idioma = entradaDeDados.nextLine();

                while (idioma.isEmpty()) {
                    System.out.print("\nInforme um idioma válido.\nInsira o idioma que deseja pesquisar (Ex: en ou pt): " );
                    idioma = entradaDeDados.nextLine();
                }

                List<Livro> livrosPorIdioma = livroService.listarLivrosPorIdioma(idioma);

                if (livrosPorIdioma.isEmpty()) {
                    System.out.println("\nNenhum livro encontrado no idioma '" + idioma + "'.");
                } else {
                    System.out.printf("\nForam encontrados %d livros no idioma '%s':\n\n",
                            livrosPorIdioma.size(), idioma);
                    for (Livro l : livrosPorIdioma) {
                        exibirLivro(l);
                    }
                }

                exibirMenu();
                break;
            case 6:
                IntSummaryStatistics estatisticas = livroService.gerarEstatisticasDosLivros();
                System.out.printf(
                        """
                                
                                ----- ESTATÍSTICAS DOS LIVROS -----
                                Número total de livros: %d
                                Número mínimo de downloads: %d
                                Número máximo de downloads: %d
                                Média de downloads: %.2f
                                Soma total de downloads: %d
                                -------------------------------
                                """,
                        estatisticas.getCount(),
                        estatisticas.getMin(),
                        estatisticas.getMax(),
                        estatisticas.getAverage(),
                        estatisticas.getSum()
                );
                exibirMenu();
                break;
            case 7:
                List<Livro> top10LivrosBaixados = livroService.listarTop10Download();
                System.out.println("\nTop 10 livros mais baixados:");

                for (Livro l : top10LivrosBaixados) {
                    exibirLivro(l);
                }

                exibirMenu();
                break;
            case 8:
                System.out.print("Insira o nome do autor que você deseja procurar: ");
                String nomeDoAutor = entradaDeDados.nextLine();

                while (nomeDoAutor.isEmpty()) {
                    System.out.print("\nNome do autor vazio.\nInsira o nome do autor que você deseja procurar: ");
                    nomeDoAutor = entradaDeDados.nextLine();
                }

                List<Livro> listaAutorPorNome = livroService.listarLivrosPorAutor(nomeDoAutor);

                if (listaAutorPorNome.isEmpty()) {
                    System.out.println("\nAutor não encontrado no banco de dados.");
                    exibirMenu();
                    break;
                }

                prepararListaAutores(nomeDoAutor, listaAutorPorNome);

                exibirMenu();
                break;
            case 0:
                System.out.println("Saindo do programa...");
                System.exit(0);
            default:
                System.out.println("Opção inválida. Tente novamente.");
                exibirMenu();
        }
    }

    private void prepararListaLivros(String nomeDoLivro, List<Livro> listaDeLivros) {
        if (listaDeLivros.size() == 1) {
            System.out.println("\nLivro encontrado:");
            exibirLivro(listaDeLivros.get(0));
            return;
        }

        System.out.printf("\nForam encontrados %d livros com o título '%s'\n\n",
                listaDeLivros.size(), nomeDoLivro);

        for (int i = 0; i < listaDeLivros.size(); i++) {
            Livro l = listaDeLivros.get(i);
            System.out.printf("%d- %s\n", i + 1, l.getTitulo());
        }

        System.out.print("0- Para voltar ao menu principal\n\nEscolha o número de sua opção: ");

        int index;

        try {
            index = Integer.parseInt(entradaDeDados.nextLine()) - 1;

            while (index < -1 || index > listaDeLivros.size() - 1) {
                System.out.println("Opção inválida!\nEscolha o número de sua opção: ");
                index = Integer.parseInt(entradaDeDados.nextLine()) - 1;
            }

            if (index == -1) {
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("\nOpção inválida!\nTente novamente.");
            return;
        }

        exibirLivro(listaDeLivros.get(index));
    }

    private void prepararListaAutores(String nomeDoAutor, List<Livro> listaDeLivros) {
        // Proteção contra lista nula/vazia
        if (listaDeLivros == null || listaDeLivros.isEmpty()) {
            System.out.println("\nNenhum autor encontrado.");
            return;
        }

        String termoBusca = nomeDoAutor.toLowerCase(Locale.ROOT).trim();

        // Quando há um único livro, filtra autor pelo termo (LIKE case-insensitive)
        if (listaDeLivros.size() == 1) {
            Livro livroUnico = listaDeLivros.get(0);
            Optional<Autor> autorMatch = livroUnico.getAutores()
                    .stream()
                    .filter(a -> a.getNome() != null && a.getNome().toLowerCase(Locale.ROOT).contains(termoBusca))
                    .findFirst();

            Autor autorParaExibir = autorMatch.orElseGet(() -> livroUnico.getAutores().isEmpty() ? null : livroUnico.getAutores().get(0));
            if (autorParaExibir == null) {
                System.out.println("\nNenhum autor encontrado.");
                return;
            }
            System.out.println("\nAutor encontrado:");
            exibirAutor(autorParaExibir, listaDeLivros);
            return;
        }

        Map<String, Autor> autoresMapNome = new HashMap<>();
        Map<String, List<Livro>> livrosPorAutorNome = new HashMap<>();

        for (Livro l : listaDeLivros) {
            for (Autor a : l.getAutores()) {
                if (a.getNome() != null && a.getNome().toLowerCase(Locale.ROOT).contains(termoBusca)) {
                    autoresMapNome.putIfAbsent(a.getNome(), a);
                    livrosPorAutorNome.computeIfAbsent(a.getNome(), k -> new ArrayList<>()).add(l);
                }
            }
        }

        if (autoresMapNome.isEmpty()) {
            System.out.println("\nNenhum autor encontrado com o nome informado.");
            return;
        }

        // Ordenar nomes para exibição determinística
        List<String> nomesAutores = new ArrayList<>(autoresMapNome.keySet());
        nomesAutores.sort(String.CASE_INSENSITIVE_ORDER);

        System.out.printf("\nForam encontrados %d autores com nome semelhante a '%s'\n\n",
                nomesAutores.size(), nomeDoAutor);

        for (int i = 0; i < nomesAutores.size(); i++) {
            System.out.printf("%d- %s\n", i + 1, nomesAutores.get(i));
        }

        System.out.print("0- Para voltar ao menu principal\n\nEscolha o número de sua opção: ");

        int index;

        try {
            index = Integer.parseInt(entradaDeDados.nextLine()) - 1;

            if (index == -1) {
                exibirMenu();
                return;
            }

            while (index < -1 || index > nomesAutores.size() - 1) {
                System.out.print("\n\nOpção inválida!\n\nEscolha o número de sua opção: ");
                index = Integer.parseInt(entradaDeDados.nextLine()) - 1;
            }

        } catch (NumberFormatException e) {
            System.out.print("\n\nOpção inválida!\n\nEscolha o número de sua opção: ");
            index = Integer.parseInt(entradaDeDados.nextLine()) - 1;
        }

        String autorSelecionado = nomesAutores.get(index);
        exibirAutor(autoresMapNome.get(autorSelecionado), livrosPorAutorNome.get(autorSelecionado));
    }

    private void exibirLivro(Livro livro) {
        System.out.printf("""
                        
                        ----- LIVRO -----
                        Título: %s
                        Autor: %s
                        Idioma: %s
                        Número de Downloads: %d
                        -----------------
                        """,
                livro.getTitulo(),
                livro.getAutores().stream().map(Autor::getNome).collect(Collectors.joining(" | ")),
                String.join(", ", livro.getIdioma()),
                livro.getTotalDeDownloads());
    }

    private void exibirAutor(Autor autor, List<Livro> listaDeLivros) {
        String livrosStr = (listaDeLivros == null || listaDeLivros.isEmpty())
                ? "Nenhum livro registrado"
                : listaDeLivros.stream()
                .map(Livro::getTitulo)
                .distinct()
                .collect(Collectors.joining(" | "));

        System.out.printf("""
                        
                        ----- AUTOR -----
                        Autor: %s
                        Ano de Nascimento: %d
                        Ano de Falecimento: %d
                        Livros: %s
                        -----------------
                        """,
                autor.getNome(),
                autor.getAnoDeNascimento(),
                autor.getAnoDeFalecimento(),
                livrosStr
        );
    }
}

