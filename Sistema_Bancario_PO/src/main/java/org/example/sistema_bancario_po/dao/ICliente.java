package org.example.sistema_bancario_po.dao;

import org.example.sistema_bancario_po.model.Cliente;

public interface ICliente {
    void inserir(Cliente cliente) throws DaoException;          // Create
    Cliente buscarPorCpf(String cpf) throws DaoException;       // Read
    void atualizar(Cliente cliente) throws DaoException;        // Update
    void excluir(int id) throws DaoException;                   // Delete
}