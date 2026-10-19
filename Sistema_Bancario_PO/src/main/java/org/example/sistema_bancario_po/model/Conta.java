package org.example.sistema_bancario_po.model;
import java.math.BigDecimal;

public class Conta {
    private int id;
    private String numero;
    private BigDecimal saldo;
    private int clienteId;

    public Conta(String numero, BigDecimal saldo, int clienteId) {
        this.numero = numero;
        this.saldo = saldo;
        this.clienteId = clienteId;
    }

    public Conta(int id, String numero, BigDecimal saldo, int clienteId) {
        this(numero, saldo, clienteId);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
}