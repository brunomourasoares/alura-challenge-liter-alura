package br.com.alurachallange.literalura.service;

import br.com.alurachallange.literalura.dto.RespostaAPI;
import br.com.alurachallange.literalura.exception.NotFoundException;
import br.com.alurachallange.literalura.model.Livro;
import br.com.alurachallange.literalura.repository.LivroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;

@Service
public class LivroService {

    private final ConsumoApi consumo = new ConsumoApi();
    private final IConverteDados conversor = new ConverteDados();
    private final LivroRepository livroRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public RespostaAPI buscarLivrosNaAPI(String nomeDoLivro) {
        String json = consumo.obterDados("https://gutendex.com/books/?search=" + nomeDoLivro);

        if (json == null || json.isBlank()) {
            throw new NotFoundException("Livro não encontrado na API");
        }

        return conversor.jsonParaClasse(json, RespostaAPI.class);
    }

    public List<Livro> procurarLivrosNoBancoDeDados(String nomeDoLivro) {
        return livroRepository.findAllByTituloContainingIgnoreCase(nomeDoLivro);
    }

    public List<Livro> listarTodosOsLivrosCadastrados() {
        return livroRepository.findAll();
    }

    public List<Livro> listarLivrosPorIdioma(String idioma) {
        return livroRepository.findAllByIdiomaIgnoreCase(idioma);
    }

    public List<Livro> listarLivrosPorAutor(String nomeDoAutor) {
        return livroRepository.findAllByAutores_NomeContainingIgnoreCase(nomeDoAutor);
    }

    public List<Livro> listarTop10Download() {
        return livroRepository.findTop10ByOrderByTotalDeDownloadsDesc();
    }

    @Transactional
    public void salvarTodosOsLivros(List<Livro> livros) {

        Set<String> titulosExistentes = new HashSet<>(livroRepository.findAllByTitulo());

        List<Livro> novosLivros = livros.stream()
                .filter(l -> l.getTitulo() != null && !titulosExistentes.contains(l.getTitulo()))
                .toList();

        final int batchSize = 32;

        for (int i = 0; i < novosLivros.size(); i += batchSize) {
            List<Livro> chunk = novosLivros.subList(i, Math.min(i + batchSize, novosLivros.size()));
            livroRepository.saveAll(chunk);
            livroRepository.flush();
            entityManager.clear();
        }
    }

    public IntSummaryStatistics gerarEstatisticasDosLivros() {
        return livroRepository.findAll().stream()
                .mapToInt(Livro::getTotalDeDownloads)
                .summaryStatistics();
    }
}