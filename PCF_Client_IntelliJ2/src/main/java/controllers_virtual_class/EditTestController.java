package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import objects.ResourceTest;
import objects.TestAnswer;
import objects.TestQuestion;
import pcf_client.executables.App;
import pcf_client.executables.QuestionsItemController;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class EditTestController implements Initializable {
    private SharedStore sharedStore;

    private ObservableList<String> questionsObservableList;
    private ResourceTest resourceTest;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> realTest;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> visualTestQuestionAnswers;
    private LinkedHashMap<String, LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>>> visualTest; // Modelo visual
    private String selectedKey;

    private TestQuestion selectedQuestion;
    private LinkedHashSet<TestAnswer> realTestAnswers;
    private TestAnswer selectedOkAnswer;
    private TestAnswer selectedWrong1Answer;
    private TestAnswer selectedWrong2Answer;
    private TestAnswer selectedWrong3Answer;
    private TestAnswer selectedWrong4Answer;
    private boolean focused;

    private String questionString;
    private String okAnswerString;
    private String wrong1AnswerString;
    private String wrong2AnswerString;
    private String wrong3AnswerString;
    private String wrong4AnswerString;

    private String clientMessage;

    public EditTestController() {
        this.sharedStore = SharedStore.getInstance();

        this.resourceTest = (ResourceTest) sharedStore.getSelectedResource();
        this.visualTest = new LinkedHashMap<>();

        this.selectedQuestion = null;
        this.realTestAnswers = new LinkedHashSet<>();
        this.selectedOkAnswer = null;
        this.selectedWrong1Answer = null;
        this.selectedWrong2Answer = null;
        this.selectedWrong3Answer = null;
        this.selectedWrong4Answer = null;

        this.focused = false;

        this.questionString = "";
        this.okAnswerString = "";
        this.wrong1AnswerString = "";
        this.wrong2AnswerString = "";
        this.wrong3AnswerString = "";
        this.wrong4AnswerString = "";

        this.clientMessage = "";
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private AnchorPane questionsListAnchorPane;

    @FXML
    private Label testTitleLabel;

    @FXML
    private Label questionsQuantityLabel;

    @FXML
    private ListView<String> questionsListView;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button configureTestButton;

    @FXML
    private AnchorPane questionAnswersAnchorPane;
    @FXML
    private TextArea questionTextArea;
    @FXML
    private TextArea okTextArea;
    @FXML
    private TextArea wrong1TextArea;
    @FXML
    private TextArea wrong2TextArea;
    @FXML
    private TextArea wrong3TextArea;
    @FXML
    private TextArea wrong4TextArea;

    @FXML
    private Button newQuestionButton;
    @FXML
    private Button deleteQuestionButton;
    @FXML
    private Button saveQuestionButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    void looseListFocus(ActionEvent event) {
        selectedQuestion = null;
        selectedOkAnswer = null;
        selectedWrong1Answer = null;
        selectedWrong2Answer = null;
        selectedWrong3Answer = null;
        selectedWrong4Answer = null;
        questionTextArea.setText("");
        okTextArea.setText("");
        wrong1TextArea.setText("");
        wrong2TextArea.setText("");
        wrong3TextArea.setText("");
        wrong4TextArea.setText("");
        focused = false;
        saveQuestionButton.setText("Guardar Pregunta");
        questionsListView.getSelectionModel().clearSelection(); // El listener salta
    }

    @FXML
    void switchToConfigureTest(ActionEvent event) {
        App.setRoot("vc_create_test");
    }

    public void fillQuestionsList() {
        visualTest.clear();

        int i = 1;
        for (TestQuestion testQuestion : realTest.keySet()) {
            LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> questionWithAnswers = new LinkedHashMap<>();
            questionWithAnswers.put(testQuestion, realTest.get(testQuestion)); // TestQuestion -> LinkedHashSet<TestAnswer>

            if (testQuestion.getText().length() >= 11) {
                if (testQuestion.getText().startsWith("<<<<<<<<<<") && sharedStore.getApp().integerComponentsValidation(testQuestion.getText().substring(10))) {
                    testQuestion.setText(testQuestion.getText().replaceAll("<<<<<<<<<<", "")) ;
                }
            }

            visualTest.put("Pregunta " + i, questionWithAnswers); // Pregunta X -> TestQuestion -> LinkedHashSet<TestAnswer>
            i++;
        }

        questionsObservableList = FXCollections.observableArrayList();
        questionsObservableList.addAll(visualTest.keySet());

        questionsListView.setItems(questionsObservableList);
        questionsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> coursesListView) {
                return new QuestionsItemController();
            }
        });

        questionsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                fillTextAreas(newValue);
            }
        });
    }

    public void fillTextAreas(String question) {
        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> selectedQuestionsWithAnswers = visualTest.get(question);
        selectedKey = question;

        if (selectedQuestionsWithAnswers != null) { // Para evitar problemas de concurrencia al acceder a visualTest: (hiloGrafico <-> listener)

            selectedOkAnswer = null;
            selectedWrong1Answer = null;
            selectedWrong2Answer = null;
            selectedWrong3Answer = null;
            selectedWrong4Answer = null;
            wrong2TextArea.setText("");
            wrong3TextArea.setText("");
            wrong4TextArea.setText("");

            for (TestQuestion testQuestion : selectedQuestionsWithAnswers.keySet()) { // Solo tiene 1
                selectedQuestion = testQuestion;
                questionTextArea.setText(testQuestion.getText());

                int i = 0;
                for (TestAnswer answer : selectedQuestionsWithAnswers.get(testQuestion)) {

                    if (answer.getText().length() >= 11) { // Valores numéricos
                        if (answer.getText().startsWith("<<<<<<<<<<") && sharedStore.getApp().integerComponentsValidation(answer.getText().substring(10))) {
                            answer.setText(answer.getText().replaceAll("<<<<<<<<<<", "")) ;
                        }
                    }

                    if (i == 0) {
                        selectedOkAnswer = answer;
                        okTextArea.setText(answer.getText());
                    } else if (i == 1) {
                        selectedWrong1Answer = answer;
                        wrong1TextArea.setText(answer.getText());
                    } else if (i == 2) {
                        selectedWrong2Answer = answer;
                        wrong2TextArea.setText(answer.getText());
                    } else if (i == 3) {
                        selectedWrong3Answer = answer;
                        wrong3TextArea.setText(answer.getText());
                    } else if (i == 4) {
                        selectedWrong4Answer = answer;
                        wrong4TextArea.setText(answer.getText());
                    }
                    i++;
                }
            }

            saveQuestionButton.setText("Guardar cambios");
            focused = true;
        }
    }

    @FXML
    void deleteQuestion(ActionEvent event) {
        if (focused) {
            if (checkErrorsDelete()) {
                if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                        "Confirmación necesaria para el borrado.",
                        "Si activas esta opción la pregunta seleccionada\n" +
                                "y sus respectivas respuestas serán eliminadas.\n" +
                                "¿Estas seguro de que quieres borrar la pregunta?")) {

                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(38) // C38
                            + "#" + "delete"
                            + "#" + selectedQuestion.getIdQuestion()
                            + "#" + selectedQuestion.getText();

                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()

                    if (sharedStore.getApp().checkResponseResult()) { // Comprobación de errores

                        realTest.remove(selectedQuestion); // Borramos en el lado Cliente
                        resourceTest.setTest(realTest);
                        sharedStore.setSelectedResource(resourceTest); // Borramos de verdad
                        visualTest.remove(selectedKey);

                        fillQuestionsList(); // Recargamos

                        selectedQuestion = null;
                        selectedOkAnswer = null;
                        selectedWrong1Answer = null;
                        selectedWrong2Answer = null;
                        selectedWrong3Answer = null;
                        selectedWrong4Answer = null;

                        questionTextArea.setText("");
                        okTextArea.setText("");
                        wrong1TextArea.setText("");
                        wrong2TextArea.setText("");
                        wrong3TextArea.setText("");
                        wrong4TextArea.setText("");

                        focused = false;

                        questionsQuantityLabel.setText(realTest.size() + "/" + resourceTest.getQuestionsQuantity());
                    }
                }
            }
        } else {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                    "Borrado no aceptado.",
                    "No hay ningún elemento seleccionado.",
                    "Debe seleccionar una de las preguntas para poder borrarla.");
        }
    }

    @FXML
    void saveQuestion(ActionEvent event) {
        realTestAnswers = new LinkedHashSet<>();

        questionString = questionTextArea.getText();
        okAnswerString = okTextArea.getText();
        wrong1AnswerString = wrong1TextArea.getText();
        wrong2AnswerString = wrong2TextArea.getText();
        wrong3AnswerString = wrong3TextArea.getText();
        wrong4AnswerString = wrong4TextArea.getText();

        if (checkErrors()) {
            numericTextControl();
            if (!focused) { // INSERT all
                insertTestQuestionAnswers();
            } else { // INSERT/UPDATE/DELETE por partes
                updateTestQuestionAnswers();
            }
        }
    }

    public void insertTestQuestionAnswers() {
        clientMessage = sharedStore.getProtocolMessages().getClientArgument(33) // C33
                + "#" + sharedStore.getSelectedResource().getIdResource()
                + "#" + questionString
                + "#" + okAnswerString;

        selectedQuestion = new TestQuestion(-1, questionString, sharedStore.getSelectedResource().getIdResource());
        selectedOkAnswer = new TestAnswer(-1, okAnswerString, true, selectedQuestion.getIdQuestion());
        realTestAnswers.add(selectedOkAnswer);

        if (!wrong1AnswerString.isEmpty()) {
            clientMessage += "#" + wrong1AnswerString;
            selectedWrong1Answer = new TestAnswer(-1, wrong1AnswerString, false, selectedQuestion.getIdQuestion());
            realTestAnswers.add(selectedWrong1Answer);
        }
        if (!wrong2AnswerString.isEmpty()) {
            clientMessage += "#" + wrong2AnswerString;
            selectedWrong2Answer = new TestAnswer(-1, wrong2AnswerString, false, selectedQuestion.getIdQuestion());
            realTestAnswers.add(selectedWrong2Answer);
        }
        if (!wrong3AnswerString.isEmpty()) {
            clientMessage += "#" + wrong3AnswerString;
            selectedWrong3Answer = new TestAnswer(-1, wrong3AnswerString, false, selectedQuestion.getIdQuestion());
            realTestAnswers.add(selectedWrong3Answer);
        }
        if (!wrong4AnswerString.isEmpty()) {
            clientMessage += "#" + wrong4AnswerString;
            selectedWrong4Answer = new TestAnswer(-1, wrong4AnswerString, false, selectedQuestion.getIdQuestion());
            realTestAnswers.add(selectedWrong4Answer);
        }

        realTest.put(selectedQuestion, realTestAnswers);

        resourceTest.setTest(realTest);
        sharedStore.setSelectedResource(resourceTest);

        visualTestQuestionAnswers = new LinkedHashMap<>();
        visualTestQuestionAnswers.put(selectedQuestion, realTest.get(selectedQuestion));
        String newQuestionString = "Pregunta " + realTest.size();
        visualTest.put(newQuestionString, visualTestQuestionAnswers);

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()

        if (sharedStore.getApp().checkResponseResult()) { // Comprobación de errores
            fillQuestionsList(); // Recarga el test
            questionsListView.getSelectionModel().select(newQuestionString);
        }

        questionsQuantityLabel.setText(realTest.size() + "/" + resourceTest.getQuestionsQuantity());
    }

    public void updateTestQuestionAnswers() {
        clientMessage = sharedStore.getProtocolMessages().getClientArgument(38); // C48

        if (!questionString.equals(selectedQuestion.getText())) { // Si ha cambiado -> Actualizar
            clientMessage += "#" + "update";
        } else {
            clientMessage += "#" + "noUpdate";
        }
        clientMessage += "#" + selectedQuestion.getIdQuestion() + "#" + questionString;

        if (!okAnswerString.equals(selectedOkAnswer.getText())) { // Si ha cambiado -> Actualizar
            clientMessage += "#" + "update" + "#" + selectedOkAnswer.getIdAnswer() + "#" + okAnswerString + "#" + selectedOkAnswer.isCorrect();
        }

        selectedOkAnswer = new TestAnswer(selectedOkAnswer.getIdAnswer(), okAnswerString, true, selectedQuestion.getIdQuestion());
        realTestAnswers.add(selectedOkAnswer);

        if (selectedWrong1Answer != null) {
            if (wrong1AnswerString.isEmpty()) {
                clientMessage += "#" + "delete" + "#" + selectedWrong1Answer.getIdAnswer() + "#" + "none" + "#" + selectedWrong1Answer.isCorrect(); // Paso texto y boolean x el bucle del Server
            } else if (!wrong1AnswerString.equals(selectedWrong1Answer.getText())) { // Si ha cambiado -> Actualizar
                clientMessage += "#" + "update" + "#" + selectedWrong1Answer.getIdAnswer() + "#" + wrong1AnswerString + "#" + selectedWrong1Answer.isCorrect();
                selectedWrong1Answer = new TestAnswer(selectedWrong1Answer.getIdAnswer(), wrong1AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong1Answer);
            } else {
                selectedWrong1Answer = new TestAnswer(selectedWrong1Answer.getIdAnswer(), wrong1AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong1Answer);
            }
        } else {
            if (!wrong1AnswerString.isEmpty()) {
                clientMessage += "#" + "insert" + "#" + "-1" + "#" + wrong1AnswerString + "#" + false;
                selectedWrong1Answer = new TestAnswer(-1, wrong1AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong1Answer);
            }
        }

        if (selectedWrong2Answer != null) {
            if (wrong2AnswerString.isEmpty()) {
                clientMessage += "#" + "delete" + "#" + selectedWrong2Answer.getIdAnswer() + "#" + "none" + "#" + selectedWrong2Answer.isCorrect(); // Paso texto y boolean x el bucle del Server
            } else if (!wrong2AnswerString.equals(selectedWrong2Answer.getText())) { // Si ha cambiado -> Actualizar
                clientMessage += "#" + "update" + "#" + selectedWrong2Answer.getIdAnswer() + "#" + wrong2AnswerString + "#" + selectedWrong2Answer.isCorrect();
                selectedWrong2Answer = new TestAnswer(selectedWrong2Answer.getIdAnswer(), wrong2AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong2Answer);
            } else {
                selectedWrong2Answer = new TestAnswer(selectedWrong2Answer.getIdAnswer(), wrong2AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong2Answer);
            }
        } else {
            if (!wrong2AnswerString.isEmpty()) {
                clientMessage += "#" + "insert" + "#" + "-1" + "#" + wrong2AnswerString + "#" + false;
                selectedWrong2Answer = new TestAnswer(-1, wrong2AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong2Answer);
            }
        }

        if (selectedWrong3Answer != null) {
            if (wrong3AnswerString.isEmpty()) {
                clientMessage += "#" + "delete" + "#" + selectedWrong3Answer.getIdAnswer() + "#" + "none" + "#" + selectedWrong3Answer.isCorrect(); // Paso texto x el bucle del Server
            } else if (!wrong3AnswerString.equals(selectedWrong3Answer.getText())) { // Si ha cambiado -> Actualizar
                clientMessage += "#" + "update" + "#" + selectedWrong3Answer.getIdAnswer() + "#" + wrong3AnswerString + "#" + selectedWrong3Answer.isCorrect();
                selectedWrong3Answer = new TestAnswer(selectedWrong3Answer.getIdAnswer(), wrong3AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong3Answer);
            } else {
                selectedWrong3Answer = new TestAnswer(selectedWrong3Answer.getIdAnswer(), wrong3AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong3Answer);
            }
        } else {
            if (!wrong3AnswerString.isEmpty()) {
                clientMessage += "#" + "insert" + "#" + "-1" + "#" + wrong3AnswerString + "#" + false;
                selectedWrong3Answer = new TestAnswer(-1, wrong3AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong3Answer);
            }
        }

        if (selectedWrong4Answer != null) {
            if (wrong4AnswerString.isEmpty()) {
                clientMessage += "#" + "delete" + "#" + selectedWrong4Answer.getIdAnswer() + "#" + "none" + "#" + selectedWrong4Answer.isCorrect(); // Paso texto x el bucle del Server
            } else if (!wrong4AnswerString.equals(selectedWrong4Answer.getText())) { // Si ha cambiado -> Actualizar
                clientMessage += "#" + "update" + "#" + selectedWrong4Answer.getIdAnswer() + "#" + wrong4AnswerString + "#" + selectedWrong4Answer.isCorrect();
                selectedWrong4Answer = new TestAnswer(selectedWrong4Answer.getIdAnswer(), wrong4AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong4Answer);
            } else {
                selectedWrong4Answer = new TestAnswer(selectedWrong4Answer.getIdAnswer(), wrong4AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong4Answer);
            }
        } else {
            if (!wrong4AnswerString.isEmpty()) {
                clientMessage += "#" + "insert" + "#" + "-1" + "#" + wrong4AnswerString + "#" + false;
                selectedWrong4Answer = new TestAnswer(-1, wrong4AnswerString, false, selectedQuestion.getIdQuestion());
                realTestAnswers.add(selectedWrong4Answer);
            }
        }

        realTest.remove(selectedQuestion);
        selectedQuestion = new TestQuestion(selectedQuestion.getIdQuestion(), questionString, sharedStore.getSelectedResource().getIdResource());
        realTest.put(selectedQuestion, realTestAnswers);

        resourceTest.setTest(realTest);
        sharedStore.setSelectedResource(resourceTest);

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()

        if (sharedStore.getApp().checkResponseResult()) { // Comprobación de errores

            int cont = 1; // Añado al modelo visual
            for (TestQuestion testQuestion : realTest.keySet()) {
                if (cont == realTest.size()) {
                    visualTestQuestionAnswers = new LinkedHashMap<>();
                    visualTestQuestionAnswers.put(testQuestion, realTest.get(testQuestion)); // (TestQuestion -> LinkedHashSet<TestAnswer>)
                    visualTest.put(selectedKey, visualTestQuestionAnswers);
                }
                cont++;
            }

            fillQuestionsList(); // Recarga el test
        }

    }

    public void numericTextControl() { // Para evitar confundirlos con ids en la lectura
        if (sharedStore.getApp().integerComponentsValidation(questionString)) {
            questionString = "<<<<<<<<<<" + questionString;
        }
        if (sharedStore.getApp().integerComponentsValidation(okAnswerString)) {
            okAnswerString = "<<<<<<<<<<" + okAnswerString;
        }
        if (sharedStore.getApp().integerComponentsValidation(wrong1AnswerString)) {
            wrong1AnswerString = "<<<<<<<<<<" + wrong1AnswerString;
        }
        if (sharedStore.getApp().integerComponentsValidation(wrong2AnswerString)) {
            wrong2AnswerString = "<<<<<<<<<<" + wrong2AnswerString;
        }
        if (sharedStore.getApp().integerComponentsValidation(wrong3AnswerString)) {
            wrong3AnswerString = "<<<<<<<<<<" + wrong3AnswerString;
        }
        if (sharedStore.getApp().integerComponentsValidation(wrong4AnswerString)) {
            wrong4AnswerString = "<<<<<<<<<<" + wrong4AnswerString;
        }
    }

    public boolean checkErrorsDelete() {
        boolean alright = true;
        if (!sharedStore.getSelectedResource().isHidden()) {

            // Dia de apertura "ayer", Dia cierre "mañana"
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            if (!alright) { // Retorno los datos
                questionTextArea.setText(selectedQuestion.getText());
                okTextArea.setText(selectedOkAnswer.getText());
                wrong1TextArea.setText(selectedWrong1Answer.getText());
                wrong2TextArea.setText(selectedWrong2Answer.getText());
                wrong3TextArea.setText(selectedWrong3Answer.getText());
                wrong4TextArea.setText(selectedWrong4Answer.getText());
            }

        }
        return alright;
    }

    public boolean checkErrors() {
        boolean alright = true;

        // Si el Alumno puede interactuar...
        //
        if (!sharedStore.getSelectedResource().isHidden()) {
            ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();

            // Dia de apertura "ayer", Dia cierre "mañana"
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            if (!alright) { // Retorno los datos
                if (selectedQuestion != null) {
                    questionTextArea.setText(selectedQuestion.getText());
                } else {
                    questionTextArea.setText("");
                }

                if (selectedOkAnswer != null) {
                    okTextArea.setText(selectedOkAnswer.getText());
                } else {
                    okTextArea.setText("");
                }
                if (selectedWrong1Answer != null) {
                    wrong1TextArea.setText(selectedWrong1Answer.getText());
                } else {
                    wrong1TextArea.setText("");
                }
                if (selectedWrong2Answer != null) {
                    wrong2TextArea.setText(selectedWrong2Answer.getText());
                } else {
                    wrong2TextArea.setText("");
                }
                if (selectedWrong3Answer != null) {
                    wrong3TextArea.setText(selectedWrong3Answer.getText());
                } else {
                    wrong3TextArea.setText("");
                }
                if (selectedWrong4Answer != null) {
                    wrong4TextArea.setText(selectedWrong4Answer.getText());
                } else {
                    wrong4TextArea.setText("");
                }
            }
        }

        if (alright) {
            if (questionString.isEmpty() || !sharedStore.getApp().allComponentsValidation(questionString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Pregunta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + questionTextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + questionTextArea.getLayoutY());
            }

            if (okAnswerString.isEmpty() || !sharedStore.getApp().allComponentsValidation(okAnswerString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Respuesta correcta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + okTextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + okTextArea.getLayoutY());
            }

            if (!sharedStore.getApp().allComponentsValidation(wrong1AnswerString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Respuesta incorrecta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + wrong1TextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + wrong1TextArea.getLayoutY());
            }

            if (!sharedStore.getApp().allComponentsValidation(wrong2AnswerString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Respuesta incorrecta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + wrong2TextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + wrong2TextArea.getLayoutY());
            }

            if (!sharedStore.getApp().allComponentsValidation(wrong3AnswerString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Respuesta incorrecta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + wrong3TextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + wrong3TextArea.getLayoutY());
            }

            if (!sharedStore.getApp().allComponentsValidation(wrong4AnswerString)) {
                alright = false;
                sharedStore.getApp().errorPopup("Respuesta incorrecta inválida.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + wrong4TextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + wrong4TextArea.getLayoutY());
            }

            if (wrong1AnswerString.isEmpty() && wrong2AnswerString.isEmpty() && wrong3AnswerString.isEmpty() && wrong4AnswerString.isEmpty()) {
                alright = false;
                sharedStore.getApp().errorPopup("Debe haber al menos una respuesta incorrecta.",
                        questionsListAnchorPane.getWidth() + questionAnswersAnchorPane.getLayoutX() + wrong1TextArea.getLayoutX(),
                        questionAnswersAnchorPane.getLayoutY() + wrong1TextArea.getLayoutY());
            }

            if (okAnswerString.equals(wrong1AnswerString) && !okAnswerString.isEmpty() || // 1 y 2
                    okAnswerString.equals(wrong2AnswerString) && !okAnswerString.isEmpty() || // 1 y 3
                    okAnswerString.equals(wrong3AnswerString) && !okAnswerString.isEmpty() || // 1 y 4
                    okAnswerString.equals(wrong4AnswerString) && !okAnswerString.isEmpty() || // 1 y 5
                    wrong1AnswerString.equals(wrong2AnswerString) && !wrong1AnswerString.isEmpty() || // 2 y 3
                    wrong1AnswerString.equals(wrong3AnswerString) && !wrong1AnswerString.isEmpty() || // 2 y 4
                    wrong1AnswerString.equals(wrong4AnswerString) && !wrong1AnswerString.isEmpty() || // 2 y 5
                    wrong2AnswerString.equals(wrong3AnswerString) && !wrong2AnswerString.isEmpty() || // 3 y 4
                    wrong2AnswerString.equals(wrong4AnswerString) && !wrong2AnswerString.isEmpty() || // 3 y 5
                    wrong3AnswerString.equals(wrong4AnswerString) && !wrong3AnswerString.isEmpty()) { // 4 y 5
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Respuesta repetida.",
                        "No se pueden crear respuestas repetidas\ndentro de una misma pregunta.");
            }

            // Lo compara con null x lo que no pasa nunca
            for (TestQuestion testQuestion : realTest.keySet()) {
                if (testQuestion.getText().equals(questionString) && !focused) { // Insert
                    alright = false;
                    sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                            "ERROR",
                            "Pregunta repetida.",
                            "No se pueden crear preguntas repetidas\ndentro de un mismo test.");
                }
                if (testQuestion.getText().equals(questionString) && !selectedQuestion.getText().equals(questionString)) { // Update
                    alright = false;
                    sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                            "ERROR",
                            "Pregunta repetida.",
                            "No se pueden crear preguntas repetidas\ndentro de un mismo test.");
                }
            }
        }

        return alright;
    }

    public void configComponents() {
        questionTextArea.setWrapText(true);
        okTextArea.setWrapText(true);
        wrong1TextArea.setWrapText(true);
        wrong2TextArea.setWrapText(true);
        wrong3TextArea.setWrapText(true);
        wrong4TextArea.setWrapText(true);

        questionTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        okTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        wrong1TextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        wrong2TextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        wrong3TextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        wrong4TextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
    }

    public void LoadData() {
        resourceTest = (ResourceTest) sharedStore.getSelectedResource();

        this.realTest = resourceTest.getTest();

        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        testTitleLabel.setText(resourceTest.getTitleResource());
        questionsQuantityLabel.setText(realTest.size() + "/" + resourceTest.getQuestionsQuantity());

        int i = 1;
        for (TestQuestion testQuestion : realTest.keySet()) {
            LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> questionWithAnswers = new LinkedHashMap<>();
            questionWithAnswers.put(testQuestion, realTest.get(testQuestion)); // TestQuestion -> LinkedHashSet<TestAnswer>

            visualTest.put("Pregunta " + i, questionWithAnswers); // Pregunta X -> TestQuestion -> LinkedHashSet<TestAnswer>
            i++;
        }

        fillQuestionsList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();
        LoadData();
    }
}
