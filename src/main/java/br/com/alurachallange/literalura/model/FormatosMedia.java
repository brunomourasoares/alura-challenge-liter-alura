package br.com.alurachallange.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "formatos_media")
public class FormatosMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonAlias({"text/html", "text/html; charset=utf-8"})
    private String textHtml;

    @JsonAlias("application/epub+zip")
    private String applicationEpubZip;

    @JsonAlias("application/x-mobipocket-ebook")
    private String applicationXMobipocketEbook;

    @JsonAlias({"text/plain; charset=us-ascii", "text/plain; charset=utf-8"})
    private String textPlainASCIIorUtf8;

    @JsonAlias("application/rdf+xml")
    private String applicationRdfXml;

    @JsonAlias("image/jpeg")
    private String imageJpeg;

    @JsonAlias("application/octet-stream")
    private String applicationOctetStream;

    // capture any other formats not modeled above (e.g. "text/html; charset=utf-8")
    @Transient
    private Map<String, String> additionalFormats = new HashMap<>();

    // Construtor padrão exigido pelo JPA
    public FormatosMedia() {
    }

    public FormatosMedia(Long id, String textHtml, String applicationEpubZip, String applicationXMobipocketEbook,
                         String textPlainASCII, String applicationRdfXml, String imageJpeg, String applicationOctetStream) {
        this.id = id;
        this.textHtml = textHtml;
        this.applicationEpubZip = applicationEpubZip;
        this.applicationXMobipocketEbook = applicationXMobipocketEbook;
        this.textPlainASCIIorUtf8 = textPlainASCII;
        this.applicationRdfXml = applicationRdfXml;
        this.imageJpeg = imageJpeg;
        this.applicationOctetStream = applicationOctetStream;
    }

    @JsonAnySetter
    public void setAdditionalFormat(String name, Object value) {
        if (name == null) return;
        this.additionalFormats.put(name, value == null ? null : value.toString());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextHtml() {
        return textHtml;
    }

    public void setTextHtml(String textHtml) {
        this.textHtml = textHtml;
    }

    public String getApplicationEpubZip() {
        return applicationEpubZip;
    }

    public void setApplicationEpubZip(String applicationEpubZip) {
        this.applicationEpubZip = applicationEpubZip;
    }

    public String getApplicationXMobipocketEbook() {
        return applicationXMobipocketEbook;
    }

    public void setApplicationXMobipocketEbook(String applicationXMobipocketEbook) {
        this.applicationXMobipocketEbook = applicationXMobipocketEbook;
    }

    public String getTextPlainASCIIorUtf8() {
        return textPlainASCIIorUtf8;
    }

    public void setTextPlainASCIIorUtf8(String textPlainASCIIorUtf8) {
        this.textPlainASCIIorUtf8 = textPlainASCIIorUtf8;
    }

    public String getApplicationRdfXml() {
        return applicationRdfXml;
    }

    public void setApplicationRdfXml(String applicationRdfXml) {
        this.applicationRdfXml = applicationRdfXml;
    }

    public String getImageJpeg() {
        return imageJpeg;
    }

    public void setImageJpeg(String imageJpeg) {
        this.imageJpeg = imageJpeg;
    }

    public String getApplicationOctetStream() {
        return applicationOctetStream;
    }

    public void setApplicationOctetStream(String applicationOctetStream) {
        this.applicationOctetStream = applicationOctetStream;
    }

    public Map<String, String> getAdditionalFormats() {
        return additionalFormats;
    }

    public void setAdditionalFormats(Map<String, String> additionalFormats) {
        this.additionalFormats = additionalFormats;
    }
}