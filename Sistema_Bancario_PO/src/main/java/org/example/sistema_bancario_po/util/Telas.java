package org.example.sistema_bancario_po.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Telas {

    public static void ir(Node origem, String fxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(Telas.class.getResource("/org/example/sistema_bancario_po/view/" + fxml));
            Stage stage = (Stage) origem.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            erro("Erro ao abrir a tela: " + e.getMessage());
        }
    }

    private static void mostrar(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void info(String msg) { mostrar(Alert.AlertType.INFORMATION, msg); }
    public static void aviso(String msg) { mostrar(Alert.AlertType.WARNING, msg); }
    public static void erro(String msg) { mostrar(Alert.AlertType.ERROR, msg); }

    public static boolean confirmar(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    public static BigDecimal converterValor(String texto) {
        try {
            BigDecimal v = new BigDecimal(texto.trim().replace(",", ".")).setScale(2, RoundingMode.HALF_UP);
            if (v.signum() <= 0) {
                aviso("O valor deve ser maior que zero.");
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            aviso("Valor inválido. Digite apenas números (ex.: 150,00).");
            return null;
        }
    }

    public static Optional<BigDecimal> pedirValor(String titulo, String mensagem) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle(titulo);
        d.setHeaderText(mensagem);
        d.setContentText("R$");
        return d.showAndWait().map(Telas::converterValor);
    }
}