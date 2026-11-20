package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.Empresa;
import br.garbage.ColetaDeResiduos.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpresaService {
    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Empresa> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Empresa buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada: " + id));
    }

    @Transactional
    public Empresa cadastrar(Empresa dados) {
        validar(dados);
        dados.setIdEmpresa(null);
        return repository.save(dados);
    }

    @Transactional
    public Empresa atualizar(Long id, Empresa dados) {
        validar(dados);
        Empresa atual = buscar(id);
        atual.setNomeEmpresa(dados.getNomeEmpresa());
        atual.setCnpjOperadora(dados.getCnpjOperadora());
        atual.setCidadeSede(dados.getCidadeSede());
        atual.setEstadoSede(dados.getEstadoSede().toUpperCase());
        atual.setLatitude(dados.getLatitude());
        atual.setLongitude(dados.getLongitude());
        atual.setAnoFundacao(dados.getAnoFundacao());
        atual.setFuncionarios(dados.getFuncionarios());
        atual.setClientesAtivos(dados.getClientesAtivos());
        atual.setCertificacaoIso14001(Boolean.TRUE.equals(dados.getCertificacaoIso14001()));
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }

    private void validar(Empresa e) {
        if (e == null || e.getNomeEmpresa() == null || e.getNomeEmpresa().isBlank()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório.");
        }
        if (e.getCnpjOperadora() == null || e.getCnpjOperadora().isBlank()) e.setCnpjOperadora("12.345.678/0001-99");
        if (e.getLatitude() == null) e.setLatitude(new BigDecimal("-23.550520"));
        if (e.getLongitude() == null) e.setLongitude(new BigDecimal("-46.633308"));
        if (e.getAnoFundacao() == null) e.setAnoFundacao(2015);
        if (e.getFuncionarios() == null) e.setFuncionarios(100);
        if (e.getClientesAtivos() == null) e.setClientesAtivos(50);
        if (e.getCertificacaoIso14001() == null) e.setCertificacaoIso14001(false);
    }
}
