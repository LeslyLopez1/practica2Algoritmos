package com.example.demoxxx;

import com.example.demoxxx.solitaire.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;

public class HelloApplication extends Application {

    private SolitaireGame game;

    //paneles del juego
    private Pane drawPilePane;
    private Pane wastePilePane;
    private ArrayList<Pane> tableauPanes;
    private ArrayList<Pane> foundationPanes;

    //atributos para origen-destino
    private CartaInglesa cartaSeleccionada;
    private OrigenCarta  origenSeleccionado;
    private int          indiceSeleccionado;

    //boton undo para deshacer los movimeintos del juego
    private Button botonUndo;

    @Override
    public void start(Stage stage) {
        tableauPanes   = new ArrayList<>();
        foundationPanes = new ArrayList<>();

        //layout principal VERDE fondo de juego
        HBox mainLayout = new HBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #0B6623;");

        //1-LEFT (WASTE p y DRAW p)
        VBox leftColumn = new VBox(15);
        //drawPile
        drawPilePane = new Pane();
        drawPilePane.setPrefSize(100, 150);
        drawPilePane.setStyle("-fx-border-color: transparent; -fx-border-width: 2; " +
                "-fx-border-radius: 8; -fx-background-radius: 8;");
        drawPilePane.setOnMouseClicked(e -> manejarClickDraw());

        wastePilePane = new Pane();
        wastePilePane.setPrefSize(100, 150);
        //cosas para que se vea bonito el juego
        wastePilePane.setStyle("-fx-border-color: transparent; -fx-border-width: 2; " +
                "-fx-border-radius: 8; -fx-background-radius: 8;");
        //se agregan los hijos a la columna izquierda
        leftColumn.getChildren().addAll(drawPilePane, wastePilePane);

        //2–CENTER (tableusitos 7) que se van agregando iterando con el for hasta 7
        HBox tableauBox = new HBox(15);
        for (int i = 0; i < 7; i++) {
            Pane tableauPane = new Pane();
            //tamaño fijo
            tableauPane.setPrefSize(100, 500);
            tableauPane.setMinWidth(100);
            //decoración de los tableaus
            tableauPane.setStyle("-fx-border-color: rgba(255,255,255,0.15); -fx-border-width: 2; " +
                    "-fx-border-style: dashed; -fx-border-radius: 8; -fx-background-radius: 8;");

            //se guarda el panel para despues acceder a ellos como agregar o quitar cartas
            //al momento de jugar
            tableauPanes.add(tableauPane);
            //se agrega para que sea visible
            tableauBox.getChildren().add(tableauPane);

            final int idx = i;
            tableauPane.setOnMouseClicked(e -> manejarClickTableauVacio(idx));
        }

        // 3 – RIGHT (foundations)
        VBox rightColumn = new VBox(15);
        for (int i = 0; i < 4; i++) {
            Pane foundationPane = new Pane();
            foundationPane.setPrefSize(100, 150);
            foundationPane.setStyle("-fx-border-color: rgba(255,255,255,0.25); -fx-border-width: 3; " +
                    "-fx-border-style: dashed; -fx-border-radius: 8; " +
                    "-fx-background-color: rgba(0,100,0,0.3); -fx-background-radius: 8;");
            foundationPanes.add(foundationPane);
            rightColumn.getChildren().add(foundationPane);
            //se crea una copia para despues usar la lambda
            final int idx = i;
            //evento de clic al panel
            //Cuando el usuario hace clic en una columna vacía,
            //llama al manejarClickTableauVacio pasándole el indice de columna para
            //saber donde se clickeo
            foundationPane.setOnMouseClicked(e -> manejarClickFoundation(idx));
        }
        mainLayout.getChildren().addAll(leftColumn, tableauBox, rightColumn);

        //botones
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #0B6623;");

        HBox barraButtons = new HBox(15);
        barraButtons.setPadding(new Insets(10, 20, 0, 20));

        Button botonReiniciar = crearBoton("Reiniciar");
        botonReiniciar.setOnAction(e -> reiniciarJuego());

        botonUndo = crearBoton("Deshacer");
        botonUndo.setDisable(true);
        botonUndo.setOnAction(e -> manejarUndo());

        barraButtons.getChildren().addAll(botonReiniciar, botonUndo);
        root.getChildren().addAll(barraButtons, mainLayout);

        //inicializacion game
        game = new SolitaireGame();
        actualizarVista();
        Scene scene = new Scene(root, 1200, 650);
        stage.setTitle("Solitario");
        stage.setScene(scene);
        stage.show();
    }

    private void actualizarVista() {
        actualizarDrawPile();
        actualizarWastePile();
        actualizarFoundations();
        actualizarTableaux();
        actualizarBotonesEstado();
        verificarVictoria();
    }

    private void actualizarBotonesEstado() {
        botonUndo.setDisable(!game.hayUndo());
    }

    private void actualizarDrawPile() {
        drawPilePane.getChildren().clear();
        if (game.getDrawPile().hayCartas()) {
            CartaInglesa dorso = new CartaInglesa(1, Palo.CORAZON, "rojo");
            dorso.makeFaceDown();
            drawPilePane.getChildren().add(new CartaGrafica(dorso).getImagenVista());
        }
    }

    private void actualizarWastePile() {
        wastePilePane.getChildren().clear();
        CartaInglesa carta = game.getWastePile().verCarta();
        if (carta != null) {
            CartaGrafica cg = new CartaGrafica(carta);
            if (origenSeleccionado == OrigenCarta.WASTE) {
                cg.getImagenVista().setEffect(new javafx.scene.effect.DropShadow(25, Color.YELLOW));
                cg.getImagenVista().setScaleX(1.05);
                cg.getImagenVista().setScaleY(1.05);
            }
            cg.getImagenVista().setOnMouseClicked(e -> manejarClickWaste());
            cg.getImagenVista().setStyle("-fx-cursor: hand;");
            wastePilePane.getChildren().add(cg.getImagenVista());
        }
    }

    private void actualizarFoundations() {
        ArrayList<FoundationDeck> foundations = game.foundation;
        for (int i = 0; i < foundations.size(); i++) {
            Pane pane = foundationPanes.get(i);
            pane.getChildren().clear();
            CartaInglesa carta = foundations.get(i).getUltimaCarta();
            if (carta != null) {
                pane.getChildren().add(new CartaGrafica(carta).getImagenVista());
            }
        }
    }

    private void actualizarTableaux() {
        ArrayList<TableauDeck> tableaux = game.getTableau();
        for (int i = 0; i < tableaux.size(); i++) {
            Pane pane = tableauPanes.get(i);
            pane.getChildren().clear();

            TableauDeck tableau = tableaux.get(i);
            ArrayList<CartaInglesa> cartas = tableau.getCards();

            double contY = 0;
            for (CartaInglesa carta : cartas) {
                CartaGrafica cg = new CartaGrafica(carta);
                cg.getImagenVista().setLayoutX(0);
                cg.getImagenVista().setLayoutY(contY);

                if (carta.isFaceup()) {
                    final int tableauIdx = i;
                    if (origenSeleccionado == OrigenCarta.TABLEAU && indiceSeleccionado == tableauIdx) {
                        CartaInglesa ultima = tableau.getUltimaCarta();
                        if (carta == ultima || (cartaSeleccionada != null &&
                                carta.getValor() <= cartaSeleccionada.getValor())) {
                            cg.getImagenVista().setEffect(new javafx.scene.effect.DropShadow(25, Color.YELLOW));
                           tableauPanes.get(tableauIdx).getChildren().add(cg.getImagenVista());
                        }
                    }
                    cg.getImagenVista().setOnMouseClicked(e -> manejarClickTableau(tableauIdx, carta));
                    cg.getImagenVista().setStyle("-fx-cursor: hand;");
                }
                pane.getChildren().add(cg.getImagenVista());
                contY += carta.isFaceup() ? 30 : 20;
            }
        }
    }
    //  Manejadores de click
    private void manejarClickDraw() {
        cancelarSeleccion();
        if (game.getDrawPile().hayCartas()) {
            game.drawCards();
        } else {
            game.reloadDrawPile();
        }
        actualizarVista();
    }

    private void manejarClickWaste() {
        if (origenSeleccionado == OrigenCarta.WASTE) {
            cancelarSeleccion();
        } else {
            cancelarSeleccion();
            origenSeleccionado  = OrigenCarta.WASTE;
            cartaSeleccionada   = game.getWastePile().verCarta();
        }
        actualizarVista();
    }

    private void manejarClickTableau(int tableauIdx, CartaInglesa carta) {
        if (origenSeleccionado == null) {
            origenSeleccionado  = OrigenCarta.TABLEAU;
            indiceSeleccionado  = tableauIdx;
            cartaSeleccionada   = carta;
            actualizarVista();
        } else {
            if (origenSeleccionado == OrigenCarta.WASTE) {
                game.moveWasteToTableau(tableauIdx + 1);
            } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
                game.moveTableauToTableau(indiceSeleccionado + 1, tableauIdx + 1);
            }
            cancelarSeleccion();
            actualizarVista();
        }
    }

    private void manejarClickTableauVacio(int tableauIdx) {
        if (origenSeleccionado == null) return;
        if (origenSeleccionado == OrigenCarta.WASTE) {
            game.moveWasteToTableau(tableauIdx + 1);
        } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
            game.moveTableauToTableau(indiceSeleccionado + 1, tableauIdx + 1);
        }
        cancelarSeleccion();
        actualizarVista();
    }

    private void manejarClickFoundation(int foundationIdx) {
        if (origenSeleccionado == null) return;
        if (origenSeleccionado == OrigenCarta.WASTE) {
            game.moveWasteToFoundation();
        } else if (origenSeleccionado == OrigenCarta.TABLEAU) {
            game.moveTableauToFoundation(indiceSeleccionado + 1);
        }
        cancelarSeleccion();
        actualizarVista();
    }

    private void manejarUndo() {
        cancelarSeleccion();
        game.undo();
        actualizarVista();
    }

    private void cancelarSeleccion() {
        origenSeleccionado = null;
        cartaSeleccionada  = null;
        indiceSeleccionado = -1;
    }

    private void reiniciarJuego() {
        cancelarSeleccion();
        game = new SolitaireGame();
        actualizarVista();
    }

    private void verificarVictoria() {
        if (game.isGameOver()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Wuju, felicidades!");
            alert.setHeaderText("¡Has ganado! :)");
            alert.setContentText("¿Otra partida?");
            alert.showAndWait();
            reiniciarJuego();
        }
    }
    private Button crearBoton(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: #2d5a1a; -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 16; " +
                "-fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnMouseEntered(e ->
                btn.setStyle(btn.getStyle().replace("#2d5a1a", "#3d7a29")));
        btn.setOnMouseExited(e ->
                btn.setStyle(btn.getStyle().replace("#3d7a29", "#2d5a1a")));
        return btn;
    }

    private enum OrigenCarta {
        WASTE, TABLEAU, FOUNDATION
    }

    public static void main(String[] args) {
        launch(args);
    }
}