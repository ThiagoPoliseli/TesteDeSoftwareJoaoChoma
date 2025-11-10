package com.example.demo.service;

import com.example.demo.dto.PessoaDTO;
import com.example.demo.dto.TrabalhoDTO;
import com.example.demo.entity.Pessoa;
import com.example.demo.entity.Trabalho;
import com.example.demo.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public PessoaDTO salvar(PessoaDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());

        List<Trabalho> trabalhos = dto.getTrabalhos().stream().map(t -> {
            Trabalho trabalho = new Trabalho();
            trabalho.setDescricao(t.getDescricao());
            trabalho.setPessoa(pessoa);
            return trabalho;
        }).collect(Collectors.toList());

        pessoa.setTrabalhos(trabalhos);
        return toDTO(pessoaRepository.save(pessoa));
    }

    public PessoaDTO buscarPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        return toDTO(pessoa);
    }

    public List<PessoaDTO> listarTodos() {
        return pessoaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PessoaDTO atualizar(Long id, PessoaDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());

        // Limpa a lista de trabalhos existente
        pessoa.getTrabalhos().clear();

        // Adiciona os novos trabalhos
        List<Trabalho> trabalhos = dto.getTrabalhos().stream().map(t -> {
            Trabalho trabalho = new Trabalho();
            trabalho.setDescricao(t.getDescricao());
            trabalho.setPessoa(pessoa);
            return trabalho;
        }).collect(Collectors.toList());

        pessoa.setTrabalhos(trabalhos);

        return toDTO(pessoaRepository.save(pessoa));
    }

    public void deletar(Long id) {
        pessoaRepository.deleteById(id);
    }

    private PessoaDTO toDTO(Pessoa pessoa) {
        PessoaDTO dto = new PessoaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setTrabalhos(pessoa.getTrabalhos().stream()
                .map(t -> {
                    TrabalhoDTO tDTO = new TrabalhoDTO();
                    tDTO.setId(t.getId());
                    tDTO.setDescricao(t.getDescricao());
                    return tDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
