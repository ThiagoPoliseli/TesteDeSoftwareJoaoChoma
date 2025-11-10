package com.example.demo;

import com.example.demo.dto.PessoaDTO;
import com.example.demo.dto.TrabalhoDTO;
import com.example.demo.entity.Pessoa;
import com.example.demo.entity.Trabalho;
import com.example.demo.repository.PessoaRepository;
import com.example.demo.service.PessoaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private PessoaService pessoaService;

    private Pessoa pessoa;
    private PessoaDTO pessoaDTO;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João Silva");
        pessoa.setCpf("12345678900");

        Trabalho trabalho = new Trabalho();
        trabalho.setId(1L);
        trabalho.setDescricao("Desenvolvedor");
        trabalho.setPessoa(pessoa);
        pessoa.setTrabalhos(Arrays.asList(trabalho));

        pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(1L);
        pessoaDTO.setNome("João Silva");
        pessoaDTO.setCpf("12345678900");
        
        TrabalhoDTO trabalhoDTO = new TrabalhoDTO();
        trabalhoDTO.setId(1L);
        trabalhoDTO.setDescricao("Desenvolvedor");
        pessoaDTO.setTrabalhos(Arrays.asList(trabalhoDTO));
    }

    @Test
    void salvar_deveSalvarPessoaComTrabalhos() {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("Maria Santos");
        dto.setCpf("98765432100");
        
        TrabalhoDTO trabalhoDTO = new TrabalhoDTO();
        trabalhoDTO.setDescricao("Analista");
        dto.setTrabalhos(Arrays.asList(trabalhoDTO));

        Pessoa pessoaSalva = new Pessoa();
        pessoaSalva.setId(1L);
        pessoaSalva.setNome("Maria Santos");
        pessoaSalva.setCpf("98765432100");
        pessoaSalva.setTrabalhos(new java.util.ArrayList<>());

        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoaSalva);

        PessoaDTO resultado = pessoaService.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Maria Santos");
        assertThat(resultado.getCpf()).isEqualTo("98765432100");
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void salvar_deveSalvarPessoaSemTrabalhos() {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("Carlos Oliveira");
        dto.setCpf("11111111111");
        dto.setTrabalhos(Arrays.asList());

        Pessoa pessoaSalva = new Pessoa();
        pessoaSalva.setId(1L);
        pessoaSalva.setNome("Carlos Oliveira");
        pessoaSalva.setCpf("11111111111");
        pessoaSalva.setTrabalhos(new java.util.ArrayList<>());

        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoaSalva);

        PessoaDTO resultado = pessoaService.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Carlos Oliveira");
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void buscarPorId_deveRetornarPessoaQuandoExiste() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));

        PessoaDTO resultado = pessoaService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getCpf()).isEqualTo("12345678900");
        assertThat(resultado.getTrabalhos()).hasSize(1);
        assertThat(resultado.getTrabalhos().get(0).getDescricao()).isEqualTo("Desenvolvedor");
        verify(pessoaRepository).findById(1L);
    }

    @Test
    void buscarPorId_deveLancarExceptionQuandoNaoExiste() {
        when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pessoaService.buscarPorId(999L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");

        verify(pessoaRepository).findById(999L);
    }

    @Test
    void listarTodos_deveRetornarListaDePessoas() {
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setId(1L);
        pessoa1.setNome("João");
        pessoa1.setCpf("11111111111");
        pessoa1.setTrabalhos(new java.util.ArrayList<>());

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setId(2L);
        pessoa2.setNome("Maria");
        pessoa2.setCpf("22222222222");
        pessoa2.setTrabalhos(new java.util.ArrayList<>());

        List<Pessoa> pessoas = Arrays.asList(pessoa1, pessoa2);

        when(pessoaRepository.findAll()).thenReturn(pessoas);

        List<PessoaDTO> resultado = pessoaService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria");
        verify(pessoaRepository).findAll();
    }

    @Test
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaPessoas() {
        when(pessoaRepository.findAll()).thenReturn(Arrays.asList());

        List<PessoaDTO> resultado = pessoaService.listarTodos();

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(pessoaRepository).findAll();
    }

    @Test
    void atualizar_deveAtualizarPessoaComSucesso() {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João Silva Atualizado");
        dto.setCpf("12345678900");
        
        TrabalhoDTO trabalhoDTO = new TrabalhoDTO();
        trabalhoDTO.setDescricao("Gerente");
        dto.setTrabalhos(Arrays.asList(trabalhoDTO));

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(1L);
            if (p.getTrabalhos() == null) {
                p.setTrabalhos(new java.util.ArrayList<>());
            }
            return p;
        });

        PessoaDTO resultado = pessoaService.atualizar(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva Atualizado");
        verify(pessoaRepository).findById(1L);
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void atualizar_deveLancarExceptionQuandoNaoExiste() {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João");
        dto.setCpf("12345678900");

        when(pessoaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pessoaService.atualizar(999L, dto))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");

        verify(pessoaRepository).findById(999L);
        verify(pessoaRepository, never()).save(any());
    }

    @Test
    void atualizar_deveLimparTrabalhosAntigosEAdicionarNovos() {
        Pessoa pessoaComTrabalhos = new Pessoa();
        pessoaComTrabalhos.setId(1L);
        pessoaComTrabalhos.setNome("João");
        pessoaComTrabalhos.setCpf("12345678900");
        
        Trabalho trabalhoAntigo = new Trabalho();
        trabalhoAntigo.setId(1L);
        trabalhoAntigo.setDescricao("Desenvolvedor");
        trabalhoAntigo.setPessoa(pessoaComTrabalhos);
        pessoaComTrabalhos.setTrabalhos(new java.util.ArrayList<>(Arrays.asList(trabalhoAntigo)));

        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João Atualizado");
        dto.setCpf("12345678900");
        
        TrabalhoDTO novoTrabalhoDTO = new TrabalhoDTO();
        novoTrabalhoDTO.setDescricao("Analista");
        dto.setTrabalhos(Arrays.asList(novoTrabalhoDTO));

        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaComTrabalhos));
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            if (p.getTrabalhos() == null) {
                p.setTrabalhos(new java.util.ArrayList<>());
            }
            return p;
        });

        PessoaDTO resultado = pessoaService.atualizar(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTrabalhos()).hasSize(1);
        assertThat(resultado.getTrabalhos().get(0).getDescricao()).isEqualTo("Analista");
        verify(pessoaRepository).findById(1L);
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deletar_deveDeletarPessoaComSucesso() {
        doNothing().when(pessoaRepository).deleteById(1L);

        pessoaService.deletar(1L);

        verify(pessoaRepository).deleteById(1L);
    }

    @Test
    void salvar_deveSalvarPessoaComMultiplosTrabalhos() {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("Ana Costa");
        dto.setCpf("33333333333");
        
        TrabalhoDTO trabalho1 = new TrabalhoDTO();
        trabalho1.setDescricao("Desenvolvedor");
        
        TrabalhoDTO trabalho2 = new TrabalhoDTO();
        trabalho2.setDescricao("Analista");
        
        TrabalhoDTO trabalho3 = new TrabalhoDTO();
        trabalho3.setDescricao("Gerente");
        
        dto.setTrabalhos(Arrays.asList(trabalho1, trabalho2, trabalho3));

        Pessoa pessoaSalva = new Pessoa();
        pessoaSalva.setId(1L);
        pessoaSalva.setNome("Ana Costa");
        pessoaSalva.setCpf("33333333333");
        pessoaSalva.setTrabalhos(new java.util.ArrayList<>());

        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoaSalva);

        PessoaDTO resultado = pessoaService.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTrabalhos()).hasSize(3);
        verify(pessoaRepository).save(any(Pessoa.class));
    }
}

