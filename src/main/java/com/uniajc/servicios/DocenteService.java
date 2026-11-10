package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.modelo.Docente;

/** Lógica de negocio de docentes. */
public class DocenteService {

    private final DocenteDao docenteDao;
    private final GrupoDao grupoDao;

    public DocenteService(DocenteDao docenteDao) {
        this(docenteDao, new GrupoDao());
    }

    public DocenteService(DocenteDao docenteDao, GrupoDao grupoDao) {
        this.docenteDao = docenteDao;
        this.grupoDao = grupoDao;
    }

    public void registrarDocente(Docente docente) {
        validarDocente(docente);
        validarEmailDisponible(docente.getEmail(), 0);
        docenteDao.guardar(docente);
    }

    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.obtenerTodos();
    }

    public Optional<Docente> buscarDocentePorId(int id) {
        Validaciones.idValido(id, "docente");
        return docenteDao.buscarPorId(id);
    }

    public void actualizarDocente(Docente docente) {
        Validaciones.obligatorio(docente, "El docente");
        exigirExistente(docente.getIdDocente());
        validarDocente(docente);
        validarEmailDisponible(docente.getEmail(), docente.getIdDocente());
        docenteDao.actualizar(docente);
    }

    public void eliminarDocente(int id) {
        exigirExistente(id);
        if (grupoDao.contarPorDocente(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el docente porque tiene grupos asignados. Reasígnalos o elimínalos primero.");
        }
        docenteDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        Validaciones.idValido(id, "docente");
        if (docenteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un docente con ID " + id + ".");
        }
    }

    private void validarDocente(Docente docente) {
        Validaciones.obligatorio(docente, "El docente");
        docente.setNombre(Validaciones.limpiar(docente.getNombre()));
        docente.setEspecialidad(Validaciones.limpiar(docente.getEspecialidad()));
        docente.setEmail(Validaciones.limpiar(docente.getEmail()).toLowerCase());

        Validaciones.nombrePropio(docente.getNombre(), "El nombre");
        Validaciones.texto(docente.getEspecialidad(), "La especialidad", 2, 100);
        Validaciones.email(docente.getEmail());
    }

    private void validarEmailDisponible(String email, int idPropio) {
        docenteDao.buscarPorEmail(email)
                .filter(otro -> otro.getIdDocente() != idPropio)
                .ifPresent(otro -> {
                    throw new IllegalArgumentException("Ya existe un docente con el email " + email + ".");
                });
    }
}
