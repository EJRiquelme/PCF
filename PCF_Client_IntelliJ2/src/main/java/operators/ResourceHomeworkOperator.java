package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.ArchiveHomework;
import objects.ResourceHomework;
import objects.User;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class ResourceHomeworkOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceHomeworkOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    public void saveHomeworkData(String[] serverArguments) {
        LocalDate openDate = sharedStore.getConversions().convertStringToLocalDate(serverArguments[1]);
        LocalTime openTime = sharedStore.getConversions().convertStringToLocalTime(serverArguments[2]);
        LocalDate closeDate = sharedStore.getConversions().convertStringToLocalDate(serverArguments[3]);
        LocalTime closeTime = sharedStore.getConversions().convertStringToLocalTime(serverArguments[4]);
        int percentage = Integer.parseInt(serverArguments[5]);

        ResourceHomework resourceHomework = new ResourceHomework(sharedStore.getSelectedResource(), openDate, openTime, closeDate, closeTime, percentage);
        sharedStore.setSelectedResource(resourceHomework);

        if (sharedStore.getUser().getIdUser() != sharedStore.getSelectedCourse().getIdTeacher()) {
            LinkedList<ArchiveHomework> archiveHomeworks = new LinkedList<>();
            for (int i = 6; i < serverArguments.length; i += 2) {
                int idArchiveHomework = Integer.parseInt(serverArguments[i]);
                String pathArchiveHomework = serverArguments[i + 1];

                String fileSeparatorPutoWindows = File.separator;
                String[] pathsFile = pathArchiveHomework.split(fileSeparatorPutoWindows);
                String fileName = pathsFile[pathsFile.length - 1];

                ArchiveHomework archiveHomework = new ArchiveHomework(idArchiveHomework, pathArchiveHomework, fileName);
                archiveHomeworks.add(archiveHomework);
            }
            if (!archiveHomeworks.isEmpty()) {
                sharedStore.setFilesHomework(archiveHomeworks);
            }
        } else {
            LinkedHashMap<User, LinkedList<ArchiveHomework>> studentsFiles = new LinkedHashMap<>();

            for (int i = 6; i < serverArguments.length; i += 2) {
                int id = Integer.parseInt(serverArguments[i]);
                String userName = serverArguments[i + 1];
                double score = Double.parseDouble(serverArguments[i + 2]);

                User user = new User(id, userName, score);
                LinkedList<ArchiveHomework> archiveHomeworks = new LinkedList<>();

                for (int z = i + 3; z < serverArguments.length; z += 2) {
                    if (serverArguments[z].equals(">>>>>>>>>>")) {
                        i = z + 1;
                        break;
                    }
                    int idResource = Integer.parseInt(serverArguments[z]);
                    String path = serverArguments[z + 1];

                    String separator = File.separator;
                    String[] previous = path.split(separator);
                    String name = previous[previous.length - 1];

                    ArchiveHomework archiveHomework = new ArchiveHomework(idResource, path, name);
                    archiveHomeworks.add(archiveHomework);
                }

                studentsFiles.put(user, archiveHomeworks);
            }

            if (!studentsFiles.isEmpty()) {
                sharedStore.setStudentFilesMap(studentsFiles); // Cargamos los datos
            }
        }
    }
}
