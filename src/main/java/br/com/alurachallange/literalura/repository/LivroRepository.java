package br.com.alurachallange.literalura.repository;

import br.com.alurachallange.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    @Query("select l.titulo from Livro l")
    List<String> findAllByTitulo();
    List<Livro> findAllByTituloContainingIgnoreCase(String nomeDoLivro);
    @Query("SELECT l FROM Livro l JOIN l.idioma i WHERE LOWER(i) = LOWER(:idioma)")
    List<Livro> findAllByIdiomaIgnoreCase(@Param("idioma") String idioma);
    List<Livro> findTop10ByOrderByTotalDeDownloadsDesc();
    List<Livro> findAllByAutores_NomeContainingIgnoreCase(String nomeDoAutor);
}