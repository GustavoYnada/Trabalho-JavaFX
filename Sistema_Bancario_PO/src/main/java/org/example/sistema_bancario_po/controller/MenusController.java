package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.util.Sessao;
import org.example.sistema_bancario_po.util.Telas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class MenusController {

    @FXML private Label lblNome;
    @FXML private Label lblSaldo;
    @FXML private Label lblConta;
    @FXML private Button btnOcultar;

    private final ICliente clienteDAO = new ClienteDAO();
    private final IConta contaDAO = new ContaDAO();
    private boolean saldoOculto = false;

    @FXML
    private void initialize() {
        lblNome.setText("Olá, " + Sessao.getCliente().getNome());
        lblConta.setText("Conta " + Sessao.getConta().getNumero());
        atualizarSaldo();
    }

    private void atualizarSaldo() {
        if (saldoOculto) {
            lblSaldo.setText("R$ ••••••");
            btnOcultar.setText("Mostrar");
        } else {
            lblSaldo.setText(String.format("R$ %.2f", Sessao.getConta().getSaldo()));
            btnOcultar.setText("Ocultar");
        }
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
                Telas.info("Depósito de R$ " + valor + " realizado!");
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
                Telas.info("Saque de R$ " + valor + " realizado!");
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

        TextInputDialog dialogo = new TextInputDialog(atual.getNome());
        dialogo.setTitle("Editar perfil");
        dialogo.setHeaderText("Digite o novo nome:");

        dialogo.showAndWait().ifPresent(nome -> {
            nome = nome.trim();
            if (nome.isEmpty()) {
                Telas.aviso("O nome não pode ficar vazio.");
                return;
            }
            Cliente atualizado = new Cliente(atual.getId(), nome, atual.getCpf(), atual.getSenha());
            try {
                clienteDAO.atualizar(atualizado);
                Sessao.setCliente(atualizado);
                lblNome.setText("Olá, " + nome);
                Telas.info("Nome atualizado!");
            } catch (DaoException e) {
                Telas.erro(e.getMessage());
            }
        });
    }

    @FXML
    private void excluirConta() {
        if (!Telas.confirmar("Tem certeza que deseja excluir sua conta?\nEssa ação não pode ser desfeita.")) {
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