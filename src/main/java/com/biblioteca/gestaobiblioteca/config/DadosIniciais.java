package com.biblioteca.gestaobiblioteca.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.biblioteca.gestaobiblioteca.model.Funcionario;
import com.biblioteca.gestaobiblioteca.model.Livro;
import com.biblioteca.gestaobiblioteca.model.Usuario;
import com.biblioteca.gestaobiblioteca.repository.FuncionarioRepository;
import com.biblioteca.gestaobiblioteca.repository.LivroRepository;
import com.biblioteca.gestaobiblioteca.repository.UsuarioRepository;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;

    public DadosIniciais(LivroRepository livroRepository,
                         UsuarioRepository usuarioRepository,
                         FuncionarioRepository funcionarioRepository) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.save(new Usuario(1, "Felipe",
                LocalDate.of(2000, 5, 20), "11999990000", "lapetina"));
        usuarioRepository.save(new Usuario(2, "Maria",
                LocalDate.of(1998, 11, 3), "11988880000", "mabel"));
        usuarioRepository.save(new Usuario(3, "Isa",
                LocalDate.of(2002, 2, 14), "11977770000", "gil"));

        funcionarioRepository.save(new Funcionario(1, "Lucas Natal",
                LocalDate.of(1990, 3, 10), "11955550000", "func123"));
        funcionarioRepository.save(new Funcionario(2, "Pepe",
                LocalDate.of(1985, 7, 25), "11944440000", "func123"));

        Livro domCasmurro = new Livro(1, "Dom Casmurro", "Machado de Assis", 1899);
        domCasmurro.adicionarExemplar(10);
        domCasmurro.adicionarExemplar(11);
        livroRepository.save(domCasmurro);

        Livro cortico = new Livro(2, "O Cortiço", "Aluísio Azevedo", 1890);
        cortico.adicionarExemplar(20);
        cortico.adicionarExemplar(21);
        livroRepository.save(cortico);

        Livro capitaes = new Livro(3, "Capitães da Areia", "Jorge Amado", 1937);
        capitaes.adicionarExemplar(30);
        livroRepository.save(capitaes);

        Livro vidasSecas = new Livro(4, "Vidas Secas", "Graciliano Ramos", 1938);
        vidasSecas.adicionarExemplar(40);
        vidasSecas.adicionarExemplar(41);
        livroRepository.save(vidasSecas);
    }
}