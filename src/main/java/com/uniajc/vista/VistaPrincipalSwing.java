package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;

/**
 * Ventana principal de la interfaz gráfica (capa <b>Vista</b>).
 *
 * <p>Es un {@link JFrame} (una ventana con barra de título) dividido en tres zonas con
 * {@link BorderLayout}: encabezado azul arriba, menú lateral a la izquierda y, en el
 * centro, el panel de la entidad elegida.</p>
 *
 * <p>Los cinco paneles viven dentro de un {@link CardLayout}, que funciona como un
 * mazo de cartas: todos están cargados, pero solo se ve el de arriba. Al elegir una
 * opción del menú se "saca" esa carta. Cada panel vuelve a consultar la base cuando
 * se muestra, así siempre ve los datos más recientes (incluidos los que agregaron
 * los compañeros en la base compartida).</p>
 *
 * <p>Cubre la sección "Integración" de la guía: opción en el menú, instancia creada en
 * el constructor y panel agregado al CardLayout.</p>
 */
public class VistaPrincipalSwing extends JFrame {

    /** Administrador de diseño que muestra un panel a la vez. */
    private final CardLayout tarjetas = new CardLayout();
    /** Panel central que contiene las cinco "cartas". */
    private final JPanel contenedor = new JPanel(tarjetas);
    /**
     * Paneles por nombre. {@link LinkedHashMap} conserva el orden de inserción, así el
     * menú sale en el mismo orden. {@code VistaCrudSwing<?>} significa "de cualquier entidad".
     */
    private final Map<String, VistaCrudSwing<?>> paneles = new LinkedHashMap<>();
    /** Botones del menú lateral, para resaltar el que está activo. */
    private final Map<String, JButton> botonesNavegacion = new LinkedHashMap<>();

    /**
     * Construye la ventana completa y muestra la pestaña de Estudiantes.
     * Cada panel recibe solo los controladores que necesita.
     *
     * @param estudiantes   controlador de estudiantes
     * @param docentes      controlador de docentes
     * @param materias      controlador de materias
     * @param grupos        controlador de grupos
     * @param inscripciones controlador de inscripciones
     */
    public VistaPrincipalSwing(ControladorEstudiante estudiantes,
                               ControladorDocente docentes,
                               ControladorMateria materias,
                               ControladorGrupo grupos,
                               ControladorInscripcionCurso inscripciones) {
        super("Sistema de Gestión Académica UNIAJC");
        EstiloSwing.configurarLookAndFeel();

        paneles.put("Estudiantes", new VistaEstudianteSwing(estudiantes));
        paneles.put("Docentes", new VistaDocenteSwing(docentes));
        paneles.put("Materias", new VistaMateriaSwing(materias));
        paneles.put("Grupos", new VistaGrupoSwing(grupos, materias, docentes));
        paneles.put("Inscripciones", new VistaInscripcionCursoSwing(inscripciones, estudiantes, grupos, materias));
        paneles.forEach((nombre, panel) -> contenedor.add(panel, nombre));

        setJMenuBar(crearMenu());
        setLayout(new BorderLayout());
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearBarraLateral(), BorderLayout.WEST);
        add(contenedor, BorderLayout.CENTER);

        // Al cerrar la ventana termina el programa (y el shutdown hook de Main cierra la conexión).
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1050, 640);
        setLocationRelativeTo(null);

        mostrar("Estudiantes");
    }

    /**
     * Cambia al panel indicado, resalta su botón en el menú lateral y recarga sus datos.
     *
     * @param nombre nombre del panel, por ejemplo "Grupos"
     */
    public void mostrar(String nombre) {
        tarjetas.show(contenedor, nombre);
        botonesNavegacion.forEach((clave, boton) ->
                boton.setBackground(clave.equals(nombre) ? EstiloSwing.PRIMARIO : EstiloSwing.TEXTO));
        paneles.get(nombre).cargarDatos();
    }

    /**
     * Crea la barra de menú superior: Archivo (Salir), Gestión (una opción por entidad)
     * y Ayuda (Acerca de). "Salir" simula el clic en la X de la ventana, para que se
     * cierre igual que siempre.
     */
    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        archivo.add(salir);

        JMenu gestion = new JMenu("Gestión");
        for (String nombre : paneles.keySet()) {
            JMenuItem item = new JMenuItem(nombre);
            item.addActionListener(e -> mostrar(nombre));
            gestion.add(item);
        }

        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acerca = new JMenuItem("Acerca de");
        acerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema de Gestión Académica UNIAJC\nPráctica MVC + JDBC (PostgreSQL / Neon)",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        ayuda.add(acerca);

        barra.add(archivo);
        barra.add(gestion);
        barra.add(ayuda);
        return barra;
    }

    /** Crea la franja azul superior con el nombre del sistema. */
    private Component crearEncabezado() {
        JLabel titulo = new JLabel("  Sistema de Gestión Académica UNIAJC");
        titulo.setOpaque(true);
        titulo.setBackground(EstiloSwing.PRIMARIO);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(EstiloSwing.fuente(Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return titulo;
    }

    /**
     * Crea el menú lateral oscuro con un botón por entidad, apilados en una columna con
     * {@link GridLayout} (0 filas = las que hagan falta, 1 columna).
     */
    private Component crearBarraLateral() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(EstiloSwing.TEXTO);
        barra.setPreferredSize(new Dimension(170, 0));

        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 2));
        botones.setOpaque(false);
        botones.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        for (String nombre : paneles.keySet()) {
            JButton boton = EstiloSwing.crearBoton(nombre, EstiloSwing.TEXTO);
            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            boton.setPreferredSize(new Dimension(170, 42));
            boton.addActionListener(e -> mostrar(nombre));
            botonesNavegacion.put(nombre, boton);
            botones.add(boton);
        }
        barra.add(botones, BorderLayout.NORTH);
        return barra;
    }
}
