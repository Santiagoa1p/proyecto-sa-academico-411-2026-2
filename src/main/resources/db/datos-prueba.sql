-- Datos de ejemplo (opcional), SOLO para tu propia base recién creada con schema.sql.
-- ¡No lo ejecutes en la base compartida del profesor! Allí ya hay datos de
-- tus compañeros y los IDs usados abajo (1, 2, 3) no coincidirían.

INSERT INTO "practica-mvc".estudiantes (name, lastname, email) VALUES
    ('Laura', 'Gómez', 'laura.gomez@estudiante.uniajc.edu.co'),
    ('Andrés', 'Ríos', 'andres.rios@estudiante.uniajc.edu.co'),
    ('Valentina', 'Muñoz', 'valentina.munoz@estudiante.uniajc.edu.co');

INSERT INTO "practica-mvc".docentes (nombre, especialidad) VALUES
    ('Carlos Pérez', 'Bases de datos'),
    ('María López', 'Ingeniería de software');

INSERT INTO "practica-mvc".materias (nombre_materia, creditos) VALUES
    ('Programación orientada a objetos', 3),
    ('Bases de datos I', 4);

INSERT INTO "practica-mvc".grupos (id_materia, id_docente, aula, horario) VALUES
    (1, 2, 'A-201', 'Lunes 18:00-20:00'),
    (2, 1, 'B-105', 'Miércoles 18:00-20:00');

INSERT INTO "practica-mvc".inscripciones (id_estudiante, id_grupo, nota_final, estado) VALUES
    (1, 1, 4.2, 'APROBADO'),
    (2, 1, NULL, 'ACTIVO'),
    (3, 2, 2.5, 'REPROBADO');
