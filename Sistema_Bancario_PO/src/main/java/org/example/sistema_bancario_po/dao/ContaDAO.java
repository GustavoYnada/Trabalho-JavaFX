package org.example.sistema_bancario_po.dao;

import org.example.sistema_bancario_po.model.Conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContaDAO implements IConta {

    private Conta mapear(ResultSet rs) throws SQLException {
        return new Conta(rs.getInt("id"), rs.getString("numero"),
                rs.getBigDecimal("saldo"), rs.getInt("cliente_id"));
    }

    @Override
    public void inserir(Conta c) throws DaoException {
        String sql = "INSERT INTO conta (numero, saldo, cliente_id) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNumero());
            ps.setBigDecimal(2, c.getSaldo());
            ps.setInt(3, c.getClienteId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao criar conta: " + e.getMessage(), e);
        }
    }

    @Override
    public Conta buscarPorClienteId(int clienteId) throws DaoException {
        String sql = "SELECT id, numero, saldo, cliente_id FROM conta WHERE cliente_id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar conta: " + e.getMessage(), e);
        }
    }

    @Override
    public Conta buscarPorCpf(String cpf) throws DaoException {
        String sql = "SELECT c.id, c.numero, c.saldo, c.cliente_id FROM conta c "
                + "JOIN cliente cl ON cl.id = c.cliente_id WHERE cl.cpf = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar conta pelo CPF: " + e.getMessage(), e);
        }
    }

    @Override
    public void depositar(int contaId, BigDecimal valor) throws DaoException {
        String sql = "UPDATE conta SET saldo = saldo + ? WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, valor);
            ps.setInt(2, contaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao depositar: " + e.getMessage(), e);
        }
    }

    @Override
    public void sacar(int contaId, BigDecimal valor) throws DaoException {
        String sql = "UPDATE conta SET saldo = saldo - ? WHERE id = ? AND saldo >= ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, valor);
            ps.setInt(2, contaId);
            ps.setBigDecimal(3, valor);
            if (ps.executeUpdate() == 0) {
                throw new DaoException("Saldo insuficiente.");
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao sacar: " + e.getMessage(), e);
        }
    }

    @Override
    public void transferir(int origemId, int destinoId, BigDecimal valor) throws DaoException {
        String debitar = "UPDATE conta SET saldo = saldo - ? WHERE id = ? AND saldo >= ?";
        String creditar = "UPDATE conta SET saldo = saldo + ? WHERE id = ?";
        try (Connection con = Conexao.getConexao()) {
            con.setAutoCommit(false);   // inicia transação
            try (PreparedStatement psD = con.prepareStatement(debitar);
                 PreparedStatement psC = con.prepareStatement(creditar)) {
                psD.setBigDecimal(1, valor);
                psD.setInt(2, origemId);
                psD.setBigDecimal(3, valor);
                if (psD.executeUpdate() == 0) {
                    throw new DaoException("Saldo insuficiente.");
                }
                psC.setBigDecimal(1, valor);
                psC.setInt(2, destinoId);
                psC.executeUpdate();
                con.commit();
            } catch (SQLException | DaoException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro na transferência: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int contaId) throws DaoException {
        String sql = "DELETE FROM conta WHERE id = ?";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir conta: " + e.getMessage(), e);
        }
    }
}