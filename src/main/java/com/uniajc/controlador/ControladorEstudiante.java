package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

/**
 * Controlador de estudiantes (capa <b>Controlador</b> del patrón MVC).
 *
 * <p>Es el intermediario entre las vistas (consola y Swing) y el servicio. Recibe
 * los pedidos de la vista y los delega a {@link EstudianteService}, que aplica las reglas de
 * negocio. No valida ni accede a la base de datos: solo coordina.</p>
 *
 * <p>Gracias a esta capa, las dos vistas comparten exactamente el mismo camino
 * hacia los datos, y la vista no necesita conocer al servicio.</p>
 *
 * <p>Los errores del servicio ({@code IllegalArgumentException}) y de la base de
 * datos ({@code ErrorBaseDatosException}) no se capturan aquí: siguen subiendo
 * hasta la vista, que es la que los muestra al usuario.</p>
 */
public class ControladorEstudiante {

    /** Servicio al que se delegan todas las operaciones. */
    private final EstudianteService servicio;

    /**
     * Crea el controlador. El servicio se recibe desde afuera (inyección de
     * dependencias); lo arma {@code Main} al iniciar la aplicación.
     *
     * @param servicio servicio de estudiantes
     */
    public ControladorEstudiante(EstudianteService servicio) {
        this.servicio = servicio;
    }

    /**
     * Devuelve todos los registros de estudiantes.
     *
     * @return lista de estudiantes (vacía si no hay)
     */
    public List<Estudiante> listar() {
        return servicio.obtenerTodosLosEstudiantes();
    }

    /**
     * Busca un estudiante por su ID.
     *
     * @param id identificador
     * @return un estudiante si existe, o vacío si no
     */
    public Optional<Estudiante> buscar(int id) {
        return servicio.buscarEstudiantePorId(id);
    }

    /**
     * Registra un estudiante nuevo. Las validaciones las hace el servicio.
     *
     * @param entidad datos a registrar
     */
    public void registrar(Estudiante entidad) {
        servicio.registrarEstudiante(entidad);
    }

    /**
     * Actualiza un estudiante existente.
     *
     * @param entidad datos nuevos (su ID indica cuál se actualiza)
     */
    public void actualizar(Estudiante entidad) {
        servicio.actualizarEstudiante(entidad);
    }

    /**
     * Elimina un estudiante por su ID.
     *
     * @param id identificador del registro a eliminar
     */
    public void eliminar(int id) {
        servicio.eliminarEstudiante(id);
    }
}
