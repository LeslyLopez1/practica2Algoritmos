package com.example.demoxxx.solitaire;

import com.example.demoxxx.CartaInglesa;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * @author Cecilia M. Curlango
 */
public class TableauDeck {
    ArrayList<CartaInglesa> cartas = new ArrayList<>();

    public void inicializar(ArrayList<CartaInglesa> cartas) {
        this.cartas = cartas;
        cartas.getLast().makeFaceUp();
    }

    public ArrayList<CartaInglesa> removeStartingAt(int value) {
        ArrayList<CartaInglesa> removed = new ArrayList<>();
        Iterator<CartaInglesa> it = cartas.iterator();
        while (it.hasNext()) {
            CartaInglesa next = it.next();
            if (next.isFaceup() && next.getValor() <= value) {
                removed.add(next);
                it.remove();
            }
        }
        return removed;
    }

    public CartaInglesa viewCardStartingAt(int value) {
        for (CartaInglesa next : cartas) {
            if (next.isFaceup() && next.getValor() <= value) return next;
        }
        return null;
    }

    public boolean agregarCarta(CartaInglesa carta) {
        if (sePuedeAgregarCarta(carta)) {
            carta.makeFaceUp();
            cartas.add(carta);
            return true;
        }
        return false;
    }

    CartaInglesa verUltimaCarta() {
        return cartas.isEmpty() ? null : cartas.getLast();
    }

    CartaInglesa removerUltimaCarta() {
        if (cartas.isEmpty()) return null;
        CartaInglesa ultima = cartas.removeLast();
        if (!cartas.isEmpty()) {
            cartas.getLast().makeFaceUp();
        }
        return ultima;
    }

    public boolean agregarBloqueDeCartas(ArrayList<CartaInglesa> cartasRecibidas) {
        if (cartasRecibidas.isEmpty()) return false;
        CartaInglesa primera = cartasRecibidas.getFirst();
        if (sePuedeAgregarCarta(primera)) {
            cartas.addAll(cartasRecibidas);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return cartas.isEmpty();
    }

    public boolean sePuedeAgregarCarta(CartaInglesa carta) {
        if (!cartas.isEmpty()) {
            CartaInglesa ultima = cartas.getLast();
            return !ultima.getColor().equals(carta.getColor())
                    && ultima.getValor() == carta.getValor() + 1;
        }
        return carta.getValor() == 13;
    }

    public CartaInglesa getUltimaCarta() {
        return cartas.isEmpty() ? null : cartas.getLast();
    }

    public ArrayList<CartaInglesa> getCards() {
        return cartas;
    }

    //mtodos nuevos para Undo
    public void agregarBloqueSinValidar(ArrayList<CartaInglesa> bloque) {
        cartas.addAll(bloque);
    }

    public void agregarCartaSinValidar(CartaInglesa carta) {
        cartas.add(carta);
    }


    @Override
    public String toString() {
        if (cartas.isEmpty()) return "---";
        StringBuilder sb = new StringBuilder();
        for (CartaInglesa c : cartas) sb.append(c);
        return sb.toString();
    }
}