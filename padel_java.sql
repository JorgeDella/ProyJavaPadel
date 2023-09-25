--  Crear base de datos
CREATE DATABASE padel_java;
USE padel_java;

--  Crear la taula ADMIN
CREATE TABLE USUARIS(
    dni VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(20) NOT NULL,
    contrasenya VARCHAR(255) NOT NULL
);

--  Crear la taula USUARIS
CREATE TABLE USUARIS(
    dni VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(20) NOT NULL,
    cognoms VARCHAR(50) NOT NULL,
    correu VARCHAR(50) NOT NULL,
    telefon VARCHAR(20) NOT NULL,
    contrasenya VARCHAR(255) NOT NULL
);

--  Crear la taula PISTA
CREATE TABLE PISTA(
    id_pista INT AUTO_INCREMENT PRIMARY KEY,
    estat VARCHAR(20) NOT NULL,
    disponibilitat BOOLEAN NOT NULL,
    ubicacio VARCHAR(50) NOT NULL,
    sol VARCHAR(20) NOT NULL,
    pared VARCHAR(20) NOT NULL
);

--  Crear la taula PISTA
CREATE TABLE PISTA(
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    FOREIGN KEY (fk_dni) REFERENCES USUARIS(dni)
    FOREIGN KEY (fk_id_pista) REFERENCES PISTA(id_pista)
    dia_reserva DATE NOT NULL,
    hora_inici TIMESTAMP NOT NULL,
    hora_fi TIMESTAMP NOT NULL
);


