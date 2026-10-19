package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.util.Seguranca;
import org.example.sistema_bancario_po.util.Sessao;
import org.example.sistema_bancario_po.util.Telas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;
import java.util.Locale;

public class MenusController {

    @FXML private Label lblNome;
    @FXML private Label lblSaldo;
    @FXML private Label lblConta;
    @FXML private Button btnOcultar;

    private final ICliente clienteDAO = new ClienteDAO();
    private final IConta contaDAO = new ContaDAO();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private boolean saldoOculto = false;

    @FXML
    private void initialize() {
        lblNome.setText("Olá, " + Sessao.getCliente().getNome());
        lblConta.setText("Conta " + Sessao.getConta().getNumero());
        atualizarSaldo();
    }

    private void atualizarSaldo() {
        lblSaldo.setText(saldoOculto ? "R$ ••••••" : moeda.format(Sessao.getConta().getSaldo()));
        btnOcultar.setText(saldoOculto ? "Mostrar" : "Ocultar");
    }

    private void recarregarConta() throws DaoException {
        Sessao.setConta(contaDAO.buscarPorClienteId(Sessao.getCliente().getId()));
        atualizarSaldo();
    }

    @FXML
    private void alternarSaldo() {
        saldoOculto = !saldoOculto;
        atualizarSaldo();
    }

    @FXML
    private void depositar() {
        Telas.pedirValor("Depositar", "Quanto você quer depositar?").ifPresent(valor -> {
            try {
                contaDAO.depositar(Sessao.getConta().getId(), valor);
                recarregarConta();
                Telas.info("Depósito de " + moeda.format(valor) + " realizado!");
            } catch (DaoException e) {
                Telas.erro(e.getMessage());
            }
        });
    }

    @FXML
    private void sacar() {
        Telas.pedirValor("Sacar", "Quanto você quer sacar?").ifPresent(valor -> {
            try {
                contaDAO.sacar(Sessao.getConta().getId(), valor);
                recarregarConta();
                Telas.info("Saque de " + moeda.format(valor) + " realizado!");
            } catch (DaoException e) {
                Telas.erro(e.getMessage());
            }
        });
    }

    @FXML
    private void abrirPix() {
        Telas.ir(lblNome, "TelaPix.fxml", "Transferir");
    }

    @FXML
    private void editarPerfil() {
        Cliente atual = Sessao.getCliente();

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Editar perfil");
        dialogo.setHeaderText("Altere seus dados");

        TextField tfNome = new TextField(atual.getNome());
        PasswordField pfNova = new PasswordField();
        pfNova.setPromptText("Deixe em branco para manter");

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(10);
        grade.addRow(0, new Label("Nome:"), tfNome);
        grade.addRow(1, new Label("Nova senha:"), pfNova);

        dialogo.getDialogPane().setContent(grade);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialogo.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            String nome = tfNome.getText().trim();
            String nova = pfNova.getText();

            if (nome.isEmpty()) {
                Telas.aviso("O nome não pode ficar vazio.");
                return;
            }
            if (!nova.isEmpty() && nova.length() < 4) {
                Telas.aviso("A nova senha deve ter pelo menos 4 caracteres.");
                return;
            }

            String senhaFinal = nova.isEmpty() ? atual.getSenha() : Seguranca.hash(nova);
            Cliente atualizado = new Cliente(atual.getId(), nome, atual.getCpf(), senhaFinal);
            try {
                clienteDAO.atualizar(atualizado);
                Sessao.setCliente(atualizado);
                lblNome.setText("Olá, " + nome);
                Telas.info("Perfil atualizado!");
            } catch (DaoException e) {
                Telas.erro(e.getMessage());
            }
        });
    }

    @FXML
    private void excluirConta() {
        String aviso = "Tem certeza que deseja excluir sua conta?\nEssa ação não pode ser desfeita.";
        if (Sessao.getConta().getSaldo().signum() > 0) {
            aviso += "\n\nVocê ainda tem " + moeda.format(Sessao.getConta().getSaldo()) + " na conta.";
        }
        if (!Telas.confirmar(aviso)) {
            return;
        }
        try {
            contaDAO.excluir(Sessao.getConta().getId());
            clienteDAO.excluir(Sessao.getCliente().getId());
            Sessao.encerrar();
            Telas.info("Conta excluída.");
            Telas.ir(lblNome, "TelaLogin.fxml", "Meu Banco - Login");
        } catch (DaoException e) {
            Telas.erro(e.getMessage());
        }
    }

    @FXML
    private void sair() {
        Sessao.encerrar();
        Telas.ir(lblNome, "TelaLogin.fxml", "Meu Banco - Login");
    }
}