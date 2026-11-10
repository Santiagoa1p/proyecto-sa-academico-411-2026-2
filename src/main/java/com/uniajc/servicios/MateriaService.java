package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

/** Lógica de negocio de materias. */
public class MateriaService {

    public static final int CREDITOS_MINIMOS = 1;
    public static final int CREDITOS_MAXIMOS = 10;

    private final MateriaDao materiaDao;
    private final GrupoDao grupoDao;

    public MateriaService(MateriaDao materiaDao) {
        this(materiaDao, new GrupoDao());
    }

    public MateriaService(MateriaDao materiaDao, GrupoDao grupoDao) {
        this.materiaDao = materiaDao;
        this.grupoDao = grupoDao;
    }

    public void registrarMateria(Materia materia) {
        validarMateria(materia);
        validarNombreDisponible(materia.getNombreMateria(), 0);
        materiaDao.guardar(materia);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.obtenerTodos();
    }

    public Optional<Materia> buscarMateriaPorId(int id) {
        Validaciones.idValido(id, "materia");
        return materiaDao.buscarPorId(id);
    }

    public void actualizarMateria(Materia materia) {
        Validaciones.obligatorio(materia, "La materia");
        exigirExistente(materia.getIdMateria());
        validarMateria(materia);
        validarNombreDisponible(materia.getNombreMateria(), materia.getIdMateria());
        materiaDao.actualizar(materia);
    }

    public void eliminarMateria(int id) {
        exigirExistente(id);
        if (grupoDao.contarPorMateria(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la materia porque tiene grupos. Elimina esos grupos primero.");
        }
        materiaDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        Validaciones.idValido(id, "materia");
        if (materiaDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una materia con ID " + id + ".");
        }
    }

    private void validarMateria(Materia materia) {
        Validaciones.obligatorio(materia, "La materia");
        materia.setNombreMateria(Validaciones.limpiar(materia.getNombreMateria()));
        Validaciones.texto(materia.getNombreMateria(), "El nombre de la materia", 3, 100);
        if (materia.getCreditos() < CREDITOS_MINIMOS || materia.getCreditos() > CREDITOS_MAXIMOS) {
            throw new IllegalArgumentException(
                    "Los créditos deben estar entre " + CREDITOS_MINIMOS + " y " + CREDITOS_MAXIMOS + ".");
        }
    }

    private void validarNombreDisponible(String nombre, int idPropio) {
        materiaDao.buscarPorNombre(nombre)
                .filter(otra -> otra.getIdMateria() != idPropio)
                .ifPresent(otra -> {
                    throw new IllegalArgumentException("Ya existe una materia llamada " + nombre + ".");
                });
    }
}
