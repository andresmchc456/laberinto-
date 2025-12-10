// package com.laberinto;

import algorithms.CaminoMasCorto;  // Algoritmo para encontrar el camino más corto
import algorithms.Recorridos;      // Diferentes tipos de recorridos (DFS, BFS)
import grafo.Grafo;                 // Estructura del grafo del laberinto
import models.Nodo;                 // Nodos que componen el grafo
import utils.LaberintoParser;        // Parser para leer archivos de laberinto
import utils.MatrizesGrafo;          // Generador de matrices (adyacencia e incidencia)

import java.io.IOException;          // Para manejar excepciones de entrada/salida
import java.util.List;               // Listas de datos
import java.util.Scanner;            // Para leer entrada desde la consola

/**
 * ============================================================
 * CLASE PRINCIPAL: LaberintoMain
 * ============================================================
 * Esta es la clase principal de la aplicación de resolución de laberintos.
 * Proporciona una interfaz de línea de comandos (CLI) que permite:
 *   - Cargar archivos de laberinto (formato .txt)
 *   - Construir un grafo a partir del laberinto
 *   - Encontrar el camino más corto entre dos puntos (A y B) usando BFS
 *   - Ejecutar diferentes algoritmos de recorrido (DFS, BFS, Greedy)
 *   - Visualizar matrices del grafo (adyacencia e incidencia)
 * 
 * El programa utiliza un menú interactivo para que el usuario pueda
 * seleccionar qué operación desea realizar.
 */
public class LaberintoMain {
    // ===== ATRIBUTOS DE LA APLICACIÓN =====
    private LaberintoParser parser;          // Lee archivos de laberinto
    private Grafo grafo;                     // Grafo construido desde el laberinto
    private CaminoMasCorto caminoMasCorto;   // Busca el camino más corto (BFS)
    private Recorridos recorridos;           // Ejecuta DFS, BFS, Greedy
    private MatrizesGrafo matrices;          // Genera matrices del grafo

    /**
     * Método principal: punto de entrada de la aplicación
     * Crea una instancia de LaberintoMain e inicia el programa
     */
    public static void main(String[] args) {
        LaberintoMain app = new LaberintoMain();
        app.ejecutar();  // Inicia el ciclo principal
    }

    /**
     * Método principal de ejecución: ciclo de menú interactivo
     * Muestra opciones al usuario y procesa sus selecciones
     */
    private void ejecutar() {
        Scanner scanner = new Scanner(System.in);  // Lee entrada del usuario
        boolean salir = false;  // Controla si continuar o salir

        // ===== CICLO PRINCIPAL DEL PROGRAMA =====
        while (!salir) {
            mostrarMenu();  // Muestra las opciones disponibles
            System.out.print("\nSeleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    cargarArchivo(scanner);
                    break;
                case "2":
                    if (grafo != null) {
                        mostrarInfoGrafo();
                    } else {
                        System.out.println("Primero debe cargar un archivo de laberinto.");
                    }
                    break;
                case "3":
                    if (grafo != null) {
                        encontrarCaminoMasCorto();
                    } else {
                        System.out.println("Primero debe cargar un archivo de laberinto.");
                    }
                    break;
                case "4":
                    if (grafo != null) {
                        ejecutarRecorridos();
                    } else {
                        System.out.println("Primero debe cargar un archivo de laberinto.");
                    }
                    break;
                case "5":
                    if (grafo != null) {
                        mostrarMatrices();
                    } else {
                        System.out.println("Primero debe cargar un archivo de laberinto.");
                    }
                    break;
                case "6":
                    salir = true;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }

        scanner.close();
    }

    private void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       SISTEMA DE RESOLUCIÓN DE LABERINTOS");
        System.out.println("=".repeat(50));
        System.out.println("1. Cargar archivo de laberinto");
        System.out.println("2. Mostrar información del grafo");
        System.out.println("3. Encontrar camino más corto (A -> B)");
        System.out.println("4. Ejecutar recorridos del grafo");
        System.out.println("5. Mostrar matrices (adyacencia e incidencia)");
        System.out.println("6. Salir");
        System.out.println("=".repeat(50));
    }

    private void cargarArchivo(Scanner scanner) {
        System.out.print("Ingrese la ruta del archivo del laberinto: ");
        String ruta = scanner.nextLine().trim();

        try {
            parser = new LaberintoParser();
            parser.leerArchivo(ruta);
            grafo = parser.construirGrafo();
            caminoMasCorto = new CaminoMasCorto(grafo);
            recorridos = new Recorridos(grafo);
            matrices = new MatrizesGrafo(grafo);

            System.out.println("\n✓ Archivo cargado exitosamente.");
            parser.imprimirMapa();
            System.out.println("\nGrafo construido:");
            System.out.println("- Nodos: " + grafo.getCantidadNodos());
            System.out.println("- Aristas: " + grafo.getCantidadAristas());
            System.out.println("- Punto de inicio (A): " + grafo.getNodoA());
            System.out.println("- Punto de fin (B): " + grafo.getNodoB());

        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void mostrarInfoGrafo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("INFORMACIÓN DEL GRAFO");
        System.out.println("=".repeat(50));

        Nodo nodoA = grafo.getNodoA();
        Nodo nodoB = grafo.getNodoB();

        System.out.println("Cantidad de nodos: " + grafo.getCantidadNodos());
        System.out.println("Cantidad de aristas: " + grafo.getCantidadAristas());
        System.out.println("\nPunto de inicio (A): Posición (" + nodoA.getX() + ", " + nodoA.getY() + ")");
        System.out.println("Punto de fin (B): Posición (" + nodoB.getX() + ", " + nodoB.getY() + ")");

        parser.imprimirMapa();
    }

    private void encontrarCaminoMasCorto() {
        Nodo nodoA = grafo.getNodoA();
        Nodo nodoB = grafo.getNodoB();

        List<Integer> camino = caminoMasCorto.encontrarCaminoMasCorto(nodoA.getId(), nodoB.getId());

        caminoMasCorto.imprimirCamino(camino);
        caminoMasCorto.imprimirMapaConCamino(parser.getMapa(), camino);
    }

    private void ejecutarRecorridos() {
        Nodo nodoA = grafo.getNodoA();
        Nodo nodoB = grafo.getNodoB();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("EJECUTANDO RECORRIDOS DEL GRAFO");
        System.out.println("=".repeat(50));

        // DFS Preorden
        List<Integer> dfsPreorden = recorridos.dfsPreorden(nodoA.getId());
        recorridos.imprimirRecorrido("DFS - PREORDEN", dfsPreorden);

        // DFS Inorden
        List<Integer> dfsInorden = recorridos.dfsInorden(nodoA.getId());
        recorridos.imprimirRecorrido("DFS - INORDEN", dfsInorden);

        // DFS Postorden
        List<Integer> dfsPostorden = recorridos.dfsPostorden(nodoA.getId());
        recorridos.imprimirRecorrido("DFS - POSTORDEN", dfsPostorden);

        // BFS
        List<Integer> bfs = recorridos.bfs(nodoA.getId());
        recorridos.imprimirRecorrido("BFS (AMPLITUD)", bfs);

        // Greedy Best-First Search
        List<Integer> greedy = recorridos.greedyBestFirstSearch(nodoA.getId(), nodoB.getId());
        recorridos.imprimirRecorrido("GREEDY BEST-FIRST SEARCH (HEURÍSTICO)", greedy);
    }

    private void mostrarMatrices() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MATRICES DEL GRAFO");
        System.out.println("=".repeat(50));

        matrices.imprimirMatrizAdyacencia();
        matrices.imprimirMatrizIncidencia();
    }
}
