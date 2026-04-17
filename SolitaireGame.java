package com.example.demoxxx.solitaire;

import com.example.demoxxx.CartaInglesa;
import com.example.demoxxx.Palo;
import com.example.demoxxx.Pila;
import java.util.ArrayList;

/*
 * @author (Cecilia Curlango Rosas)
 * @version 2025-2
 */
public class SolitaireGame {

    ArrayList<TableauDeck>  tableau    = new ArrayList<>();
    public ArrayList<FoundationDeck> foundation = new ArrayList<>();
    FoundationDeck lastFoundationUpdated;
    DrawPile   drawPile;
    WastePile  wastePile;

    //Pila de historial de movimientos para Undo
    private Pila<Movimiento> historial;

    public SolitaireGame() {
        drawPile  = new DrawPile();
        wastePile = new WastePile();
        historial = new Pila<>(200);
        createTableaux();
        createFoundations();
        wastePile.addCartas(drawPile.retirarCartas());
    }

    public void reloadDrawPile() {
        //almacena estado del waste antes de vaciarlo para revertir el move
        ArrayList<CartaInglesa> estadoWaste = wastePile.getCopiaCompleta();
        historial.push(Movimiento.crearReload(estadoWaste));
        ArrayList<CartaInglesa> cards = wastePile.emptyPile();
        drawPile.recargar(cards);
    }

    //cartas del DrawPile al WastePile
    public void drawCards() {
        ArrayList<CartaInglesa> cards = drawPile.retirarCartas();
        if (!cards.isEmpty()) {
            historial.push(Movimiento.crearDraw(cards));
            wastePile.addCartas(cards);
        }
    }

    public boolean moveWasteToTableau(int tableauDestino) {
        TableauDeck destino = tableau.get(tableauDestino - 1);
        CartaInglesa carta = wastePile.verCarta();
        if (carta != null && moveCartaToTableau(carta, destino)) {
            wastePile.getCarta();   // remover del waste
            historial.push(Movimiento.crearWasteToTableau(carta, tableauDestino - 1));
            return true;
        }
        return false;
    }

    public boolean moveTableauToTableau(int tableauFuente, int tableauDestino) {
        TableauDeck fuente  = tableau.get(tableauFuente - 1);
        TableauDeck destino = tableau.get(tableauDestino - 1);

        if (fuente.isEmpty()) return false;

        int valorQueDebeTenerLaCartaInicial;
        if (!destino.isEmpty()) {
            valorQueDebeTenerLaCartaInicial = destino.verUltimaCarta().getValor() - 1;
        } else {
            valorQueDebeTenerLaCartaInicial = 13;
        }

        CartaInglesa cartaInicio = fuente.viewCardStartingAt(valorQueDebeTenerLaCartaInicial);
        if (cartaInicio == null || !destino.sePuedeAgregarCarta(cartaInicio)) return false;

        ArrayList<CartaInglesa> cartas = fuente.removeStartingAt(valorQueDebeTenerLaCartaInicial);
        if (!destino.agregarBloqueDeCartas(cartas)) {
            //revertir
            fuente.agregarBloqueDeCartas(cartas);
            return false;
        }
        boolean destapoCarta = false;
        if (!fuente.isEmpty()) {
            CartaInglesa nuevaUltima = fuente.verUltimaCarta();
            if (!nuevaUltima.isFaceup()) {
                nuevaUltima.makeFaceUp();
                destapoCarta = true;
            }
        }
        historial.push(Movimiento.crearTableauToTableau(
                cartas, tableauFuente - 1, tableauDestino - 1, destapoCarta));
        return true;
    }

    public boolean moveTableauToFoundation(int numero) {
        TableauDeck fuente = tableau.get(numero - 1);
        CartaInglesa carta = fuente.removerUltimaCarta();
        if (carta == null) return false;

        int foundIdx = carta.getPalo().ordinal();
        if (moveCartaToFoundation(carta)) {
            boolean destapoCarta = false;
            if (!fuente.isEmpty()) {
                CartaInglesa nuevaUltima = fuente.verUltimaCarta();
                if (!nuevaUltima.isFaceup()) {
                    nuevaUltima.makeFaceUp();
                    destapoCarta = true;
                }
            }
            historial.push(Movimiento.crearTableauToFoundation(
                    carta, numero - 1, foundIdx, destapoCarta));
            return true;
        }
        //devolve si no se pudo
        fuente.agregarCarta(carta);
        return false;
    }

    public boolean moveWasteToFoundation() {
        CartaInglesa carta = wastePile.verCarta();
        if (carta == null) return false;

        int foundIdx = carta.getPalo().ordinal();
        if (moveCartaToFoundation(carta)) {
            wastePile.getCarta();
            historial.push(Movimiento.crearWasteToFoundation(carta, foundIdx));
            return true;
        }
        return false;
    }

    public boolean undo() {
        if (historial.pilaVacia()) return false;

        Movimiento m = historial.pop();
        switch (m.getTipo()) {
            case DRAW:
                undoDraw(m);
                break;
            case RELOAD:
                undoReload(m);
                break;
            case WASTE_TO_TABLEAU:
                undoWasteToTableau(m);
                break;
            case WASTE_TO_FOUNDATION:
                undoWasteToFoundation(m);
                break;
            case TABLEAU_TO_TABLEAU:
                undoTableauToTableau(m);
                break;
            case TABLEAU_TO_FOUNDATION:
                undoTableauToFoundation(m);
                break;
        }
        return true;
    }
    public boolean hayUndo() {
        return !historial.pilaVacia();
    }

    private void undoDraw(Movimiento m) {
        ArrayList<CartaInglesa> cartas = m.getCartasMovidas();
        //quita las cartas del final del waste
        for (int i = cartas.size() - 1; i >= 0; i--) {
            wastePile.getCarta();   // remover del tope del waste
        }
        // regresal frente del draw (boca abajo)
        drawPile.devolverAlFrente(cartas);
    }

    // restaura el WastePile y vaciel DrawPile de las cartas que se recargaron
    private void undoReload(Movimiento m) {
        //vaciael draw actual (que contiene lo que estaba en waste)
        ArrayList<CartaInglesa> cartasDraw = drawPile.vaciar();
        //reatauera waste con el estado guardado (ya estaban boca arriba)
        wastePile.restaurar(m.getEstadoWasteAntesReload());
        //cartas del draw quedan descartadas (ya estaban en waste antes)
    }
    private void undoWasteToTableau(Movimiento m) {
        TableauDeck td = tableau.get(m.getTableauDestino());
        CartaInglesa carta = td.removerUltimaCarta();
        if (carta != null) {
            wastePile.devolverCarta(carta);
        }
    }
    private void undoWasteToFoundation(Movimiento m) {
        FoundationDeck fd = foundation.get(m.getFoundationIdx());
        CartaInglesa carta = fd.removerUltimaCarta();
        if (carta != null) {
            wastePile.devolverCarta(carta);
        }
    }

    private void undoTableauToTableau(Movimiento m) {
        TableauDeck fuente  = tableau.get(m.getTableauFuente());
        TableauDeck destino = tableau.get(m.getTableauDestino());
        //fi se destapa una carta en fuentevolvemos a taparla
        if (m.isSeDestapoCarta() && !fuente.isEmpty()) {
            fuente.verUltimaCarta().makeFaceDown();
        }
        ArrayList<CartaInglesa> cartas = m.getCartasMovidas();
        for (int i = 0; i < cartas.size(); i++) {
            destino.removerUltimaCarta();
        }
        fuente.agregarBloqueSinValidar(cartas);
    }
    private void undoTableauToFoundation(Movimiento m) {
        TableauDeck fuente = tableau.get(m.getTableauFuente());
        FoundationDeck fd  = foundation.get(m.getFoundationIdx());
        if (m.isSeDestapoCarta() && !fuente.isEmpty()) {
            fuente.verUltimaCarta().makeFaceDown();
        }

        CartaInglesa carta = fd.removerUltimaCarta();
        if (carta != null) {
            fuente.agregarCartaSinValidar(carta);
        }
    }

    private boolean moveCartaToTableau(CartaInglesa carta, TableauDeck destino) {
        return destino.agregarCarta(carta);
    }

    private boolean moveCartaToFoundation(CartaInglesa carta) {
        int cualFoundation = carta.getPalo().ordinal();
        FoundationDeck destino = foundation.get(cualFoundation);
        lastFoundationUpdated = destino;
        return destino.agregarCarta(carta);
    }
    //estado game
    public boolean isGameOver() {
        for (FoundationDeck fd : foundation) {
            if (fd.estaVacio()) return false;
            if (fd.getUltimaCarta().getValor() != 13) return false;
        }
        return true;
    }

    private void createFoundations() {
        for (Palo palo : Palo.values()) {
            foundation.add(new FoundationDeck(palo));
        }
    }

    private void createTableaux() {
        for (int i = 0; i < 7; i++) {
            TableauDeck td = new TableauDeck();
            td.inicializar(drawPile.getCartas(i + 1));
            tableau.add(td);
        }
    }
    //getters

    public DrawPile getDrawPile()   { return drawPile; }
    public ArrayList<TableauDeck> getTableau() { return tableau; }
    public WastePile getWastePile() { return wastePile; }
    public FoundationDeck getLastFoundationUpdated() { return lastFoundationUpdated; }

    public boolean moveWasteToTableau(TableauDeck tableau) {
        CartaInglesa carta = wastePile.verCarta();
        if (carta != null && moveCartaToTableau(carta, tableau)) {
            wastePile.getCarta();
            int idx = this.tableau.indexOf(tableau);
            historial.push(Movimiento.crearWasteToTableau(carta, idx));
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Foundation\n");
        for (FoundationDeck fd : foundation) {
            str.append(fd).append("\n");
        }
        str.append("\nTableaux\n");
        int n = 1;
        for (TableauDeck td : tableau) {
            str.append(n++).append(" ").append(td).append("\n");
        }
        str.append("Waste\n").append(wastePile);
        str.append("\nDraw\n").append(drawPile);
        return str.toString();
    }
}