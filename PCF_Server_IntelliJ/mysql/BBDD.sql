DROP DATABASE PCF;
-- Es más rápido para hacer cambios
CREATE DATABASE PCF;

USE PCF;

CREATE TABLE pcf_user (
	id_user INT AUTO_INCREMENT PRIMARY KEY,
	photo_user BOOLEAN DEFAULT FALSE,
	name_user VARCHAR(50) NOT NULL,
	email_user VARCHAR(50) UNIQUE NOT NULL,
	password_user VARCHAR(100) NOT NULL,
	province_user VARCHAR(50) NOT NULL,
	township_user VARCHAR(50) NOT NULL,
	short_presentation_user VARCHAR(500) NOT NULL,
	long_presentation_user VARCHAR(1000) -- Opcional
);

CREATE TABLE pcf_message(
	id_message INT AUTO_INCREMENT PRIMARY KEY,
	subject_message VARCHAR(100) NOT NULL,
	text_message VARCHAR(5000) NOT NULL,
	sent_date_message DATE NOT NULL,
	sent_time_message TIME NOT NULL,
	readed_message BOOLEAN DEFAULT false,
	type_message VARCHAR(50) NOT NULL, -- Mensaje / Solicitud
	id_teacher_course INT, -- En caso de Solicitud, el curso de dicho profesor (el destinatario) que se esta solicitando
	id_sender_user INT, -- Para los Borrados de Usuarios
	id_receiver_user INT,
	deleted_by_sender_message BOOLEAN DEFAULT false, -- Para los borrados de Mensajes
	deleted_by_receiver_message BOOLEAN DEFAULT false
);

CREATE TABLE pcf_course(
	id_course INT AUTO_INCREMENT PRIMARY KEY,
	name_course VARCHAR(50) NOT NULL,
	short_presentation_course VARCHAR(500) NOT NULL,
	long_presentation_course VARCHAR(1000), -- Opcional
	start_date_course DATE NOT NULL,
	end_date_course DATE NOT NULL, -- Para el acceso al Aula Virtual
	hidden_course BOOLEAN DEFAULT true, -- Para el Motor de Búsquedas
	closed_course BOOLEAN DEFAULT true, -- Da acceso al Aula Virtual de forma prematura y da por finalizado el curso: Debe ser ejecutado manualmente por el profesor
	id_teacher_user INT NOT NULL
);
ALTER TABLE pcf_course ADD UNIQUE KEY(name_course, id_teacher_user); -- Para evitar duplicados:  SELECT Id Curso

CREATE TABLE pcf_course_tag( -- Para el Motor de Busqueda
	id_tag INT AUTO_INCREMENT PRIMARY KEY,
	word_tag VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE pcf_unit(
	id_unit INT AUTO_INCREMENT PRIMARY KEY,
	title_unit VARCHAR(50) NOT NULL,
	order_unit INT NOT NULL, -- Dentro del Curso
	hidden_unit BOOLEAN DEFAULT false,
	percentage_exercises INT DEFAULT 0,
	percentage_controls INT DEFAULT 0,
	percentage_exams INT DEFAULT 0,
	percentage_tests INT DEFAULT 0,
	id_course INT NOT NULL
);
ALTER TABLE pcf_unit ADD UNIQUE KEY(title_unit, id_course); -- Para evitar duplicados:  SELECT Id Tema

CREATE TABLE pcf_resource(
	id_resource INT AUTO_INCREMENT PRIMARY KEY,
	title_resource VARCHAR(50) NOT NULL,
	presentation_resource VARCHAR(1000), -- Un Test por ejemplo no tiene porque tener enunciado
	type_resource VARCHAR(50) NOT NULL, -- Archivo / Tarea / Enlace / Test
	order_resource INT NOT NULL, -- Dentro del Tema
	hidden_resource BOOLEAN DEFAULT false,
	id_unit INT NOT NULL
);
ALTER TABLE pcf_resource ADD UNIQUE KEY(title_resource, id_unit); -- Para evitar duplicados:  SELECT Id Recurso

CREATE TABLE pcf_archive(
	id_archive INT PRIMARY KEY, -- FOREIGN KEY id_resource
	path_archive VARCHAR(500) NOT NULL
);

CREATE TABLE pcf_link(
	id_link INT PRIMARY KEY, -- FOREIGN KEY id_resource
	url_link VARCHAR(500) NOT NULL
);

CREATE TABLE pcf_homework(
	id_homework INT PRIMARY KEY, -- FOREIGN KEY id_resource
	init_date_homework DATE NOT NULL,
	init_time_homework TIME NOT NULL,
	end_date_homework DATE NOT NULL,
	end_time_homework TIME NOT NULL,
	percentage_homework INT NOT NULL
);

CREATE TABLE pcf_homework_archive( -- Del Alumno, obviamente
	id_homework_archive INT AUTO_INCREMENT PRIMARY KEY,
	path_homework_archive VARCHAR(500) NOT NULL
);

CREATE TABLE pcf_test(
	id_test INT PRIMARY KEY, -- FOREIGN KEY id_resource
	questions_quantity INT NOT NULL,
	init_date_test DATE NOT NULL,
	init_time_test TIME NOT NULL,
	end_date_test DATE NOT NULL,
	end_time_test TIME NOT NULL,
	percentage_test INT NOT NULL
);
ALTER TABLE pcf_test ADD FOREIGN KEY(id_test) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Test - Recurso

CREATE TABLE pcf_test_question(
	id_test_question INT AUTO_INCREMENT PRIMARY KEY,
	text_test_question VARCHAR(1000) NOT NULL,
	id_test INT NOT NULL -- FOREIGN KEY id_test
);
ALTER TABLE pcf_test_question ADD FOREIGN KEY(id_test) REFERENCES pcf_test(id_test) ON DELETE CASCADE;

CREATE TABLE pcf_test_answer(
	id_test_answer INT AUTO_INCREMENT PRIMARY KEY,
	text_test_answer VARCHAR(1000) NOT NULL,
	correct_test_answer BOOLEAN NOT NULL,
	id_test_question INT NOT NULL -- FOREIGN KEY id_test
);
ALTER TABLE pcf_test_answer ADD FOREIGN KEY(id_test_question) REFERENCES pcf_test_question(id_test_question) ON DELETE CASCADE;

CREATE TABLE pcf_solved_test(
	id_solved_test INT AUTO_INCREMENT PRIMARY KEY,
	id_original_test INT NOT NULL,
	id_student INT NOT NULL,
	score DOUBLE
);
-- AMPLIACIÓN: Test de Prácticas
-- ALTER TABLE pcf_solved_test ADD UNIQUE KEY(id_solved_test, id_original_test, id_student); -- No puede haber Test Completados repetidos por un mismo Usuario

CREATE TABLE pcf_record(
	id_record INT AUTO_INCREMENT PRIMARY KEY,
	date_record DATE NOT NULL,
	time_record TIME NOT NULL,
	event_record VARCHAR(500) NOT NULL,
	id_student INT NOT NULL,
	id_resource INT NOT NULL
);

-- RELACIONES:

CREATE TABLE pcf_user_receives_course( -- Alumno <-> Curso
	id_user INT,
	id_course INT
);
ALTER TABLE pcf_user_receives_course ADD PRIMARY KEY(id_user, id_course); -- Un usuario no se puede apuntar más de una vez al mismo curso
ALTER TABLE pcf_user_receives_course ADD FOREIGN KEY(id_user) REFERENCES pcf_user(id_user) ON DELETE CASCADE;
ALTER TABLE pcf_user_receives_course ADD FOREIGN KEY(id_course) REFERENCES pcf_course(id_course) ON DELETE CASCADE;

CREATE TABLE pcf_tag_defines_course( -- Etiqueta <-> Curso
	id_tag INT,
	id_course INT
);
ALTER TABLE pcf_tag_defines_course ADD PRIMARY KEY(id_tag, id_course); -- Una etiqueta no se puede asignar más de una vez al mismo curso
ALTER TABLE pcf_tag_defines_course ADD FOREIGN KEY(id_tag) REFERENCES pcf_course_tag(id_tag) ON DELETE CASCADE;
ALTER TABLE pcf_tag_defines_course ADD FOREIGN KEY(id_course) REFERENCES pcf_course(id_course) ON DELETE CASCADE;

CREATE TABLE pcf_user_upload_homework_archive( -- Usuario <-> Tarea <-> Archivo
	id_user INT,
	id_homework_archive INT,
	id_homework INT,
	score DOUBLE
);
-- Hay que borrar los archivos Antes de borrar la Cuenta, el Curso, el Tema o la Tarea
ALTER TABLE pcf_user_upload_homework_archive ADD FOREIGN KEY(id_user) REFERENCES pcf_user(id_user) ON DELETE CASCADE;
ALTER TABLE pcf_user_upload_homework_archive ADD FOREIGN KEY(id_homework_archive) REFERENCES pcf_homework_archive(id_homework_archive) ON DELETE CASCADE;
ALTER TABLE pcf_user_upload_homework_archive ADD FOREIGN KEY(id_homework) REFERENCES pcf_homework(id_homework) ON DELETE CASCADE;

CREATE TABLE pcf_solved_test_answer(
	id_solved_test INT NOT NULL,
	id_test_answer INT NOT NULL,
	chosen BOOLEAN DEFAULT FALSE -- Por si se deja en blanco -> Se "selecciona" una erronea para poder conocer la pregunta a la que pertenece
);
ALTER TABLE pcf_solved_test_answer ADD FOREIGN KEY(id_solved_test) REFERENCES pcf_solved_test(id_solved_test) ON DELETE CASCADE;
ALTER TABLE pcf_solved_test_answer ADD FOREIGN KEY(id_test_answer) REFERENCES pcf_test_answer(id_test_answer) ON DELETE CASCADE;

ALTER TABLE pcf_record ADD FOREIGN KEY(id_student) REFERENCES pcf_user(id_user) ON DELETE CASCADE; -- Registros
ALTER TABLE pcf_record ADD FOREIGN KEY(id_resource) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE;

ALTER TABLE pcf_message ADD FOREIGN KEY(id_sender_user) REFERENCES pcf_user(id_user) ON DELETE SET NULL; -- Mensaje - Emisor
ALTER TABLE pcf_message ADD FOREIGN KEY(id_receiver_user) REFERENCES pcf_user(id_user) ON DELETE SET NULL; -- Mensaje - Receptor

ALTER TABLE pcf_course ADD FOREIGN KEY(id_teacher_user) REFERENCES pcf_user(id_user) ON DELETE CASCADE; -- Curso - Profesor
ALTER TABLE pcf_unit ADD FOREIGN KEY(id_course) REFERENCES pcf_course(id_course) ON DELETE CASCADE; -- Unidad - Curso
ALTER TABLE pcf_resource ADD FOREIGN KEY(id_unit) REFERENCES pcf_unit(id_unit) ON DELETE CASCADE; -- Recurso - Unidad

ALTER TABLE pcf_archive ADD FOREIGN KEY(id_archive) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Archivo - Recurso
ALTER TABLE pcf_link ADD FOREIGN KEY(id_link) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Link - Recurso
ALTER TABLE pcf_homework ADD FOREIGN KEY(id_homework) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Tarea - Recurso


