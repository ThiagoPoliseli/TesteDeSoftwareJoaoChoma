package com.teste.spring.teste;

import com.teste.spring.teste.controller.ClienteController;
import com.teste.spring.teste.dto.ClienteDto;
import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
    }

    @Test
    void criarBuscarAtualizarExcluir_deveFuncionarCompleto() throws Exception {
        ClienteDto dto = new ClienteDto();
        dto.setNome("João Teste");
        dto.setEmail("joao.teste@ex.com");
        dto.setTelefone("11999999999");

        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Teste"))
                .andExpect(jsonPath("$.email").value("joao.teste@ex.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteDto clienteCriado = objectMapper.readValue(response, ClienteDto.class);
        Long clienteId = clienteCriado.getId();

        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.nome").value("João Teste"));

        dto.setNome("João Teste Atualizado");
        dto.setTelefone("11888888888");

        mockMvc.perform(put("/api/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Teste Atualizado"))
                .andExpect(jsonPath("$.telefone").value("11888888888"));

        mockMvc.perform(delete("/api/clientes/{id}", clienteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_deveLancarErroQuandoEmailJaExiste() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Primeiro");
        cliente.setEmail("duplicado@ex.com");
        cliente.setTelefone("11111111111");
        clienteRepository.save(cliente);

        ClienteDto dto = new ClienteDto();
        dto.setNome("Segundo");
        dto.setEmail("duplicado@ex.com");
        dto.setTelefone("22222222222");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Email já cadastrado"));
    }

    @Test
    void listar_deveRetornarPaginacao() throws Exception {
        for (int i = 1; i <= 5; i++) {
            Cliente cliente = new Cliente();
            cliente.setNome("Cliente " + i);
            cliente.setEmail("cliente" + i + "@ex.com");
            cliente.setTelefone("1199999999" + i);
            clienteRepository.save(cliente);
        }

        mockMvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(3));
    }
}

