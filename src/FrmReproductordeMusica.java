import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class FrmReproductordeMusica extends JFrame {

    private JTree arbol;
    private JLabel lblFoto;
    private JButton btnPlayPause;
    private Clip clip;
    private boolean reproduciendo = false;

    private List<Artista> artistas = new ArrayList<>();

    public FrmReproductordeMusica() {
        setTitle("Mi música");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Árbol
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Artistas");
        arbol = new JTree(raiz);
        JScrollPane scroll = new JScrollPane(arbol);
        scroll.setPreferredSize(new Dimension(250, 500));
        add(scroll, BorderLayout.WEST);

        // Panel derecho
        JPanel panelDerecho = new JPanel(new BorderLayout());
        lblFoto = new JLabel("Selecciona un artista o canción", SwingConstants.CENTER);
        panelDerecho.add(lblFoto, BorderLayout.CENTER);

        btnPlayPause = new JButton("Reproducir");
        btnPlayPause.setEnabled(false);
        panelDerecho.add(btnPlayPause, BorderLayout.SOUTH);
        add(panelDerecho, BorderLayout.CENTER);

        String ruta = System.getProperty("user.dir") + "/src/recursos/Musiteca.json";
        cargarDatosJSON(ruta);
        llenarArbol(raiz);
        configurarEventos();

        setVisible(true);
    }

    private void cargarDatosJSON(String ruta) {
    try {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "No se encontró el archivo JSON en:\n" + archivo.getAbsolutePath());
            return;
        }

        System.out.println("Leyendo JSON desde: " + archivo.getAbsolutePath());

        try (JsonReader reader = Json.createReader(new FileReader(archivo))) {
            JsonObject root = reader.readObject();
            if (root == null) {
                JOptionPane.showMessageDialog(this, "El contenido del JSON es nulo o está vacío.");
                return;
            }

            JsonArray artistasArray = root.getJsonArray("artistas");
            if (artistasArray == null) {
                JOptionPane.showMessageDialog(this, "El JSON no contiene el arreglo 'artistas'.");
                return;
            }

            for (JsonObject a : artistasArray.getValuesAs(JsonObject.class)) {
                Artista artista = new Artista(
                        a.getString("nombre"),
                        a.getString("tipo"),
                        a.getString("pais"),
                        a.getString("imagen")
                );

                JsonArray cancionesArray = a.getJsonArray("canciones");
                if (cancionesArray != null) {
                    for (JsonObject c : cancionesArray.getValuesAs(JsonObject.class)) {
                        Cancion cancion = new Cancion(
                                c.getString("titulo"),
                                c.getString("duracion"),
                                c.getString("ano"),
                                c.getString("genero"),
                                c.getString("archivo")
                        );
                        artista.agregarCancion(cancion);
                    }
                }

                artistas.add(artista);
            }

            System.out.println("JSON cargado correctamente con " + artistas.size() + " artistas.");

        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al leer JSON: " + e.getClass().getName() + " -> " + e.getMessage());
    }
}

    private void llenarArbol(DefaultMutableTreeNode raiz) {
        for (Artista artista : artistas) {
            DefaultMutableTreeNode nodoArtista = new DefaultMutableTreeNode(artista);
            for (Cancion c : artista.getCanciones()) {
                nodoArtista.add(new DefaultMutableTreeNode(c));
            }
            raiz.add(nodoArtista);
        }
        ((DefaultTreeModel) arbol.getModel()).reload();
        arbol.expandRow(0);
    }

    private void configurarEventos() {
        arbol.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
                if (nodo == null)
                    return;

                Object obj = nodo.getUserObject();

                if (obj instanceof Artista) {
                    mostrarImagen((Artista) obj);
                    btnPlayPause.setEnabled(false);
                } else if (obj instanceof Cancion) {
                    reproducirCancion((Cancion) obj);
                    btnPlayPause.setEnabled(true);
                }
            }
        });

        btnPlayPause.addActionListener(e -> {
            if (clip == null)
                return;

            if (!reproduciendo) {
                clip.start();
                btnPlayPause.setText("Pausar");
                reproduciendo = true;
            } else {
                clip.stop();
                btnPlayPause.setText("Reanudar");
                reproduciendo = false;
            }
        });
    }

    private void mostrarImagen(Artista artista) {
        ImageIcon icon = new ImageIcon(artista.getImagen());
        Image img = icon.getImage().getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(img));
        lblFoto.setText("");
    }

    private void reproducirCancion(Cancion cancion) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            File file = new File(cancion.getArchivo());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            btnPlayPause.setText("Pausar");
            reproduciendo = true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reproducir: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmReproductordeMusica());
    }
}
