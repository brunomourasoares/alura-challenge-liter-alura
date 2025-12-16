package br.com.alurachallange.literalura.service;

public interface IConverteDados {
    <T> T  jsonParaClasse(String json, Class<T> classe);
}