package br.garbage.ColetaDeResiduos.carga;

import br.garbage.ColetaDeResiduos.model.*;
import br.garbage.ColetaDeResiduos.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Configuration
public class CargaAutomatica {

    @Bean
    public CommandLineRunner inicializarUsuariosEBaseCompleta(
            UsuarioRepository usuarioRepo,
            TipoEdificacaoRepository tipoRepo,
            EnderecoRepository enderecoRepo,
            ServicoRepository servicoRepo,
            EmpresaRepository empresaRepo,
            IndicadorMensalRepository indicadorRepo,
            ClienteRepository clienteRepo,
            PedidoRepository pedidoRepo,
            ItemPedidoRepository itemRepo,
            PasswordEncoder passwordEncoder,
            Environment environment,
            TransactionTemplate txTemplate) {

        return args -> {
            System.out.println("Iniciando carga automática de usuários e base ambiental...");

            String username = environment.getProperty("APP_ADMIN_USER", "admin");
            String senha = environment.getProperty("APP_ADMIN_PASSWORD", "admin123");

            if (usuarioRepo.findByUsername(username).isEmpty()) {
                Usuario admin = new Usuario(username, passwordEncoder.encode(senha), Perfil.ADMIN, true);
                usuarioRepo.saveAndFlush(admin);
                System.out.println("Administrador cadastrado com sucesso: " + username + " / " + senha);
            }

            if (usuarioRepo.findByUsername("operador").isEmpty()) {
                usuarioRepo.saveAndFlush(new Usuario("operador", passwordEncoder.encode("operador123"), Perfil.OPERADOR, true));
                System.out.println("Operador cadastrado com sucesso: operador / operador123");
            }

            if (usuarioRepo.findByUsername("cliente").isEmpty()) {
                usuarioRepo.saveAndFlush(new Usuario("cliente", passwordEncoder.encode("cliente123"), Perfil.CLIENTE, true));
                System.out.println("Cliente cadastrado com sucesso: cliente / cliente123");
            }

            if (pedidoRepo.count() > 0) {
                System.out.println("Base de dados ambiental já carregada (" + pedidoRepo.count() + " pedidos).");
                return;
            }

            txTemplate.executeWithoutResult(statusTx -> {
                try {
                    Object[][] segmentos = {
                        {"Condomínio", "NBR 10004 - Resíduos Domiciliares e Condominiais", false},
                        {"Construção Civil", "CONAMA 307 - Resíduos da Construção Civil", true},
                        {"Hospital", "ANVISA RDC 222 - Resíduos de Serviços de Saúde (RSS)", true},
                        {"Indústria", "NBR 10004 - Resíduos Sólidos Industriais", true},
                        {"Restaurante", "Lei 12.305/2010 - Resíduos Orgânicos Comerciais", false},
                        {"Shopping", "Plano de Gerenciamento de Grandes Geradores", false},
                        {"Supermercado", "Logística Reversa e Orgânicos Comerciais", false},
                        {"Universidade", "Gerenciamento Ambiental de Campus Universitário", false}
                    };
                    Map<String, TipoEdificacao> mapTipos = new HashMap<>();
                    for (Object[] s : segmentos) {
                        TipoEdificacao te = tipoRepo.save(new TipoEdificacao((String) s[0], (String) s[1], (Boolean) s[2]));
                        mapTipos.put(te.getNomeSegmento(), te);
                    }

                    Object[][] servicosBase = {
                        {"Eletrônico", "Classe I - Perigoso (Logística Reversa)", "Reciclagem", true, "356.18"},
                        {"Hospitalar", "Classe I - Infectante (RDC 222)", "Incineração", false, "420.00"},
                        {"Industrial", "Classe II-A - Não Inerte Industrial", "Aterro Sanitário", false, "390.00"},
                        {"Metal", "Classe II-B - Inerte Reciclável", "Reciclagem", true, "361.75"},
                        {"Orgânico", "Classe II-A - Biodegradável", "Compostagem", false, "280.00"},
                        {"Papel", "Classe II-B - Inerte Celulósico", "Reciclagem", true, "362.19"},
                        {"Plástico", "Classe II-B - Polímero Reciclável", "Reciclagem", true, "371.04"},
                        {"Vidro", "Classe II-B - Inerte Vítreo", "Reciclagem", true, "361.06"}
                    };
                    Map<String, Servico> mapServicos = new HashMap<>();
                    for (Object[] sv : servicosBase) {
                        Servico serv = servicoRepo.save(new Servico(
                                (String) sv[0], (String) sv[1], (String) sv[2],
                                (Boolean) sv[3], new BigDecimal((String) sv[4])
                        ));
                        mapServicos.put(serv.getTipoResiduo(), serv);
                    }

                    String[][] cidadesBase = {
                        {"Salvador", "BA", "40010-000", "Av. Tancredo Neves"},
                        {"Fortaleza", "CE", "60010-000", "Av. Bezerra de Menezes"},
                        {"Brasília", "DF", "70040-010", "Setor Comercial Sul Quadra 4"},
                        {"Belo Horizonte", "MG", "30110-000", "Av. Afonso Pena"},
                        {"Recife", "PE", "50010-000", "Av. Conde da Boa Vista"},
                        {"Curitiba", "PR", "80010-000", "Av. Marechal Deodoro"},
                        {"Rio de Janeiro", "RJ", "20010-000", "Av. Presidente Vargas"},
                        {"Porto Alegre", "RS", "90010-000", "Av. Borges de Medeiros"},
                        {"Campinas", "SP", "13010-000", "Av. Francisco Glicério"},
                        {"São Paulo", "SP", "01310-100", "Av. Paulista"}
                    };
                    Map<String, Endereco> mapEnderecos = new HashMap<>();
                    int numLog = 100;
                    for (String[] cid : cidadesBase) {
                        for (TipoEdificacao te : mapTipos.values()) {
                            Endereco end = enderecoRepo.save(new Endereco(
                                    cid[3] + " - Polo " + te.getNomeSegmento(),
                                    String.valueOf(numLog += 15),
                                    cid[2], cid[0], cid[1], te
                            ));
                            mapEnderecos.put(cid[0] + "-" + te.getNomeSegmento(), end);
                        }
                    }

                    Map<Integer, Empresa> mapEmpresas = new HashMap<>();
                    try (InputStream in = getClass().getResourceAsStream("/arquivos/empresas.txt");
                         BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String linha;
                        while ((linha = br.readLine()) != null) {
                            if (linha.isBlank() || linha.startsWith("id_empresa")) continue;
                            String[] p = linha.split(";");
                            int idLegado = Integer.parseInt(p[0].trim());
                            Empresa e = new Empresa();
                            e.setNomeEmpresa(p[1].trim());
                            e.setCnpjOperadora(String.format("12.345.678/00%02d-%02d", idLegado, (idLegado * 7) % 99));
                            e.setCidadeSede(p[2].trim());
                            e.setEstadoSede(p[3].trim());
                            e.setLatitude(new BigDecimal(p[4].trim()));
                            e.setLongitude(new BigDecimal(p[5].trim()));
                            e.setAnoFundacao(Integer.parseInt(p[6].trim()));
                            e.setFuncionarios(Integer.parseInt(p[7].trim()));
                            e.setClientesAtivos(Integer.parseInt(p[8].trim()));
                            e.setCertificacaoIso14001("Sim".equalsIgnoreCase(p[9].trim()));
                            e = empresaRepo.save(e);
                            mapEmpresas.put(idLegado, e);
                        }
                    }

                    try (InputStream in = getClass().getResourceAsStream("/arquivos/indicadores_mensais.txt");
                         BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String linha;
                        List<IndicadorMensal> loteInd = new ArrayList<>();
                        while ((linha = br.readLine()) != null) {
                            if (linha.isBlank() || linha.startsWith("id_empresa")) continue;
                            String[] p = linha.split(";");
                            Empresa emp = mapEmpresas.get(Integer.parseInt(p[0].trim()));
                            IndicadorMensal ind = new IndicadorMensal();
                            ind.setEmpresa(emp);
                            ind.setMesReferencia(p[1].trim());
                            ind.setTotalResiduosTon(new BigDecimal(p[2].trim()));
                            ind.setTaxaReciclagemPercentual(new BigDecimal(p[3].trim()));
                            ind.setCustoTotalOperacao(new BigDecimal(p[4].trim()));
                            ind.setReceitaTotal(new BigDecimal(p[5].trim()));
                            ind.setEmissaoCo2Evitaton(new BigDecimal(p[6].trim()));
                            ind.setClientesAtendidos(Integer.parseInt(p[7].trim()));
                            loteInd.add(ind);
                        }
                        indicadorRepo.saveAll(loteInd);
                    }

                    Map<String, List<Cliente>> clientesPorCidade = new HashMap<>();
                    List<Cliente> todosClientes = new ArrayList<>();
                    try (InputStream in = getClass().getResourceAsStream("/arquivos/clientes.txt");
                         BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String linha;
                        while ((linha = br.readLine()) != null) {
                            if (linha.isBlank() || linha.startsWith("id_cliente")) continue;
                            String[] p = linha.split(";");
                            int numCli = Integer.parseInt(p[0].trim());
                            String segNome = p[1].trim();
                            String cidNome = p[2].trim();
                            String ufSigla = p[3].trim();

                            TipoEdificacao te = mapTipos.get(segNome);
                            Cliente cli = new Cliente();
                            cli.setRazaoSocial(String.format("%s %s #%04d Ltda", segNome, cidNome, numCli));
                            cli.setCnpj(String.format("%02d.%03d.%03d/0001-%02d", (numCli % 89) + 10, (numCli * 3) % 900 + 100, numCli, (numCli * 11) % 89 + 10));
                            cli.setCidade(cidNome);
                            cli.setEstadoUf(ufSigla);
                            cli.setVolumeContratadoMensalTon(new BigDecimal(p[4].trim()));
                            cli.setContratoAtivo("Sim".equalsIgnoreCase(p[5].trim()));
                            cli.setIndiceSatisfacao(new BigDecimal(p[6].trim()));
                            cli.setTempoContratoMeses(Integer.parseInt(p[7].trim()));
                            cli.setTipoEdificacao(te);
                            todosClientes.add(cli);
                        }
                        todosClientes = clienteRepo.saveAll(todosClientes);
                        for (Cliente c : todosClientes) {
                            clientesPorCidade.computeIfAbsent(c.getCidade(), k -> new ArrayList<>()).add(c);
                        }
                    }

                    try (InputStream in = getClass().getResourceAsStream("/arquivos/coletas_residuos.txt");
                         BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String linha;
                        List<Pedido> lotePedidos = new ArrayList<>(500);
                        List<ItemPedido> loteItens = new ArrayList<>(500);
                        int idx = 0;

                        while ((linha = br.readLine()) != null) {
                            if (linha.isBlank() || linha.startsWith("id_coleta")) continue;
                            String[] p = linha.split(";");

                            int idColeta = Integer.parseInt(p[0].trim());
                            int idEmp = Integer.parseInt(p[1].trim());
                            LocalDate dataColeta = LocalDate.parse(p[2].trim());
                            String nomeTipoRes = p[3].trim();
                            BigDecimal pesoTon = new BigDecimal(p[4].trim());
                            String destinoFinal = p[5].trim();
                            BigDecimal custoOp = new BigDecimal(p[6].trim());
                            BigDecimal receitaRec = new BigDecimal(p[7].trim());
                            StatusPedido status = StatusPedido.fromTextoLegado(p[8].trim());
                            String cidade = p[9].trim();

                            Empresa emp = mapEmpresas.get(idEmp);
                            Servico servico = mapServicos.get(nomeTipoRes);

                            List<Cliente> candsCidade = clientesPorCidade.getOrDefault(cidade, todosClientes);
                            Cliente clienteAtribuido = candsCidade.get(idx % candsCidade.size());
                            idx++;

                            Endereco endColeta = mapEnderecos.get(cidade + "-" + clienteAtribuido.getTipoEdificacao().getNomeSegmento());
                            if (endColeta == null) {
                                endColeta = mapEnderecos.values().iterator().next();
                            }

                            Pedido ped = new Pedido();
                            ped.setCodigoProtocolo(String.format("PED-2026-%05d", idColeta));
                            ped.setDataSolicitacao(dataColeta.minusDays(2));
                            ped.setDataColetaExecucao(dataColeta);
                            ped.setStatus(status);
                            ped.setCustoOperacionalTotal(custoOp);
                            ped.setReceitaReciclagemTotal(status == StatusPedido.CANCELADO ? BigDecimal.ZERO : receitaRec);
                            ped.setCliente(clienteAtribuido);
                            ped.setEmpresa(emp);

                            ItemPedido item = new ItemPedido();
                            item.setPedido(ped);
                            item.setServico(servico);
                            item.setEndereco(endColeta);
                            item.setPesoToneladas(pesoTon);
                            item.setDestinoFinal(destinoFinal);
                            item.setCustoItem(custoOp);
                            item.setReceitaReciclagemItem(ped.getReceitaReciclagemTotal());
                            item.setNumeroMtrCertificado(String.format("MTR-%s-%06d", endColeta.getEstadoUf(), idColeta));

                            lotePedidos.add(ped);
                            loteItens.add(item);

                            if (lotePedidos.size() >= 500) {
                                pedidoRepo.saveAll(lotePedidos);
                                itemRepo.saveAll(loteItens);
                                lotePedidos.clear();
                                loteItens.clear();
                            }
                        }
                        if (!lotePedidos.isEmpty()) {
                            pedidoRepo.saveAll(lotePedidos);
                            itemRepo.saveAll(loteItens);
                        }
                    }
                    System.out.println("Carga automática completa finalizada! 5.000 pedidos e itens persistidos.");
                } catch (Exception e) {
                    throw new RuntimeException("Falha na carga automática: " + e.getMessage(), e);
                }
            });
        };
    }
}
