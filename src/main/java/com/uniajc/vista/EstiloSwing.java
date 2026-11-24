package com.uniajc.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Paleta de colores y fuentes UNIAJC para la interfaz Swing, tomada de la sección
 * "Referencias de Estilo Visual" de {@code GUIA-IMPLEMENTACION.md}.
 *
 * <p>Centralizar los colores aquí permite cambiar el aspecto de toda la aplicación
 * tocando un solo archivo. Los colores se escriben en hexadecimal ({@code 0x2980B9}),
 * igual que en la guía ({@code #2980B9}).</p>
 */
public final class EstiloSwing {

    /** Azul principal: encabezados, botón Editar y fila seleccionada. */
    public static final Color PRIMARIO = new Color(0x2980B9);
    /** Gris muy claro: fondo de los paneles. */
    public static final Color SECUNDARIO = new Color(0xECF0F1);
    /** Rojo: botón Eliminar y errores. */
    public static final Color ACENTO = new Color(0xE74C3C);
    /** Verde: botones Registrar y Guardar. */
    public static final Color EXITO = new Color(0x27AE60);
    /** Naranja: advertencias. */
    public static final Color ADVERTENCIA = new Color(0xF39C12);
    /** Azul oscuro: texto principal y menú lateral. */
    public static final Color TEXTO = new Color(0x2C3E50);
    /** Gris: textos de ayuda y contador de registros. */
    public static final Color TEXTO_SECUNDARIO = new Color(0x7F8C8D);
    /** Gris medio: botones secundarios (Actualizar, Limpiar, Cancelar). */
    public static final Color NEUTRO = new Color(0x95A5A6);

    /** Fuente de toda la interfaz (viene instalada en Windows). */
    public static final String FUENTE = "Segoe UI";

    /** Constructor privado: clase de constantes y utilidades estáticas. */
    private EstiloSwing() {
    }

    /**
     * Crea la fuente de la aplicación con el estilo y tamaño indicados.
     *
     * @param estilo {@code Font.PLAIN}, {@code Font.BOLD} o {@code Font.ITALIC}
     * @param tamano tamaño en puntos
     * @return la fuente lista para usar en un componente
     */
    public static Font fuente(int estilo, int tamano) {
        return new Font(FUENTE, estilo, tamano);
    }

    /**
     * Cambia la fuente por defecto de todos los componentes Swing.
     * {@link UIManager} guarda la configuración global del aspecto (<i>Look and Feel</i>);
     * se llama una vez al iniciar, antes de crear las ventanas.
     */
    public static void configurarLookAndFeel() {
        UIManager.put("Label.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("Button.font", new FontUIResource(FUENTE, Font.BOLD, 13));
        UIManager.put("Table.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new FontUIResource(FUENTE, Font.BOLD, 13));
        UIManager.put("TextField.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("ComboBox.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("Menu.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("MenuItem.font", new FontUIResource(FUENTE, Font.PLAIN, 13));
        UIManager.put("OptionPane.messageFont", new FontUIResource(FUENTE, Font.PLAIN, 13));
    }

    /**
     * Crea un botón plano con el estilo de la aplicación: fondo de color, texto blanco
     * en negrita y cursor de mano al pasar el mouse.
     *
     * @param texto texto del botón
     * @param color color de fondo (usar las constantes de esta clase)
     * @return el botón configurado
     */
    public static JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(120, 35));
        return boton;
    }
}
