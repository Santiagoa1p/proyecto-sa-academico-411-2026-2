package com.uniajc.vista;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;

/** Panel Swing para gestionar inscripciones. Estudiante, grupo y estado se eligen de listas. */
public class VistaInscripcionCursoSwing extends VistaCrudSwing<InscripcionCurso> {

    private static final List<String> ESTADOS = List.of(
            InscripcionCurso.ACTIVO, InscripcionCurso.APROBADO,
            InscripcionCurso.REPROBADO, InscripcionCurso.CANCELADO);

    private final ControladorInscripcionCurso controlador;
    private final ControladorEstudiante estudiantes;
    private final ControladorGrupo grupos;
    private final ControladorMateria materias;

    private final Map<Integer, String> nombresEstudiantes = new HashMap<>();
    private final Map<Integer, String> nombresGrupos = new HashMap<>();

    private JComboBox<Opcion> campoEstudiante;
    private JComboBox<Opcion> campoGrupo;
    private JTextField campoNota;
    private JComboBox<String> campoEstado;

    public VistaInscripcionCursoSwing(ControladorInscripcionCurso controlador, ControladorEstudiante estudiantes,
                                      ControladorGrupo grupos, ControladorMateria materias) {
        super("Inscripciones", "inscripción", new String[]{"ID", "Estudiante", "Grupo", "Nota final", "Estado"});
        this.controlador = controlador;
        this.estudiantes = estudiantes;
        this.grupos = grupos;
        this.materias = materias;
        anchosColumnas(50, 170, 380, 80, 100);
    }

    @Override protected List<InscripcionCurso> listar() { return controlador.listar(); }
    @Override protected Optional<InscripcionCurso> buscar(int id) { return controlador.buscar(id); }
    @Override protected InscripcionCurso nuevo() { return new InscripcionCurso(); }
    @Override protected void registrar(InscripcionCurso i) { controlador.registrar(i); }
    @Override protected void actualizar(InscripcionCurso i) { controlador.actualizar(i); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    @Override
    protected void antesDeCargar() {
        nombresEstudiantes.clear();
        estudiantes.listar().forEach(e ->
                nombresEstudiantes.put(e.getIdEstudiante(), e.getNombreCompleto()));
        nombresGrupos.clear();
        opcionesGrupo().forEach(o -> nombresGrupos.put(o.id(), o.texto()));
    }

    @Override
    protected Object[] fila(InscripcionCurso i) {
        return new Object[]{
                i.getIdInscripcion(),
                nombresEstudiantes.getOrDefault(i.getIdEstudiante(), "#" + i.getIdEstudiante()),
                nombresGrupos.getOrDefault(i.getIdGrupo(), "#" + i.getIdGrupo()),
                i.getNotaFinal() == null ? "-" : String.format("%.2f", i.getNotaFinal()),
                i.getEstado()};
    }

    @Override
    protected void construirFormulario(Formulario f, InscripcionCurso actual) {
        List<Opcion> opcionesEstudiante = estudiantes.listar().stream()
                .map(e -> new Opcion(e.getIdEstudiante(), e.getNombreCompleto() + (e.getEmail() == null ? "" : " (" + e.getEmail() + ")")))
                .toList();
        List<Opcion> opcionesGrupo = opcionesGrupo();

        campoEstudiante = f.combo("Estudiante", opcionesEstudiante,
                actual == null ? null : Opcion.conId(opcionesEstudiante, actual.getIdEstudiante()));
        campoGrupo = f.combo("Grupo", opcionesGrupo,
                actual == null ? null : Opcion.conId(opcionesGrupo, actual.getIdGrupo()));
        String nota = actual == null || actual.getNotaFinal() == null ? "" : String.valueOf(actual.getNotaFinal());
        campoNota = f.texto("Nota final", nota);
        campoEstado = f.combo("Estado", ESTADOS, actual == null ? InscripcionCurso.ACTIVO : actual.getEstado());
        f.nota("Nota de 0.0 a 5.0; déjala vacía si aún no hay.  Se aprueba con 3.0  ");
    }

    @Override
    protected void leerFormulario(InscripcionCurso destino) {
        destino.setIdEstudiante(idElegido(campoEstudiante, "Primero registra al menos un estudiante."));
        destino.setIdGrupo(idElegido(campoGrupo, "Primero registra al menos un grupo."));
        String nota = campoNota.getText().trim().replace(',', '.');
        if (nota.isEmpty()) {
            destino.setNotaFinal(null);
        } else {
            try {
                destino.setNotaFinal(Float.parseFloat(nota));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("La nota debe ser un número, por ejemplo 4.5.");
            }
        }
        destino.setEstado((String) campoEstado.getSelectedItem());
    }

    /** Describe cada grupo como "Materia - Aula, Horario". */
    private List<Opcion> opcionesGrupo() {
        Map<Integer, String> nombresMaterias = new HashMap<>();
        materias.listar().forEach(m -> nombresMaterias.put(m.getIdMateria(), m.getNombreMateria()));
        return grupos.listar().stream()
                .map((Grupo g) -> new Opcion(g.getIdGrupo(),
                        nombresMaterias.getOrDefault(g.getIdMateria(), "Materia #" + g.getIdMateria())
                                + " - " + g.getAula() + ", " + g.getHorario()))
                .toList();
    }
}
