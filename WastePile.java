package com.example.demoxxx.solitaire;

import com.example.demoxxx.CartaInglesa;

import java.util.ArrayList;

/*
 * @author Cecilia Curlango Rosas
 */
public class WastePile {
    private ArrayList<CartaInglesa> cartas;

    public WastePile() {
        cartas = new ArrayList<>();
    }

    public void addCartas(ArrayList<CartaInglesa> nuevas) {
        cartas.addAll(nuevas);
    }

    public ArrayList<CartaInglesa> emptyPile() {
        ArrayList<CartaInglesa> pile = new ArrayList<>(cartas);
        cartas = new ArrayList<>();
        return pile;
    }

    public CartaInglesa verCarta() {
        return cartas.isEmpty() ? null : cartas.getLast();
    }

    public CartaInglesa getCarta() {
        return cartas.isEmpty() ? null : cartas.removeLast();
    }

    public boolean hayCartas() {
        return !cartas.isEmpty();
    }

    //mtodos nuevos para Undo
    public void devolverCarta(CartaInglesa carta) {
        carta.makeFaceUp();
        cartas.add(carta);
    }

    public ArrayList<CartaInglesa> getCopiaCompleta() {
        return new ArrayList<>(cartas);
    }

    public void restaurar(ArrayList<CartaInglesa> snapshot) {
        cartas = new ArrayList<>(snapshot);
        for (CartaInglesa c : cartas) {
            c.makeFaceUp();
        }
    }

    @Override
    public String toString() {
        if (cartas.isEmpty()) return "---";
        CartaInglesa top = cartas.getLast();
        top.makeFaceUp();
        return top.toString();
    }
}