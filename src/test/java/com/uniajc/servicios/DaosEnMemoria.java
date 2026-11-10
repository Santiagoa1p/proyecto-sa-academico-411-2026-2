package com.uniajc.servicios;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

/**
 * DAOs falsos que guardan los datos en memoria, para probar los servicios
 * sin conectarse a la base de datos.
 */
public final class DaosEnMemoria {

    public final EstudianteFalso estudiantes = new EstudianteFalso();
    public final DocenteFalso docentes = new DocenteFalso();
    public final MateriaFalsa materias = new MateriaFalsa();
    public final GrupoFalso grupos = new GrupoFalso();
    public final InscripcionFalsa inscripciones = new InscripcionFalsa();

    public EstudianteService estudianteService() { return new EstudianteService(estudiantes, inscripciones); }
    public DocenteService docenteService() { return new DocenteService(docentes, grupos); }
    public MateriaService materiaService() { return new MateriaService(materias, grupos); }
    public GrupoService grupoService() { return new GrupoService(grupos, materias, docentes, inscripciones); }
    public InscripcionCursoService inscripcionService() {
        return new InscripcionCursoService(inscripciones, estudiantes, grupos);
    }

    public static final class EstudianteFalso extends EstudianteDao {
        public final Map<Integer, Estudiante> datos = new LinkedHashMap<>();
        private int siguiente = 1;

        @Override public void guardar(Estudiante e) { e.setIdEstudiante(siguiente++); datos.put(e.getIdEstudiante(), e); }
        @Override public List<Estudiante> obtenerTodos() { return new ArrayList<>(datos.values()); }
        @Override public Optional<Estudiante> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public Optional<Estudiante> buscarPorEmail(String email) {
            return datos.values().stream().filter(e -> e.getEmail().equalsIgnoreCase(email)).findFirst();
        }
        @Override public void actualizar(Estudiante e) { datos.put(e.getIdEstudiante(), e); }
        @Override public void eliminar(int id) { datos.remove(id); }
    }

    public static final class DocenteFalso extends DocenteDao {
        public final Map<Integer, Docente> datos = new LinkedHashMap<>();
        private int siguiente = 1;

        @Override public void guardar(Docente d) { d.setIdDocente(siguiente++); datos.put(d.getIdDocente(), d); }
        @Override public List<Docente> obtenerTodos() { return new ArrayList<>(datos.values()); }
        @Override public Optional<Docente> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public void actualizar(Docente d) { datos.put(d.getIdDocente(), d); }
        @Override public void eliminar(int id) { datos.remove(id); }
    }

    public static final class MateriaFalsa extends MateriaDao {
        public final Map<Integer, Materia> datos = new LinkedHashMap<>();
        private int siguiente = 1;

        @Override public void guardar(Materia m) { m.setIdMateria(siguiente++); datos.put(m.getIdMateria(), m); }
        @Override public List<Materia> obtenerTodos() { return new ArrayList<>(datos.values()); }
        @Override public Optional<Materia> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public Optional<Materia> buscarPorNombre(String nombre) {
            return datos.values().stream().filter(m -> m.getNombreMateria().equalsIgnoreCase(nombre)).findFirst();
        }
        @Override public void actualizar(Materia m) { datos.put(m.getIdMateria(), m); }
        @Override public void eliminar(int id) { datos.remove(id); }
    }

    public static final class GrupoFalso extends GrupoDao {
        public final Map<Integer, Grupo> datos = new LinkedHashMap<>();
        private int siguiente = 1;

        @Override public void guardar(Grupo g) { g.setIdGrupo(siguiente++); datos.put(g.getIdGrupo(), g); }
        @Override public List<Grupo> obtenerTodos() { return new ArrayList<>(datos.values()); }
        @Override public Optional<Grupo> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public int contarPorMateria(int id) {
            return (int) datos.values().stream().filter(g -> g.getIdMateria() == id).count();
        }
        @Override public int contarPorDocente(int id) {
            return (int) datos.values().stream().filter(g -> g.getIdDocente() == id).count();
        }
        @Override public void actualizar(Grupo g) { datos.put(g.getIdGrupo(), g); }
        @Override public void eliminar(int id) { datos.remove(id); }
    }

    public static final class InscripcionFalsa extends InscripcionCursoDao {
        public final Map<Integer, InscripcionCurso> datos = new LinkedHashMap<>();
        private int siguiente = 1;

        @Override public void guardar(InscripcionCurso i) {
            i.setIdInscripcion(siguiente++);
            datos.put(i.getIdInscripcion(), i);
        }
        @Override public List<InscripcionCurso> obtenerTodos() { return new ArrayList<>(datos.values()); }
        @Override public Optional<InscripcionCurso> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
        @Override public Optional<InscripcionCurso> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
            return datos.values().stream()
                    .filter(i -> i.getIdEstudiante() == idEstudiante && i.getIdGrupo() == idGrupo)
                    .findFirst();
        }
        @Override public int contarPorEstudiante(int id) {
            return (int) datos.values().stream().filter(i -> i.getIdEstudiante() == id).count();
        }
        @Override public int contarPorGrupo(int id) {
            return (int) datos.values().stream().filter(i -> i.getIdGrupo() == id).count();
        }
        @Override public void actualizar(InscripcionCurso i) { datos.put(i.getIdInscripcion(), i); }
        @Override public void eliminar(int id) { datos.remove(id); }
    }
}
