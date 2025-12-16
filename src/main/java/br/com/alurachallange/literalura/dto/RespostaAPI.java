package br.com.alurachallange.literalura.dto;

import br.com.alurachallange.literalura.model.Livro;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record RespostaAPI(
        @JsonAlias("count")
        int encontrados,
        @JsonAlias("next")
        String proximo,
        @JsonAlias("previous")
        String anterior,
        @JsonAlias("results")
        List<Livro> resultados
) {}
