package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.text.Normalizer;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import static com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FinanceiroRepository.*;
@Service
public class FinanceiroService {
    private final FinanceiroRepository banco;
    private final FinanceiroUsuarioRepository usuarios;
    private final FamiliaRepository familias;
    public FinanceiroService(FinanceiroRepository banco,
                             FinanceiroUsuarioRepository usuarios,FamiliaRepository familias) {
        this.banco=banco;this.usuarios=usuarios;this.familias=familias;
    }
    private ResponseStatusException erro(HttpStatus status,String texto) {return new ResponseStatusException(status,texto);}
    private String texto(Map<String,Object> row,String key) {Object v=row.get(key);return v==null?"":v.toString();}
    private String papel(Usuario usuario) {
        return usuario.getFuncaoSistema()==null?"":normalizar(usuario.getFuncaoSistema().getNome());
    }
    private String normalizar(String value) {return Normalizer.normalize(value==null?"":value,Normalizer.Form.NFD)
            .replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);}
    private boolean titular(Usuario u) {String n=papel(u);return u.getFamilia()!=null && (n.contains("responsavel")||n.contains("titular"));}
        private boolean dependente(Usuario u) {String n=papel(u);return u.getFamilia()!=null && n.contains("associad") && !titular(u);}
        private String grupo(Usuario usuario,Set<Integer> responsaveis) {
            if (papel(usuario).contains("diretoria")) return "Diretoria";
            if (responsaveis.contains(usuario.getId())) return "Departamentos";
            if (titular(usuario)) return "Associados responsáveis";
            if (dependente(usuario)) return "Associados";
            return "Outros";
        }
        @Transactional(readOnly=true)
        public Map<String,Object> pesquisarUsuarios(String busca,int pagina,int tamanho) {
            if(pagina<0||tamanho<1||tamanho>100) throw erro(HttpStatus.BAD_REQUEST,"Paginação inválida");
            var encontrados=usuarios.pesquisar((busca==null?"":busca.trim()),PageRequest.of(pagina,tamanho));
            Set<Integer> responsaveis=banco.responsaveisDepartamento();
            return Map.of("usuarios",encontrados.getContent().stream().map(u->new DestinatarioResponse(u.getId(),u.getNome(),u.getEmail(),grupo(u,responsaveis))).toList(), "total",encontrados.getTotalElements(),"pagina",pagina,"paginas",encontrados.getTotalPages());
        }
        private Set<Integer> selecionar(CobrancaCreateRequest request) {
            List<Usuario> ativos=usuarios.findByAtivoTrue();
            Map<Integer,Usuario> porId=ativos.stream().collect(Collectors.toMap(Usuario::getId,u->u));
            Set<Integer> responsaveis=banco.responsaveisDepartamento();
            Set<Integer> ids=new LinkedHashSet<>();
            String modo=request.forma_selecao()==null?"":request.forma_selecao().trim();
            if (modo.equals("grupos")) {
                List<String> grupos=request.grupos_destinatarios();
                if(grupos==null||grupos.isEmpty()) throw erro(HttpStatus.BAD_REQUEST,"Selecione um grupo");
                Set<String> permitidos=Set.of("diretoria","departamentos","associados_responsaveis","associados");
                for(String g:grupos) if(!permitidos.contains(g)) throw erro(HttpStatus.BAD_REQUEST,"Grupo inválido: "+g);
                for(Usuario u:ativos) {
                    String papel=papel(u);
                    if (grupos.contains("diretoria") && papel.contains("diretoria")) ids.add(u.getId());
                    if (grupos.contains("departamentos") && responsaveis.contains(u.getId())) ids.add(u.getId());
                    if (grupos.contains("associados_responsaveis") && titular(u)) ids.add(u.getId());
                    if (grupos.contains("associados") && dependente(u)) ids.add(u.getId());
                }
            } else if (modo.equals("usuarios")) {
                List<Integer> selecionados=request.usuarios_especificos();
                if(selecionados==null||selecionados.isEmpty()||selecionados.size()>1000)
                    throw erro(HttpStatus.BAD_REQUEST,"Selecione de 1 a 1000 usuários");
                for(Integer id:selecionados) {
                    if(id==null||!porId.containsKey(id)) throw erro(HttpStatus.BAD_REQUEST,"Usuário inexistente ou inativo: "+id);
                    ids.add(id);
                }
            } else throw erro(HttpStatus.BAD_REQUEST,"Escolha grupos ou usuários específicos");
            if(ids.isEmpty()) throw erro(HttpStatus.BAD_REQUEST,"Nenhum usuário elegível foi encontrado. Confira os perfis cadastrados");
            String periodo=normalizar(request.periodo());
            if(periodo.equals("mensal") && ids.stream().anyMatch(id->!responsaveis.contains(id)))
                throw erro(HttpStatus.BAD_REQUEST,"Cobrança mensal deve ser dirigida a responsáveis por departamentos");
            if(periodo.equals("anual") && ids.stream().anyMatch(id->!titular(porId.get(id))))
                throw erro(HttpStatus.BAD_REQUEST,"Cobrança anual deve ser dirigida a associados responsáveis");
            return ids;
        }
        private String periodo(String periodo) {
            return switch(normalizar(periodo)) {
                case "mensal" -> "Mensal";case "anual" -> "Anual";case "avulsa" -> "Avulsa";
                default -> throw erro(HttpStatus.BAD_REQUEST,"Periodicidade inválida");
            };
        }
        @Transactional
        public CobrancaResponse emitir(CobrancaCreateRequest request) {
            String per=periodo(request.periodo());
            if(request.nome_da_cobranca()==null||request.nome_da_cobranca().trim().isEmpty()||request.nome_da_cobranca().length()>500)
                throw erro(HttpStatus.BAD_REQUEST,"Nome da cobrança inválido");
            if(request.valor()==null||request.valor().signum()<=0||request.valor().scale()>2)
                throw erro(HttpStatus.BAD_REQUEST,"Informe um valor monetário válido");
            if(request.vencimento()==null) throw erro(HttpStatus.BAD_REQUEST,"Informe o vencimento");
            Set<Integer> ids=selecionar(request);
// Serializa a verificação de recorrência e a emissão sem criar índices ou tabelas.
            banco.bloquearEmissao();
            List<Map<String,Object>> cobrancas=banco.cobrancas();
            List<Map<String,Object>> pagamentos=banco.pagamentos();
            if (!per.equals("Avulsa")) {
                Map<Integer,Integer> familiasSelecionadas=new HashMap<>();
                for(Usuario u:usuarios.findByAtivoTrue())
                    if(ids.contains(u.getId()) && u.getFamilia()!=null)
                        familiasSelecionadas.put(u.getId(),u.getFamilia().getId());
                for(var p:pagamentos) {
                    var antiga=cobrancas.stream().filter(c->numero(c,"id_cobranca")==numero(p,"id_cobranca"))
                            .findFirst().orElse(null);
                    if(antiga==null||!per.equalsIgnoreCase(texto(antiga,"periodo"))) continue;
                    boolean mesmoUsuario=ids.contains(numero(p,"fk_usuario"));
                    boolean mesmaFamilia=per.equals("Anual") && numeroOpcional(p,"fk_familia")!=null &&
                            familiasSelecionadas.containsValue(numeroOpcional(p,"fk_familia"));
                    if(!mesmoUsuario && !mesmaFamilia) continue;
                    LocalDate v=data(antiga,"vencimento");
                    boolean duplicada=per.equals("Mensal") ?
                            YearMonth.from(v).equals(YearMonth.from(request.vencimento())) &&
                                    texto(antiga,"nome_da_cobranca").trim().equalsIgnoreCase(request.nome_da_cobranca().trim())
                            : v.getYear()==request.vencimento().getYear();
                    if(duplicada) throw erro(HttpStatus.CONFLICT,"Já existe uma cobrança "+per.toLowerCase(Locale.ROOT)+
                            " desse período para um dos destinatários selecionados");
                }
            }
            int id=banco.criarCobranca(request.nome_da_cobranca().trim(),per,request.valor(),request.vencimento());
            Map<Integer,Usuario> mapa=usuarios.findByAtivoTrue().stream().collect(Collectors.toMap(Usuario::getId,u->u));
            Set<Integer> familiasNoPeriodo=new HashSet<>();
            int enviados=0;
            for(int usuarioId:ids) {
                Usuario u=mapa.get(usuarioId);
                Integer familia=u.getFamilia()==null?null:u.getFamilia().getId();
// Anuidade é uma cobrança por família, mesmo quando há vários titulares selecionados.
                if(per.equals("Anual") && familia!=null && !familiasNoPeriodo.add(familia)) continue;
                banco.criarPagamento(usuarioId,familia,id);enviados++;
            }
            return new CobrancaResponse(id,request.nome_da_cobranca().trim(),per,request.valor(),
                    request.vencimento(),enviados,0);
        }
        private PagamentoCobrancaResponse respostaPagamento(Map<String,Object> pagamento,
                Map<Integer,Map<String,Object>> cobrancas,Map<Integer,String> nomes) {
            int idC=numero(pagamento,"id_cobranca");
            Map<String,Object> cobranca=cobrancas.get(idC);
            if(cobranca==null) throw erro(HttpStatus.CONFLICT,"Pagamento sem cobrança correspondente: "+idC);
            int idUsuario=numero(pagamento,"fk_usuario");
            return new PagamentoCobrancaResponse(numero(pagamento,"id_pagamento_cobranca"),idC,
                    idUsuario,numeroOpcional(pagamento,"fk_familia"),nomes.getOrDefault(idUsuario,"Usuário #"+idUsuario),
                    texto(cobranca,"nome_da_cobranca"),texto(cobranca,"periodo"),
                    dinheiro(cobranca,"valor"),data(cobranca,"vencimento"),
                    texto(pagamento,"status"),texto(pagamento,"forma_pagamento"));
        }
        private List<PagamentoCobrancaResponse> pagamentos() {
            Map<Integer,Map<String,Object>> cobrancas=banco.cobrancas().stream()
                    .collect(Collectors.toMap(c->numero(c,"id_cobranca"),c->c));
            Map<Integer,String> nomes=usuarios.findAll().stream()
                    .collect(Collectors.toMap(Usuario::getId,Usuario::getNome));
            return banco.pagamentos().stream().map(p->respostaPagamento(p,cobrancas,nomes))
                    .sorted(Comparator.comparing(PagamentoCobrancaResponse::vencimento).reversed()).toList();
        }
        @Transactional(readOnly=true)
        public List<PagamentoCobrancaResponse> todosPagamentos(){return pagamentos();}
        @Transactional(readOnly=true)
        public List<PagamentoCobrancaResponse> meusPagamentos(int usuarioId){return pagamentos().stream()
                .filter(p->p.fk_usuario()==usuarioId).toList();}
        @Transactional(readOnly=true)
        public List<CobrancaResponse> listarCobrancas() {
            var pagamentos=pagamentos();
            return banco.cobrancas().stream().map(c -> {
                int id=numero(c,"id_cobranca");
                long quantidade=pagamentos.stream().filter(p->p.id_cobranca()==id).count();
                long recebidos=pagamentos.stream().filter(p->p.id_cobranca()==id && pago(p.status())).count();
                return new CobrancaResponse(id,texto(c,"nome_da_cobranca"),texto(c,"periodo"),
                        dinheiro(c,"valor"),data(c,"vencimento"),quantidade,recebidos);
            }).sorted(Comparator.comparing(CobrancaResponse::vencimento).reversed()).toList();
        }
        private boolean pago(String status) {return status!=null && Set.of("pago","confirmado","recebido")
                .contains(normalizar(status));}
        @Transactional
        public PagamentoCobrancaResponse informar(int id,int usuarioId,PagamentoInformarRequest request) {
            if(request.forma_pagamento()==null||request.forma_pagamento().isBlank()||request.forma_pagamento().length()>45)
                throw erro(HttpStatus.BAD_REQUEST,"Informe a forma de pagamento");
            var p=banco.pagamento(id);
            if(numero(p,"fk_usuario")!=usuarioId) throw erro(HttpStatus.FORBIDDEN,"Essa cobrança não pertence ao usuário");
            String status=texto(p,"status");
            if(!status.equalsIgnoreCase("Pendente")&&!status.equalsIgnoreCase("Recusado"))
                throw erro(HttpStatus.CONFLICT,"Pagamento não pode ser informado neste status");
            if(banco.informar(id,request.forma_pagamento().trim(),status)!=1)
                throw erro(HttpStatus.CONFLICT,"Pagamento foi alterado. Atualize a tela");
            return meusPagamentos(usuarioId).stream().filter(x->x.id_pagamento_cobranca()==id).findFirst().orElseThrow();
        }
        @Transactional
        public PagamentoCobrancaResponse revisar(int id,boolean confirmar) {
            var p=banco.pagamento(id);
            String status=texto(p,"status");
            if(!status.equalsIgnoreCase("Em verificação")) throw erro(HttpStatus.CONFLICT,"Pagamento não está em verificação");
            if(banco.revisar(id,confirmar?"Pago":"Recusado",status)!=1)
                throw erro(HttpStatus.CONFLICT,"Pagamento foi alterado. Atualize a tela");
            return pagamentos().stream().filter(x->x.id_pagamento_cobranca()==id).findFirst().orElseThrow();
        }
        private String tipo(String valor) {
            return switch (normalizar(valor)) {
                case "entrada", "receita" -> "ENTRADA";
                case "saida", "despesa" -> "SAIDA";
                default -> throw erro(
                        HttpStatus.BAD_REQUEST,
                        "Tipo de lançamento inválido: " + valor
                );
            };
        }
        private String status(String valor) {
            return switch(normalizar(valor)) {
                case "pago","recebido","confirmado"->"Pago";
                case "pendente"->"Pendente";
                default -> throw erro(HttpStatus.BAD_REQUEST,"Status inválido");
            };
        }
        private void validar(LancamentoCreateRequest req) {
            if(req.dt_lancamento()==null||req.valor()==null||req.valor().signum()<=0||req.valor().scale()>2)
                throw erro(HttpStatus.BAD_REQUEST,"Data ou valor inválido");
            if(req.descricao()==null||req.descricao().isBlank()||req.descricao().length()>200||
                    req.categoria()==null||req.categoria().isBlank()||req.categoria().length()>100)
                throw erro(HttpStatus.BAD_REQUEST,"Descrição ou categoria inválida");
            tipo(req.tipo_lancamento());status(req.status());
            if(req.fk_familia()!=null && !familias.existsById(req.fk_familia()))
                throw erro(HttpStatus.NOT_FOUND,"Família não encontrada");
        }
        @Transactional
        public LancamentoResponse criarLancamento(LancamentoCreateRequest req,int usuarioId) {
            validar(req);
            int id=banco.criarLancamento(req.dt_lancamento(),tipo(req.tipo_lancamento()),req.descricao().trim(),
                    req.categoria().trim(),req.valor(),status(req.status()),usuarioId,req.fk_familia());
            return converterLancamento(banco.lancamento(id));
        }
        @Transactional
        public LancamentoResponse editarLancamento(int id,LancamentoCreateRequest req) {
            banco.lancamento(id);validar(req);
            banco.atualizarLancamento(id,req.dt_lancamento(),tipo(req.tipo_lancamento()),req.descricao().trim(),
                    req.categoria().trim(),req.valor(),status(req.status()),req.fk_familia());
            return converterLancamento(banco.lancamento(id));
        }
        @Transactional
        public LancamentoResponse confirmarLancamento(int id) {
            var atual=banco.lancamento(id);
            if(pago(texto(atual,"status"))) throw erro(HttpStatus.CONFLICT,"Lançamento já confirmado");
            banco.atualizarStatusLancamento(id,"Pago");return converterLancamento(banco.lancamento(id));
        }
        @Transactional
        public void excluirLancamento(int id) {banco.lancamento(id);banco.excluirLancamento(id);}
        private LancamentoResponse converterLancamento(Map<String,Object> l) {
            int id=numero(l,"id_lancamento");
            return new LancamentoResponse("LAN-"+id,data(l,"dt_lancamento"),
                    texto(l,"descricao"),texto(l,"categoria"),tipo(texto(l,"tipo_lancamento")),
                    dinheiro(l,"valor"),texto(l,"status"),false,numeroOpcional(l,"fk_usuario"),
                    numeroOpcional(l,"fk_familia"));
        }
        @Transactional(readOnly=true)
        public DashboardFinanceiroConsolidadoResponse dashboard(int ano,int mes) {
            if(ano<2000||ano>2100||mes<1||mes>12)throw erro(HttpStatus.BAD_REQUEST,"Período inválido");
            List<LancamentoResponse> itens=new ArrayList<>();
            for(var l:banco.lancamentos()) itens.add(converterLancamento(l));
// DER financeiro vigente não contém data de recebimento em pagamentoCobranca.
// A competência do recebimento confirmado é a data de vencimento da cobrança.
            for(var p:pagamentos()) {
                itens.add(new LancamentoResponse("PCO-"+p.id_pagamento_cobranca(),p.vencimento(),
                        p.nome_da_cobranca()+" · "+p.destinatario(),
                        p.periodo().equalsIgnoreCase("Mensal") ? "Mensalidades" :
                                p.periodo().equalsIgnoreCase("Anual") ? "Anuidades" : "Cobranças avulsas", "ENTRADA",
                        p.valor(),pago(p.status())?"Pago":p.status(),true,p.fk_usuario(),p.fk_familia()));
            }
            itens.sort(Comparator.comparing(LancamentoResponse::dt_lancamento).reversed());
            YearMonth atual=YearMonth.of(ano,mes);
            BigDecimal saldoMes=BigDecimal.ZERO, saldoAnterior=BigDecimal.ZERO,
                    entradas=BigDecimal.ZERO, saidas=BigDecimal.ZERO;
            for(var item:itens) {
                if(!pago(item.status()))continue;
                BigDecimal valor=item.tipo_lancamento().equals("ENTRADA")?item.valor():item.valor().negate();
                YearMonth referencia=YearMonth.from(item.dt_lancamento());
                if(referencia.equals(atual))saldoMes=saldoMes.add(valor);
                if(referencia.equals(atual.minusMonths(1)))saldoAnterior=saldoAnterior.add(valor);
                if(item.dt_lancamento().getYear()==ano) {
                    if(item.tipo_lancamento().equals("ENTRADA"))entradas=entradas.add(item.valor());
                    else saidas=saidas.add(item.valor());
                }
            }
            BigDecimal variacao=saldoAnterior.signum()==0?null:saldoMes.subtract(saldoAnterior)
                    .multiply(BigDecimal.valueOf(100)).divide(saldoAnterior.abs(),2,RoundingMode.HALF_UP);
            return new DashboardFinanceiroConsolidadoResponse(new ResumoFinanceiroConsolidadoResponse(
                    saldoMes,entradas.subtract(saidas),entradas,saidas,variacao),itens);
        }
    }