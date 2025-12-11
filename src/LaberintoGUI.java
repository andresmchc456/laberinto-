import algorithms.CaminoMasCorto;
import algorithms.Recorridos;
import grafo.Grafo;
import models.Nodo;
import utils.LaberintoParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.MatrizesGrafo;

/**
 * Interfaz gráfica sencilla para seleccionar un archivo de laberinto
 * y visualizar la animación de los algoritmos (camino más corto y recorridos).
 */
public class LaberintoGUI extends JFrame {
    private LaberintoParser parser;
    private Grafo grafo;
    private CaminoMasCorto caminoMasCorto;
    private Recorridos recorridos;

    private MazePanel mazePanel;
    private JLabel infoLabel;
    private JComboBox<String> algoritmoCombo;
    private JSlider speedSlider;
    private Timer timer;
    private List<Integer> sequence; // secuencia a animar
    private List<Integer> finalPath; // camino final (para CaminoMasCorto)
    private int stepIndex;
    private boolean isPaused = false; // Control de pausa
    private JButton pauseResumeBtn; // Botón de pausa/reanudación
    private JButton anteriorBtn; // Botón paso anterior
    private JButton siguienteBtn; // Botón paso siguiente

    public LaberintoGUI() {
        super("Laberinto - Visualizador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // asegurar disposición visible de los botones
        JButton abrirBtn = new JButton("Abrir archivo");
        abrirBtn.addActionListener(e -> abrirArchivo());
        controlPanel.add(abrirBtn);
        
        // Botón para mostrar matrices (se asegura que esté añadido)
        JButton matricesBtn = new JButton("Mostrar matrices");
        matricesBtn.addActionListener(e -> mostrarMatrices());
        controlPanel.add(matricesBtn);
        
        algoritmoCombo = new JComboBox<>(new String[]{
            "Camino más corto (A->B)",
            "DFS - Preorden",
            "DFS - Inorden",
            "DFS - Postorden",
            "BFS",
            "Greedy (A->B)"
        });
        controlPanel.add(algoritmoCombo);

        JButton ejecutarBtn = new JButton("Ejecutar");
        ejecutarBtn.addActionListener(e -> ejecutarAlgoritmo());
        controlPanel.add(ejecutarBtn);

        pauseResumeBtn = new JButton("Pausar");
        pauseResumeBtn.setEnabled(false);
        pauseResumeBtn.addActionListener(e -> pausarReanudarAnimacion());
        controlPanel.add(pauseResumeBtn);

        anteriorBtn = new JButton("◀ Anterior");
        anteriorBtn.setEnabled(false);
        anteriorBtn.addActionListener(e -> pasoPrevio());
        controlPanel.add(anteriorBtn);

        siguienteBtn = new JButton("Siguiente ▶");
        siguienteBtn.setEnabled(false);
        siguienteBtn.addActionListener(e -> pasoSiguiente());
        controlPanel.add(siguienteBtn);

        JButton detenerBtn = new JButton("Detener");
        detenerBtn.addActionListener(e -> detenerAnimacion());
        controlPanel.add(detenerBtn);

        JButton exitBtn = new JButton("Salir");
        exitBtn.addActionListener(e -> System.exit(0));
        controlPanel.add(exitBtn);
        speedSlider = new JSlider(50, 1000, 250);
        speedSlider.setToolTipText("Velocidad (ms)");
        speedSlider.addChangeListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.setDelay(speedSlider.getValue());
            }
        });
        controlPanel.add(new JLabel("Velocidad:"));
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.NORTH);

        mazePanel = new MazePanel();
        add(new JScrollPane(mazePanel), BorderLayout.CENTER);

        infoLabel = new JLabel("Cargue un archivo para comenzar");
        add(infoLabel, BorderLayout.SOUTH);

        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                parser = new LaberintoParser();
                parser.leerArchivo(file.getAbsolutePath());
                grafo = parser.construirGrafo();
                caminoMasCorto = new CaminoMasCorto(grafo);
                recorridos = new Recorridos(grafo);

                mazePanel.setMapa(parser.getMapa(), grafo);
                infoLabel.setText(String.format("Archivo: %s  •  Nodos: %d  Aristas: %d",
                        file.getName(), grafo.getCantidadNodos(), grafo.getCantidadAristas()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer archivo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarAlgoritmo() {
        if (grafo == null || parser == null) {
            JOptionPane.showMessageDialog(this, "Primero abra un archivo de laberinto.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String seleccionado = (String) algoritmoCombo.getSelectedItem();
        stepIndex = 0;
        finalPath = null;

        switch (seleccionado) {
            case "Camino más corto (A->B)":
                Nodo a = grafo.getNodoA();
                Nodo b = grafo.getNodoB();
                if (a == null || b == null) {
                    JOptionPane.showMessageDialog(this, "El grafo no tiene A o B.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                finalPath = caminoMasCorto.encontrarCaminoMasCorto(a.getId(), b.getId());
                // Si no se encuentra camino, avisar
                if (finalPath == null || finalPath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Camino no encontrado.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // Para este algoritmo animamos directamente el camino
                sequence = finalPath;
                break;
            case "DFS - Preorden":
                sequence = recorridos.dfsPreorden(grafo.getNodoA().getId());
                break;
            case "DFS - Inorden":
                sequence = recorridos.dfsInorden(grafo.getNodoA().getId());
                break;
            case "DFS - Postorden":
                sequence = recorridos.dfsPostorden(grafo.getNodoA().getId());
                break;
            case "BFS":
                sequence = recorridos.bfs(grafo.getNodoA().getId());
                break;
            case "Greedy (A->B)":
                sequence = recorridos.greedyBestFirstSearch(grafo.getNodoA().getId(), grafo.getNodoB().getId());
                break;
            default:
                sequence = null;
        }

        if (sequence == null || sequence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La ejecución no produjo una secuencia para animar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Si existe un nodo B en el grafo, comprobar si la secuencia lo alcanzó.
        Nodo nodoB = grafo.getNodoB();
        if (nodoB != null) {
            int idB = nodoB.getId();
            if (!sequence.contains(idB)) {
                // Mostrar mensaje solicitado y no iniciar animación
                JOptionPane.showMessageDialog(this, "camino no encotrado :3", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // Crear timer
        int delay = speedSlider.getValue();
        if (timer != null && timer.isRunning()) timer.stop();

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused && stepIndex < sequence.size()) {
                    int id = sequence.get(stepIndex);
                    mazePanel.markVisited(id);
                    stepIndex++;
                } else if (stepIndex >= sequence.size()) {
                    // Si tenemos camino final, marcarlo
                    if (finalPath != null && !finalPath.isEmpty()) {
                        mazePanel.setPath(finalPath);
                    }
                    timer.stop();
                    pauseResumeBtn.setEnabled(false);
                    anteriorBtn.setEnabled(false);
                    siguienteBtn.setEnabled(false);
                }
            }
        });
        timer.setDelay(speedSlider.getValue());
        
        // Habilitar botones de control
        isPaused = false;
        pauseResumeBtn.setText("Pausar");
        pauseResumeBtn.setEnabled(true);
        anteriorBtn.setEnabled(true);
        siguienteBtn.setEnabled(true);
        
        timer.start();
    }

    private void detenerAnimacion() {
        if (timer != null && timer.isRunning()) timer.stop();
        if (mazePanel != null) mazePanel.clearMarks();
        isPaused = false;
        pauseResumeBtn.setEnabled(false);
        anteriorBtn.setEnabled(false);
        siguienteBtn.setEnabled(false);
        pauseResumeBtn.setText("Pausar");
    }

    private void pausarReanudarAnimacion() {
        if (timer == null || sequence == null) return;

        if (isPaused) {
            // Reanudar
            isPaused = false;
            pauseResumeBtn.setText("Pausar");
            timer.start();
        } else {
            // Pausar
            isPaused = true;
            pauseResumeBtn.setText("Reanudar");
            timer.stop();
        }
    }

    private void pasoSiguiente() {
        if (sequence == null || stepIndex >= sequence.size()) return;

        if (timer != null && timer.isRunning()) {
            timer.stop();
            isPaused = true;
            pauseResumeBtn.setText("Reanudar");
        }

        if (stepIndex < sequence.size()) {
            int id = sequence.get(stepIndex);
            mazePanel.markVisited(id);
            stepIndex++;
        }

        if (stepIndex >= sequence.size() && finalPath != null && !finalPath.isEmpty()) {
            mazePanel.setPath(finalPath);
        }
    }

    private void pasoPrevio() {
        if (sequence == null || stepIndex == 0) return;

        if (timer != null && timer.isRunning()) {
            timer.stop();
            isPaused = true;
            pauseResumeBtn.setText("Reanudar");
        }

        stepIndex--;
        mazePanel.clearMarks();

        // Redibujar todos los pasos hasta stepIndex
        for (int i = 0; i < stepIndex; i++) {
            int id = sequence.get(i);
            mazePanel.markVisited(id);
        }
    }

    private void mostrarMatrices() {
        if (grafo == null) {
            JOptionPane.showMessageDialog(this, "Primero abra un archivo de laberinto.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MatrizesGrafo mg = new MatrizesGrafo(grafo);
        int[][] ady = mg.getMatrizAdyacencia();
        int[][] inc = mg.getMatrizIncidencia();
        Map<Integer, Integer> mapIdToIndex = mg.getMapaIdAIndice();

        // Invertir mapeo para mostrar índice -> nodoId
        int n = ady.length;
        int[] indexToId = new int[n];
        for (Map.Entry<Integer, Integer> e : mapIdToIndex.entrySet()) {
            int id = e.getKey();
            int idx = e.getValue();
            if (idx >= 0 && idx < n) indexToId[idx] = id;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Mapeo (índice -> nodoId):\n");
        for (int i = 0; i < indexToId.length; i++) {
            sb.append(String.format("%3d -> %d\n", i, indexToId[i]));
        }

        sb.append("\n=== MATRIZ DE ADYACENCIA ===\n");
        // Encabezado
        sb.append("    ");
        for (int j = 0; j < n; j++) {
            sb.append(String.format("%3d ", j));
        }
        sb.append('\n');

        for (int i = 0; i < n; i++) {
            sb.append(String.format("%3d ", i));
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%3d ", ady[i][j]));
            }
            sb.append('\n');
        }

        sb.append("\n=== MATRIZ DE INCIDENCIA ===\n");
        if (inc.length == 0 || inc[0].length == 0) {
            sb.append("(Sin aristas)\n");
        } else {
            int m = inc[0].length;
            sb.append("    ");
            for (int j = 0; j < m; j++) sb.append(String.format("%3d ", j));
            sb.append('\n');

            for (int i = 0; i < inc.length; i++) {
                sb.append(String.format("%3d ", i));
                for (int j = 0; j < m; j++) {
                    sb.append(String.format("%3d ", inc[i][j]));
                }
                sb.append('\n');
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scroll, "Matrices del grafo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LaberintoGUI gui = new LaberintoGUI();
            gui.setVisible(true);
        });
    }

    /**
     * Panel responsable de pintar el laberinto y destacar visitas/camino.
     */
    private static class MazePanel extends JPanel {
        private char[][] mapa;
        private Grafo grafo;
        private Set<Integer> visited = new HashSet<>();
        private Set<Integer> path = new HashSet<>();

        public MazePanel() {
            setBackground(Color.DARK_GRAY);
        }

        public void setMapa(char[][] mapa, Grafo grafo) {
            this.mapa = mapa;
            this.grafo = grafo;
            this.visited.clear();
            this.path.clear();
            revalidate();
            repaint();
        }

        public void markVisited(int id) {
            visited.add(id);
            repaint();
        }

        public void setPath(List<Integer> pathList) {
            this.path.clear();
            this.path.addAll(pathList);
            repaint();
        }

        public void clearMarks() {
            visited.clear();
            path.clear();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            if (mapa == null) return new Dimension(400, 400);
            int rows = mapa.length;
            int cols = mapa[0].length;
            int cell = Math.max(6, Math.min(30, 600 / Math.max(rows, cols)));
            return new Dimension(cols * cell, rows * cell);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mapa == null) return;

            int rows = mapa.length;
            int cols = mapa[0].length;
            int cell = Math.max(6, Math.min(30, 600 / Math.max(rows, cols)));

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = mapa[i][j];
                    int x = j * cell;
                    int y = i * cell;

                    // Fondo
                    if (c == '*') {
                        g.setColor(Color.BLACK);
                    } else if (c == 'A') {
                        g.setColor(Color.GREEN.darker());
                    } else if (c == 'B') {
                        g.setColor(Color.RED.darker());
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.fillRect(x, y, cell, cell);

                    // Líneas de rejilla
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x, y, cell, cell);
                }
            }

            // Dibujar visitas y camino
            if (grafo != null) {
                for (Integer id : visited) {
                    Nodo nodo = grafo.getNodo(id);
                    if (nodo == null) continue;
                    int px = nodo.getY() * cell;
                    int py = nodo.getX() * cell;
                    g.setColor(new Color(30, 144, 255, 200)); // azul semitransparente
                    g.fillOval(px + cell/4, py + cell/4, cell/2, cell/2);
                }

                for (Integer id : path) {
                    Nodo nodo = grafo.getNodo(id);
                    if (nodo == null) continue;
                    int px = nodo.getY() * cell;
                    int py = nodo.getX() * cell;
                    g.setColor(new Color(199, 21, 133, 220)); // magenta
                    g.fillRect(px + cell/6, py + cell/6, cell*2/3, cell*2/3);
                }
            }
        }
    }
}
