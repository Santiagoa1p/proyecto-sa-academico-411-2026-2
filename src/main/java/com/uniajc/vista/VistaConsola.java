package com.uniajc.vista;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.dao.DataAccessException;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.modelo.Materia;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class VistaConsola {
    private final Scanner scanner;
    private final ControladorEstudiante estudiantes;
    private final ControladorDocente docentes;
    private final ControladorMateria materias;
    private final ControladorGrupo grupos;
    private final ControladorInscripcionCurso inscripciones;

    public VistaConsola(
            Scanner scanner,
            ControladorEstudiante estudiantes,
            ControladorDocente docentes,
            ControladorMateria materias,
            ControladorGrupo grupos,
            ControladorInscripcionCurso inscripciones) {
        this.scanner = scanner;
        this.estudiantes = estudiantes;
        this.docentes = docentes;
        this.materias = materias;
        this.grupos = grupos;
        this.inscripciones = inscripciones;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar && scanner.hasNextLine()) {
            System.out.println("\n=== Sistema de Gestión Académica UNIAJC ===");
            System.out.println("1. Estudiantes");
            System.out.println("2. Docentes");
            System.out.println("3. Materias");
            System.out.println("4. Grupos");
            System.out.println("5. Inscripciones");
            System.out.println("0. Salir");
            switch (leerOpcion("Opción: ")) {
                case 1 -> gestionarEstudiantes();
                case 2 -> gestionarDocentes();
                case 3 -> gestionarMaterias();
                case 4 -> gestionarGrupos();
                case 5 -> gestionarInscripciones();
                case 0 -> continuar = false;
                default -> System.out.println("Opción no válida.");
            }
        }
        System.out.println("Hasta luego.");
    }

    private void gestionarEstudiantes() {
        while (scanner.hasNextLine()) {
            int opcion = leerOpcion("\nEstudiantes: 1 Listar | 2 Registrar | 3 Editar | 4 Eliminar | 0 Volver: ");
            if (opcion == 0) {
                return;
            }
            switch (opcion) {
                case 1 -> ejecutar(() -> mostrarEstudiantes(estudiantes.listar()));
                case 2 -> ejecutar(() -> {
                    Estudiante estudiante = leerEstudiante(0);
                    estudiantes.registrar(estudiante);
                    System.out.println("Estudiante registrado.");
                });
                case 3 -> ejecutar(() -> {
                    int id = leerId("ID del estudiante: ");
                    Estudiante estudiante = buscar(estudiantes.buscar(id), "No existe el estudiante.");
                    copiar(leerEstudiante(id), estudiante);
                    estudiantes.actualizar(estudiante);
                    System.out.println("Estudiante actualizado.");
                });
                case 4 -> ejecutar(() -> {
                    estudiantes.eliminar(leerId("ID del estudiante: "));
                    System.out.println("Estudiante eliminado.");
                });
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void gestionarDocentes() {
        while (scanner.hasNextLine()) {
            int opcion = leerOpcion("\nDocentes: 1 Listar | 2 Registrar | 3 Editar | 4 Eliminar | 0 Volver: ");
            if (opcion == 0) {
                return;
            }
            switch (opcion) {
                case 1 -> ejecutar(() -> mostrarDocentes(docentes.listar()));
                case 2 -> ejecutar(() -> {
                    docentes.registrar(leerDocente(0));
                    System.out.println("Docente registrado.");
                });
                case 3 -> ejecutar(() -> {
                    int id = leerId("ID del docente: ");
                    Docente docente = buscar(docentes.buscar(id), "No existe el docente.");
                    copiar(leerDocente(id), docente);
                    docentes.actualizar(docente);
                    System.out.println("Docente actualizado.");
                });
                case 4 -> ejecutar(() -> {
                    docentes.eliminar(leerId("ID del docente: "));
                    System.out.println("Docente eliminado.");
                });
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void gestionarMaterias() {
        while (scanner.hasNextLine()) {
            int opcion = leerOpcion("\nMaterias: 1 Listar | 2 Registrar | 3 Editar | 4 Eliminar | 0 Volver: ");
            if (opcion == 0) {
                return;
            }
            switch (opcion) {
                case 1 -> ejecutar(() -> mostrarMaterias(materias.listar()));
                case 2 -> ejecutar(() -> {
                    materias.registrar(leerMateria(0));
                    System.out.println("Materia registrada.");
                });
                case 3 -> ejecutar(() -> {
                    int id = leerId("ID de la materia: ");
                    Materia materia = buscar(materias.buscar(id), "No existe la materia.");
                    copiar(leerMateria(id), materia);
                    materias.actualizar(materia);
                    System.out.println("Materia actualizada.");
                });
                case 4 -> ejecutar(() -> {
                    materias.eliminar(leerId("ID de la materia: "));
                    System.out.println("Materia eliminada.");
                });
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void gestionarGrupos() {
        while (scanner.hasNextLine()) {
            int opcion = leerOpcion("\nGrupos: 1 Listar | 2 Registrar | 3 Editar | 4 Eliminar | 0 Volver: ");
            if (opcion == 0) {
                return;
            }
            switch (opcion) {
                case 1 -> ejecutar(() -> mostrarGrupos(grupos.listar()));
                case 2 -> ejecutar(() -> {
                    grupos.registrar(leerGrupo(0));
                    System.out.println("Grupo registrado.");
                });
                case 3 -> ejecutar(() -> {
                    int id = leerId("ID del grupo: ");
                    Grupo grupo = buscar(grupos.buscar(id), "No existe el grupo.");
                    copiar(leerGrupo(id), grupo);
                    grupos.actualizar(grupo);
                    System.out.println("Grupo actualizado.");
                });
                case 4 -> ejecutar(() -> {
                    grupos.eliminar(leerId("ID del grupo: "));
                    System.out.println("Grupo eliminado.");
                });
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void gestionarInscripciones() {
        while (scanner.hasNextLine()) {
            int opcion = leerOpcion("\nInscripciones: 1 Listar | 2 Registrar | 3 Editar | 4 Eliminar | 0 Volver: ");
            if (opcion == 0) {
                return;
            }
            switch (opcion) {
                case 1 -> ejecutar(() -> mostrarInscripciones(inscripciones.listar()));
                case 2 -> ejecutar(() -> {
                    inscripciones.registrar(leerInscripcion(0));
                    System.out.println("Inscripción registrada.");
                });
                case 3 -> ejecutar(() -> {
                    int id = leerId("ID de la inscripción: ");
                    InscripcionCurso inscripcion = buscar(inscripciones.buscar(id), "No existe la inscripción.");
                    copiar(leerInscripcion(id), inscripcion);
                    inscripciones.actualizar(inscripcion);
                    System.out.println("Inscripción actualizada.");
                });
                case 4 -> ejecutar(() -> {
                    inscripciones.eliminar(leerId("ID de la inscripción: "));
                    System.out.println("Inscripción eliminada.");
                });
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private Estudiante leerEstudiante(int id) {
        return new Estudiante(id, leerTexto("Nombre: "), leerTexto("Apellido: "), leerTexto("Email: "));
    }

    private Docente leerDocente(int id) {
        return new Docente(
                id,
                leerTexto("Nombre: "),
                leerTexto("Especialidad: "),
                leerTexto("Email: "));
    }

    private Materia leerMateria(int id) {
        return new Materia(id, leerTexto("Nombre de la materia: "), leerEntero("Créditos: "));
    }

    private Grupo leerGrupo(int id) {
        return new Grupo(
                id,
                leerEntero("ID de la materia: "),
                leerEntero("ID del docente: "),
                leerTexto("Aula: "),
                leerTexto("Horario: "));
    }

    private InscripcionCurso leerInscripcion(int id) {
        int idEstudiante = leerEntero("ID del estudiante: ");
        int idGrupo = leerEntero("ID del grupo: ");
        String notaTexto = leerTexto("Nota final (vacío si está pendiente): ");
        Float nota = notaTexto.isBlank() ? null : convertirDecimal(notaTexto, "La nota");
        String estado = leerTexto("Estado (ACTIVO, APROBADO, REPROBADO, CANCELADO): ");
        return new InscripcionCurso(id, idEstudiante, idGrupo, nota, estado);
    }

    private void mostrarEstudiantes(List<Estudiante> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }
        lista.forEach(e -> System.out.printf("%d | %s %s | %s%n",
                e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()));
    }

    private void mostrarDocentes(List<Docente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay docentes registrados.");
            return;
        }
        lista.forEach(d -> System.out.printf("%d | %s | %s | %s%n",
                d.getIdDocente(), d.getNombre(), d.getEspecialidad(), d.getEmail()));
    }

    private void mostrarMaterias(List<Materia> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay materias registradas.");
            return;
        }
        lista.forEach(m -> System.out.printf("%d | %s | %d créditos%n",
                m.getIdMateria(), m.getNombreMateria(), m.getCreditos()));
    }

    private void mostrarGrupos(List<Grupo> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay grupos registrados.");
            return;
        }
        lista.forEach(g -> System.out.printf("%d | Materia %d | Docente %d | %s | %s%n",
                g.getIdGrupo(), g.getIdMateria(), g.getIdDocente(), g.getAula(), g.getHorario()));
    }

    private void mostrarInscripciones(List<InscripcionCurso> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay inscripciones registradas.");
            return;
        }
        lista.forEach(i -> System.out.printf("%d | Estudiante %d | Grupo %d | Nota %s | %s%n",
                i.getIdInscripcion(), i.getIdEstudiante(), i.getIdGrupo(),
                i.getNotaFinal() == null ? "pendiente" : i.getNotaFinal(), i.getEstado()));
    }

    private int leerOpcion(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero.");
            }
        }
    }

    private int leerId(String mensaje) {
        return leerEntero(mensaje);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero.");
            }
        }
    }

    private Float convertirDecimal(String valor, String campo) {
        try {
            return Float.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número válido.");
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }

    private void ejecutar(Runnable operacion) {
        try {
            operacion.run();
        } catch (IllegalArgumentException | DataAccessException e) {
            System.out.println("No se pudo completar la operación: " + e.getMessage());
        }
    }

    private <T> T buscar(Optional<T> resultado, String mensaje) {
        return resultado.orElseThrow(() -> new IllegalArgumentException(mensaje));
    }

    private void copiar(Estudiante origen, Estudiante destino) {
        destino.setNombre(origen.getNombre());
        destino.setApellido(origen.getApellido());
        destino.setEmail(origen.getEmail());
    }

    private void copiar(Docente origen, Docente destino) {
        destino.setNombre(origen.getNombre());
        destino.setEspecialidad(origen.getEspecialidad());
        destino.setEmail(origen.getEmail());
    }

    private void copiar(Materia origen, Materia destino) {
        destino.setNombreMateria(origen.getNombreMateria());
        destino.setCreditos(origen.getCreditos());
    }

    private void copiar(Grupo origen, Grupo destino) {
        destino.setIdMateria(origen.getIdMateria());
        destino.setIdDocente(origen.getIdDocente());
        destino.setAula(origen.getAula());
        destino.setHorario(origen.getHorario());
    }

    private void copiar(InscripcionCurso origen, InscripcionCurso destino) {
        destino.setIdEstudiante(origen.getIdEstudiante());
        destino.setIdGrupo(origen.getIdGrupo());
        destino.setNotaFinal(origen.getNotaFinal());
        destino.setEstado(origen.getEstado());
    }
}
