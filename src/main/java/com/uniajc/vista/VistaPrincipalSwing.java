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
 * Ventana principal. Un menú y una barra lateral cambian entre los paneles
 * de cada entidad, que viven en un CardLayout. Cada panel consulta la base
 * de datos cuando se muestra, así siempre ve los datos más recientes.
 */
public class VistaPrincipalSwing extends JFrame {

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenedor = new JPanel(tarjetas);
    private final Map<String, VistaCrudSwing<?>> paneles = new LinkedHashMap<>();
    private final Map<String, JButton> botonesNavegacion = new LinkedHashMap<>();

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

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1050, 640);
        setLocationRelativeTo(null);

        mostrar("Estudiantes");
    }

    /** Cambia al panel indicado y recarga sus datos. */
    public void mostrar(String nombre) {
        tarjetas.show(contenedor, nombre);
        botonesNavegacion.forEach((clave, boton) ->
                boton.setBackground(clave.equals(nombre) ? EstiloSwing.PRIMARIO : EstiloSwing.TEXTO));
        paneles.get(nombre).cargarDatos();
    }

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

    private Component crearEncabezado() {
        JLabel titulo = new JLabel("  Sistema de Gestión Académica UNIAJC");
        titulo.setOpaque(true);
        titulo.setBackground(EstiloSwing.PRIMARIO);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(EstiloSwing.fuente(Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return titulo;
    }

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
