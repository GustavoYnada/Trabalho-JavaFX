package org.example.sistema_bancario_po.util;

import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.model.Conta;

public class Sessao {
    private static Cliente cliente;
    private static Conta conta;

    public static void iniciar(Cliente c, Conta ct) { cliente = c; conta = ct; }
    public static void encerrar() { cliente = null; conta = null; }

    public static Cliente getCliente() { return cliente; }
    public static Conta getConta() { return conta; }
    public static void setCliente(Cliente c) { cliente = c; }
    public static void setConta(Conta ct) { conta = ct; }
}