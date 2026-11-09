package com.uniajc.servicios;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.modelo.Materia;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiciosAcademicosTest {
    @Test
    void estudianteRechazaCorreoInvalidoAntesDeGuardar() {
        EstudianteDaoFake dao = new EstudianteDaoFake();
        EstudianteService servicio = new EstudianteService(dao);

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante(0, "Ana", "Pérez", "correo-invalido")));
        assertFalse(dao.guardado);
    }

    @Test
    void materiaRechazaCreditosFueraDelRangoPermitido() {
        MateriaDaoFake dao = new MateriaDaoFake();
        MateriaService servicio = new MateriaService(dao);

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMateria(new Materia(0, "Matemáticas", 0)));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMateria(new Materia(0, "Matemáticas", 11)));
        assertFalse(dao.guardado);
    }

    @Test
    void materiaValidaSeRegistra() {
        MateriaDaoFake dao = new MateriaDaoFake();
        new MateriaService(dao).registrarMateria(new Materia(0, "Matemáticas", 4));

        assertTrue(dao.guardado);
    }

    @Test
    void grupoRechazaMateriaInexistente() {
        GrupoDaoFake grupos = new GrupoDaoFake();
        GrupoService servicio = new GrupoService(grupos, new MateriaDaoFake(), new DocenteDaoFake());

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(0, 1, 1, "Aula 1", "Lunes 8:00")));
        assertFalse(grupos.guardado);
    }

    @Test
    void inscripcionRechazaNotaFueraDelRangoPermitido() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new InscripcionCurso(0, 1, 1, 5.1f, "ACTIVO")));
        assertFalse(inscripciones.guardado);
    }

    @Test
    void inscripcionRechazaEstadoInvalido() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "PENDIENTE")));
        assertFalse(inscripciones.guardado);
    }

    @Test
    void inscripcionValidaPermiteNotaPendiente() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        servicio.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "ACTIVO"));

        assertTrue(inscripciones.guardado);
    }

    @Test
    void inscripcionRechazaEstudianteYaInscritoEnElGrupo() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        inscripciones.duplicada = true;
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new InscripcionCurso(0, 1, 1, null, "ACTIVO")));
        assertFalse(inscripciones.guardado);
    }

    @Test
    void actualizarInscripcionExcluyeElRegistroActualAlVerificarDuplicados() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        inscripciones.existente = true;
        inscripciones.duplicada = true;
        inscripciones.idInscripcionDuplicada = 1;
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        servicio.actualizarInscripcion(new InscripcionCurso(1, 1, 1, null, "ACTIVO"));

        assertTrue(inscripciones.actualizada);
    }

    @Test
    void actualizarInscripcionRechazaDuplicadoDeOtroRegistro() {
        InscripcionDaoFake inscripciones = new InscripcionDaoFake();
        inscripciones.existente = true;
        inscripciones.duplicada = true;
        inscripciones.idInscripcionDuplicada = 2;
        InscripcionCursoService servicio = new InscripcionCursoService(
                inscripciones, new EstudianteDaoFake(true), new GrupoDaoFake(true));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(new InscripcionCurso(1, 1, 1, null, "ACTIVO")));
        assertFalse(inscripciones.actualizada);
    }

    private static class EstudianteDaoFake extends EstudianteDao {
        private final boolean existente;
        private boolean guardado;

        private EstudianteDaoFake() {
            this(false);
        }

        private EstudianteDaoFake(boolean existente) {
            this.existente = existente;
        }

        @Override
        public Optional<Estudiante> buscarPorId(int id) {
            return existente ? Optional.of(new Estudiante(id, "Ana", "Pérez", "ana@example.com")) : Optional.empty();
        }

        @Override
        public void guardar(Estudiante estudiante) {
            guardado = true;
        }
    }

    private static class DocenteDaoFake extends DocenteDao {
        @Override
        public Optional<Docente> buscarPorId(int id) {
            return Optional.empty();
        }
    }

    private static class MateriaDaoFake extends MateriaDao {
        private boolean guardado;

        @Override
        public Optional<Materia> buscarPorId(int id) {
            return Optional.empty();
        }

        @Override
        public void guardar(Materia materia) {
            guardado = true;
        }
    }

    private static class GrupoDaoFake extends GrupoDao {
        private final boolean existente;
        private boolean guardado;

        private GrupoDaoFake() {
            this(false);
        }

        private GrupoDaoFake(boolean existente) {
            this.existente = existente;
        }

        @Override
        public Optional<Grupo> buscarPorId(int id) {
            return existente ? Optional.of(new Grupo(id, 1, 1, "Aula 1", "Lunes 8:00")) : Optional.empty();
        }

        @Override
        public void guardar(Grupo grupo) {
            guardado = true;
        }
    }

    private static class InscripcionDaoFake extends InscripcionCursoDao {
        private boolean guardado;
        private boolean actualizada;
        private boolean existente;
        private boolean duplicada;
        private int idInscripcionDuplicada = 1;

        @Override
        public void guardar(InscripcionCurso inscripcion) {
            guardado = true;
        }

        @Override
        public Optional<InscripcionCurso> buscarPorId(int id) {
            return existente
                    ? Optional.of(new InscripcionCurso(id, 1, 1, null, "ACTIVO"))
                    : Optional.empty();
        }

        @Override
        public boolean existeParaEstudianteYGrupo(int idEstudiante, int idGrupo, int idInscripcionExcluida) {
            return duplicada && idInscripcionExcluida != idInscripcionDuplicada;
        }

        @Override
        public void actualizar(InscripcionCurso inscripcion) {
            actualizada = true;
        }
    }
}
