package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Grupo;
import com.uniajc.servicios.GrupoService;

/**
 * Controlador de grupos (capa <b>Controlador</b> del patrón MVC).
 *
 * <p>Es el intermediario entre las vistas (consola y Swing) y el servicio. Recibe
 * los pedidos de la vista y los delega a {@link GrupoService}, que aplica las reglas de
 * negocio. No valida ni accede a la base de datos: solo coordina.</p>
 *
 * <p>Gracias a esta capa, las dos vistas comparten exactamente el mismo camino
 * hacia los datos, y la vista no necesita conocer al servicio.</p>
 *
 * <p>Los errores del servicio ({@code IllegalArgumentException}) y de la base de
 * datos ({@code ErrorBaseDatosException}) no se capturan aquí: siguen subiendo
 * hasta la vista, que es la que los muestra al usuario.</p>
 */
public class ControladorGrupo {

    /** Servicio al que se delegan todas las operaciones. */
    private final GrupoService servicio;

    /**
     * Crea el controlador. El servicio se recibe desde afuera (inyección de
     * dependencias); lo arma {@code Main} al iniciar la aplicación.
     *
     * @param servicio servicio de grupos
     */
    public ControladorGrupo(GrupoService servicio) {
        this.servicio = servicio;
    }

    /**
     * Devuelve todos los registros de grupos.
     *
     * @return lista de grupos (vacía si no hay)
     */
    public List<Grupo> listar() {
        return servicio.obtenerTodosLosGrupos();
    }

    /**
     * Busca un grupo por su ID.
     *
     * @param id identificador
     * @return un grupo si existe, o vacío si no
     */
    public Optional<Grupo> buscar(int id) {
        return servicio.buscarGrupoPorId(id);
    }

    /**
     * Registra un grupo nuevo. Las validaciones las hace el servicio.
     *
     * @param entidad datos a registrar
     */
    public void registrar(Grupo entidad) {
        servicio.registrarGrupo(entidad);
    }

    /**
     * Actualiza un grupo existente.
     *
     * @param entidad datos nuevos (su ID indica cuál se actualiza)
     */
    public void actualizar(Grupo entidad) {
        servicio.actualizarGrupo(entidad);
    }

    /**
     * Elimina un grupo por su ID.
     *
     * @param id identificador del registro a eliminar
     */
    public void eliminar(int id) {
        servicio.eliminarGrupo(id);
    }
}
