package operators;

import objects.Unit;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.File;

public class UnitOperator {
    private final ClientServiceThread serviceThread;
    private final SharedObject sharedObject;
    private Conversions conversions;

    private int idUnit;
    private String titleUnit;
    private int orderUnit;
    private boolean hiddenUnit;
    int percentageExercises;
    int percentageControls;
    int percentageExams;
    int percentageTests;
    private int idCourse;

    public UnitOperator(ClientServiceThread clientService, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = clientService;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    // ALTA TEMA -> 0.C25, 1.titleUnit, 2.hiddenUnit, 3.percentageExercises, 4.percentageControls, 5.percentageExams, 6.percentageTests
    //
    public String registerUnit(String[] clientArguments) {
        titleUnit = clientArguments[1];
        orderUnit = sharedObject.getMysqlConnection().getNewPositionUnit(serviceThread.getActiveVirtualClass().getIdCourse());

        if (orderUnit == -1) {
            return "Error de asignación de posición";
        }

        hiddenUnit = Boolean.parseBoolean(clientArguments[2]);
        percentageExercises = Integer.parseInt(clientArguments[3]);
        percentageControls = Integer.parseInt(clientArguments[4]);
        percentageExams = Integer.parseInt(clientArguments[6]);
        percentageTests = Integer.parseInt(clientArguments[6]);
        idCourse = serviceThread.getActiveVirtualClass().getIdCourse();

        Unit unit = new Unit(titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
        String registerResult = sharedObject.getMysqlConnection().registerUnit(unit);

        if (registerResult.equals("Error")) {
            return "Error: Error de registro";
        } else if (registerResult.equals("Clave")) {
            return "Error: Clave única duplicada";
        }

        int registeredUnitId = sharedObject.getMysqlConnection().getIdUnit(unit);
        File courseDirectory = new File("users" + File.separator + serviceThread.getUser().getEmail() + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() + File.separator + registeredUnitId);
        courseDirectory.mkdirs(); // Carpeta "38"

        if (registeredUnitId == -1) {
            return ".4" + "#" + "Error: Error de creación de carpeta";
        }

        return "#" + "Ok";
    }

    // UPDATE TEMA -> 0.C26, 1.idUnit, 2.titleUnit, 3.hiddenUnit, 4.percentageExercises, 5.percentageControls, 6.percentageExams, 7.percentageTests
    //
    public String updateUnit(String[] clientArguments) {
        idUnit = Integer.parseInt(clientArguments[1]);
        titleUnit = clientArguments[2];
        // orderUnit; // Aqui no
        hiddenUnit = Boolean.parseBoolean(clientArguments[3]);
        percentageExercises = Integer.parseInt(clientArguments[4]);

        percentageControls = Integer.parseInt(clientArguments[5]);
        percentageExams = Integer.parseInt(clientArguments[6]);
        percentageTests = Integer.parseInt(clientArguments[7]);
        idCourse = serviceThread.getActiveVirtualClass().getIdCourse();

        Unit unit = new Unit(idUnit, titleUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);

        String firstResult = sharedObject.getMysqlConnection().updateUnit(unit);

        if (!firstResult.equals("Ok")) {
            if (firstResult.equals("Clave")) {
                return "Error: Clave duplicada";
            } else {
                return "Error: Error de actualización";
            }
        }

        return "Ok";
    }

    // REORDER TEMA -> 0.C24, 1.Id Unit, 2.Position, 3.Id, 4.Position, 5.Id, 6.Position...
    //
    public String reorderUnits(String[] clientArguments) {
        for (int i = 1; i < clientArguments.length; i++) {
            if (i % 2 != 0) { // Id - Pos
                boolean reorderResult = sharedObject.getMysqlConnection().reorderUnit(Integer.parseInt(clientArguments[i]), Integer.parseInt(clientArguments[i + 1]));

                if (!reorderResult) {
                    return "Error: Error de actualización";
                }
            }
        }
        return "Ok";
    }

    // DELETE TEMA -> 0.C25, 1.IdUnit
    //
    public String deleteUnit(String[] clientArguments) {
        idUnit = Integer.parseInt(clientArguments[1]);
        boolean deleteResult = sharedObject.getMysqlConnection().deleteUnit(idUnit);

        if (!deleteResult) {
            return "Error: Error de borrado";
        }

        File unitDirectory = new File("users" + File.separator // users / a@gmail.com / idCurso / idTema
                + serviceThread.getUser().getEmail() + File.separator
                + serviceThread.getActiveVirtualClass().getIdCourse() + File.separator
                + idUnit);
        sharedObject.deleteDirectory(unitDirectory);

        return "Ok";
    }
}
