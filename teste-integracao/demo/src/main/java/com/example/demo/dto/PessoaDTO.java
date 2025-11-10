package com.example.demo.dto;

import java.util.List;

public class PessoaDTO {

    private Long id;
    private String nome;
    private String cpf;
    private List<TrabalhoDTO> trabalhos;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<TrabalhoDTO> getTrabalhos() {
        return trabalhos;
    }

    public void setTrabalhos(List<TrabalhoDTO> trabalhos) {
        this.trabalhos = trabalhos;
    }
}
