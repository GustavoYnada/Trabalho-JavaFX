package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.model.Conta;
import org.example.sistema_bancario_po.util.Telas;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class CadastroController {

    @FXML private TextField tfNome;
    @FXML private TextField tfCpf;
    @FXML private PasswordField pfSenha;
    @FXML private PasswordField pfConfirmar;

    private final ICliente clienteDAO = new ClienteDAO();
    private final IConta contaDAO = new ContaDAO();

    @FXML
    private void cadastrar() {
        String nome = tfNome.getText().trim();
        String cpf = tfCpf.getText().trim();
        String senha = pfSenha.getText();

        if (nome.isEmpty()) {
            Telas.aviso("Informe o nome.");
            return;
        }
        if (cpf.length() != 11) {
            Telas.aviso("O CPF deve ter 11 números.");
            return;
        }
        if (senha.isEmpty()) {
            Telas.aviso("Informe uma senha.");
            return;
        }
        if (!senha.equals(pfConfirmar.getText())) {
            Telas.aviso("As senhas não conferem.");
            return;
        }

        try {
            Cliente cliente = new Cliente(nome, cpf, senha);
            clienteDAO.inserir(cliente);

            String numero = String.format("%06d", cliente.getId());
            contaDAO.inserir(new Conta(numero, BigDecimal.ZERO, cliente.getId()));

            Telas.info("Conta criada com sucesso!\nNúmero da conta: " + numero);
            voltar();
        } catch (DaoException e) {
            Telas.erro(e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        Telas.ir(tfNome, "TelaLogin.fxml", "Meu Banco - Login");
    }
}