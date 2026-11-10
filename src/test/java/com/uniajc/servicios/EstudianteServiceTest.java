package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.InscripcionCurso;

class EstudianteServiceTest {

    private DaosEnMemoria daos;
    private EstudianteService servicio;

    @BeforeEach
    void preparar() {
        daos = new DaosEnMemoria();
        servicio = daos.estudianteService();
    }

    @Test
    void registraEstudianteValidoYLimpiaLosDatos() {
        Estudiante estudiante = new Estudiante(0, "  José  ", "Muñoz", " Jose.Munoz@UNIAJC.edu.co ");

        servicio.registrarEstudiante(estudiante);

        assertEquals(1, estudiante.getIdEstudiante());
        assertEquals("José", estudiante.getNombre());
        assertEquals("jose.munoz@uniajc.edu.co", estudiante.getEmail());
        assertEquals(1, servicio.obtenerTodosLosEstudiantes().size());
    }

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

    @Test
    void noPermiteEmailRepetidoAunqueCambienLasMayusculas() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "Otra", "Persona", "ANA@uniajc.edu.co")));
        assertTrue(error.getMessage().contains("Ya existe"));
    }

    @Test
    void actualizaConservandoSuPropioEmail() {
        Estudiante estudiante = new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co");
        servicio.registrarEstudiante(estudiante);

        Estudiante cambios = new Estudiante(estudiante.getIdEstudiante(), "Ana María", "Pérez", "ana@uniajc.edu.co");
        servicio.actualizarEstudiante(cambios);

        assertEquals("Ana María", servicio.buscarEstudiantePorId(1).orElseThrow().getNombre());
    }

    @Test
    void noActualizaNiEliminaEstudiantesInexistentes() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarEstudiante(new Estudiante(99, "Ana", "Pérez", "ana@uniajc.edu.co")));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(99));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(0));
    }

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
