package org.example.sistema_bancario_po;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/sistema_bancario_po/view/TelaLogin.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Meu Banco - Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}