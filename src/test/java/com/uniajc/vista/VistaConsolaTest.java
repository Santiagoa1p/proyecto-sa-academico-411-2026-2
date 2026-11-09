package com.uniajc.vista;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.InscripcionCursoService;
import com.uniajc.servicios.MateriaService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VistaConsolaTest {
    @Test
    void registraEstudianteDesdeMenuSinConectarseALaBaseDeDatos() {
        EstudianteDaoEnMemoria estudianteDao = new EstudianteDaoEnMemoria();
        VistaConsola vista = crearVista(
                "1\n2\nAna\nPérez\nana@example.com\n0\n0\n",
                estudianteDao);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream salidaOriginal = System.out;

        try (PrintStream capturada = new PrintStream(salida, true, StandardCharsets.UTF_8)) {
            System.setOut(capturada);
            vista.iniciar();
        } finally {
            System.setOut(salidaOriginal);
        }

        assertEquals(1, estudianteDao.estudiantes.size());
        Estudiante estudiante = estudianteDao.estudiantes.get(0);
        assertEquals("Ana", estudiante.getNombre());
        assertEquals("Pérez", estudiante.getApellido());
        assertEquals("ana@example.com", estudiante.getEmail());
        assertTrue(salida.toString(StandardCharsets.UTF_8).contains("Estudiante registrado."));
    }

    @Test
    void terminaSiLaEntradaAcabaDespuesDeUnaOpcionInvalida() {
        VistaConsola vista = crearVista("opción-inválida\n", new EstudianteDaoEnMemoria());
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream salidaOriginal = System.out;

        try (PrintStream capturada = new PrintStream(salida, true, StandardCharsets.UTF_8)) {
            System.setOut(capturada);
            vista.iniciar();
        } finally {
            System.setOut(salidaOriginal);
        }

        String texto = salida.toString(StandardCharsets.UTF_8);
        assertTrue(texto.contains("Ingresa un número entero."));
        assertTrue(texto.contains("Hasta luego."));
    }

    private VistaConsola crearVista(String entrada, EstudianteDao estudianteDao) {
        DocenteDao docenteDao = new DocenteDao();
        MateriaDao materiaDao = new MateriaDao();
        GrupoDao grupoDao = new GrupoDao();
        InscripcionCursoDao inscripcionDao = new InscripcionCursoDao();
        return new VistaConsola(
                new Scanner(entrada),
                new ControladorEstudiante(new EstudianteService(estudianteDao)),
                new ControladorDocente(new DocenteService(docenteDao)),
                new ControladorMateria(new MateriaService(materiaDao)),
                new ControladorGrupo(new GrupoService(grupoDao, materiaDao, docenteDao)),
                new ControladorInscripcionCurso(
                        new InscripcionCursoService(inscripcionDao, estudianteDao, grupoDao)));
    }

    private static class EstudianteDaoEnMemoria extends EstudianteDao {
        private final List<Estudiante> estudiantes = new ArrayList<>();

        @Override
        public void guardar(Estudiante estudiante) {
            estudiante.setIdEstudiante(estudiantes.size() + 1);
            estudiantes.add(estudiante);
        }

        @Override
        public List<Estudiante> obtenerTodos() {
            return List.copyOf(estudiantes);
        }

        @Override
        public Optional<Estudiante> buscarPorId(int id) {
            return estudiantes.stream()
                    .filter(estudiante -> estudiante.getIdEstudiante() == id)
                    .findFirst();
        }
    }
}
