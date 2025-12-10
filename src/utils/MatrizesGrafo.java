package utils;
import grafo.Grafo;
import models.Nodo;
import java.util.*;

/**
 * ============================================================
 * CLASE: MatrizesGrafo
 * ============================================================
 * Genera e imprime matrices de representación del grafo:
 * 
 * MATRIZ DE ADYACENCIA:
 *   - Tamaño: NxN (N = número de nodos)
 *   - M[i][j] = 1 si existe arista entre nodo i y nodo j
 *   - M[i][j] = 0 si NO existe arista
 *   - Simétrica para grafos no dirigidos: M[i][j] = M[j][i]
 * 
 * MATRIZ DE INCIDENCIA:
 *   - Tamaño: NxM (N = nodos, M = aristas)
 *   - Inc[i][j] = 1 si nodo i toca arista j
 *   - Inc[i][j] = 0 si nodo i NO toca arista j
 *   - Para grafo no dirigido: dos 1s por columna (arista toca 2 nodos)
 * 
 * Útiles para:
 *   - Análisis matricial del grafo
 *   - Cálculos algebraicos
 *   - Visualización de conectividad
 */
public class MatrizesGrafo {
    // ===== ATRIBUTOS =====
    private Grafo grafo;                              // El grafo del cual se generan matrices
    private int[][] matrizAdyacencia;                 // Matriz de adyacencia (NxN)
    private int[][] matrizIncidencia;                 // Matriz de incidencia (NxM)
    private int cantidadNodos;                        // Número de nodos
    private int cantidadAristas;                      // Número de aristas
    private Map<Integer, Integer> mapaIdAIndice;      // Mapea ID de nodo → índice de matriz

    /**
     * Constructor
     * @param grafo el grafo del cual generar matrices
     */
    public MatrizesGrafo(Grafo grafo) {
        this.grafo = grafo;
        this.cantidadNodos = grafo.getCantidadNodos();
        this.cantidadAristas = grafo.getCantidadAristas();
        this.mapaIdAIndice = new HashMap<>();
        construirMapaIndices();
    }

    /**
     * Construye un mapeo de ID de nodo → índice de matriz
     * Necesario porque los IDs pueden no ser secuenciales del 0 al N
     */
    private void construirMapaIndices() {
        int indice = 0;
        // Itera sobre todos los nodos y asigna índices secuenciales
        for (Nodo nodo : grafo.getTodosNodos()) {
            mapaIdAIndice.put(nodo.getId(), indice);
            indice++;
        }
    }

    /**
     * Genera la matriz de adyacencia
     * M[i][j] = 1 si existe arista (nodo i, nodo j), 0 en caso contrario
     * @return Matriz de adyacencia NxN
     */
    public int[][] generarMatrizAdyacencia() {
        matrizAdyacencia = new int[cantidadNodos][cantidadNodos];
        
        // Itera sobre la lista de adyacencia del grafo
        Map<Integer, List<Integer>> listaAdyacencia = grafo.getListaAdyacencia();
        for (Integer idOrigen : listaAdyacencia.keySet()) {
            int i = mapaIdAIndice.get(idOrigen);  // Índice del nodo origen
            
            // Itera sobre los adyacentes de este nodo
            for (Integer idDestino : listaAdyacencia.get(idOrigen)) {
                int j = mapaIdAIndice.get(idDestino);  // Índice del nodo destino
                matrizAdyacencia[i][j] = 1;  // Marca la conexión
            }
        }

        return matrizAdyacencia;
    }

    /**
     * Genera la matriz de incidencia
     * Inc[i][j] = 1 si nodo i toca arista j, 0 en caso contrario
     * Para grafos no dirigidos, cada arista toca 2 nodos
     * @return Matriz de incidencia NxM
     */
    public int[][] generarMatrizIncidencia() {
        matrizIncidencia = new int[cantidadNodos][cantidadAristas];
        int indexArista = 0;
        Set<String> aristasAgregadas = new HashSet<>();  // Evita procesar la misma arista dos veces

        // Itera sobre la lista de adyacencia
        Map<Integer, List<Integer>> listaAdyacencia = grafo.getListaAdyacencia();
        for (Integer idOrigen : listaAdyacencia.keySet()) {
            int i = mapaIdAIndice.get(idOrigen);
            
            for (Integer idDestino : listaAdyacencia.get(idOrigen)) {
                // Para grafo no dirigido, solo procesa cada arista una vez
                // Usa un identificador único para la arista (nodo menor-nodo mayor)
                String aristaKey = Math.min(idOrigen, idDestino) + "-" + Math.max(idOrigen, idDestino);
                
                if (!aristasAgregadas.contains(aristaKey)) {
                    int j = mapaIdAIndice.get(idDestino);
                    matrizIncidencia[i][indexArista] = 1;  // Nodo origen toca la arista
                    matrizIncidencia[j][indexArista] = 1;  // Nodo destino toca la arista
                    aristasAgregadas.add(aristaKey);
                    indexArista++;
                }
            }
        }

        return matrizIncidencia;
    }

    /**
     * Imprime la matriz de adyacencia de forma legible
     * Muestra encabezados de índices y datos formateados
     */
    public void imprimirMatrizAdyacencia() {
        if (matrizAdyacencia == null) {
            generarMatrizAdyacencia();
        }

        System.out.println("\n=== MATRIZ DE ADYACENCIA ===");
        System.out.println("Dimensión: " + cantidadNodos + "x" + cantidadNodos);
        
        // Encabezado de columnas
        System.out.print("    ");
        for (int j = 0; j < cantidadNodos; j++) {
            System.out.print(String.format("%3d ", j));
        }
        System.out.println();

        // Datos
        for (int i = 0; i < cantidadNodos; i++) {
            System.out.print(String.format("%3d ", i));  // Encabezado de fila
            for (int j = 0; j < cantidadNodos; j++) {
                System.out.print(String.format("%3d ", matrizAdyacencia[i][j]));
            }
            System.out.println();
        }
    }

    /**
     * Imprime la matriz de incidencia de forma legible
     * Muestra encabezados de índices y datos formateados
     */
    public void imprimirMatrizIncidencia() {
        if (matrizIncidencia == null) {
            generarMatrizIncidencia();
        }

        System.out.println("\n=== MATRIZ DE INCIDENCIA ===");
        System.out.println("Dimensión: " + cantidadNodos + "x" + cantidadAristas);
        
        // Encabezado de columnas
        System.out.print("    ");
        for (int j = 0; j < cantidadAristas; j++) {
            System.out.print(String.format("%3d ", j));
        }
        System.out.println();

        // Datos
        for (int i = 0; i < cantidadNodos; i++) {
            System.out.print(String.format("%3d ", i));  // Encabezado de fila
            for (int j = 0; j < cantidadAristas; j++) {
                System.out.print(String.format("%3d ", matrizIncidencia[i][j]));
            }
            System.out.println();
        }
    }

    /**
     * Obtiene la matriz de adyacencia (la genera si no existe)
     */
    public int[][] getMatrizAdyacencia() {
        if (matrizAdyacencia == null) {
            generarMatrizAdyacencia();
        }
        return matrizAdyacencia;
    }

    /**
     * Obtiene la matriz de incidencia (la genera si no existe)
     */
    public int[][] getMatrizIncidencia() {
        if (matrizIncidencia == null) {
            generarMatrizIncidencia();
        }
        return matrizIncidencia;
    }

    /**
     * Obtiene el mapeo de IDs a índices de matriz
     */
    public Map<Integer, Integer> getMapaIdAIndice() {
        return new HashMap<>(mapaIdAIndice);
    }
}
