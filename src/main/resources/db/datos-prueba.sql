-- Datos de ejemplo (opcional). Ejecútalo después de schema.sql.

INSERT INTO "practica-mvc".estudiantes (name, lastname, email) VALUES
    ('Laura', 'Gómez', 'laura.gomez@estudiante.uniajc.edu.co'),
    ('Andrés', 'Ríos', 'andres.rios@estudiante.uniajc.edu.co'),
    ('Valentina', 'Muñoz', 'valentina.munoz@estudiante.uniajc.edu.co');

INSERT INTO "practica-mvc".docentes (name, specialty, email) VALUES
    ('Carlos Pérez', 'Bases de datos', 'carlos.perez@uniajc.edu.co'),
    ('María López', 'Ingeniería de software', 'maria.lopez@uniajc.edu.co');

INSERT INTO "practica-mvc".materias (name, credits) VALUES
    ('Programación orientada a objetos', 3),
    ('Bases de datos I', 4);

INSERT INTO "practica-mvc".grupos (id_materia, id_docente, classroom, schedule) VALUES
    (1, 2, 'A-201', 'Lunes 18:00-20:00'),
    (2, 1, 'B-105', 'Miércoles 18:00-20:00');

INSERT INTO "practica-mvc".inscripciones (id_estudiante, id_grupo, final_grade, status) VALUES
    (1, 1, 4.20, 'APROBADO'),
    (2, 1, NULL, 'ACTIVO'),
    (3, 2, 2.50, 'REPROBADO');
