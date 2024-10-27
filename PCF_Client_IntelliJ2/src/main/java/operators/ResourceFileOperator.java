package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.ArchiveHomework;
import objects.ResourceFile;
import tools.ResponseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResourceFileOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceFileOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    public void saveFileData(String[] serverArguments) {
        ResourceFile resourceFile = new ResourceFile(sharedStore.getSelectedResource(), serverArguments[1]);
        sharedStore.setSelectedResource(resourceFile);
    }

    public void uploadFile() { // [Usuario -> Foto] && [Profesor -> Archivo]
        try {
            if (!sharedStore.getFilePath().equals("false")) {
                connectionToServer.sendArchive(sharedStore.getFilePath());
                finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
            }
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Archivo no encontrado.",
                        "No se ha podido localizar el archivo que se pretende subir."));
            } else {
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error en la comunicación.",
                        "Se ha producido un error inesperado durante la subida del archivo."));
            }
        }
    }

    public void uploadHomeworkFiles() { // [Usuario -> Foto] && [Profesor -> Archivo]
        try {
            for(ArchiveHomework archiveHomework: sharedStore.getFilesHomework()){
                if(archiveHomework.getId() == -1) { // Se suben los nuevos, no los antiguos
                    connectionToServer.sendArchive(archiveHomework.getPath());
                }
            }

            finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Archivo no encontrado.",
                        "No se ha podido localizar el archivo que se pretende subir."));
            } else {
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error en la comunicación.",
                        "Se ha producido un error inesperado durante la subida del archivo."));
            }
        }
    }

    public void downloadFile(String[] serverArguments) { // Rol Alumno (y Profesor)
        String downloadsFile = System.getProperty("user.home") + File.separator + "Descargas" + File.separator; // "Pepito/Descargas/"

        connectionToServer.receiveArchive(downloadsFile + serverArguments[1]); // "Pepito/Descargas/nombreArchivo.pdf"

        //ResourceFile resourceFile = new ResourceFile(sharedStore.getSelectedResource(), serverArguments[1]);
        //sharedStore.setSelectedResource(resourceFile);

        finalArguments = connectionToServer.receiveServerResponse().split("#"); // Fin del Envío
    }
}
