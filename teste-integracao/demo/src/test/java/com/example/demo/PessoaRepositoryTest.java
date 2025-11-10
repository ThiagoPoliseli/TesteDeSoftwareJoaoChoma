package com.example.demo;

import com.example.demo.entity.Pessoa;
import com.example.demo.entity.Trabalho;
import com.example.demo.repository.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PessoaRepositoryTest {

    @Autowired
    PessoaRepository repository;

    @Test
    void deveSalvarEConsultarPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf("12345678900");
        
        Pessoa salva = repository.save(pessoa);
        
        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getNome()).isEqualTo("João Silva");
        assertThat(salva.getCpf()).isEqualTo("12345678900");
    }

    @Test
    void deveSalvarPessoaComTrabalhos() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria Santos");
        pessoa.setCpf("98765432100");
        
        Trabalho trabalho1 = new Trabalho();
        trabalho1.setDescricao("Desenvolvedor");
        trabalho1.setPessoa(pessoa);
        
        Trabalho trabalho2 = new Trabalho();
        trabalho2.setDescricao("Analista");
        trabalho2.setPessoa(pessoa);
        
        pessoa.setTrabalhos(Arrays.asList(trabalho1, trabalho2));
        
        Pessoa salva = repository.save(pessoa);
        
        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getTrabalhos()).hasSize(2);
        assertThat(salva.getTrabalhos().get(0).getDescricao()).isEqualTo("Desenvolvedor");
        assertThat(salva.getTrabalhos().get(1).getDescricao()).isEqualTo("Analista");
    }

    @Test
    void deveBuscarPessoaPorId() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Carlos Oliveira");
        pessoa.setCpf("11111111111");
        
        Pessoa salva = repository.save(pessoa);
        
        Optional<Pessoa> encontrada = repository.findById(salva.getId());
        
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNome()).isEqualTo("Carlos Oliveira");
        assertThat(encontrada.get().getCpf()).isEqualTo("11111111111");
    }

    @Test
    void deveListarTodasAsPessoas() {
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("João");
        pessoa1.setCpf("11111111111");
        
        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Maria");
        pessoa2.setCpf("22222222222");
        
        repository.save(pessoa1);
        repository.save(pessoa2);
        
        List<Pessoa> pessoas = repository.findAll();
        
        assertThat(pessoas).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deveDeletarPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Ana Costa");
        pessoa.setCpf("33333333333");
        
        Pessoa salva = repository.save(pessoa);
        Long id = salva.getId();
        
        repository.deleteById(id);
        
        Optional<Pessoa> deletada = repository.findById(id);
        assertThat(deletada).isEmpty();
    }

    @Test
    void deveDeletarTrabalhosQuandoPessoaEhDeletada() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Pedro Alves");
        pessoa.setCpf("44444444444");
        
        Trabalho trabalho = new Trabalho();
        trabalho.setDescricao("Desenvolvedor");
        trabalho.setPessoa(pessoa);
        pessoa.setTrabalhos(Arrays.asList(trabalho));
        
        Pessoa salva = repository.save(pessoa);
        Long pessoaId = salva.getId();
        Long trabalhoId = salva.getTrabalhos().get(0).getId();
        
        repository.deleteById(pessoaId);
        
        Optional<Pessoa> deletada = repository.findById(pessoaId);
        assertThat(deletada).isEmpty();
    }
}

