package utils;

import models.Nodo;
import grafo.Grafo;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ============================================================
 * CLASE: LaberintoParser
 * ============================================================
 * Responsable de leer archivos de laberinto y construir el grafo.
 * 
 * Formato de archivo esperado:
 *   - Cada línea representa una fila del laberinto
 *   - '*' = pared (obstáculo, no transitable)
 *   - ' ' = espacio (celda transitable)
 *   - 'A' = punto de inicio del laberinto
 *   - 'B' = punto de destino del laberinto
 * 
 * Proceso:
 *   1. Lee el archivo de texto
 *   2. Crea un mapa 2D de caracteres
 *   3. Valida que existan puntos A y B
 *   4. Construye un grafo con nodos y aristas
 */
public class LaberintoParser {
    // ===== ATRIBUTOS =====
    private char[][] mapa;                  // Mapa 2D del laberinto
    private Map<String, Integer> posicionANodo;  // Mapea "x,y" al ID del nodo
    private Grafo grafo;                    // Grafo construido
    private int filas;                      // Número de filas del mapa
    private int columnas;                   // Número de columnas del mapa
    private int contadorNodos = 0;          // Contador para asignar IDs únicos

    /**
     * Lee un archivo de laberinto y carga el mapa en memoria
     * @param rutaArchivo ruta al archivo .txt del laberinto
     * @throws IOException si hay error al leer el archivo
     */
    public void leerArchivo(String rutaArchivo) throws IOException {
        try {
            // Lee todas las líneas del archivo
            List<String> lineas = Files.readAllLines(Paths.get(rutaArchivo));
            
            // Encontrar la máxima longitud para ajustar columnas (por si tienen diferente tamaño)
            int maxLongitud = 0;
            for (String linea : lineas) {
                maxLongitud = Math.max(maxLongitud, linea.length());
            }

            // Inicializa dimensiones
            this.filas = lineas.size();
            this.columnas = maxLongitud;
            this.mapa = new char[filas][columnas];

            // Rellena con espacios por defecto
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    mapa[i][j] = ' ';
                }
            }

            // Copia datos del archivo al mapa
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i);
                for (int j = 0; j < linea.length(); j++) {
                    mapa[i][j] = linea.charAt(j);
                }
            }

            validarMapa();  // Verifica que tenga puntos A y B
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el mapa contenga los puntos requeridos A (inicio) y B (fin)
     * @throws IOException si faltan los puntos A o B
     */
    private void validarMapa() throws IOException {
        boolean tieneA = false;
        boolean tieneB = false;

        // Busca los puntos A y B en el mapa
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (mapa[i][j] == 'A') tieneA = true;
                if (mapa[i][j] == 'B') tieneB = true;
            }
        }

        // Lanza excepción si faltan
        if (!tieneA || !tieneB) {
            throw new IOException("El laberinto debe contener un punto A (inicio) y un punto B (fin)");
        }
    }

    /**
     * Construye el grafo a partir del mapa del laberinto
     * Proceso en dos pasos:
     *   1. Crea nodos para todas las celdas transitables
     *   2. Conecta nodos adyacentes con aristas
     * @return El grafo construido
     */
    public Grafo construirGrafo() {
        this.grafo = new Grafo();
        this.posicionANodo = new HashMap<>();

        // ===== PASO 1: CREAR NODOS =====
        // Itera sobre todas las celdas del mapa
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                char celda = mapa[i][j];
                // Crea nodo para cualquier celda que NO sea pared
                if (celda != '*') {  // '*' = pared, no transitable
                    Nodo nodo = new Nodo(contadorNodos, i, j, celda);
                    grafo.agregarNodo(nodo);
                    posicionANodo.put(i + "," + j, contadorNodos);  // Almacena mapeo posición -> ID
                    contadorNodos++;
                }
            }
        }

        // ===== PASO 2: CREAR ARISTAS =====
        // Conecta nodos adyacentes (arriba, abajo, izquierda, derecha)
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                char celda = mapa[i][j];
                // Solo procesa celdas transitables
                if (celda != '*') {
                    Integer idActual = posicionANodo.get(i + "," + j);
                    
                    // Define las 4 direcciones: arriba, abajo, izquierda, derecha
                    int[][] direcciones = {
                        {i - 1, j},  // Arriba
                        {i + 1, j},  // Abajo
                        {i, j - 1},  // Izquierda
                        {i, j + 1}   // Derecha
                    };

                    // Verifica cada dirección y crea arista si es válida
                    for (int[] dir : direcciones) {
                        int ni = dir[0];
                        int nj = dir[1];
                        // Verifica que esté dentro de límites y no sea pared
                        if (ni >= 0 && ni < filas && nj >= 0 && nj < columnas && 
                            mapa[ni][nj] != '*') {
                            Integer idVecino = posicionANodo.get(ni + "," + nj);
                            grafo.agregarArista(idActual, idVecino);
                        }
                    }
                }
            }
        }

        return grafo;
    }

    // ===== GETTERS =====
    /**
     * Retorna el mapa del laberinto
     */
    public char[][] getMapa() {
        return mapa;
    }

    /**
     * Retorna el número de filas
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Retorna el número de columnas
     */
    public int getColumnas() {
        return columnas;
    }

    /**
     * Retorna el grafo construido
     */
    public Grafo getGrafo() {
        return grafo;
    }

    /**
     * Retorna el mapeo de posiciones a IDs de nodos
     */
    public Map<String, Integer> getPosicionANodo() {
        return posicionANodo;
    }

    /**
     * Imprime el mapa de forma legible en consola
     */
    public void imprimirMapa() {
        System.out.println("\n=== MAPA DEL LABERINTO ===");
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(mapa[i][j]);
            }
            System.out.println();
        }
    }
}
