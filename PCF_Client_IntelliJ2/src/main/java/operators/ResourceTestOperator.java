package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ResourceTestOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    public ResourceTestOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    //S45/S46 # titulo # presentation # isHidden # questionsQuantity # openDate # openTime # closeDate # closeTime # percentage # testCompleto
    public void saveTestData(String[] serverArguments) {
        String titleResource = serverArguments[1];
        String presentationResource = serverArguments[2];
        boolean isHidden = Boolean.parseBoolean(serverArguments[3]);

        Resource resource = sharedStore.getSelectedResource();
        resource.setTitleResource(titleResource);
        resource.setPresentation(presentationResource);
        resource.setHidden(isHidden);

        int questionsQuantity = Integer.parseInt(serverArguments[4]);
        LocalDate openDate = sharedStore.getConversions().convertStringToLocalDate(serverArguments[5]);
        LocalTime openTime = sharedStore.getConversions().convertStringToLocalTime(serverArguments[6]);
        LocalDate closeDate = sharedStore.getConversions().convertStringToLocalDate(serverArguments[7]);
        LocalTime closeTime = sharedStore.getConversions().convertStringToLocalTime(serverArguments[8]);
        int percentage = Integer.parseInt(serverArguments[9]);

        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = fillTest(serverArguments);

        ResourceTest resourceTest = new ResourceTest(resource, questionsQuantity, test, openDate, openTime, closeDate, closeTime, percentage);
        System.out.println(openDate);

        sharedStore.setSelectedResource(resourceTest);
        System.out.println("ASIGNO " + (sharedStore.getSelectedResource() ==null));
    }

    // idP # p # idR1 # r1 # true # idR2 # r2 # false # |X| #idP2 # p2 # idR1 # r1 # true # idR2 # r2 # false # idR3 # r3 # false # idR4 # r4 # false
    //
    public LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> fillTest(String[] serverArguments) {
        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> testLinkedHashMap = new LinkedHashMap<>();
        TestQuestion question = new TestQuestion();

        boolean isQuestion = true;
        for (int i = 10; i < serverArguments.length; i++) {

            if (sharedStore.getApp().integerComponentsValidation(serverArguments[i])) {
                int id = Integer.parseInt(serverArguments[i]);
                String text = serverArguments[(i + 1)];

                if (text.length() >= 11) {
                    if (text.startsWith("<<<<<<<<<<") && sharedStore.getApp().integerComponentsValidation(text.substring(10))) {
                        text = text.replaceAll("<<<<<<<<<<", "");
                    }
                }

                if (isQuestion) {
                    question = new TestQuestion(id, text, 21);
                    testLinkedHashMap.put(question, new LinkedHashSet<>());

                } else {
                    boolean correct = Boolean.parseBoolean(serverArguments[(i + 2)]);
                    TestAnswer answer = new TestAnswer(id, text, correct, question.getIdQuestion());

                    testLinkedHashMap.get(question).add(answer);
                }
            }

            if (serverArguments[i].equals(">>>>>>>>>>")) { // => El siguiente dato es Clave, no valor
                isQuestion = true;
            } else {
                isQuestion = false;
            }

        }

        return testLinkedHashMap;
    }

    public void insertIds(String[] serverArguments) { // S47 # idPregunta # idRespuestaTrue # idRespuestaFalse # idRespuesta2False? # idRespuesta3False? # idRespuesta4False?

        ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();
        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = resourceTest.getTest();
        TestQuestion lastTestQuestion = new TestQuestion();

        int i = 1;
        for (TestQuestion iteratedTestQuestion : test.keySet()) {
            if (i == test.size()) {
                lastTestQuestion = iteratedTestQuestion; // Recojo la última pregunta del mapa
            }
            i++;
        }

        LinkedHashSet<TestAnswer> lastTestAnswers = test.get(lastTestQuestion); // Cojo y elimino para la posterior inserción final
        test.remove(lastTestQuestion);

        int idQuestion = Integer.parseInt(serverArguments[1]);
        lastTestQuestion.setIdQuestion(idQuestion);

        int index = 2;
        for (TestAnswer testAnswer : lastTestAnswers) {
            int idAnswer = Integer.parseInt(serverArguments[index]);
            testAnswer.setIdAnswer(idAnswer);
            testAnswer.setIdQuestion(idQuestion);

            index++;
        }

        test.put(lastTestQuestion, lastTestAnswers); // Introduzco en el mapa los elementos con sus ids correspondientes
        resourceTest.setTest(test); // Y los guardo
        sharedStore.setSelectedResource(resourceTest);
    }

    // S48 #  idPregunta # idRespuestaTrue # idRespuestaFalse # idRespuesta2False? # idRespuesta3False? # idRespuesta4False?
    // El problema: DELETE podría haber eliminado respuestas existentes en el antiguo listado
    public void insertLastIds(String[] serverArguments) {

        if(!serverArguments[1].equals("deleted")) {
            ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();
            LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = resourceTest.getTest();
            TestQuestion lastTestQuestion = new TestQuestion();


            for (String string : serverArguments) {
                System.out.println(string);
            }

            int i = 1;
            for (TestQuestion iteratedTestQuestion : test.keySet()) {
                if (i == test.size()) {
                    lastTestQuestion = iteratedTestQuestion; // Recojo la última pregunta del mapa
                }
                i++;
            }

            LinkedHashSet<TestAnswer> lastTestAnswers = test.get(lastTestQuestion); // Cojo y elimino para la posterior inserción final
            test.remove(lastTestQuestion);

            int idQuestion = Integer.parseInt(serverArguments[1]);
            lastTestQuestion.setIdQuestion(idQuestion);

            int index = 2;
            for (TestAnswer testAnswer : lastTestAnswers) {
                int idAnswer = Integer.parseInt(serverArguments[index]); // AUN NO HA SIDO INSERTADA
                testAnswer.setIdAnswer(idAnswer);
                testAnswer.setIdQuestion(idQuestion);

                index++;
            }

            test.put(lastTestQuestion, lastTestAnswers); // Introduzco en el mapa los elementos con sus ids correspondientes
            resourceTest.setTest(test); // Y los guardo
            sharedStore.setSelectedResource(resourceTest);
        }
    }
}
