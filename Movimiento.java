package com.example.demoxxx.solitaire;

import com.example.demoxxx.CartaInglesa;
import java.util.ArrayList;

/*
 *movimientos:
 *DRAw = se sacaron cartas del DrawPile al WastePile
 *RELOAD = se recargo el DrawPile desde el WastePile
 *WASTE_TO_TABLEAU=carta del WastePile a un Tableau
 *TABLEAU_TO_TABLEAU= bloque de cartas de un Tableau a otro
 *TABLEAU_TO_FOUNDATION=carta de un Tableau a Foundation
 */
public class Movimiento {

    public enum TipoMovimiento {
        DRAW,
        RELOAD,
        WASTE_TO_TABLEAU,
        WASTE_TO_FOUNDATION,
        TABLEAU_TO_TABLEAU,
        TABLEAU_TO_FOUNDATION
    }

    private final TipoMovimiento tipo;

    //cartas que se movieron del Draw al Waste
    private ArrayList<CartaInglesa> cartasMovidas;

    //copia completa del waste antes del reload
    private ArrayList<CartaInglesa> estadoWasteAntesReload;

    //indices para tableau
    private int tableauFuente;
    private int tableauDestino;
    private boolean seDestapoCarta;

    // tableau y waste a foundation
    private int foundationIdx;

    public static Movimiento crearDraw(ArrayList<CartaInglesa> cartasMovidas) {
        Movimiento m = new Movimiento(TipoMovimiento.DRAW);
        m.cartasMovidas = new ArrayList<>(cartasMovidas);
        return m;
    }

    public static Movimiento crearReload(ArrayList<CartaInglesa> estadoWaste) {
        Movimiento m = new Movimiento(TipoMovimiento.RELOAD);
        m.estadoWasteAntesReload = new ArrayList<>(estadoWaste);
        return m;
    }

    public static Movimiento crearWasteToTableau(CartaInglesa carta, int tableauDestino) {
        Movimiento m = new Movimiento(TipoMovimiento.WASTE_TO_TABLEAU);
        m.cartasMovidas = new ArrayList<>();
        m.cartasMovidas.add(carta);
        m.tableauDestino = tableauDestino;
        return m;
    }

    public static Movimiento crearWasteToFoundation(CartaInglesa carta, int foundationIdx) {
        Movimiento m = new Movimiento(TipoMovimiento.WASTE_TO_FOUNDATION);
        m.cartasMovidas = new ArrayList<>();
        m.cartasMovidas.add(carta);
        m.foundationIdx = foundationIdx;
        return m;
    }

    public static Movimiento crearTableauToTableau(ArrayList<CartaInglesa> cartas, int fuente, int destino, boolean seDestapoCarta) {
        Movimiento m = new Movimiento(TipoMovimiento.TABLEAU_TO_TABLEAU);
        m.cartasMovidas = new ArrayList<>(cartas);
        m.tableauFuente = fuente;
        m.tableauDestino = destino;
        m.seDestapoCarta = seDestapoCarta;
        return m;
    }

    public static Movimiento crearTableauToFoundation(CartaInglesa carta, int tableauFuente, int foundationIdx, boolean seDestapoCarta) {
        Movimiento m = new Movimiento(TipoMovimiento.TABLEAU_TO_FOUNDATION);
        m.cartasMovidas = new ArrayList<>();
        m.cartasMovidas.add(carta);
        m.tableauFuente = tableauFuente;
        m.foundationIdx = foundationIdx;
        m.seDestapoCarta = seDestapoCarta;
        return m;
    }

    private Movimiento(TipoMovimiento tipo) {
        this.tipo = tipo;
        this.tableauFuente = -1;
        this.tableauDestino = -1;
        this.foundationIdx = -1;
        this.seDestapoCarta = false;
    }

    //getters
    public TipoMovimiento getTipo() { return tipo; }
    public ArrayList<CartaInglesa> getCartasMovidas() { return cartasMovidas; }
    public ArrayList<CartaInglesa> getEstadoWasteAntesReload() { return estadoWasteAntesReload; }
    public int getTableauFuente() { return tableauFuente; }
    public int getTableauDestino() { return tableauDestino; }
    public boolean isSeDestapoCarta() { return seDestapoCarta; }
    public int getFoundationIdx() { return foundationIdx; }

    @Override
    public String toString() {
        return "Movimiento{" + tipo + ", fuente=" + tableauFuente +
                ", destino=" + tableauDestino + ", cartas=" + cartasMovidas + "}";
    }
}