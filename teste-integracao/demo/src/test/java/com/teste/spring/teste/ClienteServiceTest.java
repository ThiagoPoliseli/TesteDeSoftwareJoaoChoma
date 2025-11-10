package com.teste.spring.teste;


import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.exception.*;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository repo;

    @InjectMocks
    ClienteService service;

    @Test
    void criar_deveSalvarClienteQuandoEmailNaoExiste() {
        Cliente c = new Cliente();
        c.setNome("João");
        c.setEmail("joao@ex.com");
        c.setTelefone("11999999999");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João");
        clienteSalvo.setEmail("joao@ex.com");
        clienteSalvo.setTelefone("11999999999");

        when(repo.existsByEmail("joao@ex.com")).thenReturn(false);
        when(repo.save(c)).thenReturn(clienteSalvo);

        Cliente resultado = service.criar(c);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João");
        assertThat(resultado.getEmail()).isEqualTo("joao@ex.com");
        verify(repo).existsByEmail("joao@ex.com");
        verify(repo).save(c);
    }

    @Test
    void criar_deveLancarSeEmailJaExiste() {
        Cliente c = new Cliente();
        c.setNome("João");
        c.setEmail("j@ex.com");
        when(repo.existsByEmail("j@ex.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(c))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");
        verify(repo, never()).save(any());
    }

    @Test
    void buscar_deveRetornarClienteQuandoExiste() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@ex.com");

        when(repo.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscar(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João");
        verify(repo).findById(1L);
    }

    @Test
    void buscar_deveLancarExceptionQuandoNaoExiste() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(repo).findById(999L);
    }

    @Test
    void atualizar_deveAtualizarCamposBasicos() {
        Cliente antigo = new Cliente();
        antigo.setId(1L);
        antigo.setNome("Antigo");
        antigo.setEmail("a@ex.com");
        antigo.setTelefone("11");

        when(repo.findById(1L)).thenReturn(Optional.of(antigo));
        when(repo.findByEmail("a@ex.com")).thenReturn(Optional.of(antigo));
        when(repo.existsByEmail("a@ex.com")).thenReturn(true);
        when(repo.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente dados = new Cliente();
        dados.setNome("Novo");
        dados.setEmail("a@ex.com");
        dados.setTelefone("22");

        Cliente atualizado = service.atualizar(1L, dados);

        assertThat(atualizado.getNome()).isEqualTo("Novo");
        assertThat(atualizado.getEmail()).isEqualTo("a@ex.com");
        assertThat(atualizado.getTelefone()).isEqualTo("22");
        verify(repo).findById(1L);
        verify(repo).save(antigo);
    }

    @Test
    void atualizar_deveLancarExceptionQuandoEmailJaExisteEmOutroCliente() {
        Cliente antigo = new Cliente();
        antigo.setId(1L);
        antigo.setNome("Antigo");
        antigo.setEmail("a@ex.com");

        Cliente outroCliente = new Cliente();
        outroCliente.setId(2L);
        outroCliente.setEmail("novo@ex.com");

        when(repo.findById(1L)).thenReturn(Optional.of(antigo));
        when(repo.existsByEmail("novo@ex.com")).thenReturn(true);
        when(repo.findByEmail("novo@ex.com")).thenReturn(Optional.of(outroCliente));

        Cliente dados = new Cliente();
        dados.setNome("Novo");
        dados.setEmail("novo@ex.com");

        assertThatThrownBy(() -> service.atualizar(1L, dados))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado para outro cliente");

        verify(repo).findById(1L);
        verify(repo, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExceptionQuandoClienteNaoExiste() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Cliente dados = new Cliente();
        dados.setNome("Novo");
        dados.setEmail("novo@ex.com");

        assertThatThrownBy(() -> service.atualizar(999L, dados))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(repo).findById(999L);
        verify(repo, never()).save(any());
    }

    @Test
    void excluir_deveExcluirClienteQuandoExiste() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@ex.com");

        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        doNothing().when(repo).delete(cliente);

        service.excluir(1L);

        verify(repo).findById(1L);
        verify(repo).delete(cliente);
    }

    @Test
    void excluir_deveLancarExceptionQuandoClienteNaoExiste() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(repo).findById(999L);
        verify(repo, never()).delete(any());
    }
}
