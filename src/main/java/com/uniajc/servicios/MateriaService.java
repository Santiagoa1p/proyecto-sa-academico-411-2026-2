package com.uniajc.servicios;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

import java.util.List;
import java.util.Optional;

public class MateriaService {
    private final MateriaDao materiaDao;

    public MateriaService(MateriaDao materiaDao) {
        this.materiaDao = materiaDao;
    }

    public void registrarMateria(Materia materia) {
        validar(materia);
        materiaDao.guardar(materia);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.obtenerTodos();
    }

    public Optional<Materia> buscarMateriaPorId(int id) {
        Validaciones.idPositivo(id, "materia");
        return materiaDao.buscarPorId(id);
    }

    public void actualizarMateria(Materia materia) {
        validar(materia);
        Validaciones.idPositivo(materia.getIdMateria(), "materia");
        exigirExistente(materia.getIdMateria());
        materiaDao.actualizar(materia);
    }

    public void eliminarMateria(int id) {
        Validaciones.idPositivo(id, "materia");
        exigirExistente(id);
        materiaDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        if (materiaDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe la materia con ID " + id + ".");
        }
    }

    private void validar(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia es obligatoria.");
        }
        Validaciones.requerido(materia.getNombreMateria(), "nombre de la materia");
        if (materia.getCreditos() < 1 || materia.getCreditos() > 10) {
            throw new IllegalArgumentException("Los créditos deben estar entre 1 y 10.");
        }
    }
}
