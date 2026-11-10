package com.uniajc.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/** Paleta de colores y fuentes UNIAJC (ver GUIA-IMPLEMENTACION.md). */
public final class EstiloSwing {

    public static final Color PRIMARIO = new Color(0x2980B9);
    public static final Color SECUNDARIO = new Color(0xECF0F1);
    public static final Color ACENTO = new Color(0xE74C3C);
    public static final Color EXITO = new Color(0x27AE60);
    public static final Color ADVERTENCIA = new Color(0xF39C12);
    public static final Color TEXTO = new Color(0x2C3E50);
    public static final Color TEXTO_SECUNDARIO = new Color(0x7F8C8D);
    public static final Color NEUTRO = new Color(0x95A5A6);

    public static final String FUENTE = "Segoe UI";

    private EstiloSwing() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(FUENTE, estilo, tamano);
    }

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
