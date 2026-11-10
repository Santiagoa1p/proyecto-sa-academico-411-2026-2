package com.uniajc.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.servicios.DaosEnMemoria;

/** Simula a un usuario escribiendo en la consola. */
class VistaConsolaTest {

    private DaosEnMemoria daos;

    @BeforeEach
    void preparar() {
        daos = new DaosEnMemoria();
    }

    private String ejecutar(String... lineas) {
        String entrada = String.join("\n", lineas) + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        LectorConsola lector = new LectorConsola(
                new Scanner(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
                new PrintStream(bytes, true, StandardCharsets.UTF_8));
        new VistaConsola(lector,
                new ControladorEstudiante(daos.estudianteService()),
                new ControladorDocente(daos.docenteService()),
                new ControladorMateria(daos.materiaService()),
                new ControladorGrupo(daos.grupoService()),
                new ControladorInscripcionCurso(daos.inscripcionService())).iniciar();
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void cicloCompletoDeEstudiante() {
        String salida = ejecutar(
                "1",                                    // Estudiantes
                "2", "José", "Muñoz", "jose@uniajc.edu.co", // registrar
                "4", "1", "", "Muñoz Ruiz", "",         // actualizar solo el apellido
                "1",                                    // listar
                "5", "1", "s",                          // eliminar
                "0", "0");

        assertTrue(salida.contains("Estudiante registrado con ID 1"));
        assertTrue(salida.contains("Muñoz Ruiz"));
        assertTrue(salida.contains("Registro eliminado"));
        assertTrue(daos.estudiantes.datos.isEmpty());
    }

    @Test
    void muestraErroresDeValidacionSinCerrarElPrograma() {
        String salida = ejecutar(
                "3",
                "2", "Cálculo", "abc", "15",   // texto no numérico y luego créditos fuera de rango
                "2", "Cálculo", "4",
                "0", "0");

        assertTrue(salida.contains("Escribe un número entero"));
        assertTrue(salida.contains("Los créditos deben estar entre 1 y 10"));
        assertTrue(salida.contains("Materia registrada con ID 1"));
        assertEquals(1, daos.materias.datos.size());
    }

    @Test
    void registraGrupoEInscripcionConNota() {
        String salida = ejecutar(
                "1", "2", "Ana", "Pérez", "ana@uniajc.edu.co", "0",
                "2", "2", "Carlos Gómez", "Software", "carlos@uniajc.edu.co", "0",
                "3", "2", "POO", "3", "0",
                "4", "2", "1", "1", "A-201", "Lunes 18:00", "0",
                "5", "2", "1", "1", "4,5", "aprobado", "1", "0",
                "0");

        assertTrue(salida.contains("Grupo registrado con ID 1"));
        assertTrue(salida.contains("Inscripción registrada con ID 1"));
        assertEquals("APROBADO", daos.inscripciones.datos.get(1).getEstado());
        assertEquals(4.5f, daos.inscripciones.datos.get(1).getNotaFinal());
    }

    @Test
    void terminaBienSiSeAcabaLaEntrada() {
        String salida = ejecutar("1");
        assertTrue(salida.contains("Saliendo"));
    }
}
