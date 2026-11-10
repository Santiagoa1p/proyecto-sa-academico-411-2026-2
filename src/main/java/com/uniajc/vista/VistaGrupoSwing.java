package com.uniajc.vista;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Grupo;

/** Panel Swing para gestionar grupos. Materia y docente se eligen de una lista. */
public class VistaGrupoSwing extends VistaCrudSwing<Grupo> {

    private final ControladorGrupo controlador;
    private final ControladorMateria materias;
    private final ControladorDocente docentes;

    private final Map<Integer, String> nombresMaterias = new HashMap<>();
    private final Map<Integer, String> nombresDocentes = new HashMap<>();

    private JComboBox<Opcion> campoMateria;
    private JComboBox<Opcion> campoDocente;
    private JTextField campoAula;
    private JTextField campoHorario;

    public VistaGrupoSwing(ControladorGrupo controlador, ControladorMateria materias, ControladorDocente docentes) {
        super("Grupos", "grupo", new String[]{"ID", "Materia", "Docente", "Aula", "Horario"});
        this.controlador = controlador;
        this.materias = materias;
        this.docentes = docentes;
        anchosColumnas(50, 280, 180, 80, 190);
    }

    @Override protected List<Grupo> listar() { return controlador.listar(); }
    @Override protected Optional<Grupo> buscar(int id) { return controlador.buscar(id); }
    @Override protected Grupo nuevo() { return new Grupo(); }
    @Override protected void registrar(Grupo g) { controlador.registrar(g); }
    @Override protected void actualizar(Grupo g) { controlador.actualizar(g); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    @Override
    protected void antesDeCargar() {
        nombresMaterias.clear();
        materias.listar().forEach(m -> nombresMaterias.put(m.getIdMateria(), m.getNombreMateria()));
        nombresDocentes.clear();
        docentes.listar().forEach(d -> nombresDocentes.put(d.getIdDocente(), d.getNombre()));
    }

    @Override
    protected Object[] fila(Grupo g) {
        return new Object[]{
                g.getIdGrupo(),
                nombresMaterias.getOrDefault(g.getIdMateria(), "#" + g.getIdMateria()),
                nombresDocentes.getOrDefault(g.getIdDocente(), "#" + g.getIdDocente()),
                g.getAula(),
                g.getHorario()};
    }

    @Override
    protected void construirFormulario(Formulario f, Grupo actual) {
        List<Opcion> opcionesMateria = materias.listar().stream()
                .map(m -> new Opcion(m.getIdMateria(), m.getNombreMateria() + " (" + m.getCreditos() + " cr.)"))
                .toList();
        List<Opcion> opcionesDocente = docentes.listar().stream()
                .map(d -> new Opcion(d.getIdDocente(), d.getNombre() + " - " + d.getEspecialidad()))
                .toList();

        campoMateria = f.combo("Materia", opcionesMateria,
                actual == null ? null : Opcion.conId(opcionesMateria, actual.getIdMateria()));
        campoDocente = f.combo("Docente", opcionesDocente,
                actual == null ? null : Opcion.conId(opcionesDocente, actual.getIdDocente()));
        campoAula = f.texto("Aula", actual == null ? "" : actual.getAula());
        campoHorario = f.texto("Horario", actual == null ? "" : actual.getHorario());
        f.nota("Ejemplo de horario: Lunes 18:00-20:00");
    }

    @Override
    protected void leerFormulario(Grupo destino) {
        destino.setIdMateria(idElegido(campoMateria, "Primero registra al menos una materia."));
        destino.setIdDocente(idElegido(campoDocente, "Primero registra al menos un docente."));
        destino.setAula(campoAula.getText());
        destino.setHorario(campoHorario.getText());
    }
}
