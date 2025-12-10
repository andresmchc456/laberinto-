package algorithms;

import grafo.Grafo;
import models.Nodo;
import java.util.*;

/**
 * ============================================================
 * CLASE: CaminoMasCorto
 * ============================================================
 * Implementa el algoritmo BFS (Breadth-First Search) para encontrar
 * el camino más corto entre dos nodos en un grafo no dirigido.
 * 
 * BFS es ideal para encontrar el camino más corto porque:
 *   - Explora nivel a nivel (por amplitud)
 *   - Garantiza encontrar el camino con menos aristas
 *   - Utiliza una cola (FIFO) para mantener el orden
 * 
 * Complejidad:
 *   - Tiempo: O(V + E) donde V es vértices y E es aristas
 *   - Espacio: O(V) para la cola y visitados
 */
public class CaminoMasCorto {
    // ===== ATRIBUTOS =====
    private Grafo grafo;  // El grafo en el que buscar el camino

    /**
     * Constructor
     * @param grafo el grafo en el que se buscará el camino
     */
    public CaminoMasCorto(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Encuentra el camino más corto entre dos nodos usando BFS
     * @param idOrigen ID del nodo de inicio
     * @param idDestino ID del nodo de destino
     * @return Lista de IDs de nodos que forman el camino, vacía si no existe
     */
    public List<Integer> encontrarCaminoMasCorto(int idOrigen, int idDestino) {
        Queue<Integer> cola = new LinkedList<>();     // Cola para BFS
        Map<Integer, Integer> padres = new HashMap<>();  // Almacena el padre de cada nodo
        Set<Integer> visitados = new HashSet<>();     // Nodos ya visitados

        // Inicializa el BFS con el nodo origen
        cola.offer(idOrigen);
        visitados.add(idOrigen);
        padres.put(idOrigen, -1);  // El origen no tiene padre

        // ===== ALGORITMO BFS =====
        while (!cola.isEmpty()) {
            int idActual = cola.poll();  // Extrae el primer nodo de la cola

            // Si encontramos el destino, reconstruimos el camino
            if (idActual == idDestino) {
                return reconstruirCamino(padres, idDestino);
            }

            // Explora los vecinos del nodo actual
            for (Integer idVecino : grafo.getAdyacentes(idActual)) {
                if (!visitados.contains(idVecino)) {  // Si no ha sido visitado
                    visitados.add(idVecino);
                    padres.put(idVecino, idActual);   // Registra que idActual es padre de idVecino
                    cola.offer(idVecino);             // Añade a la cola para explorar después
                }
            }
        }

        return new ArrayList<>();  // Sin camino encontrado
    }

    /**
     * Reconstruye el camino desde el origen hasta el destino
     * usando el mapa de padres. Retrocede desde destino hasta origen.
     * @param padres mapa de ID -> ID del padre
     * @param destino ID del nodo destino
     * @return Lista de IDs del camino (origen -> destino)
     */
    private List<Integer> reconstruirCamino(Map<Integer, Integer> padres, int destino) {
        List<Integer> camino = new ArrayList<>();
        int actual = destino;

        // Retrocede desde el destino hasta el origen siguiendo los padres
        while (actual != -1) {
            camino.add(0, actual);  // Añade al principio para mantener orden
            actual = padres.get(actual);
        }

        return camino;
    }

    /**
     * Imprime el camino más corto de forma legible en consola
     * @param camino lista de IDs del camino
     */
    public void imprimirCamino(List<Integer> camino) {
        if (camino.isEmpty()) {
            System.out.println("No hay camino entre A y B");
            return;
        }

        System.out.println("\n=== CAMINO MÁS CORTO ===");
        System.out.println("Longitud del camino: " + (camino.size() - 1) + " pasos");
        System.out.print("Camino: ");
        for (int i = 0; i < camino.size(); i++) {
            int id = camino.get(i);
            Nodo nodo = grafo.getNodo(id);
            if (i > 0) System.out.print(" -> ");
            System.out.print(nodo.getTipo() == ' ' ? "·" : nodo.getTipo());  // Punto o letra
        }
        System.out.println();
    }

    /**
     * Crea y imprime un mapa visual del laberinto con el camino marcado
     * Marca los nodos del camino con puntos ('·')
     * @param mapa el mapa original del laberinto
     * @param camino lista de IDs del camino a marcar
     */
    public void imprimirMapaConCamino(char[][] mapa, List<Integer> camino) {
        if (camino.isEmpty()) {
            return;
        }

        // Crear copia del mapa para no modificar el original
        char[][] mapaConCamino = new char[mapa.length][];
        for (int i = 0; i < mapa.length; i++) {
            mapaConCamino[i] = mapa[i].clone();
        }

        // Marcar el camino (excepto origen y destino que ya tienen símbolos A y B)
        for (int i = 1; i < camino.size() - 1; i++) {
            int id = camino.get(i);
            Nodo nodo = grafo.getNodo(id);
            mapaConCamino[nodo.getX()][nodo.getY()] = '·';  // Marca con punto
        }

        // Imprime el mapa con el camino
        System.out.println("\n=== MAPA CON CAMINO MARCADO ===");
        for (int i = 0; i < mapaConCamino.length; i++) {
            for (int j = 0; j < mapaConCamino[i].length; j++) {
                System.out.print(mapaConCamino[i][j]);
            }
            System.out.println();
        }
    }
}
