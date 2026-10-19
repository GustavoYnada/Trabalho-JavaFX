package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.model.Conta;
import org.example.sistema_bancario_po.util.Seguranca;
import org.example.sistema_bancario_po.util.Telas;
import org.example.sistema_bancario_po.util.Validador;
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
        String cpf = Validador.somenteDigitos(tfCpf.getText());
        String senha = pfSenha.getText();

        if (nome.isEmpty()) {
            Telas.aviso("Informe o nome.");
            return;
        }
        if (!Validador.cpfValido(cpf)) {
            Telas.aviso("CPF inválido.");
            return;
        }
        if (senha.length() < 4) {
            Telas.aviso("A senha deve ter pelo menos 4 caracteres.");
            return;
        }
        if (!senha.equals(pfConfirmar.getText())) {
            Telas.aviso("As senhas não conferem.");
            return;
        }

        Cliente cliente = new Cliente(nome, cpf, Seguranca.hash(senha));
        try {
            clienteDAO.inserir(cliente);
            try {
                String numero = String.format("%06d", cliente.getId());
                contaDAO.inserir(new Conta(numero, BigDecimal.ZERO, cliente.getId()));
                Telas.info("Conta criada com sucesso!\nNúmero da conta: " + numero);
            } catch (DaoException e) {
                clienteDAO.excluir(cliente.getId());
                throw e;
            }
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