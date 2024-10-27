package communication;


public class ProtocolMessages {

    // Formato de mensajes con argumentos: "mensaje"¿arg1¿arg2¿arg3...
    private final String[] CLIENT_MESSAGES = {
            "C0", // Salir
            "C1", // Registro Usuario
            "C2", // Iniciar Sesión
            "C3", // Cerrar Sesión
            "C4", // Ver Perfil (Propio o ajeno [por id])
            "C5", // Actualizar Usuario
            "C6", // Borrar Usuario

            "C7", // Enviar Mensaje
            "C8", // Enviar Solicitud
            "C9", // Enviar Invitación
            "C10", // Consultar Bandeja de Entrada - Recibidos
            "C11", // Consultar Bandeja de Salida - Enviados
            "C12", // Leer Mensaje (Devuelve al Cliente y Cambia a Leido)
            "C13", // Aceptar Solicitud
            "C14", // Aceptar Invitación
            "C15", // Borrar Mensaje

            "C16", // Alta Curso
            "C17", // Actualizar Curso
            "C18", // Listar Cursos impartidos
            "C19", // Listar Cursos recibidos
            "C20", // Borrar Curso

            "C21", // Motor de Busqueda: Usuarios
            "C22", // Motor de Busqueda: Cursos

            "C23", // Apertura de Curso [AUTOMÁTICA]

            "C24", // Aula Virtual

            "C25", // Alta de Tema
            "C26", // Actualizar Tema
            "C27", // Borrar Tema
            "C28", // Reordenar Tema

            "C29", // Alta de Recurso (Enlace)
            "C30", // Alta de Recurso (Archivo)
            "C31", // Alta de Recurso (Tarea)
            "C32", // Alta de Recurso (Test)
            "C33", // Alta de Pregunta/Respuestas

            "C34", // Actualizar Recurso (Enlace)
            "C35", // Actualizar Recurso (Archivo)
            "C36", // Actualizar Recurso (Tarea)
            "C37", // Actualizar de Recurso (Test)
            "C38", // Actualizar de Pregunta/Respuestas

            "C39", // Ver Enlace
            "C40", // Ver Archivo
            "C41", // Descargar Archivo
            "C42", // Ver Tarea
            "C43", // Subir Archivo (Tarea)
            "C44", // Ver Test [Profesor]
            "C45", // Ver Test [Alumno]
            "C46", // Resolver Test

            "C47", // Borrar Recurso
            "C48", // Reordenar Recursos

            "C49", // Listar Alumnos y Tareas subidas
            "C50", // Asignar Nota
            "C51", // Envio de Correo a Todos los Alumnos

            "C52", // Listar Registros

            "C53", // Finalización de Curso

            "C54", // Registro de Test "Resuelto"

            "Default"};// Error Grave (Cierre de Sesión)

    private final String[] SERVER_MESSAGES = {
            "S0", // Salir
            "S1", // Registro Usuario
            "S2", // Iniciar Sesión
            "S3", // Cerrar Sesión
            "S4", // Ver Perfil (Propio o ajeno [por id])
            "S5", // Actualizar Usuario
            "S6", // Borrar Usuario

            "S7", // Enviar Mensaje
            "S8", // Enviar Solicitud
            "S9", // Enviar Invitación
            "S10", // Consultar Bandeja de Entrada - Recibidos
            "S11", // Consultar Bandeja de Salida - Enviados
            "S12", // Leer Mensaje (Devuelve al Cliente y Cambia a Leido)
            "S13", // Aceptar Solicitud
            "S14", // Aceptar Invitación
            "S15", // Borrar Mensaje

            "S16", // Alta Curso
            "S17", // Actualizar Curso
            "S18", // Listar Cursos impartidos
            "S19", // Listar Cursos recibidos
            "S20", // Borrar Curso

            "S21", // Motor de Busqueda: Usuarios
            "S22", // Motor de Busqueda: Cursos

            "S23", // Apertura de Curso [AUTOMÁTICA]

            "S24", // Aula Virtual

            "S25", // Alta de Tema
            "S26", // Actualizar Tema
            "S27", // Borrar Tema
            "S28", // Reordenar Tema

            "S29", // Alta de Recurso (Enlace)
            "S30", // Alta de Recurso (Archivo)
            "S31", // Alta de Recurso (Tarea)
            "S32", // Alta de Recurso (Test)
            "S33", // Alta de Pregunta/Respuestas

            "S34", // Actualizar Recurso (Enlace)
            "S35", // Actualizar Recurso (Archivo)
            "S36", // Actualizar Recurso (Tarea)
            "S37", // Actualizar de Recurso (Test)
            "S38", // Actualizar de Pregunta/Respuestas

            "S39", // Ver Enlace
            "S40", // Ver Archivo
            "S41", // Descargar Archivo
            "S42", // Ver Tarea
            "S43", // Subir Archivo (Tarea)
            "S44", // Ver Test [Profesor]
            "S45", // Ver Test [Alumno]
            "S46", // Resolver Test

            "S47", // Borrar Recurso
            "S48", // Reordenar Recursos

            "S49", // Listar Alumnos y Tareas subidas
            "S50", // Asignar Nota
            "S51", // Envio de Correo a Todos los Alumnos

            "S52", // Listar Registros

            "S53", // Finalización de Curso

            "Default"};// Error Grave (Cierre de Sesión)

    public int getTotalServerMessages() {
        return SERVER_MESSAGES.length;
    }

    public String getClientArgument(int which) {
        return CLIENT_MESSAGES[which];
    }

    public String getServerMessage(int which) {
        return SERVER_MESSAGES[which];
    }

}
