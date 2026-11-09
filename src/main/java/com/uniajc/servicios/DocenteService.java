package com.uniajc.servicios;

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;

import java.util.List;
import java.util.Optional;

public class DocenteService {
    private final DocenteDao docenteDao;

    public DocenteService(DocenteDao docenteDao) {
        this.docenteDao = docenteDao;
    }

    public void registrarDocente(Docente docente) {
        validar(docente);
        docenteDao.guardar(docente);
    }

    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.obtenerTodos();
    }

    public Optional<Docente> buscarDocentePorId(int id) {
        Validaciones.idPositivo(id, "docente");
        return docenteDao.buscarPorId(id);
    }

    public void actualizarDocente(Docente docente) {
        validar(docente);
        Validaciones.idPositivo(docente.getIdDocente(), "docente");
        exigirExistente(docente.getIdDocente());
        docenteDao.actualizar(docente);
    }

    public void eliminarDocente(int id) {
        Validaciones.idPositivo(id, "docente");
        exigirExistente(id);
        docenteDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        if (docenteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe el docente con ID " + id + ".");
        }
    }

    private void validar(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("El docente es obligatorio.");
        }
        Validaciones.requerido(docente.getNombre(), "nombre");
        Validaciones.requerido(docente.getEspecialidad(), "especialidad");
        Validaciones.email(docente.getEmail());
    }
}
