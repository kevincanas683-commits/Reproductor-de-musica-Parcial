public class Cancion {
    private String titulo;
    private String duracion;
    private String ano;
    private String genero;
    private String archivo;

    public Cancion(String titulo, String duracion, String ano, String genero, String archivo) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.ano = ano;
        this.genero = genero;
        this.archivo = archivo;
    }

    public String getTitulo() { return titulo; }
    public String getArchivo() { return archivo; }

    @Override
    public String toString() {
        return titulo + " (" + duracion + ")";
    }
}
