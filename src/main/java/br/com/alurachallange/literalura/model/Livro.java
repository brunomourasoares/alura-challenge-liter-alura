package br.com.alurachallange.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonAlias("id")
    private Long id;

    @JsonAlias("title")
    @Column(name = "titulo", length = 1000)
    private String titulo;

    @JsonAlias("authors")
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    @JsonAlias("summaries")
    @ElementCollection
    @CollectionTable(name = "livro_resumos", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "resumo", length = 4000)
    private List<String> resumos = new ArrayList<>();

    @JsonAlias("editors")
    @JsonDeserialize(using = EditorsDeserializer.class)
    @ElementCollection
    @CollectionTable(name = "livro_editores", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "editor")
    private List<String> editores = new ArrayList<>();

    @JsonAlias("translators")
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "livro_tradutor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> tradutores = new ArrayList<>();

    @JsonAlias("subjects")
    @ElementCollection
    @CollectionTable(name = "livro_assuntos", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "assunto")
    private List<String> assuntos = new ArrayList<>();

    @JsonAlias("bookshelves")
    @ElementCollection
    @CollectionTable(name = "livro_estantes", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "estante")
    private List<String> estantes = new ArrayList<>();

    @JsonAlias("languages")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "livro_idiomas", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "idioma")
    private List<String> idioma = new ArrayList<>();

    @JsonAlias("copyright")
    private Boolean direitosAutorais;

    @JsonAlias("media_type")
    private String tipoDeMidia;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "formatos_id")
    @JsonAlias("formats")
    private FormatosMedia formatos;

    @JsonAlias("download_count")
    private int totalDeDownloads;

    public Livro() {
    }

    public Livro(Long id, String titulo, List<Autor> autores, List<String> resumos, List<String> editores,
                 List<Autor> tradutores, List<String> assuntos, List<String> estantes, List<String> idioma,
                 Boolean direitosAutorais, String tipoDeMidia, FormatosMedia formatos, int totalDeDownloads) {
        this.id = id;
        this.titulo = titulo;
        this.autores = autores;
        this.resumos = resumos;
        this.editores = editores;
        this.tradutores = tradutores;
        this.assuntos = assuntos;
        this.estantes = estantes;
        this.idioma = idioma;
        this.direitosAutorais = direitosAutorais;
        this.tipoDeMidia = tipoDeMidia;
        this.formatos = formatos;
        this.totalDeDownloads = totalDeDownloads;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public void addAutor(Autor autor) {
        this.autores.add(autor);
    }

    public List<String> getResumos() {
        return resumos;
    }

    public void setResumos(List<String> resumos) {
        this.resumos = resumos;
    }

    public void addResumo(String resumo) {
        this.resumos.add(resumo);
    }

    public List<String> getEditores() {
        return editores;
    }

    public void setEditores(List<String> editores) {
        this.editores = editores;
    }

    public void addEditor(String editor) {
        this.editores.add(editor);
    }

    public List<Autor> getTradutores() {
        return tradutores;
    }

    public void setTradutores(List<Autor> tradutores) {
        this.tradutores = tradutores;
    }

    public void addTradutor(Autor autor) {
        this.tradutores.add(autor);
    }

    public List<String> getAssuntos() {
        return assuntos;
    }

    public void setAssuntos(List<String> assuntos) {
        this.assuntos = assuntos;
    }

    public void addAssunto(String assunto) {
        this.assuntos.add(assunto);
    }

    public List<String> getEstantes() {
        return estantes;
    }

    public void setEstantes(List<String> estantes) {
        this.estantes = estantes;
    }

    public void addEstante(String estante) {
        this.estantes.add(estante);
    }

    public List<String> getIdioma() {
        return idioma;
    }

    public void setIdioma(List<String> idioma) {
        this.idioma = idioma;
    }

    public void addIdioma(String idioma) {
        this.idioma.add(idioma);
    }

    public Boolean getDireitosAutorais() {
        return direitosAutorais;
    }

    public void setDireitosAutorais(Boolean direitosAutorais) {
        this.direitosAutorais = direitosAutorais;
    }

    public String getTipoDeMidia() {
        return tipoDeMidia;
    }

    public void setTipoDeMidia(String tipoDeMidia) {
        this.tipoDeMidia = tipoDeMidia;
    }

    public FormatosMedia getFormatos() {
        return formatos;
    }

    public void setFormatos(FormatosMedia formatos) {
        this.formatos = formatos;
    }

    public int getTotalDeDownloads() {
        return totalDeDownloads;
    }

    public void setTotalDeDownloads(int totalDeDownloads) {
        this.totalDeDownloads = totalDeDownloads;
    }
}