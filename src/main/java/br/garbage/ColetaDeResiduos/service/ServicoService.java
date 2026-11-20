package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Servico;
import br.garbage.ColetaDeResiduos.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServicoService {
    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Servico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Serviço não encontrado: " + id));
    }

    @Transactional
    public Servico cadastrar(Servico dados) {
        validar(dados);
        dados.setIdServico(null);
        return repository.save(dados);
    }

    @Transactional
    public Servico atualizar(Long id, Servico dados) {
        validar(dados);
        Servico atual = buscar(id);
        atual.setTipoResiduo(dados.getTipoResiduo());
        atual.setClassePericulosidade(dados.getClassePericulosidade());
        atual.setDestinoPadrao(dados.getDestinoPadrao());
        atual.setReciclavel(Boolean.TRUE.equals(dados.getReciclavel()));
        atual.setTarifaPorTonelada(dados.getTarifaPorTonelada());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void validar(Servico s) {
        if (s == null || s.getTipoResiduo() == null || s.getTipoResiduo().isBlank()) {
            throw new IllegalArgumentException("Tipo de resíduo é obrigatório.");
        }
        if (s.getTarifaPorTonelada() == null || s.getTarifaPorTonelada().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tarifa por tonelada inválida.");
        }
        if (s.getClassePericulosidade() == null || s.getClassePericulosidade().isBlank()) {
            s.setClassePericulosidade("Classe II-B - Inerte");
        }
        if (s.getDestinoPadrao() == null || s.getDestinoPadrao().isBlank()) {
            s.setDestinoPadrao("Reciclagem");
        }
        if (s.getReciclavel() == null) s.setReciclavel(true);
    }
}
