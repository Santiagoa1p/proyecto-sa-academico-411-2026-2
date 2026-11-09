package com.uniajc.vista;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.dao.DataAccessException;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.modelo.Materia;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VistaPrincipalSwing extends JFrame {
    public VistaPrincipalSwing(
            ControladorEstudiante estudiantes,
            ControladorDocente docentes,
            ControladorMateria materias,
            ControladorGrupo grupos,
            ControladorInscripcionCurso inscripciones) {
        super("Sistema de Gestión Académica UNIAJC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Estudiantes", new PanelCrud<>(new Adaptador<Estudiante>() {
            public String nombre() { return "estudiante"; }
            public String[] columnas() { return new String[]{"ID", "Nombre", "Apellido", "Email"}; }
            public String[] campos() { return new String[]{"Nombre", "Apellido", "Email"}; }
            public List<Estudiante> listar() { return estudiantes.listar(); }
            public Optional<Estudiante> buscar(int id) { return estudiantes.buscar(id); }
            public int id(Estudiante e) { return e.getIdEstudiante(); }
            public Object[] fila(Estudiante e) {
                return new Object[]{e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()};
            }
            public String[] valores(Estudiante e) {
                return new String[]{e.getNombre(), e.getApellido(), e.getEmail()};
            }
            public Estudiante crear(int id, String[] v) { return new Estudiante(id, v[0], v[1], v[2]); }
            public void registrar(Estudiante e) { estudiantes.registrar(e); }
            public void actualizar(Estudiante e, String[] v) {
                e.setNombre(v[0]); e.setApellido(v[1]); e.setEmail(v[2]); estudiantes.actualizar(e);
            }
            public void eliminar(int id) { estudiantes.eliminar(id); }
        }));
        pestañas.addTab("Docentes", new PanelCrud<>(new Adaptador<Docente>() {
            public String nombre() { return "docente"; }
            public String[] columnas() { return new String[]{"ID", "Nombre", "Especialidad", "Email"}; }
            public String[] campos() { return new String[]{"Nombre", "Especialidad", "Email"}; }
            public List<Docente> listar() { return docentes.listar(); }
            public Optional<Docente> buscar(int id) { return docentes.buscar(id); }
            public int id(Docente d) { return d.getIdDocente(); }
            public Object[] fila(Docente d) {
                return new Object[]{d.getIdDocente(), d.getNombre(), d.getEspecialidad(), d.getEmail()};
            }
            public String[] valores(Docente d) {
                return new String[]{d.getNombre(), d.getEspecialidad(), d.getEmail()};
            }
            public Docente crear(int id, String[] v) { return new Docente(id, v[0], v[1], v[2]); }
            public void registrar(Docente d) { docentes.registrar(d); }
            public void actualizar(Docente d, String[] v) {
                d.setNombre(v[0]); d.setEspecialidad(v[1]); d.setEmail(v[2]); docentes.actualizar(d);
            }
            public void eliminar(int id) { docentes.eliminar(id); }
        }));
        pestañas.addTab("Materias", new PanelCrud<>(new Adaptador<Materia>() {
            public String nombre() { return "materia"; }
            public String[] columnas() { return new String[]{"ID", "Nombre", "Créditos"}; }
            public String[] campos() { return new String[]{"Nombre", "Créditos"}; }
            public List<Materia> listar() { return materias.listar(); }
            public Optional<Materia> buscar(int id) { return materias.buscar(id); }
            public int id(Materia m) { return m.getIdMateria(); }
            public Object[] fila(Materia m) {
                return new Object[]{m.getIdMateria(), m.getNombreMateria(), m.getCreditos()};
            }
            public String[] valores(Materia m) {
                return new String[]{m.getNombreMateria(), Integer.toString(m.getCreditos())};
            }
            public Materia crear(int id, String[] v) {
                return new Materia(id, v[0], entero(v[1], "Los créditos"));
            }
            public void registrar(Materia m) { materias.registrar(m); }
            public void actualizar(Materia m, String[] v) {
                m.setNombreMateria(v[0]); m.setCreditos(entero(v[1], "Los créditos")); materias.actualizar(m);
            }
            public void eliminar(int id) { materias.eliminar(id); }
        }));
        pestañas.addTab("Grupos", new PanelCrud<>(new Adaptador<Grupo>() {
            public String nombre() { return "grupo"; }
            public String[] columnas() { return new String[]{"ID", "ID materia", "ID docente", "Aula", "Horario"}; }
            public String[] campos() { return new String[]{"ID materia", "ID docente", "Aula", "Horario"}; }
            public List<Grupo> listar() { return grupos.listar(); }
            public Optional<Grupo> buscar(int id) { return grupos.buscar(id); }
            public int id(Grupo g) { return g.getIdGrupo(); }
            public Object[] fila(Grupo g) {
                return new Object[]{g.getIdGrupo(), g.getIdMateria(), g.getIdDocente(), g.getAula(), g.getHorario()};
            }
            public String[] valores(Grupo g) {
                return new String[]{Integer.toString(g.getIdMateria()), Integer.toString(g.getIdDocente()),
                        g.getAula(), g.getHorario()};
            }
            public Grupo crear(int id, String[] v) {
                return new Grupo(id, entero(v[0], "El ID de materia"), entero(v[1], "El ID de docente"), v[2], v[3]);
            }
            public void registrar(Grupo g) { grupos.registrar(g); }
            public void actualizar(Grupo g, String[] v) {
                g.setIdMateria(entero(v[0], "El ID de materia"));
                g.setIdDocente(entero(v[1], "El ID de docente"));
                g.setAula(v[2]); g.setHorario(v[3]); grupos.actualizar(g);
            }
            public void eliminar(int id) { grupos.eliminar(id); }
        }));
        pestañas.addTab("Inscripciones", new PanelCrud<>(new Adaptador<InscripcionCurso>() {
            public String nombre() { return "inscripción"; }
            public String[] columnas() {
                return new String[]{"ID", "ID estudiante", "ID grupo", "Nota final", "Estado"};
            }
            public String[] campos() {
                return new String[]{"ID estudiante", "ID grupo", "Nota final (vacía si pendiente)", "Estado"};
            }
            public List<InscripcionCurso> listar() { return inscripciones.listar(); }
            public Optional<InscripcionCurso> buscar(int id) { return inscripciones.buscar(id); }
            public int id(InscripcionCurso i) { return i.getIdInscripcion(); }
            public Object[] fila(InscripcionCurso i) {
                return new Object[]{i.getIdInscripcion(), i.getIdEstudiante(), i.getIdGrupo(),
                        i.getNotaFinal() == null ? "Pendiente" : i.getNotaFinal(), i.getEstado()};
            }
            public String[] valores(InscripcionCurso i) {
                return new String[]{Integer.toString(i.getIdEstudiante()), Integer.toString(i.getIdGrupo()),
                        i.getNotaFinal() == null ? "" : i.getNotaFinal().toString(), i.getEstado()};
            }
            public InscripcionCurso crear(int id, String[] v) {
                return new InscripcionCurso(id, entero(v[0], "El ID de estudiante"),
                        entero(v[1], "El ID de grupo"), nota(v[2]), v[3]);
            }
            public void registrar(InscripcionCurso i) { inscripciones.registrar(i); }
            public void actualizar(InscripcionCurso i, String[] v) {
                i.setIdEstudiante(entero(v[0], "El ID de estudiante"));
                i.setIdGrupo(entero(v[1], "El ID de grupo"));
                i.setNotaFinal(nota(v[2])); i.setEstado(v[3]); inscripciones.actualizar(i);
            }
            public void eliminar(int id) { inscripciones.eliminar(id); }
        }));

        add(pestañas, BorderLayout.CENTER);
        pestañas.addChangeListener(evento -> cargarPestanaSeleccionada(pestañas));
        SwingUtilities.invokeLater(() -> cargarPestanaSeleccionada(pestañas));
    }

    private static void cargarPestanaSeleccionada(JTabbedPane pestañas) {
        Component seleccionada = pestañas.getSelectedComponent();
        if (seleccionada instanceof PanelCrud<?> panel) {
            panel.cargarSiNecesario();
        }
    }

    private static int entero(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    private static Float nota(String valor) {
        if (valor.isBlank()) {
            return null;
        }
        try {
            return Float.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La nota final debe ser un número válido.");
        }
    }

    private interface Adaptador<T> {
        String nombre();
        String[] columnas();
        String[] campos();
        List<T> listar();
        Optional<T> buscar(int id);
        int id(T entidad);
        Object[] fila(T entidad);
        String[] valores(T entidad);
        T crear(int id, String[] valores);
        void registrar(T entidad);
        void actualizar(T entidad, String[] valores);
        void eliminar(int id);
    }

    private static class PanelCrud<T> extends JPanel {
        private final Adaptador<T> adaptador;
        private final DefaultTableModel modelo;
        private final JTable tabla;
        private final JTextField textoBusqueda;
        private boolean cargado;

        private PanelCrud(Adaptador<T> adaptador) {
            super(new BorderLayout(8, 8));
            this.adaptador = adaptador;
            this.modelo = new DefaultTableModel(adaptador.columnas(), 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            this.tabla = new JTable(modelo);
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabla.setRowHeight(24);

            JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
            busqueda.add(new JLabel("Buscar:"));
            textoBusqueda = new JTextField(24);
            busqueda.add(textoBusqueda);
            agregarBoton(busqueda, "Buscar", this::cargar);
            add(busqueda, BorderLayout.NORTH);

            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            add(scroll, BorderLayout.CENTER);

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
            agregarBoton(botones, "Registrar", this::registrar);
            agregarBoton(botones, "Editar", this::editar);
            agregarBoton(botones, "Eliminar", this::eliminar);
            agregarBoton(botones, "Actualizar lista", this::cargar);
            add(botones, BorderLayout.SOUTH);
        }

        private void cargarSiNecesario() {
            if (!cargado) {
                ejecutar(this::cargar);
            }
        }

        private void agregarBoton(JPanel panel, String texto, Runnable accion) {
            JButton boton = new JButton(texto);
            boton.addActionListener(evento -> ejecutar(accion));
            panel.add(boton);
        }

        private void cargar() {
            List<T> entidades = adaptador.listar();
            modelo.setRowCount(0);
            String busqueda = textoBusqueda.getText().trim().toLowerCase(Locale.ROOT);
            entidades.stream()
                    .map(adaptador::fila)
                    .filter(fila -> busqueda.isEmpty() || coincide(fila, busqueda))
                    .forEach(modelo::addRow);
            cargado = true;
        }

        private boolean coincide(Object[] fila, String busqueda) {
            for (Object valor : fila) {
                if (valor != null && valor.toString().toLowerCase(Locale.ROOT).contains(busqueda)) {
                    return true;
                }
            }
            return false;
        }

        private void registrar() {
            String[] valores = mostrarFormulario(null);
            if (valores == null) {
                return;
            }
            adaptador.registrar(adaptador.crear(0, valores));
            cargar();
            JOptionPane.showMessageDialog(this, adaptador.nombre() + " registrado correctamente.");
        }

        private void editar() {
            Integer id = idSeleccionado();
            if (id == null) {
                return;
            }
            T entidad = adaptador.buscar(id)
                    .orElseThrow(() -> new IllegalArgumentException("El registro seleccionado ya no existe."));
            String[] valores = mostrarFormulario(entidad);
            if (valores == null) {
                return;
            }
            adaptador.actualizar(entidad, valores);
            cargar();
            JOptionPane.showMessageDialog(this, adaptador.nombre() + " actualizado correctamente.");
        }

        private void eliminar() {
            Integer id = idSeleccionado();
            if (id == null) {
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar este registro de " + adaptador.nombre() + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            adaptador.eliminar(id);
            cargar();
            JOptionPane.showMessageDialog(this, adaptador.nombre() + " eliminado correctamente.");
        }

        private Integer idSeleccionado() {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un registro de la tabla.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return ((Number) modelo.getValueAt(fila, 0)).intValue();
        }

        private String[] mostrarFormulario(T entidad) {
            String[] nombres = adaptador.campos();
            String[] iniciales = entidad == null
                    ? new String[nombres.length]
                    : adaptador.valores(entidad);
            JTextField[] campos = new JTextField[nombres.length];
            JPanel formulario = new JPanel(new GridLayout(nombres.length, 2, 8, 8));
            for (int i = 0; i < nombres.length; i++) {
                formulario.add(new JLabel(nombres[i] + ":"));
                campos[i] = new JTextField(iniciales[i] == null ? "" : iniciales[i], 20);
                formulario.add(campos[i]);
            }
            int resultado = JOptionPane.showConfirmDialog(this, formulario,
                    (entidad == null ? "Registrar " : "Editar ") + adaptador.nombre(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resultado != JOptionPane.OK_OPTION) {
                return null;
            }
            String[] valores = new String[campos.length];
            for (int i = 0; i < campos.length; i++) {
                valores[i] = campos[i].getText().trim();
            }
            return valores;
        }

        private void ejecutar(Runnable operacion) {
            try {
                operacion.run();
            } catch (IllegalArgumentException | DataAccessException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
