package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Materia;

class DocenteYMateriaServiceTest {

    private DaosEnMemoria daos;
    private DocenteService docentes;
    private MateriaService materias;

    @BeforeEach
    void preparar() {
        daos = new DaosEnMemoria();
        docentes = daos.docenteService();
        materias = daos.materiaService();
    }

    @Test
    void registraDocenteValidoYRechazaDatosInvalidos() {
        docentes.registrarDocente(new Docente(0, "Ing. María González", "Programación"));

        assertEquals(1, docentes.obtenerTodosLosDocentes().size());
        assertThrows(IllegalArgumentException.class, () -> docentes.registrarDocente(
                new Docente(0, "Docente 123", "Redes")));
        assertThrows(IllegalArgumentException.class, () -> docentes.registrarDocente(
                new Docente(0, "Sin Especialidad", " ")));
    }

    @Test
    void noEliminaDocenteConGruposAsignados() {
        docentes.registrarDocente(new Docente(0, "Carlos Pérez", "Bases de datos"));
        daos.grupos.guardar(new Grupo(0, 1, 1, "A-101", "Lunes 18:00"));

        assertThrows(IllegalArgumentException.class, () -> docentes.eliminarDocente(1));
    }

    @Test
    void validaRangoDeCreditos() {
        assertThrows(IllegalArgumentException.class, () -> materias.registrarMateria(new Materia(0, "Cálculo", 0)));
        assertThrows(IllegalArgumentException.class, () -> materias.registrarMateria(new Materia(0, "Cálculo", 11)));

        materias.registrarMateria(new Materia(0, "Cálculo", 10));
        assertEquals(1, materias.obtenerTodasLasMaterias().size());
    }

    @Test
    void noPermiteMateriasConElMismoNombre() {
        materias.registrarMateria(new Materia(0, "Bases de datos I", 4));

        assertThrows(IllegalArgumentException.class,
                () -> materias.registrarMateria(new Materia(0, "bases de datos i", 3)));
    }

    @Test
    void eliminaMateriaSoloSiNoTieneGrupos() {
        materias.registrarMateria(new Materia(0, "Física", 3));
        daos.grupos.guardar(new Grupo(0, 1, 1, "A-101", "Lunes 18:00"));
        assertThrows(IllegalArgumentException.class, () -> materias.eliminarMateria(1));

        daos.grupos.eliminar(1);
        materias.eliminarMateria(1);
        assertTrue(materias.obtenerTodasLasMaterias().isEmpty());
    }
}
