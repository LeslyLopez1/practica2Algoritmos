package com.example.demoxxx.solitaire;

import com.example.demoxxx.CartaInglesa;
import com.example.demoxxx.Mazo;
import java.util.ArrayList;

/*
 * @author Cecilia Curlango
 */
public class DrawPile {
    private ArrayList<CartaInglesa> cartas;
    private int cuantasCartasSeEntregan = 3;

    public DrawPile() {
        Mazo mazo = new Mazo();
        cartas = mazo.getCartas();
        setCuantasCartasSeEntregan(3);
    }

    public void setCuantasCartasSeEntregan(int cantidad) {
        this.cuantasCartasSeEntregan = cantidad;
    }

    public int getCuantasCartasSeEntregan() {
        return cuantasCartasSeEntregan;
    }

    public ArrayList<CartaInglesa> getCartas(int cantidad) {
        ArrayList<CartaInglesa> retiradas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            retiradas.add(cartas.remove(0));
        }
        return retiradas;
    }

    public ArrayList<CartaInglesa> retirarCartas() {
        ArrayList<CartaInglesa> retiradas = new ArrayList<>();
        int max = Math.min(cartas.size(), cuantasCartasSeEntregan);
        for (int i = 0; i < max; i++) {
            CartaInglesa c = cartas.remove(0);
            c.makeFaceUp();
            retiradas.add(c);
        }
        return retiradas;
    }

    public boolean hayCartas() {
        return !cartas.isEmpty();
    }

    public CartaInglesa verCarta() {
        return cartas.isEmpty() ? null : cartas.getLast();
    }

    public void recargar(ArrayList<CartaInglesa> cartasAgregar) {
        cartas = cartasAgregar;
        for (CartaInglesa c : cartas) {
            c.makeFaceDown();
        }
    }

    //metodos nuevos para Undo
    public void devolverAlFrente(ArrayList<CartaInglesa> cartasADevolver) {
        for (CartaInglesa c : cartasADevolver) {
            c.makeFaceDown();
        }
        cartas.addAll(0, cartasADevolver);
    }

    public ArrayList<CartaInglesa> vaciar() {
        ArrayList<CartaInglesa> todas = new ArrayList<>(cartas);
        cartas.clear();
        return todas;
    }

    @Override
    public String toString() {
        return cartas.isEmpty() ? "-E-" : "@";
    }
}