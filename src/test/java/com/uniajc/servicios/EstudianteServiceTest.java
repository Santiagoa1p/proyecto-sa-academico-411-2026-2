package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.InscripcionCurso;

/**
 * Pruebas de las reglas de negocio de {@link EstudianteService}.
 *
 * <p>Usan {@link DaosEnMemoria}: el servicio recibe DAO falsos que guardan en memoria,
 * así las pruebas no se conectan a Neon ni modifican la base compartida. Cada prueba
 * sigue el patrón preparar → actuar → comprobar.</p>
 */
class EstudianteServiceTest {

    private DaosEnMemoria daos;
    private EstudianteService servicio;

    @BeforeEach
    void preparar() {
        daos = new DaosEnMemoria();
        servicio = daos.estudianteService();
    }

    /** Registra un estudiante válido: le asigna ID, quita espacios sobrantes y pasa el email a minúsculas. */
    @Test
    void registraEstudianteValidoYLimpiaLosDatos() {
        Estudiante estudiante = new Estudiante(0, "  José  ", "Muñoz", " Jose.Munoz@UNIAJC.edu.co ");

        servicio.registrarEstudiante(estudiante);

        assertEquals(1, estudiante.getIdEstudiante());
        assertEquals("José", estudiante.getNombre());
        assertEquals("jose.munoz@uniajc.edu.co", estudiante.getEmail());
        assertEquals(1, servicio.obtenerTodosLosEstudiantes().size());
    }

    /** Rechaza nombre vacío, nombre con números, email sin @ y estudiante null. */
    @Test
    void rechazaCamposVaciosNombresConNumerosYEmailInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "", "Pérez", "a@b.co")));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "Ana2", "Pérez", "a@b.co")));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "Ana", "Pérez", "correo-sin-arroba")));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarEstudiante(null));
    }

    /** No permite registrar un email que ya existe, aunque esté escrito con otras mayúsculas. */
    @Test
    void noPermiteEmailRepetidoAunqueCambienLasMayusculas() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "Otra", "Persona", "ANA@uniajc.edu.co")));
        assertTrue(error.getMessage().contains("Ya existe"));
    }

    /** Al actualizar, el estudiante puede conservar su propio email sin que cuente como repetido. */
    @Test
    void actualizaConservandoSuPropioEmail() {
        Estudiante estudiante = new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co");
        servicio.registrarEstudiante(estudiante);

        Estudiante cambios = new Estudiante(estudiante.getIdEstudiante(), "Ana María", "Pérez", "ana@uniajc.edu.co");
        servicio.actualizarEstudiante(cambios);

        assertEquals("Ana María", servicio.buscarEstudiantePorId(1).orElseThrow().getNombre());
    }

    /** No se puede actualizar ni eliminar un ID que no existe (ni el ID 0). */
    @Test
    void noActualizaNiEliminaEstudiantesInexistentes() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarEstudiante(new Estudiante(99, "Ana", "Pérez", "ana@uniajc.edu.co")));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(99));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(0));
    }

    /** Regla de integridad: no se puede borrar un estudiante que tiene inscripciones. */
    @Test
    void noEliminaEstudianteConInscripciones() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co"));
        daos.inscripciones.guardar(new InscripcionCurso(0, 1, 1, null, InscripcionCurso.ACTIVO));

        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(1));

        daos.inscripciones.eliminar(1);
        servicio.eliminarEstudiante(1);
        assertTrue(servicio.obtenerTodosLosEstudiantes().isEmpty());
    }
}
