package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

/**
 * Controlador de materias (capa <b>Controlador</b> del patrón MVC).
 *
 * <p>Es el intermediario entre las vistas (consola y Swing) y el servicio. Recibe
 * los pedidos de la vista y los delega a {@link MateriaService}, que aplica las reglas de
 * negocio. No valida ni accede a la base de datos: solo coordina.</p>
 *
 * <p>Gracias a esta capa, las dos vistas comparten exactamente el mismo camino
 * hacia los datos, y la vista no necesita conocer al servicio.</p>
 *
 * <p>Los errores del servicio ({@code IllegalArgumentException}) y de la base de
 * datos ({@code ErrorBaseDatosException}) no se capturan aquí: siguen subiendo
 * hasta la vista, que es la que los muestra al usuario.</p>
 */
public class ControladorMateria {

    /** Servicio al que se delegan todas las operaciones. */
    private final MateriaService servicio;

    /**
     * Crea el controlador. El servicio se recibe desde afuera (inyección de
     * dependencias); lo arma {@code Main} al iniciar la aplicación.
     *
     * @param servicio servicio de materias
     */
    public ControladorMateria(MateriaService servicio) {
        this.servicio = servicio;
    }

    /**
     * Devuelve todos los registros de materias.
     *
     * @return lista de materias (vacía si no hay)
     */
    public List<Materia> listar() {
        return servicio.obtenerTodasLasMaterias();
    }

    /**
     * Busca una materia por su ID.
     *
     * @param id identificador
     * @return una materia si existe, o vacío si no
     */
    public Optional<Materia> buscar(int id) {
        return servicio.buscarMateriaPorId(id);
    }

    /**
     * Registra una materia nueva. Las validaciones las hace el servicio.
     *
     * @param entidad datos a registrar
     */
    public void registrar(Materia entidad) {
        servicio.registrarMateria(entidad);
    }

    /**
     * Actualiza una materia existente.
     *
     * @param entidad datos nuevos (su ID indica cuál se actualiza)
     */
    public void actualizar(Materia entidad) {
        servicio.actualizarMateria(entidad);
    }

    /**
     * Elimina una materia por su ID.
     *
     * @param id identificador del registro a eliminar
     */
    public void eliminar(int id) {
        servicio.eliminarMateria(id);
    }
}
