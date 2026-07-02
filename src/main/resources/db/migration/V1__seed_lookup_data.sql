-- Uloge korisnika
INSERT INTO role (role_name) VALUES
                                 ('STUDENT'),
                                 ('INSTRUCTOR'),
                                 ('ADMIN');

-- Statusi kursa
INSERT INTO course_status (course_status_name) VALUES
                                                   ('DRAFT'),
                                                   ('PUBLISHED'),
                                                   ('ARCHIVED');

-- Nivoi kursa
INSERT INTO course_level (course_level_name) VALUES
                                                 ('BEGINNER'),
                                                 ('INTERMEDIATE'),
                                                 ('ADVANCED');

-- Kategorije kursa
INSERT INTO course_category (course_category_name) VALUES
                                                       ('PROGRAMMING'),
                                                       ('DESIGN'),
                                                       ('BUSINESS'),
                                                       ('LANGUAGES'),
                                                       ('DATA_SCIENCE');

-- Tipovi lekcija
INSERT INTO lesson_type (lesson_type_name) VALUES
                                               ('VIDEO'),
                                               ('ARTICLE'),
                                               ('QUIZ'),
                                               ('ASSIGNMENT');

-- Tipovi materijala
INSERT INTO material_type (material_type_name) VALUES
                                                   ('PDF'),
                                                   ('IMAGE'),
                                                   ('LINK'),
                                                   ('PRESENTATION'),
                                                   ('VIDEO');

-- Statusi upisa (enrollment)
INSERT INTO enrollment_status (enrollment_status_name) VALUES
                                                           ('ACTIVE'),
                                                           ('COMPLETED'),
                                                           ('CANCELLED'),
                                                           ('SUSPENDED');

-- Tipovi notifikacija
INSERT INTO notification_type (notification_type_name) VALUES
                                                           ('SYSTEM'),
                                                           ('ENROLLMENT'),
                                                           ('COURSE'),
                                                           ('CERTIFICATE'),
                                                           ('REVIEW');