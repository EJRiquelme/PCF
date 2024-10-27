package operators;

import objects.Resource;
import objects.ResourceTest;
import objects.TestAnswer;
import objects.TestQuestion;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Time;
import java.util.*;

public class ResourceTestOperator {
    private ClientServiceThread serviceThread;
    private SharedObject sharedObject;
    private Conversions conversions;
    private String messageToClient;

    private Resource resource;
    private int idResource;
    private String titleResource;
    private String presentation;
    private String type;
    private int order;
    private boolean hidden;
    private int idUnit;

    private ResourceTest resourceTest;
    private int questionsQuantity;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test;
    private Date openDate;
    private Time openTime;
    private Date closeDate;
    private Time closeTime;
    private int percentage;

    private TestQuestion testQuestion;
    private int idQuestion;
    private String textQuestion;
    // private int idTest; // = idResource

    private LinkedHashSet<TestAnswer> testAnswers;
    private TestAnswer testAnswer;
    private int idAnswer;
    private String textAnswer;
    private boolean correct;
    // private int idQuestion;

    public ResourceTestOperator(ClientServiceThread serviceThread, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = serviceThread;
        this.sharedObject = sharedObject;
        this.conversions = conversions;

        this.testAnswers = new LinkedHashSet<>();
    }

    // INSERT TEST -> 0.C44, 1.tituloRecurso, 2.presentaciónRecurso, 3.tipoRecurso, 4.oculto, 5.idTema,
    // 6.cantidadPreguntas, 7.fechaApertura, 8.horaApertura, 9.fechaCierre, 10.horaCierre, 11.porcentajeNota
    public String registerTest(String[] clientArguments) {
        titleResource = clientArguments[1];
        presentation = clientArguments[2];
        type = clientArguments[3];
        order = sharedObject.getMysqlConnection().getNewPositionResource(Integer.parseInt(clientArguments[5]));
        hidden = Boolean.parseBoolean(clientArguments[4]);
        idUnit = Integer.parseInt(clientArguments[5]);

        resource = new Resource(titleResource, presentation, type, order, hidden, idUnit);
        sharedObject.getMysqlConnection().registerResource(resource);

        questionsQuantity = Integer.parseInt(clientArguments[6]);
        openDate = conversions.convertStringToDate(clientArguments[7]);
        openTime = conversions.convertStringToTime(clientArguments[8]);
        closeDate = conversions.convertStringToDate(clientArguments[9]);
        closeTime = conversions.convertStringToTime(clientArguments[10]);
        percentage = Integer.parseInt(clientArguments[11]);

        resourceTest = new ResourceTest(resource, questionsQuantity, openDate, openTime, closeDate, closeTime, percentage);
        sharedObject.getMysqlConnection().registerTest(resourceTest);

        return "#" + "Ok";
    }

    // INSERT PREGUNTA/RESPUESTAS -> 0.C47, 1.idTest, 2.pregunta, 3.respuestaOk, 4.respuestaMal,
    // 5?.respuestaMal, 6?.respuestaMal, 7?.respuestaMal
    public String registerQuestionAnswers(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);
        textQuestion = clientArguments[2];

        testQuestion = new TestQuestion(textQuestion, idResource);
        sharedObject.getMysqlConnection().registerTestQuestion(testQuestion);

        textAnswer = clientArguments[3];
        idQuestion = sharedObject.getMysqlConnection().getIdTestQuestion(testQuestion);

        testAnswer = new TestAnswer(textAnswer, true, idQuestion);
        testAnswers.add(testAnswer);

        for (int i = 4; i < clientArguments.length; i++) {
            textAnswer = clientArguments[i];
            testAnswer = new TestAnswer(textAnswer, false, idQuestion);
            testAnswers.add(testAnswer);
        }
        sharedObject.getMysqlConnection().registerTestAnswers(testAnswers);

        LinkedHashSet<Integer> idAnswers = sharedObject.getMysqlConnection().getIdTestAnswers(idQuestion);

        messageToClient = "#" + idQuestion;
        for (Integer idAns : idAnswers) {
            messageToClient += "#" + idAns;
        }

        return messageToClient;
    }

    // UPDATE TEST -> 0.C45, 1.idRecurso, 2.tituloRecurso, 3.presentaciónRecurso, 4.Tipo, 5.oculto,
    // 6.cantidadPreguntas, 7.fechaApertura, 8.horaApertura, 9.fechaCierre, 10.horaCierre, 11.porcentajeNota
    public String updateResourceTest(String[] clientArguments) {
        //Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint(); // <- TODO: Oro en paño

        //try {
        idResource = Integer.parseInt(clientArguments[1]);
        titleResource = clientArguments[2];
        presentation = clientArguments[3];
        type = clientArguments[4];
        hidden = Boolean.parseBoolean(clientArguments[5]);

        resource = new Resource(idResource, titleResource, presentation, type, hidden);
        sharedObject.getMysqlConnection().updateResource(resource);

        questionsQuantity = Integer.parseInt(clientArguments[6]);
        openDate = conversions.convertStringToDate(clientArguments[7]);
        openTime = conversions.convertStringToTime(clientArguments[8]);
        closeDate = conversions.convertStringToDate(clientArguments[9]);
        closeTime = conversions.convertStringToTime(clientArguments[10]);
        percentage = Integer.parseInt(clientArguments[11]);

        resourceTest = new ResourceTest(resource, questionsQuantity, openDate, openTime, closeDate, closeTime, percentage);
        sharedObject.getMysqlConnection().updateTest(resourceTest);

        messageToClient = "#" + resourceTest.getTitleResource() +
                "#" + resourceTest.getPresentation() +
                "#" + resourceTest.isHidden() +
                "#" + resourceTest.getQuestionsQuantity() +
                "#" + conversions.convertDateToString(resourceTest.getOpenDate()) +
                "#" + resourceTest.getOpenTime() +
                "#" + conversions.convertDateToString(resourceTest.getCloseDate()) +
                "#" + resourceTest.getCloseTime() +
                "#" + resourceTest.getPercentage();

        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = sharedObject.getMysqlConnection().selectCompleteTest(idResource);

        for (TestQuestion key : test.keySet()) {
            messageToClient += "#" + key.getIdQuestion() + "#" + key.getTextQuestion();
            for (TestAnswer value : test.get(key)) {
                messageToClient += "#" + value.getIdAnswer() + "#" + value.getTextAnswer() + "#" + value.isCorrect();
            }
            messageToClient += "#>>>>>>>>>>"; // Combinación e caracteres que me permiten saber que lo siguiente es pregunta y no respuesta.
        }

        //} catch (SQLException ex) {
        //sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
        //messageToClient = ".1";
        //}
        return messageToClient;
    }

    // UPDATE PREGUNTA/RESPUESTAS -> 0.C48,
    // 1.update/no 2.idPregunta, 3.pregunta,
    // 4.update/no, 5.idRespuestaOk, 6.respuestaOk, 7.true
    // 8.update/no, 9.idRespuestaMal, 10.respuestaMal, 11.false,
    // 12?.ins/del/upd, 13?.idRespuestaMal, 14?.respuestaMal, 15.false,
    // 16?.ins/del/upd, 17?.idRespuestaMal, 18?.respuestaMal, 19.false,
    // 20?.ins/del/upd, 21?.idRespuestaMal, 22?.respuestaMal, 23.false
    public String updateQuestionAnswers(String[] clientArguments) {
        LinkedHashSet<TestAnswer> testAnswersToInsert = new LinkedHashSet<>();
        LinkedHashSet<TestAnswer> testAnswersToUpdate = new LinkedHashSet<>();
        LinkedHashSet<Integer> testAnswersToDelete = new LinkedHashSet<>();

        String option = clientArguments[1];
        idQuestion = Integer.parseInt(clientArguments[2]);
        textQuestion = clientArguments[3];

        if (option.equals("update")) {
            testQuestion = new TestQuestion(idQuestion, textQuestion);
            sharedObject.getMysqlConnection().updateTestQuestion(testQuestion);
        } else if (option.equals("delete")) {
            sharedObject.getMysqlConnection().deleteQuestion(idQuestion);
            return "#deleted";
        }

        for (int i = 4; i < clientArguments.length; i += 4) {
            option = clientArguments[i];
            idAnswer = Integer.parseInt(clientArguments[(i + 1)]);
            textAnswer = clientArguments[(i + 2)];
            correct = Boolean.parseBoolean(clientArguments[i + 3]);

            testAnswer = new TestAnswer(idAnswer, textAnswer, correct, idQuestion);

            if (option.equals("insert")) {
                testAnswersToInsert.add(testAnswer);
            } else if (option.equals("update")) {
                testAnswersToUpdate.add(testAnswer);
            } else if (option.equals("delete")) {
                testAnswersToDelete.add(testAnswer.getIdAnswer());
            }
        }

        if (!testAnswersToInsert.isEmpty()) {
            sharedObject.getMysqlConnection().registerTestAnswers(testAnswersToInsert);
        }
        if (!testAnswersToUpdate.isEmpty()) {
            sharedObject.getMysqlConnection().updateTestAnswers(testAnswersToUpdate);
        }
        if (!testAnswersToDelete.isEmpty()) {
            sharedObject.getMysqlConnection().deleteTestAnswers(testAnswersToDelete);
        }

        LinkedHashSet<Integer> idAnswers = sharedObject.getMysqlConnection().getIdTestAnswers(idQuestion); // Paso TODOS los ids

        messageToClient = "#" + idQuestion;
        for (Integer idAns : idAnswers) {
            messageToClient += "#" + idAns;
        }

        return messageToClient;
    }

    // SELECT TEST [Profesor] -> 0.46, 1.idRecurso
    //
    public String consultCompleteTest(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);
        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = sharedObject.getMysqlConnection().selectCompleteTest(idResource);

        resourceTest = sharedObject.getMysqlConnection().selectTest(idResource);
        messageToClient = "#" + resourceTest.getTitleResource() +
                "#" + resourceTest.getPresentation() +
                "#" + resourceTest.isHidden() +
                "#" + resourceTest.getQuestionsQuantity() +
                "#" + conversions.convertDateToString(resourceTest.getOpenDate()) +
                "#" + resourceTest.getOpenTime() +
                "#" + conversions.convertDateToString(resourceTest.getCloseDate()) +
                "#" + resourceTest.getCloseTime() +
                "#" + resourceTest.getPercentage();

        for (TestQuestion key : test.keySet()) {
            messageToClient += "#" + key.getIdQuestion() + "#" + key.getTextQuestion();
            for (TestAnswer value : test.get(key)) {
                messageToClient += "#" + value.getIdAnswer() + "#" + value.getTextAnswer() + "#" + value.isCorrect();
            }
            messageToClient += "#>>>>>>>>>>"; // Combinación e caracteres que me permiten saber que lo siguiente es pregunta y no respuesta.
        }

        return messageToClient;


    } // S46 # questionsQuantity # openDate # openTime # closeDate # closeTime # percentage # test
    // idP # p # idR1 # r1 # true # idR2 # r2 # false # |X| #idP2 # p2 # idR1 # r1 # true # idR2 # r2 # false # idR3 # r3 # false # idR4 # r4 # false

    public static LinkedHashMap<TestQuestion, ArrayList<TestAnswer>> shuffleTest(LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> testStudent) {
        List<TestQuestion> shuffledQuestions = new ArrayList<>(testStudent.keySet());
        Collections.shuffle(shuffledQuestions);

        LinkedHashMap<TestQuestion, ArrayList<TestAnswer>> shuffledTestStudent = new LinkedHashMap<>();

        for (TestQuestion key : shuffledQuestions) {
            ArrayList<TestAnswer> shuffledAnswers = new ArrayList<>(testStudent.get(key));
            Collections.shuffle(shuffledAnswers);

            shuffledTestStudent.put(key, shuffledAnswers);
        }
        return shuffledTestStudent;
    }

    // INSERT TEST [Alumno] -> 0.C54, 1.idRecurso
    //
    public String registerSolvedTest(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);

        if (sharedObject.getMysqlConnection().existsSolvedTest(idResource, serviceThread.getUser().getIdUser())) {
            return ".1"; // El usuario ya ha intentado resolver el Test
        }
        sharedObject.getMysqlConnection().registerSolvedTest(idResource, serviceThread.getUser().getIdUser());
        messageToClient = "#Ok";
        return messageToClient;
    }

    // SELECT TEST [Alumno] -> 0.C49, 1.idRecurso, 2.tituloTest
    //
    public String consultRandomTest(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);

        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> testStudent = sharedObject.getMysqlConnection().selectCompleteTest(idResource);
        LinkedHashMap<TestQuestion, ArrayList<TestAnswer>> shuffledCompleteTest = shuffleTest(testStudent);

        resourceTest = sharedObject.getMysqlConnection().selectTest(idResource);

        ArrayList<TestQuestion> finalTestStudent = new ArrayList<>();

        for (TestQuestion key : shuffledCompleteTest.keySet()) {
            if (resourceTest.getQuestionsQuantity() > finalTestStudent.size()) {
                finalTestStudent.add(key);
            } else {
                break;
            }
        }

        messageToClient = "#" + resourceTest.getTitleResource() +
                "#" + resourceTest.getPresentation() +
                "#" + resourceTest.isHidden() +
                "#" + resourceTest.getQuestionsQuantity() +
                "#" + conversions.convertDateToString(resourceTest.getOpenDate()) +
                "#" + resourceTest.getOpenTime() +
                "#" + conversions.convertDateToString(resourceTest.getCloseDate()) +
                "#" + resourceTest.getCloseTime() +
                "#" + resourceTest.getPercentage();

        for (TestQuestion key : shuffledCompleteTest.keySet()) {
            if (finalTestStudent.contains(key)) {
                messageToClient += "#" + key.getIdQuestion() + "#" + key.getTextQuestion();
                for (TestAnswer value : shuffledCompleteTest.get(key)) {
                    messageToClient += "#" + value.getIdAnswer() + "#" + value.getTextAnswer() + "#" + value.isCorrect();
                }
                messageToClient += "#" + ">>>>>>>>>>"; // Combinación e caracteres que me permiten saber que lo siguiente es pregunta y no respuesta.
            }
        }

        return messageToClient;
    } // idP # p # idR1 # r1 # true # idR2 # r2 # false # |X| #idP2 # p2 # idR1 # r1 # true # idR2 # r2 # false # idR3 # r3 # false # idR4 # r4 # false

    // C46 # idTest # Nota # idRespuesta  # idRespuesta # idRespuesta # idRespuesta...
    //
    public String solveTest(String[] clientArguments) {
        //Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint(); // <- TODO: Oro en paño

        //try {
        int idOriginalTest = Integer.parseInt(clientArguments[1]);
        double score = Double.parseDouble(clientArguments[2]);
        String setScore = sharedObject.getMysqlConnection().setScore(idOriginalTest, serviceThread.getUser().getIdUser(), score);

        if(setScore.equals("error")){
            return ".1";
        }

        LinkedHashSet<TestAnswer> solvedTestAnswers = new LinkedHashSet<>();
        for (int i = 3; i < clientArguments.length; i++) {
            solvedTestAnswers.add(new TestAnswer(Integer.parseInt(clientArguments[i])));
        }
        int idSolvedTest = sharedObject.getMysqlConnection().getIdSolvedTest(idOriginalTest, serviceThread.getUser().getIdUser());

        if(idSolvedTest == -1){
            return ".2";
        }

        sharedObject.getMysqlConnection().registerSolvedTestAnswer(idSolvedTest, solvedTestAnswers);

        return "#Ok";

        /*
        LinkedHashSet<TestQuestion> solvedTestQuestions = new LinkedHashSet<>();
        LinkedHashSet<TestAnswer> solvedTestAnswers = new LinkedHashSet<>();

        for (int i = 3; i < clientArguments.length; i += 3) {
            String textQuestion = clientArguments[i];
            TestQuestion solvedTestQuestion = new TestQuestion(textQuestion);
            solvedTestQuestions.add(solvedTestQuestion);
        }
        sharedObject.getMysqlConnection().registerSolvedTestQuestions(solvedTestQuestions, idOriginalTest, serviceThread.getUser().getIdUser());

        for (int i = 3; i < clientArguments.length; i += 3) {
            String textQuestion = clientArguments[i];
            String textAnswer = clientArguments[(i + 1)];
            boolean isCorrect = Boolean.parseBoolean(clientArguments[(i + 2)]);

            TestQuestion solvedTestQuestion = new TestQuestion(textQuestion);
            int idSolvedTestQuestion = sharedObject.getMysqlConnection().getIdSolvedTestQuestion(solvedTestQuestion, idOriginalTest, serviceThread.getUser().getIdUser());

            TestAnswer solvedTestAnswer = new TestAnswer(textAnswer, isCorrect, idSolvedTestQuestion);
            solvedTestAnswers.add(solvedTestAnswer);
        }

        sharedObject.getMysqlConnection().registerSolvedTestAnswer(solvedTestAnswers);
        */

        //} catch (SQLException ex) {
        //sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
        //messageToClient = ".1";
        //}
    }

}

















