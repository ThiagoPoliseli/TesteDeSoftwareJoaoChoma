package com.example.demo;

import com.example.demo.controller.PessoaController;
import com.example.demo.dto.PessoaDTO;
import com.example.demo.dto.TrabalhoDTO;
import com.example.demo.service.PessoaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PessoaController.class)
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PessoaService pessoaService;

    @Test
    void criar_deveCriarPessoaComSucesso() throws Exception {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João Silva");
        dto.setCpf("12345678900");
        
        TrabalhoDTO trabalhoDTO = new TrabalhoDTO();
        trabalhoDTO.setDescricao("Desenvolvedor");
        dto.setTrabalhos(Arrays.asList(trabalhoDTO));

        PessoaDTO pessoaSalva = new PessoaDTO();
        pessoaSalva.setId(1L);
        pessoaSalva.setNome("João Silva");
        pessoaSalva.setCpf("12345678900");
        pessoaSalva.setTrabalhos(dto.getTrabalhos());

        when(pessoaService.salvar(any(PessoaDTO.class))).thenReturn(pessoaSalva);

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.trabalhos[0].descricao").value("Desenvolvedor"));

        verify(pessoaService).salvar(any(PessoaDTO.class));
    }

    @Test
    void buscarPorId_deveRetornarPessoaQuandoExiste() throws Exception {
        PessoaDTO pessoa = new PessoaDTO();
        pessoa.setId(1L);
        pessoa.setNome("Maria Santos");
        pessoa.setCpf("98765432100");

        when(pessoaService.buscarPorId(1L)).thenReturn(pessoa);

        mockMvc.perform(get("/api/pessoas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.cpf").value("98765432100"));

        verify(pessoaService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_deveRetornar500QuandoNaoExiste() throws Exception {
        when(pessoaService.buscarPorId(999L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(get("/api/pessoas/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(pessoaService).buscarPorId(999L);
    }

    @Test
    void listarTodos_deveRetornarListaDePessoas() throws Exception {
        PessoaDTO pessoa1 = new PessoaDTO();
        pessoa1.setId(1L);
        pessoa1.setNome("João");
        pessoa1.setCpf("11111111111");

        PessoaDTO pessoa2 = new PessoaDTO();
        pessoa2.setId(2L);
        pessoa2.setNome("Maria");
        pessoa2.setCpf("22222222222");

        List<PessoaDTO> pessoas = Arrays.asList(pessoa1, pessoa2);

        when(pessoaService.listarTodos()).thenReturn(pessoas);

        mockMvc.perform(get("/api/pessoas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Maria"));

        verify(pessoaService).listarTodos();
    }

    @Test
    void atualizar_deveAtualizarPessoaComSucesso() throws Exception {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João Silva Atualizado");
        dto.setCpf("12345678900");

        PessoaDTO pessoaAtualizada = new PessoaDTO();
        pessoaAtualizada.setId(1L);
        pessoaAtualizada.setNome("João Silva Atualizado");
        pessoaAtualizada.setCpf("12345678900");

        when(pessoaService.atualizar(eq(1L), any(PessoaDTO.class))).thenReturn(pessoaAtualizada);

        mockMvc.perform(put("/api/pessoas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.cpf").value("12345678900"));

        verify(pessoaService).atualizar(eq(1L), any(PessoaDTO.class));
    }

    @Test
    void atualizar_deveRetornar500QuandoNaoExiste() throws Exception {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("João");
        dto.setCpf("12345678900");

        when(pessoaService.atualizar(eq(999L), any(PessoaDTO.class)))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Pessoa não encontrada"));

        mockMvc.perform(put("/api/pessoas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());

        verify(pessoaService).atualizar(eq(999L), any(PessoaDTO.class));
    }

    @Test
    void deletar_deveDeletarPessoaComSucesso() throws Exception {
        doNothing().when(pessoaService).deletar(1L);

        mockMvc.perform(delete("/api/pessoas/1"))
                .andExpect(status().isNoContent());

        verify(pessoaService).deletar(1L);
    }

    @Test
    void criar_deveCriarPessoaComMultiplosTrabalhos() throws Exception {
        PessoaDTO dto = new PessoaDTO();
        dto.setNome("Carlos Oliveira");
        dto.setCpf("55555555555");
        
        TrabalhoDTO trabalho1 = new TrabalhoDTO();
        trabalho1.setDescricao("Desenvolvedor");
        
        TrabalhoDTO trabalho2 = new TrabalhoDTO();
        trabalho2.setDescricao("Analista");
        
        dto.setTrabalhos(Arrays.asList(trabalho1, trabalho2));

        PessoaDTO pessoaSalva = new PessoaDTO();
        pessoaSalva.setId(1L);
        pessoaSalva.setNome("Carlos Oliveira");
        pessoaSalva.setCpf("55555555555");
        pessoaSalva.setTrabalhos(dto.getTrabalhos());

        when(pessoaService.salvar(any(PessoaDTO.class))).thenReturn(pessoaSalva);

        mockMvc.perform(post("/api/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trabalhos", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.trabalhos[0].descricao").value("Desenvolvedor"))
                .andExpect(jsonPath("$.trabalhos[1].descricao").value("Analista"));

        verify(pessoaService).salvar(any(PessoaDTO.class));
    }
}

