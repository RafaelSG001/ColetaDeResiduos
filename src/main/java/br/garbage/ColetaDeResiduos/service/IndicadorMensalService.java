package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.IndicadorMensal;
import br.garbage.ColetaDeResiduos.repository.EmpresaRepository;
import br.garbage.ColetaDeResiduos.repository.IndicadorMensalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class IndicadorMensalService {
    private final IndicadorMensalRepository repository;
    private final EmpresaRepository empresaRepo;

    public IndicadorMensalService(IndicadorMensalRepository repository, EmpresaRepository empresaRepo) {
        this.repository = repository;
        this.empresaRepo = empresaRepo;
    }

    @Transactional(readOnly = true)
    public Page<IndicadorMensal> listarPaginado(Long idEmpresa, int pagina, int tamanho) {
        PageRequest pr = PageRequest.of(Math.max(0, pagina), tamanho, Sort.by("mesReferencia").descending());
        if (idEmpresa != null) {
            return repository.findByEmpresa_IdEmpresa(idEmpresa, pr);
        }
        return repository.findAll(pr);
    }

    @Transactional(readOnly = true)
    public IndicadorMensal buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Indicador Mensal não encontrado: " + id));
    }

    @Transactional
    public IndicadorMensal cadastrar(IndicadorMensal dados, Long idEmpresa) {
        dados.setEmpresa(empresaRepo.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada.")));
        dados.setIdIndicador(null);
        return repository.save(dados);
    }

    @Transactional
    public IndicadorMensal atualizar(Long id, IndicadorMensal dados, Long idEmpresa) {
        IndicadorMensal atual = buscar(id);
        atual.setEmpresa(empresaRepo.findById(idEmpresa)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada.")));
        atual.setMesReferencia(dados.getMesReferencia());
        atual.setTotalResiduosTon(dados.getTotalResiduosTon());
        atual.setTaxaReciclagemPercentual(dados.getTaxaReciclagemPercentual());
        atual.setCustoTotalOperacao(dados.getCustoTotalOperacao());
        atual.setReceitaTotal(dados.getReceitaTotal());
        atual.setEmissaoCo2Evitaton(dados.getEmissaoCo2Evitaton());
        atual.setClientesAtendidos(dados.getClientesAtendidos());
        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscar(id)); }
}
