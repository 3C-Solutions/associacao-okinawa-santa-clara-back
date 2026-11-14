package com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
@Entity
@Table(name = "cobranca")
public class Cobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cobranca")
    private Integer idCobranca;
    @Column(name = "nome_da_cobranca", length = 500)
    private String nomeDaCobranca;
    @Column(name = "periodo", length = 45)
    private String periodo;
    // O DER define DOUBLE; esta Entity respeita o tipo fisico. 25
// No Service os valores de dinheiro continuam em BigDecimal.
    @Column(name = "valor")
    private Double valor;
    @Column(name = "vencimento")
    private LocalDate vencimento;
    public Integer getIdCobranca() { return idCobranca; }
    public void setIdCobranca(Integer idCobranca) { this.idCobranca = idCobranca; }
    public String getNomeDaCobranca() { return nomeDaCobranca; }
    public void setNomeDaCobranca(String nomeDaCobranca) { this.nomeDaCobranca = nomeDaCobranca; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public LocalDate getVencimento() { return vencimento; }
    public void setVencimento(LocalDate vencimento) { this.vencimento = vencimento; }
}