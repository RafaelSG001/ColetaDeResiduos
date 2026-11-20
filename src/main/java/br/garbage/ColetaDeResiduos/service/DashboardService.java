package br.garbage.ColetaDeResiduos.service;

import br.garbage.ColetaDeResiduos.model.StatusPedido;
import br.garbage.ColetaDeResiduos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class DashboardService {
    private final EmpresaRepository empresas;
    private final ClienteRepository clientes;
    private final ServicoRepository servicos;
    private final PedidoRepository pedidos;
    private final ItemPedidoRepository itens;
    private final EnderecoRepository enderecos;
    private final TipoEdificacaoRepository tiposEdificacao;
    private final IndicadorMensalRepository indicadores;
    private final UsuarioRepository usuarios;

    public DashboardService(EmpresaRepository empresas, ClienteRepository clientes,
                            ServicoRepository servicos, PedidoRepository pedidos,
                            ItemPedidoRepository itens, EnderecoRepository enderecos,
                            TipoEdificacaoRepository tiposEdificacao, IndicadorMensalRepository indicadores,
                            UsuarioRepository usuarios) {
        this.empresas = empresas;
        this.clientes = clientes;
        this.servicos = servicos;
        this.pedidos = pedidos;
        this.itens = itens;
        this.enderecos = enderecos;
        this.tiposEdificacao = tiposEdificacao;
        this.indicadores = indicadores;
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public void preencherIndicadores(Model model) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        int ano = hoje.getYear();
        int semestre = hoje.getMonthValue() <= 6 ? 1 : 2;
        model.addAttribute("anoAtual", ano);
        model.addAttribute("semestreAtual", semestre);
        model.addAttribute("totalEmpresas", empresas.count());
        model.addAttribute("totalClientes", clientes.count());
        model.addAttribute("totalServicos", servicos.count());
        model.addAttribute("totalPedidos", pedidos.count());
        model.addAttribute("totalItens", itens.count());
        model.addAttribute("totalEnderecos", enderecos.count());
        model.addAttribute("totalTiposEdificacao", tiposEdificacao.count());
        model.addAttribute("totalIndicadores", indicadores.count());
        model.addAttribute("totalUsuarios", usuarios.count());
        model.addAttribute("pedidosConcluidos", pedidos.countByStatus(StatusPedido.CONCLUIDO));
        model.addAttribute("pedidosPendentes", pedidos.countByStatus(StatusPedido.PENDENTE));
        model.addAttribute("pesoTotalTon", itens.somarPesoTotalColetado());
        model.addAttribute("receitaTotalReciclagem", pedidos.somarReceitaReciclagemTotal());
        model.addAttribute("co2TotalEvitado", indicadores.somarCo2EvitadoTotal());
    }
}
