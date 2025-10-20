import java.util.ArrayList;
import java.util.List;

public class Artista {
    private String nombre;
    private String tipo;
    private String pais;
    private String imagen;
    private List<Cancion> canciones;

    public Artista(String nombre, String tipo, String pais, String imagen) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.pais = pais;
        this.imagen = imagen;
        this.canciones = new ArrayList<>();
    }

    public void agregarCancion(Cancion c) {
        canciones.add(c);
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getPais() { return pais; }
    public String getImagen() { return imagen; }
    public List<Cancion> getCanciones() { return canciones; }

    @Override
    public String toString() {
        return nombre;
    }
}
