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
 * Panel CRUD reutilizable: búsqueda, tabla, botones y diálogo de formulario.
 * Cada vista Swing de entidad hereda de aquí y solo define sus columnas,
 * sus campos y qué método del controlador llamar.
 *
 * @param <T> entidad que se gestiona
 */
public abstract class VistaCrudSwing<T> extends JPanel {

    private final String entidad;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final TableRowSorter<DefaultTableModel> ordenador;
    private final JTextField textoBusqueda = new JTextField(25);
    private final JLabel etiquetaEstado = new JLabel(" ");

    /**
     * @param titulo   texto del encabezado (ej. "Estudiantes")
     * @param entidad  nombre en singular para los mensajes (ej. "estudiante")
     * @param columnas encabezados de la tabla; la primera debe ser el ID
     */
    protected VistaCrudSwing(String titulo, String entidad, String[] columnas) {
        this.entidad = entidad;
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

    protected abstract List<T> listar();

    protected abstract Optional<T> buscar(int id);

    protected abstract Object[] fila(T elemento);

    protected abstract T nuevo();

    /** Agrega los campos al formulario; {@code actual} es null al registrar. */
    protected abstract void construirFormulario(Formulario formulario, T actual);

    /** Copia los valores del formulario a la entidad. Lanza IllegalArgumentException si algo no es válido. */
    protected abstract void leerFormulario(T destino);

    protected abstract void registrar(T elemento);

    protected abstract void actualizar(T elemento);

    protected abstract void eliminar(int id);

    /** Se ejecuta antes de llenar la tabla (útil para refrescar nombres relacionados). */
    protected void antesDeCargar() {
    }

    // ---------------------------------------------------------------- construcción

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

    /** Vuelve a consultar la base de datos y llena la tabla. */
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

    private void filtrar() {
        String texto = textoBusqueda.getText().trim();
        ordenador.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }

    private Integer idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un " + entidad + " en la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Integer) modeloTabla.getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

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

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ---------------------------------------------------------------- utilidades para subclases

    /** Ajusta el ancho preferido de cada columna (en el mismo orden que los encabezados). */
    protected void anchosColumnas(int... anchos) {
        for (int i = 1; i < anchos.length && i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    /** Devuelve el ID de la opción elegida o lanza un mensaje si no hay ninguna. */
    protected static int idElegido(JComboBox<Opcion> combo, String mensajeSiVacio) {
        Opcion opcion = (Opcion) combo.getSelectedItem();
        if (opcion == null) {
            throw new IllegalArgumentException(mensajeSiVacio);
        }
        return opcion.id();
    }

    /** Constructor de formularios en dos columnas (etiqueta | campo). */
    protected static final class Formulario {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private int filaActual;

        private Formulario() {
            panel.setOpaque(false);
        }

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

        public JTextField texto(String etiqueta, String valor) {
            JTextField campo = new JTextField(valor == null ? "" : valor, 22);
            agregar(etiqueta, campo);
            return campo;
        }

        public JSpinner numero(String etiqueta, int valor, int minimo, int maximo) {
            JSpinner campo = new JSpinner(new SpinnerNumberModel(valor, minimo, maximo, 1));
            agregar(etiqueta, campo);
            return campo;
        }

        public <E> JComboBox<E> combo(String etiqueta, List<E> opciones, E seleccion) {
            JComboBox<E> campo = new JComboBox<>();
            opciones.forEach(campo::addItem);
            if (seleccion != null) {
                campo.setSelectedItem(seleccion);
            }
            agregar(etiqueta, campo);
            return campo;
        }

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
