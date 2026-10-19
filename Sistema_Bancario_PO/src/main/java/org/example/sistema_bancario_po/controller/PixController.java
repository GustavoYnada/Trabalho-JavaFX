package org.example.sistema_bancario_po.controller;

import org.example.sistema_bancario_po.dao.ClienteDAO;
import org.example.sistema_bancario_po.dao.ContaDAO;
import org.example.sistema_bancario_po.dao.DaoException;
import org.example.sistema_bancario_po.dao.ICliente;
import org.example.sistema_bancario_po.dao.IConta;
import org.example.sistema_bancario_po.model.Cliente;
import org.example.sistema_bancario_po.model.Conta;
import org.example.sistema_bancario_po.util.Sessao;
import org.example.sistema_bancario_po.util.Telas;
import org.example.sistema_bancario_po.util.Validador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PixController {

    @FXML private Label lblSaldo;
    @FXML private TextField tfCpfDestino;
    @FXML private TextField tfValor;

    private final ICliente clienteDAO = new ClienteDAO();
    private final IConta contaDAO = new ContaDAO();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML
    private void initialize() {
        lblSaldo.setText("Saldo disponível: " + moeda.format(Sessao.getConta().getSaldo()));
    }

    @FXML
    private void transferir() {
        String cpf = Validador.somenteDigitos(tfCpfDestino.getText());

        if (!Validador.cpfValido(cpf)) {
            Telas.aviso("CPF do destinatário inválido.");
            return;
        }
        if (cpf.equals(Sessao.getCliente().getCpf())) {
            Telas.aviso("Você não pode transferir para a sua própria conta.");
            return;
        }
        BigDecimal valor = Telas.converterValor(tfValor.getText());
        if (valor == null) {
            return;
        }

        try {
            Conta destino = contaDAO.buscarPorCpf(cpf);
            Cliente donoDestino = clienteDAO.buscarPorCpf(cpf);
            if (destino == null || donoDestino == null) {
                Telas.aviso("Nenhuma conta encontrada para esse CPF.");
                return;
            }
            if (!Telas.confirmar("Transferir " + moeda.format(valor) + " para " + donoDestino.getNome() + "?")) {
                return;
            }

            contaDAO.transferir(Sessao.getConta().getId(), destino.getId(), valor);
            Sessao.setConta(contaDAO.buscarPorClienteId(Sessao.getCliente().getId()));

            Telas.info("Transferência realizada com sucesso!");
            voltar();
        } catch (DaoException e) {
            Telas.erro(e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        Telas.ir(lblSaldo, "TelaMenu.fxml", "Meu Banco");
    }
}