package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

/**
 * Controlador de docentes (capa <b>Controlador</b> del patrón MVC).
 *
 * <p>Es el intermediario entre las vistas (consola y Swing) y el servicio. Recibe
 * los pedidos de la vista y los delega a {@link DocenteService}, que aplica las reglas de
 * negocio. No valida ni accede a la base de datos: solo coordina.</p>
 *
 * <p>Gracias a esta capa, las dos vistas comparten exactamente el mismo camino
 * hacia los datos, y la vista no necesita conocer al servicio.</p>
 *
 * <p>Los errores del servicio ({@code IllegalArgumentException}) y de la base de
 * datos ({@code ErrorBaseDatosException}) no se capturan aquí: siguen subiendo
 * hasta la vista, que es la que los muestra al usuario.</p>
 */
public class ControladorDocente {

    /** Servicio al que se delegan todas las operaciones. */
    private final DocenteService servicio;

    /**
     * Crea el controlador. El servicio se recibe desde afuera (inyección de
     * dependencias); lo arma {@code Main} al iniciar la aplicación.
     *
     * @param servicio servicio de docentes
     */
    public ControladorDocente(DocenteService servicio) {
        this.servicio = servicio;
    }

    /**
     * Devuelve todos los registros de docentes.
     *
     * @return lista de docentes (vacía si no hay)
     */
    public List<Docente> listar() {
        return servicio.obtenerTodosLosDocentes();
    }

    /**
     * Busca un docente por su ID.
     *
     * @param id identificador
     * @return un docente si existe, o vacío si no
     */
    public Optional<Docente> buscar(int id) {
        return servicio.buscarDocentePorId(id);
    }

    /**
     * Registra un docente nuevo. Las validaciones las hace el servicio.
     *
     * @param entidad datos a registrar
     */
    public void registrar(Docente entidad) {
        servicio.registrarDocente(entidad);
    }

    /**
     * Actualiza un docente existente.
     *
     * @param entidad datos nuevos (su ID indica cuál se actualiza)
     */
    public void actualizar(Docente entidad) {
        servicio.actualizarDocente(entidad);
    }

    /**
     * Elimina un docente por su ID.
     *
     * @param id identificador del registro a eliminar
     */
    public void eliminar(int id) {
        servicio.eliminarDocente(id);
    }
}
