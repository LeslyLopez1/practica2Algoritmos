package com.example.demoxxx;

public class Pila<T> {
    private T[] pila;
    private int tope;

    public Pila() {
        pila = (T[]) new Object[100];
        tope = -1;
    }

    public Pila(int cantidad) {
        pila = (T[]) new Object[cantidad];
        tope = -1;
    }

    public void push(T dato) {
        if (pilaLlena()) {
            T[] nueva = (T[]) new Object[pila.length * 2];
            System.arraycopy(pila, 0, nueva, 0, pila.length);
            pila = nueva;
        }
        tope++;
        pila[tope] = dato;
    }

    public T pop() {
        T dato = null;
        if (pilaVacia()) {
            System.out.println("subdesbordamiento");
        } else {
            dato = pila[tope];
            tope--;
        }
        return dato;
    }

    public T peek() {
        if (pilaVacia()) return null;
        return pila[tope];
    }

    public boolean pilaLlena() {
        return tope == pila.length - 1;
    }

    public boolean pilaVacia() {
        return tope == -1;
    }

    public String invierteCadena(String cadena) {
        Pila<Character> pilaInvertida = new Pila<>();
        for (char c : cadena.toCharArray()) {
            pilaInvertida.push(c);
        }
        StringBuilder invertida = new StringBuilder();
        while (!pilaInvertida.pilaVacia()) {
            invertida.append(pilaInvertida.pop());
        }
        return invertida.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i <= tope; i++) {
            sb.append(pila[i]);
            if (i < tope) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}