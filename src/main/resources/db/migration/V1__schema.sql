-- Bestioles schema, from the 2025-12-10 dump of the existing database.
-- Normalised where the dump disagreed with itself or with the entities:
-- every id is a signed INT (a Java Integer), person.active is TINYINT(1)
-- (validate refuses the dump's TINYINT(4) for a boolean), animal.sex holds one letter.

CREATE TABLE species (
    id          INT          NOT NULL AUTO_INCREMENT,
    common_name VARCHAR(50)  NOT NULL,
    latin_name  VARCHAR(200) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE animal (
    id         INT         NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    sex        VARCHAR(1)  NOT NULL,
    color      VARCHAR(50) NULL,
    species_id INT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_animal_sex CHECK (sex IN ('M', 'F')),
    CONSTRAINT fk_animal_species FOREIGN KEY (species_id) REFERENCES species (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE person (
    id        INT          NOT NULL AUTO_INCREMENT,
    firstname VARCHAR(50)  NOT NULL,
    lastname  VARCHAR(50)  NOT NULL,
    age       INT          NULL,
    login     VARCHAR(50)  NOT NULL,
    mdp       VARCHAR(100) NOT NULL,
    active    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_person_login UNIQUE (login)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE role (
    id    INT         NOT NULL AUTO_INCREMENT,
    label VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_role_label UNIQUE (label)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Join tables: no entity, a @ManyToMany on Person maps each of them.
CREATE TABLE person_animals (
    person_id  INT NOT NULL,
    animals_id INT NOT NULL,
    PRIMARY KEY (person_id, animals_id),
    CONSTRAINT fk_person_animals_person FOREIGN KEY (person_id) REFERENCES person (id),
    CONSTRAINT fk_person_animals_animal FOREIGN KEY (animals_id) REFERENCES animal (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE person_role (
    person_id INT NOT NULL,
    role_id   INT NOT NULL,
    PRIMARY KEY (person_id, role_id),
    CONSTRAINT fk_person_role_person FOREIGN KEY (person_id) REFERENCES person (id),
    CONSTRAINT fk_person_role_role FOREIGN KEY (role_id) REFERENCES role (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
