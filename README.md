# PRÁCTICA ACADÉMICA - SISTEMA DE GESTIÓN UNIAJC (MVC + DB)

## Objetivo
Replicar el ecosistema académico propuesto en el diagrama Mermaid, siguiendo los estándares de arquitectura en capas (Entidad, DAO, Service, Controller, Vista).

## Diagrama del Ecosistema
```mermaid
classDiagram
    class Estudiante {
        +int id_estudiante
        +string nombre
        +string apellido
        +string email
    }

    class Docente {
        +int id_docente
        +string nombre
        +string especialidad
    }

    class Materia {
        +int id_materia
        +string nombre_materia
        +int creditos
    }

    class Grupo {
        +int id_grupo
        +int id_materia
        +int id_docente
        +string aula
        +string horario
    }

    class Inscripcion_Curso {
        +int id_inscripcion
        +int id_estudiante
        +int id_grupo
        +float nota_final
        +string estado
    }

    Estudiante "1" -- "*" Inscripcion_Curso : se inscribe
    Grupo "1" -- "*" Inscripcion_Curso : contiene alumnos
    Materia "1" -- "*" Grupo : se dicta en
    Docente "1" -- "*" Grupo : imparte
```

## Instrucciones para el Estudiante:
1.  **Crea tu propia rama:** `feature/practica-[TuNombre]`.
2.  **Entidad:** Crea las clases en el paquete `com.uniajc.modelo` para las entidades restantes.
3.  **DAO:** Implementa la persistencia en `com.uniajc.modelo` usando JDBC y `ConexionDatabase`.
4.  **Servicio:** Agrega la lógica de negocio (validaciones de integridad).
5.  **Vistas:** Implementa interfaces tanto para Scanner como para Swing.
6.  **Pruebas:** Asegúrate de que tu código pase los tests de validación del docente.

## Configuración de BD:
Usa el archivo `config.properties` en la raíz para configurar tu conexión a MySQL, Postgres (Neon) o SQLite local.

### Paso a paso con Neon + DBeaver

1. **Crear la base en Neon:** entra a [neon.tech](https://neon.tech), inicia sesión y pulsa **New Project** (cualquier nombre, región AWS us-east).
2. **Copiar la conexión:** en el panel del proyecto pulsa **Connect** y copia el *connection string*. Se ve así:
   `postgresql://neondb_owner:CLAVE@ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require`
3. **Configurar el proyecto:** copia `config.example.properties` como `config.properties` (en la raíz) y pega ese texto en `URL`. Puedes dejar `USERNAME` y `PASSWORD` vacíos: se toman de la URL. También acepta el formato JDBC clásico (`jdbc:postgresql://...` + usuario y clave aparte). `config.properties` está en `.gitignore`, así que tu clave no se sube a GitHub.
4. **Crear las tablas desde DBeaver:** *Nueva conexión → PostgreSQL*, con el host, la base (`neondb`), el usuario y la clave de Neon. En la pestaña **SSL** activa *Usar SSL* con modo `require`. Abre un editor SQL y ejecuta completo (**Alt+X**):
   - `src/main/resources/db/schema.sql` → crea el esquema `"practica-mvc"` y las 5 tablas.
   - `src/main/resources/db/datos-prueba.sql` → (opcional) datos de ejemplo.

> Neon suspende la base tras unos minutos sin uso; la primera consulta después puede tardar un par de segundos. La aplicación se reconecta sola.

## Ejecutar

Abre en VS Code **la carpeta del proyecto** (donde está `pom.xml`) para que encuentre `config.properties`.

| Qué | Cómo |
|-----|------|
| Ventana Swing | VS Code → *Run and Debug* → **Sistema académico (Swing)** → F5 |
| Consola (Scanner) | VS Code → *Run and Debug* → **Sistema académico (Consola)** → F5 |
| Desde terminal | `mvn compile exec:java` (pregunta qué interfaz usar) o `mvn compile exec:java -Dexec.args=swing` |
| Pruebas | `mvn test` (no necesitan base de datos: usan DAOs en memoria) |

## Arquitectura implementada

```
Vista (consola / Swing) → Controlador → Servicio (validaciones) → DAO (JDBC) → PostgreSQL
```

| Capa | Paquete | Clases |
|------|---------|--------|
| Modelo | `com.uniajc.modelo` | `Estudiante`, `Docente`, `Materia`, `Grupo`, `InscripcionCurso` |
| DAO | `com.uniajc.dao` | `EstudianteDao`, `DocenteDao`, `MateriaDao`, `GrupoDao`, `InscripcionCursoDao` |
| Servicio | `com.uniajc.servicios` | `EstudianteService`, `DocenteService`, `MateriaService`, `GrupoService`, `InscripcionCursoService` |
| Controlador | `com.uniajc.controlador` | `ControladorEstudiante`, `ControladorDocente`, `ControladorMateria`, `ControladorGrupo`, `ControladorInscripcionCurso` |
| Vista consola | `com.uniajc.vista` | `VistaConsola` (menú) + `VistaEstudiante`, `VistaDocente`, `VistaMateria`, `VistaGrupo`, `VistaInscripcionCurso` |
| Vista Swing | `com.uniajc.vista` | `VistaPrincipalSwing` (menú + CardLayout) + `VistaEstudianteSwing`, `VistaDocenteSwing`, `VistaMateriaSwing`, `VistaGrupoSwing`, `VistaInscripcionCursoSwing` |
| Conexión | `com.uniajc.db` | `ConexionDatabase` (singleton que se reconecta si la conexión se cae) |

### Reglas de negocio (servicios)

- Nombres y apellidos solo con letras; emails con formato válido y **sin repetir**.
- Materias con nombre único y **1 a 10 créditos**.
- Un grupo exige materia y docente existentes; ni el **docente** ni el **aula** pueden repetirse en el mismo horario.
- Un estudiante no puede inscribirse **dos veces en el mismo grupo**.
- Nota final de **0.0 a 5.0** (opcional). `APROBADO` exige nota ≥ 3.0 y `REPROBADO` nota < 3.0.
- No se puede eliminar un estudiante, docente, materia o grupo que tenga registros relacionados.

---
*Recuerda: El Modelo nunca debe hablar con el usuario (sin System.out). El Controlador orquesta y la Vista interactúa.*

Creado para una prueba.