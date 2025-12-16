package br.com.alurachallange.literalura;

import br.com.alurachallange.literalura.menu.MenuPrincipal;
import br.com.alurachallange.literalura.service.LivroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

    private final LivroService livroService;

    public LiterAluraApplication(LivroService livroService) {
        this.livroService = livroService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);
    }

    @Override
    public void run(String... args) {
        MenuPrincipal menu = new MenuPrincipal(livroService);
        menu.exibirMenu();
    }
}
