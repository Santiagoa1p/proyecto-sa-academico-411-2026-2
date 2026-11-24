package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.uniajc.dao.ErrorBaseDatosException;

/**
 * Panel CRUD reutilizable de Swing (capa <b>Vista</b>): búsqueda, tabla, botones y
 * diálogo de formulario.
 *
 * <p>Cumple el checklist "Capa Vista (Swing)" de la guía: extiende {@link JPanel},
 * tiene panel de búsqueda, {@link JTable} con modelo, botones CRUD, diálogo de
 * registro y edición, listeners de acción y el estilo UNIAJC.</p>
 *
 * <p><b>Por qué una clase base:</b> las cinco pantallas (estudiantes, docentes...) se
 * ven y se comportan igual; solo cambian las columnas, los campos y a qué controlador
 * se llama. En vez de copiar ~400 líneas cinco veces, esta clase hace todo lo común y
 * deja como {@code abstract} lo que cambia. Cada vista concreta
 * ({@code VistaEstudianteSwing}, etc.) hereda de aquí y llena esos huecos.</p>
 *
 * <p><b>Genéricos:</b> {@code <T>} es un "tipo comodín" que se reemplaza al heredar. En
 * {@code VistaEstudianteSwing extends VistaCrudSwing<Estudiante>}, cada {@code T} de
 * esta clase pasa a significar {@code Estudiante}.</p>
 *
 * @param <T> entidad que se gestiona (Estudiante, Docente, Materia, Grupo o InscripcionCurso)
 */
public abstract class VistaCrudSwing<T> extends JPanel {

    /** Nombre de la entidad en singular para los mensajes, por ejemplo "estudiante". */
    private final String entidad;
    /**
     * Modelo de la tabla: guarda los datos (filas y columnas). En Swing la
     * {@link JTable} solo se encarga de dibujar; los datos viven en el modelo.
     */
    private final DefaultTableModel modeloTabla;
    /** Componente visual de la tabla. */
    private final JTable tabla;
    /** Permite ordenar la tabla (clic en el encabezado) y filtrarla con la búsqueda. */
    private final TableRowSorter<DefaultTableModel> ordenador;
    /** Caja de texto del buscador. */
    private final JTextField textoBusqueda = new JTextField(25);
    /** Texto inferior izquierdo con el total de registros. */
    private final JLabel etiquetaEstado = new JLabel(" ");

    /**
     * Arma el panel completo: encabezado con búsqueda (arriba), tabla (centro) y
     * botones (abajo), usando {@link BorderLayout} para ubicar cada parte.
     *
     * @param titulo   texto del encabezado (ej. "Estudiantes")
     * @param entidad  nombre en singular para los mensajes (ej. "estudiante")
     * @param columnas encabezados de la tabla; la primera debe ser el ID
     */
    protected VistaCrudSwing(String titulo, String entidad, String[] columnas) {
        this.entidad = entidad;
        // Clase anónima: se crea un DefaultTableModel "personalizado" en el momento,
        // sobrescribiendo isCellEditable para que no se pueda escribir dentro de la tabla
        // (los cambios se hacen solo con el formulario).
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tabla = new JTable(modeloTabla);
        this.ordenador = new TableRowSorter<>(modeloTabla);

        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloSwing.SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(crearPanelSuperior(titulo), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------- abstractos
    // Lo que cada vista concreta debe implementar. Casi todos solo llaman a su controlador.

    /** Trae todos los registros (llamando al controlador). */
    protected abstract List<T> listar();

    /** Busca un registro por ID (llamando al controlador). */
    protected abstract Optional<T> buscar(int id);

    /**
     * Convierte un objeto en una fila de la tabla: un arreglo con un valor por columna,
     * en el mismo orden que los encabezados. El primero debe ser el ID.
     */
    protected abstract Object[] fila(T elemento);

    /** Crea un objeto vacío que se llenará al registrar. */
    protected abstract T nuevo();

    /**
     * Agrega los campos al formulario.
     *
     * @param formulario ayudante para agregar campos
     * @param actual     el registro a editar, o {@code null} si se está registrando uno nuevo
     */
    protected abstract void construirFormulario(Formulario formulario, T actual);

    /**
     * Copia lo que el usuario escribió en el formulario hacia el objeto.
     *
     * @param destino objeto donde se guardan los valores
     * @throws IllegalArgumentException si algo no se puede convertir (por ejemplo, un combo vacío)
     */
    protected abstract void leerFormulario(T destino);

    /** Envía un registro nuevo al controlador. */
    protected abstract void registrar(T elemento);

    /** Envía un registro modificado al controlador. */
    protected abstract void actualizar(T elemento);

    /** Pide al controlador eliminar el registro con ese ID. */
    protected abstract void eliminar(int id);

    /**
     * Se ejecuta antes de llenar la tabla. Por defecto no hace nada; las vistas de
     * grupos e inscripciones lo sobrescriben para refrescar los nombres relacionados
     * (materias, docentes, estudiantes) que muestran en sus columnas.
     */
    protected void antesDeCargar() {
    }

    // ---------------------------------------------------------------- construcción

    /**
     * Crea la franja superior: título a la izquierda y buscador a la derecha.
     *
     * <p>El buscador usa un {@link DocumentListener}, que avisa cada vez que cambia
     * el texto (al escribir o al borrar). Así la tabla se filtra mientras se escribe,
     * sin necesidad de un botón "Buscar".</p>
     */
    private JPanel crearPanelSuperior(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(EstiloSwing.fuente(Font.BOLD, 22));
        etiquetaTitulo.setForeground(EstiloSwing.TEXTO);
        panel.add(etiquetaTitulo, BorderLayout.WEST);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        busqueda.setOpaque(false);
        JLabel etiquetaBuscar = new JLabel("Buscar:");
        etiquetaBuscar.setFont(EstiloSwing.fuente(Font.BOLD, 13));
        etiquetaBuscar.setForeground(EstiloSwing.PRIMARIO);
        textoBusqueda.setPreferredSize(new Dimension(220, 30));
        textoBusqueda.setToolTipText("Filtra la tabla mientras escribes");
        textoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        JButton botonLimpiar = EstiloSwing.crearBoton("Limpiar", EstiloSwing.NEUTRO);
        botonLimpiar.setPreferredSize(new Dimension(90, 30));
        botonLimpiar.addActionListener(e -> textoBusqueda.setText(""));
        busqueda.add(etiquetaBuscar);
        busqueda.add(textoBusqueda);
        busqueda.add(botonLimpiar);
        panel.add(busqueda, BorderLayout.EAST);
        return panel;
    }

    /**
     * Configura la tabla (colores, selección de una sola fila, encabezado azul) y la
     * mete en un {@link JScrollPane} para que tenga barra de desplazamiento.
     * Un doble clic sobre una fila abre directamente el formulario de edición.
     */
    private JScrollPane crearPanelTabla() {
        tabla.setRowSorter(ordenador);
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setSelectionBackground(EstiloSwing.PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setBackground(EstiloSwing.PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    abrirFormulario(true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloSwing.PRIMARIO, 2));
        scroll.setPreferredSize(new Dimension(700, 400));
        return scroll;
    }

    /**
     * Crea la franja inferior: contador de registros a la izquierda y los cuatro
     * botones CRUD a la derecha. Cada botón recibe su acción con
     * {@code addActionListener} y una lambda ({@code e -> ...}) que se ejecuta al hacer clic.
     */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        etiquetaEstado.setForeground(EstiloSwing.TEXTO_SECUNDARIO);
        etiquetaEstado.setPreferredSize(new Dimension(180, 20));
        panel.add(etiquetaEstado, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        botones.setOpaque(false);
        JButton botonRegistrar = EstiloSwing.crearBoton("Registrar", EstiloSwing.EXITO);
        JButton botonEditar = EstiloSwing.crearBoton("Editar", EstiloSwing.PRIMARIO);
        JButton botonEliminar = EstiloSwing.crearBoton("Eliminar", EstiloSwing.ACENTO);
        JButton botonRecargar = EstiloSwing.crearBoton("Actualizar", EstiloSwing.NEUTRO);
        botonRegistrar.addActionListener(e -> abrirFormulario(false));
        botonEditar.addActionListener(e -> abrirFormulario(true));
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        botonRecargar.addActionListener(e -> cargarDatos());
        botones.add(botonRegistrar);
        botones.add(botonEditar);
        botones.add(botonEliminar);
        botones.add(botonRecargar);
        panel.add(botones, BorderLayout.EAST);
        return panel;
    }

    // ---------------------------------------------------------------- acciones

    /**
     * Vuelve a consultar la base de datos y llena la tabla desde cero.
     * Se llama al abrir la pestaña, al presionar "Actualizar" y después de cada cambio.
     */
    public void cargarDatos() {
        ejecutarConEspera(() -> {
            antesDeCargar();
            List<T> elementos = listar();
            modeloTabla.setRowCount(0);
            for (T elemento : elementos) {
                modeloTabla.addRow(fila(elemento));
            }
            etiquetaEstado.setText("Total: " + elementos.size() + (elementos.size() == 1 ? " registro" : " registros"));
        }, this);
    }

    /**
     * Muestra solo las filas que contienen el texto del buscador, en cualquier columna.
     *
     * <p>Filtra lo que ya está cargado en la tabla, sin consultar la base. {@code (?i)}
     * hace que no importen las mayúsculas, y {@code Pattern.quote} hace que símbolos
     * como {@code .} o {@code +} se busquen literalmente y no como parte de una
     * expresión regular.</p>
     */
    private void filtrar() {
        String texto = textoBusqueda.getText().trim();
        ordenador.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }

    /**
     * Devuelve el ID de la fila seleccionada, o avisa si no hay ninguna.
     *
     * <p>{@code convertRowIndexToModel} es necesario porque, si la tabla está ordenada o
     * filtrada, la fila 0 que se ve en pantalla puede no ser la fila 0 del modelo.</p>
     *
     * @return el ID seleccionado, o {@code null} si no hay selección. Es {@code Integer}
     *         y no {@code int} justamente para poder devolver {@code null}.
     */
    private Integer idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un " + entidad + " en la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Integer) modeloTabla.getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

    /**
     * Abre el diálogo para registrar o editar.
     *
     * <p>Al editar, primero vuelve a buscar el registro en la base (otro compañero pudo
     * haberlo borrado mientras tanto). El diálogo es <i>modal</i>
     * ({@code APPLICATION_MODAL}): mientras está abierto no se puede usar la ventana
     * principal.</p>
     *
     * @param edicion {@code true} para editar la fila seleccionada; {@code false} para registrar
     */
    private void abrirFormulario(boolean edicion) {
        T elemento;
        if (edicion) {
            Integer id = idSeleccionado();
            if (id == null) {
                return;
            }
            try {
                Optional<T> encontrado = buscar(id);
                if (encontrado.isEmpty()) {
                    mostrarError("El " + entidad + " ya no existe. Se recargará la tabla.");
                    cargarDatos();
                    return;
                }
                elemento = encontrado.get();
            } catch (RuntimeException ex) {
                mostrarError(ex.getMessage());
                return;
            }
        } else {
            elemento = nuevo();
        }

        Window ventana = SwingUtilities.getWindowAncestor(this);
        String tituloDialogo = (edicion ? "Editar " : "Registrar ") + entidad;
        JDialog dialogo = new JDialog(ventana, tituloDialogo, JDialog.ModalityType.APPLICATION_MODAL);

        Formulario formulario = new Formulario();
        try {
            construirFormulario(formulario, edicion ? elemento : null);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JPanel contenido = new JPanel(new BorderLayout(0, 15));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        JLabel encabezado = new JLabel(tituloDialogo.substring(0, 1).toUpperCase() + tituloDialogo.substring(1));
        encabezado.setFont(EstiloSwing.fuente(Font.BOLD, 18));
        encabezado.setForeground(EstiloSwing.PRIMARIO);
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(formulario.panel, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton botonGuardar = EstiloSwing.crearBoton("Guardar", EstiloSwing.EXITO);
        JButton botonCancelar = EstiloSwing.crearBoton("Cancelar", EstiloSwing.NEUTRO);
        botonCancelar.addActionListener(e -> dialogo.dispose());
        botonGuardar.addActionListener(e -> guardar(dialogo, elemento, edicion));
        botones.add(botonGuardar);
        botones.add(botonCancelar);
        contenido.add(botones, BorderLayout.SOUTH);

        dialogo.getRootPane().setDefaultButton(botonGuardar);
        dialogo.setContentPane(contenido);
        dialogo.pack();
        dialogo.setMinimumSize(new Dimension(540, dialogo.getHeight()));
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Acción del botón "Guardar" del diálogo.
     *
     * <p>Copia los datos del formulario al objeto y lo envía al controlador. Si el
     * servicio rechaza algo ({@code IllegalArgumentException}), muestra el mensaje y
     * <b>deja el diálogo abierto</b> para que el usuario corrija sin volver a escribir
     * todo. Si sale bien, cierra el diálogo, recarga la tabla y avisa.</p>
     *
     * <p>El bloque {@code finally} se ejecuta siempre (haya error o no) para devolver el
     * cursor normal.</p>
     */
    private void guardar(JDialog dialogo, T elemento, boolean edicion) {
        try {
            dialogo.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            leerFormulario(elemento);
            if (edicion) {
                actualizar(elemento);
            } else {
                registrar(elemento);
            }
            dialogo.dispose();
            cargarDatos();
            JOptionPane.showMessageDialog(this,
                    edicion ? "Cambios guardados." : "Registro creado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Revisa los datos", JOptionPane.WARNING_MESSAGE);
        } catch (ErrorBaseDatosException ex) {
            JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            dialogo.setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Acción del botón "Eliminar": pide confirmación y elimina la fila seleccionada. */
    private void eliminarSeleccionado() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el " + entidad + " con ID " + id + "?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        ejecutarConEspera(() -> {
            eliminar(id);
            cargarDatos();
        }, this);
    }

    /**
     * Ejecuta una acción mostrando el cursor de espera y convierte cualquier error en
     * una ventana de mensaje, para que la aplicación nunca se cierre por un error.
     *
     * <p>{@link Runnable} es un "pedazo de código guardado" sin parámetros ni resultado;
     * se le pasa como lambda ({@code () -> {...}}) y aquí se ejecuta con {@code run()}.</p>
     *
     * <p>Los errores de reglas de negocio se muestran como advertencia (amarillo) y
     * los demás (por ejemplo, de base de datos) como error (rojo).</p>
     *
     * @param accion código a ejecutar
     * @param origen componente al que se le cambia el cursor
     */
    private void ejecutarConEspera(Runnable accion, JComponent origen) {
        origen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            accion.run();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        } finally {
            origen.setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Muestra una ventana de error con el mensaje indicado. */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---------------------------------------------------------------- utilidades para subclases

    /**
     * Ajusta el ancho preferido de cada columna, en el mismo orden que los encabezados.
     * El primer valor (columna ID) se ignora porque esa columna ya tiene ancho fijo.
     *
     * <p>{@code int...} son <i>varargs</i>: permite pasar cualquier cantidad de números
     * separados por comas, y adentro se reciben como un arreglo.</p>
     *
     * @param anchos ancho en píxeles de cada columna
     */
    protected void anchosColumnas(int... anchos) {
        for (int i = 1; i < anchos.length && i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    /**
     * Devuelve el ID de la opción elegida en una lista desplegable.
     *
     * @param combo          lista desplegable de {@link Opcion}
     * @param mensajeSiVacio mensaje del error si no hay nada elegido (por ejemplo, si no hay materias)
     * @return el ID elegido
     * @throws IllegalArgumentException si no hay ninguna opción seleccionada
     */
    protected static int idElegido(JComboBox<Opcion> combo, String mensajeSiVacio) {
        Opcion opcion = (Opcion) combo.getSelectedItem();
        if (opcion == null) {
            throw new IllegalArgumentException(mensajeSiVacio);
        }
        return opcion.id();
    }

    /**
     * Ayudante para armar formularios de dos columnas (etiqueta | campo) sin repetir
     * código de diseño en cada vista.
     *
     * <p>Usa {@link GridBagLayout}, el administrador de diseño más flexible de Swing:
     * ubica cada componente en una cuadrícula (fila {@code gridy}, columna {@code gridx})
     * con márgenes ({@code insets}) y reglas de estiramiento ({@code weightx}, {@code fill}).</p>
     *
     * <p>Cada método agrega una fila y devuelve el campo creado, para que la vista
     * guarde la referencia y lea su valor al guardar.</p>
     */
    protected static final class Formulario {
        /** Panel que contiene todas las filas del formulario. */
        private final JPanel panel = new JPanel(new GridBagLayout());
        /** Número de la siguiente fila libre. */
        private int filaActual;

        /** Constructor privado: solo {@link VistaCrudSwing} crea formularios. */
        private Formulario() {
            panel.setOpaque(false);
        }

        /** Agrega una fila con la etiqueta en la columna 0 y el campo en la columna 1. */
        private void agregar(String etiqueta, JComponent campo) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 0, 6, 12);
            gbc.gridy = filaActual++;
            gbc.anchor = GridBagConstraints.LINE_START;
            JLabel label = new JLabel(etiqueta + ":");
            label.setFont(EstiloSwing.fuente(Font.BOLD, 13));
            label.setForeground(EstiloSwing.TEXTO);
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 0, 6, 0);
            campo.setPreferredSize(new Dimension(360, 30));
            panel.add(campo, gbc);
        }

        /**
         * Agrega una caja de texto.
         *
         * @param etiqueta texto de la etiqueta
         * @param valor    valor inicial ({@code null} para vacío)
         * @return la caja creada
         */
        public JTextField texto(String etiqueta, String valor) {
            JTextField campo = new JTextField(valor == null ? "" : valor, 22);
            agregar(etiqueta, campo);
            return campo;
        }

        /**
         * Agrega un selector numérico ({@link JSpinner}) con flechas para subir y bajar,
         * limitado entre un mínimo y un máximo.
         *
         * @param etiqueta texto de la etiqueta
         * @param valor    valor inicial
         * @param minimo   valor mínimo permitido
         * @param maximo   valor máximo permitido
         * @return el selector creado
         */
        public JSpinner numero(String etiqueta, int valor, int minimo, int maximo) {
            JSpinner campo = new JSpinner(new SpinnerNumberModel(valor, minimo, maximo, 1));
            agregar(etiqueta, campo);
            return campo;
        }

        /**
         * Agrega una lista desplegable. Es un <i>método genérico</i> ({@code <E>}): sirve
         * para listas de cualquier tipo, por ejemplo {@link Opcion} o {@code String}.
         *
         * @param etiqueta  texto de la etiqueta
         * @param opciones  elementos de la lista
         * @param seleccion elemento que aparece elegido al abrir ({@code null} para el primero)
         * @param <E>       tipo de los elementos
         * @return la lista creada
         */
        public <E> JComboBox<E> combo(String etiqueta, List<E> opciones, E seleccion) {
            JComboBox<E> campo = new JComboBox<>();
            opciones.forEach(campo::addItem);
            if (seleccion != null) {
                campo.setSelectedItem(seleccion);
            }
            agregar(etiqueta, campo);
            return campo;
        }

        /**
         * Agrega un texto de ayuda en gris e itálica que ocupa las dos columnas.
         *
         * @param texto texto de ayuda
         */
        public void nota(String texto) {
            JLabel ayuda = new JLabel(texto);
            ayuda.setForeground(EstiloSwing.TEXTO_SECUNDARIO);
            ayuda.setFont(EstiloSwing.fuente(Font.ITALIC, 12));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = filaActual++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(ayuda, gbc);
        }
    }
}
