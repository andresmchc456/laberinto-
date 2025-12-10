package grafo;

import models.Nodo;
import java.util.*;

/**
 * ============================================================
 * CLASE: Grafo
 * ============================================================
 * Representa un grafo no dirigido basado en lista de adyacencia.
 * 
 * Un grafo está compuesto por:
 *   - Nodos: vértices del grafo (celdas transitables del laberinto)
 *   - Aristas: conexiones entre nodos adyacentes
 * 
 * Esta implementación almacena:
 *   - Un mapa de IDs a objetos Nodo
 *   - Una lista de adyacencia para cada nodo
 *   - Referencias especiales a los nodos A (inicio) y B (fin)
 * 
 * Nota: El grafo es NO DIRIGIDO, por lo que si hay una arista (u,v),
 *       también existe la arista (v,u).
 */
public class Grafo {
    // ===== ATRIBUTOS =====
    private Map<Integer, Nodo> nodos;                // Almacena todos los nodos por ID
    private Map<Integer, List<Integer>> listaAdyacencia;  // Lista de adyacencia
    private Nodo nodoA;  // Nodo de inicio (tipo 'A')
    private Nodo nodoB;  // Nodo de destino (tipo 'B')

    /**
     * Constructor: inicializa el grafo vacío
     */
    public Grafo() {
        this.nodos = new HashMap<>();
        this.listaAdyacencia = new HashMap<>();
    }

    /**
     * Agrega un nodo al grafo
     * Si el nodo es de tipo 'A' o 'B', se almacena como nodo especial
     * @param nodo el nodo a agregar
     */
    public void agregarNodo(Nodo nodo) {
        if (!nodos.containsKey(nodo.getId())) {  // Evita duplicados
            nodos.put(nodo.getId(), nodo);
            listaAdyacencia.put(nodo.getId(), new ArrayList<>());
            
            // Identifica puntos especiales
            if (nodo.getTipo() == 'A') {
                this.nodoA = nodo;  // Punto de inicio
            } else if (nodo.getTipo() == 'B') {
                this.nodoB = nodo;  // Punto de destino
            }
        }
    }

    /**
     * Agrega una arista entre dos nodos (grafo NO DIRIGIDO)
     * Crea conexión en ambas direcciones: (u,v) y (v,u)
     * @param idOrigen ID del primer nodo
     * @param idDestino ID del segundo nodo
     */
    public void agregarArista(int idOrigen, int idDestino) {
        if (nodos.containsKey(idOrigen) && nodos.containsKey(idDestino)) {
            // Agregar arista origen -> destino
            List<Integer> adyacentes = listaAdyacencia.get(idOrigen);
            if (!adyacentes.contains(idDestino)) {
                adyacentes.add(idDestino);
            }
            // Para grafo no dirigido, agregar también destino -> origen
            adyacentes = listaAdyacencia.get(idDestino);
            if (!adyacentes.contains(idOrigen)) {
                adyacentes.add(idOrigen);
            }
        }
    }

    // ===== GETTERS =====
    /**
     * Obtiene un nodo por su ID
     */
    public Nodo getNodo(int id) {
        return nodos.get(id);
    }

    /**
     * Obtiene la lista de adyacentes (vecinos) de un nodo
     */
    public List<Integer> getAdyacentes(int id) {
        return listaAdyacencia.getOrDefault(id, new ArrayList<>());
    }

    /**
     * Retorna todos los nodos del grafo
     */
    public Collection<Nodo> getTodosNodos() {
        return nodos.values();
    }

    /**
     * Obtiene el nodo de inicio (A)
     */
    public Nodo getNodoA() {
        return nodoA;
    }

    /**
     * Obtiene el nodo de destino (B)
     */
    public Nodo getNodoB() {
        return nodoB;
    }

    /**
     * Retorna la cantidad de nodos en el grafo
     */
    public int getCantidadNodos() {
        return nodos.size();
    }

    /**
     * Calcula y retorna la cantidad de aristas
     * Divide por 2 porque el grafo es no dirigido
     */
    public int getCantidadAristas() {
        int count = 0;
        for (List<Integer> adyacentes : listaAdyacencia.values()) {
            count += adyacentes.size();
        }
        return count / 2;  // Cada arista se cuenta dos veces
    }

    /**
     * Retorna una copia de la lista de adyacencia completa
     */
    public Map<Integer, List<Integer>> getListaAdyacencia() {
        return new HashMap<>(listaAdyacencia);
    }

    /**
     * Representación textual del grafo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo con ").append(nodos.size()).append(" nodos y ")
          .append(getCantidadAristas()).append(" aristas\n");
        for (Integer id : listaAdyacencia.keySet()) {
            Nodo nodo = nodos.get(id);
            sb.append("Nodo ").append(id).append(" (").append(nodo.getTipo()).append(") -> ")
              .append(listaAdyacencia.get(id)).append("\n");
        }
        return sb.toString();
    }
}
