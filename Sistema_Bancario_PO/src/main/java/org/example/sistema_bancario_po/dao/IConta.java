package org.example.sistema_bancario_po.dao;

import org.example.sistema_bancario_po.model.Conta;
import java.math.BigDecimal;

public interface IConta {
    void inserir(Conta conta) throws DaoException;                       // Create
    Conta buscarPorClienteId(int clienteId) throws DaoException;         // Read
    Conta buscarPorCpf(String cpf) throws DaoException;                  // Read (usado no Pix)
    void depositar(int contaId, BigDecimal valor) throws DaoException;   // Update (+)
    void sacar(int contaId, BigDecimal valor) throws DaoException;       // Update (-)
    void transferir(int origemId, int destinoId, BigDecimal valor) throws DaoException; // Update (2 contas)
    void excluir(int contaId) throws DaoException;                       // Delete
}