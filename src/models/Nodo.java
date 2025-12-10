package models;

import java.util.Objects;

/**
 * ============================================================\n * CLASE: Nodo
 * ============================================================\n * Representa un nodo en el grafo del laberinto.
 * Cada nodo corresponde a una celda transitable en el mapa.
 * 
 * Un nodo puede ser:
 *   - ' ' (espacio): celda transitable normal
 *   - 'A': punto de inicio del laberinto
 *   - 'B': punto de destino del laberinto
 * 
 * Cada nodo tiene una posición (x, y) y un identificador único.
 */
public class Nodo {
    // ===== ATRIBUTOS =====
    private int x;      // Coordenada X (fila) del nodo en el mapa
    private int y;      // Coordenada Y (columna) del nodo en el mapa
    private char tipo;  // Tipo de nodo: ' ' (espacio), 'A' (inicio), 'B' (fin)
    private int id;     // Identificador único del nodo en el grafo

    /**
     * Constructor de Nodo
     * @param id identificador único del nodo
     * @param x coordenada X (fila)
     * @param y coordenada Y (columna)
     * @param tipo carácter que representa el tipo de celda
     */
    public Nodo(int id, int x, int y, char tipo) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }

    // ===== GETTERS =====
    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getTipo() {
        return tipo;
    }

    // ===== SETTERS =====
    /**
     * Modifica el tipo de nodo
     */
    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    // ===== MÉTODOS ESPECIALES =====
    /**
     * Compara dos nodos por su posición (x, y)
     * Dos nodos son iguales si tienen la misma posición en el mapa
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nodo nodo = (Nodo) o;
        return x == nodo.x && y == nodo.y;  // Compara por posición
    }

    /**
     * Calcula el código hash del nodo basado en su posición
     * Necesario para usar el nodo en HashSet o HashMap
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Representación textual del nodo
     * Útil para debugging y visualización
     */
    @Override
    public String toString() {
        return "Nodo{" + "id=" + id + ", x=" + x + ", y=" + y + ", tipo='" + tipo + '\'' + '}';
    }
}
