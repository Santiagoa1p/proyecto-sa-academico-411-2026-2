CREATE SCHEMA IF NOT EXISTS "practica-mvc";

CREATE TABLE IF NOT EXISTS "practica-mvc".estudiantes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "practica-mvc".docentes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "practica-mvc".materias (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    credits INT NOT NULL CHECK (credits >= 1 AND credits <= 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "practica-mvc".grupos (
    id SERIAL PRIMARY KEY,
    id_materia INT NOT NULL REFERENCES "practica-mvc".materias(id),
    id_docente INT NOT NULL REFERENCES "practica-mvc".docentes(id),
    classroom VARCHAR(50) NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "practica-mvc".inscripciones (
    id SERIAL PRIMARY KEY,
    id_estudiante INT NOT NULL REFERENCES "practica-mvc".estudiantes(id),
    id_grupo INT NOT NULL REFERENCES "practica-mvc".grupos(id),
    final_grade DECIMAL(5,2) CHECK (final_grade >= 0 AND final_grade <= 5),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
        CHECK (status IN ('ACTIVO', 'APROBADO', 'REPROBADO', 'CANCELADO')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_estudiante, id_grupo)
);
