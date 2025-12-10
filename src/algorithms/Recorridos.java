package algorithms;

import grafo.Grafo;
import models.Nodo;
import java.util.*;

/**
 * ============================================================
 * CLASE: Recorridos
 * ============================================================
 * Implementa los diferentes tipos de recorridos del grafo:
 * 
 * RECORRIDOS EN PROFUNDIDAD (DFS):
 *   - Preorden: visita nodo ANTES de sus adyacentes
 *   - Inorden: visita nodo EN MEDIO de sus adyacentes
 *   - Postorden: visita nodo DESPUÉS de sus adyacentes
 * 
 * RECORRIDO EN AMPLITUD (BFS):
 *   - Explora nivel a nivel usando cola
 * 
 * RECORRIDO HEURÍSTICO (Greedy):
 *   - Usa distancia Manhattan al destino como heurística
 *   - Se aproxima al destino sin garantizar óptimo
 * 
 * Complejidad de todos: O(V + E)
 * Espacio: O(V) para visitados y recursión
 */
public class Recorridos {
    // ===== ATRIBUTOS =====
    private Grafo grafo;          // Grafo sobre el que hacer recorridos
    private List<Integer> resultado;  // Lista de nodos visitados
    private Set<Integer> visitados;   // Conjunto de nodos ya visitados

    /**
     * Constructor
     * @param grafo el grafo a recorrer
     */
    public Recorridos(Grafo grafo) {
        this.grafo = grafo;
    }

    /**
     * Recorrido en profundidad - PREORDEN
     * Visita el nodo ANTES de explorar sus adyacentes
     * Orden: Nodo → Adyacentes
     * @param idInicio ID del nodo donde comienza el recorrido
     * @return Lista de IDs visitados en orden DFS preorden
     */
    public List<Integer> dfsPreorden(int idInicio) {
        resultado = new ArrayList<>();
        visitados = new HashSet<>();
        dfsPreordenHelper(idInicio);  // Comienza recorrido recursivo
        return resultado;
    }

    /**
     * Método privado recursivo para DFS preorden
     */
    private void dfsPreordenHelper(int id) {
        visitados.add(id);       // Marca como visitado
        resultado.add(id);       // PREORDEN: añade antes de explorar

        // Explora todos los adyacentes
        for (Integer vecino : grafo.getAdyacentes(id)) {
            if (!visitados.contains(vecino)) {
                dfsPreordenHelper(vecino);  // Recursión
            }
        }
    }

    /**
     * Recorrido en profundidad - INORDEN
     * Para grafos generales (no binarios), visita el nodo EN MEDIO
     * Divide adyacentes por la mitad y lo visita en el medio
     * @param idInicio ID del nodo donde comienza el recorrido
     * @return Lista de IDs visitados en orden DFS inorden
     */
    public List<Integer> dfsInorden(int idInicio) {
        resultado = new ArrayList<>();
        visitados = new HashSet<>();
        dfsInordenHelper(idInicio, 0);
        return resultado;
    }

    /**
     * Método privado recursivo para DFS inorden
     * Divide adyacentes en dos mitades: izquierda (mid) y derecha
     */
    private void dfsInordenHelper(int id, int profundidad) {
        if (visitados.contains(id)) return;  // Ya fue visitado
        
        visitados.add(id);
        List<Integer> adyacentes = grafo.getAdyacentes(id);
        
        int mid = adyacentes.size() / 2;  // Punto medio
        
        // INORDEN: izquierda → nodo → derecha
        // Primero explora la mitad izquierda
        for (int i = 0; i < mid; i++) {
            Integer vecino = adyacentes.get(i);
            if (!visitados.contains(vecino)) {
                dfsInordenHelper(vecino, profundidad + 1);
            }
        }
        
        resultado.add(id);  // INORDEN: visita el nodo en el medio
        
        // Luego explora la mitad derecha
        for (int i = mid; i < adyacentes.size(); i++) {
            Integer vecino = adyacentes.get(i);
            if (!visitados.contains(vecino)) {
                dfsInordenHelper(vecino, profundidad + 1);
            }
        }
    }

    /**
     * Recorrido en profundidad - POSTORDEN
     * Visita el nodo DESPUÉS de explorar sus adyacentes
     * Orden: Adyacentes → Nodo
     * @param idInicio ID del nodo donde comienza el recorrido
     * @return Lista de IDs visitados en orden DFS postorden
     */
    public List<Integer> dfsPostorden(int idInicio) {
        resultado = new ArrayList<>();
        visitados = new HashSet<>();
        dfsPostordenHelper(idInicio);
        return resultado;
    }

    /**
     * Método privado recursivo para DFS postorden
     */
    private void dfsPostordenHelper(int id) {
        visitados.add(id);

        // Primero explora todos los adyacentes
        for (Integer vecino : grafo.getAdyacentes(id)) {
            if (!visitados.contains(vecino)) {
                dfsPostordenHelper(vecino);
            }
        }

        resultado.add(id);  // POSTORDEN: añade después de explorar
    }

    /**
     * Recorrido en amplitud - BFS (Breadth-First Search)
     * Explora nivel a nivel usando una cola FIFO
     * @param idInicio ID del nodo donde comienza el recorrido
     * @return Lista de IDs visitados en orden BFS
     */
    public List<Integer> bfs(int idInicio) {
        List<Integer> resultado = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Queue<Integer> cola = new LinkedList<>();

        // Inicializa con el nodo de inicio
        cola.offer(idInicio);
        visitados.add(idInicio);

        // Procesa nodos nivel a nivel
        while (!cola.isEmpty()) {
            int id = cola.poll();  // Extrae el primer nodo
            resultado.add(id);

            // Añade todos los adyacentes no visitados a la cola
            for (Integer vecino : grafo.getAdyacentes(id)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.offer(vecino);
                }
            }
        }

        return resultado;
    }

    /**
     * Recorrido heurístico - Greedy Best-First Search
     * Se aproxima al destino usando distancia Manhattan como guía
     * Usa una cola de prioridad ordenada por distancia al destino
     * NOTA: No garantiza encontrar el camino más corto
     * @param idInicio ID del nodo donde comienza
     * @param idDestino ID del nodo destino
     * @return Lista de IDs visitados siguiendo heurística
     */
    public List<Integer> greedyBestFirstSearch(int idInicio, int idDestino) {
        List<Integer> resultado = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        // Cola de prioridad: ordena por distancia Manhattan al destino
        PriorityQueue<Integer> cola = new PriorityQueue<>((a, b) -> {
            int distA = calcularDistancia(a, idDestino);  // Distancia a destino
            int distB = calcularDistancia(b, idDestino);
            return Integer.compare(distA, distB);  // Menor distancia = mayor prioridad
        });

        cola.offer(idInicio);
        visitados.add(idInicio);

        // Procesa nodos por proximidad al destino
        while (!cola.isEmpty()) {
            int id = cola.poll();
            resultado.add(id);

            // Si llegamos al destino, se detiene
            if (id == idDestino) {
                break;
            }

            // Añade adyacentes ordenados por distancia al destino
            for (Integer vecino : grafo.getAdyacentes(id)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.offer(vecino);
                }
            }
        }

        return resultado;
    }

    /**
     * Calcula la distancia Manhattan entre dos nodos
     * Distancia Manhattan = |x1-x2| + |y1-y2|
     * @param id1 ID del primer nodo
     * @param id2 ID del segundo nodo
     * @return Distancia Manhattan entre los nodos
     */
    private int calcularDistancia(int id1, int id2) {
        Nodo nodo1 = grafo.getNodo(id1);
        Nodo nodo2 = grafo.getNodo(id2);
        return Math.abs(nodo1.getX() - nodo2.getX()) + Math.abs(nodo1.getY() - nodo2.getY());
    }

    /**
     * Imprime un recorrido de forma legible en consola
     * @param nombre nombre del recorrido (ej: "DFS PREORDEN")
     * @param recorrido lista de IDs del recorrido
     */
    public void imprimirRecorrido(String nombre, List<Integer> recorrido) {
        System.out.println("\n=== RECORRIDO " + nombre + " ===");
        System.out.println("Total de nodos visitados: " + recorrido.size());
        System.out.print("Recorrido: ");
        for (int i = 0; i < recorrido.size(); i++) {
            int id = recorrido.get(i);
            Nodo nodo = grafo.getNodo(id);
            if (i > 0) System.out.print(" -> ");
            System.out.print(nodo.getTipo() == ' ' ? "·" : nodo.getTipo());  // Punto o letra
        }
        System.out.println();
    }
}
