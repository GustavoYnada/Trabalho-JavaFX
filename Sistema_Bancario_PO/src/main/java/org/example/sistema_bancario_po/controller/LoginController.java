package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.model.Conta;
import org.example.sistema_bancario_po.util.Seguranca;
import org.example.sistema_bancario_po.util.Sessao;
import org.example.sistema_bancario_po.util.Telas;
import org.example.sistema_bancario_po.util.Validador;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField tfCpf;
    @FXML private PasswordField pfSenha;

    private final ICliente clienteDAO = new ClienteDAO();
    private final IConta contaDAO = new ContaDAO();

    @FXML
    private void entrar() {
        String cpf = Validador.somenteDigitos(tfCpf.getText());
        String senha = pfSenha.getText();

        if (cpf.isEmpty() || senha.isEmpty()) {
            Telas.aviso("Informe o CPF e a senha.");
            return;
        }

        try {
            Cliente cliente = clienteDAO.buscarPorCpf(cpf);
            if (cliente == null || !cliente.getSenha().equals(Seguranca.hash(senha))) {
                Telas.erro("CPF ou senha inválidos.");
                return;
            }
            Conta conta = contaDAO.buscarPorClienteId(cliente.getId());
            if (conta == null) {
                Telas.erro("Este cliente não possui conta.");
                return;
            }
            Sessao.iniciar(cliente, conta);
            Telas.ir(tfCpf, "TelaMenu.fxml", "Meu Banco");
        } catch (DaoException e) {
            Telas.erro(e.getMessage());
        }
    }

    @FXML
    private void abrirCadastro() {
        Telas.ir(tfCpf, "TelaCadastro.fxml", "Criar conta");
    }
}