package org.example.sistema_bancario_po.dao;

import org.example.sistema_bancario_po.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO implements ICliente {

    @Override
    public void inserir(Cliente c) throws DaoException {
        String sql = "INSERT INTO cliente (nome, cpf, senha) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getSenha());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DaoException("Já existe um cliente cadastrado com esse CPF.", e);
            }
            throw new DaoException("Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorCpf(String cpf) throws DaoException {
        String sql = "SELECT id, nome, cpf, senha FROM cliente WHERE cpf = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), rs.getString("nome"),
                            rs.getString("cpf"), rs.getString("senha"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cliente c) throws DaoException {
        String sql = "UPDATE cliente SET nome = ?, senha = ? WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getSenha());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) throws DaoException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }
}