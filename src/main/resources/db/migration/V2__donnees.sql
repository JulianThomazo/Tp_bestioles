-- Starting data of the refuge. Passwords are placeholders until the accounts
-- receive hashed passwords and roles (V4).

INSERT INTO species (id, common_name, latin_name) VALUES
    (1, 'Chat',  'Felis silvestris catus'),
    (2, 'Chien', 'Canis lupus familiaris'),
    (3, 'Lapin', 'Oryctolagus cuniculus');

INSERT INTO animal (id, name, sex, color, species_id) VALUES
    (1, 'Rex',     'M', 'Fauve', 2),
    (2, 'Mia',     'F', 'Noir',  1),
    (3, 'Caramel', 'M', 'Roux',  3),
    (4, 'Nala',    'F', 'Tigré', 1),
    (5, 'Oscar',   'M', NULL,    2),
    (6, 'Pomme',   'F', 'Gris',  3),
    (7, 'Tigrou',  'M', 'Tigré', 1);

INSERT INTO person (id, firstname, lastname, age, login, mdp, active) VALUES
    (1, 'Henri', 'Lamarque', 41, 'hlamarque', '{noop}a-remplacer', 1),
    (2, 'Lea',   'Martin',   23, 'lmartin',   '{noop}a-remplacer', 1),
    (3, 'Karim', 'Benali',   35, 'kbenali',   '{noop}a-remplacer', 1),
    (4, 'Sofia', 'Rossi',    27, 'srossi',    '{noop}a-remplacer', 1);

-- Caramel (3) has no owner, Mia (2) has two.
INSERT INTO person_animals (person_id, animals_id) VALUES
    (2, 1), (2, 2),
    (3, 2), (3, 4),
    (4, 5), (4, 6), (4, 7);

INSERT INTO role (id, label) VALUES
    (1, 'ROLE_EMPLOYE'),
    (2, 'ROLE_USER'),
    (3, 'ROLE_ADMIN');
