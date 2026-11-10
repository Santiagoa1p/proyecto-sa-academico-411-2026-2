package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.modelo.Materia;

class GrupoEInscripcionServiceTest {

    private DaosEnMemoria daos;
    private GrupoService grupos;
    private InscripcionCursoService inscripciones;

    @BeforeEach
    void preparar() {
        daos = new DaosEnMemoria();
        grupos = daos.grupoService();
        inscripciones = daos.inscripcionService();

        daos.materias.guardar(new Materia(0, "POO", 3));                                 // materia 1
        daos.docentes.guardar(new Docente(0, "Carlos", "Software", "carlos@uniajc.edu.co")); // docente 1
        daos.docentes.guardar(new Docente(0, "María", "Datos", "maria@uniajc.edu.co"));     // docente 2
        daos.estudiantes.guardar(new Estudiante(0, "Ana", "Pérez", "ana@uniajc.edu.co"));   // estudiante 1
    }

    @Test
    void registraGrupoConMateriaYDocenteExistentes() {
        Grupo grupo = new Grupo(0, 1, 1, "a-201", "Lunes 18:00-20:00");
        grupos.registrarGrupo(grupo);

        assertEquals(1, grupo.getIdGrupo());
        assertEquals("A-201", grupo.getAula());
    }

    @Test
    void rechazaGrupoConMateriaODocenteInexistente() {
        assertThrows(IllegalArgumentException.class, () -> grupos.registrarGrupo(new Grupo(0, 9, 1, "A-1", "Lunes")));
        assertThrows(IllegalArgumentException.class, () -> grupos.registrarGrupo(new Grupo(0, 1, 9, "A-1", "Lunes")));
        assertThrows(IllegalArgumentException.class, () -> grupos.registrarGrupo(new Grupo(0, 1, 1, "", "Lunes")));
    }

    @Test
    void evitaCrucesDeDocenteYAulaEnElMismoHorario() {
        grupos.registrarGrupo(new Grupo(0, 1, 1, "A-201", "Lunes 18:00"));

        // Mismo docente, mismo horario, otra aula
        assertThrows(IllegalArgumentException.class, () -> grupos.registrarGrupo(new Grupo(0, 1, 1, "B-100", "lunes 18:00")));
        // Otro docente, misma aula y horario
        assertThrows(IllegalArgumentException.class, () -> grupos.registrarGrupo(new Grupo(0, 1, 2, "A-201", "Lunes 18:00")));
        // Otro docente, otra aula: permitido
        assertDoesNotThrow(() -> grupos.registrarGrupo(new Grupo(0, 1, 2, "B-100", "Lunes 18:00")));
        // Editar el grupo 1 sin cambiar horario no choca consigo mismo
        assertDoesNotThrow(() -> grupos.actualizarGrupo(new Grupo(1, 1, 1, "A-201", "Lunes 18:00")));
    }

    @Test
    void noEliminaGrupoConInscritos() {
        grupos.registrarGrupo(new Grupo(0, 1, 1, "A-201", "Lunes 18:00"));
        inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "ACTIVO"));

        assertThrows(IllegalArgumentException.class, () -> grupos.eliminarGrupo(1));
    }

    @Test
    void inscribeYEvitaDuplicados() {
        grupos.registrarGrupo(new Grupo(0, 1, 1, "A-201", "Lunes 18:00"));
        InscripcionCurso inscripcion = new InscripcionCurso(0, 1, 1, null, "activo");
        inscripciones.registrarInscripcion(inscripcion);

        assertEquals("ACTIVO", inscripcion.getEstado());
        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "ACTIVO")));
    }

    @Test
    void rechazaEstudianteOGrupoInexistente() {
        grupos.registrarGrupo(new Grupo(0, 1, 1, "A-201", "Lunes 18:00"));

        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 9, 1, null, "ACTIVO")));
        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 9, null, "ACTIVO")));
    }

    @Test
    void validaNotaYCoherenciaConElEstado() {
        grupos.registrarGrupo(new Grupo(0, 1, 1, "A-201", "Lunes 18:00"));

        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, 5.5f, "ACTIVO")));
        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, 2.0f, "APROBADO")));
        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, 4.0f, "REPROBADO")));
        assertThrows(IllegalArgumentException.class,
                () -> inscripciones.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "PERDIDO")));

        InscripcionCurso inscripcion = new InscripcionCurso(0, 1, 1, 3.0f, "APROBADO");
        inscripciones.registrarInscripcion(inscripcion);

        inscripcion.setNotaFinal(2.9f);
        inscripcion.setEstado("REPROBADO");
        assertDoesNotThrow(() -> inscripciones.actualizarInscripcion(inscripcion));
    }
}
