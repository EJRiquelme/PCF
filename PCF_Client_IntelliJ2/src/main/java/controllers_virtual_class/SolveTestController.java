package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import objects.ResourceTest;
import objects.TestAnswer;
import objects.TestQuestion;
import pcf_client.executables.App;
import pcf_client.executables.QuestionsItemController;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class SolveTestController implements Initializable {

    private SharedStore sharedStore;

    private ObservableList<String> questionsObservableList;
    private ResourceTest resourceTest;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> realTest;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> visualTestQuestionAnswers;
    private LinkedHashMap<String, LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>>> visualTest; // Modelo visual

    private TestQuestion selectedQuestion;
    private LinkedHashSet<TestAnswer> realTestAnswers;
    private TestAnswer testAnswer1;
    private TestAnswer testAnswer2;
    private TestAnswer testAnswer3;
    private TestAnswer testAnswer4;
    private TestAnswer testAnswer5;

    private LinkedHashMap<TestQuestion, TestAnswer> solvedTest;

    private String clientMessage;

    public SolveTestController() {

        this.sharedStore = SharedStore.getInstance();

        this.resourceTest = (ResourceTest) sharedStore.getSelectedResource();
        this.visualTest = new LinkedHashMap<>();
        this.solvedTest = new LinkedHashMap<>();

        this.selectedQuestion = null;
        this.realTestAnswers = new LinkedHashSet<>();
        this.testAnswer1 = null;
        this.testAnswer2 = null;
        this.testAnswer3 = null;
        this.testAnswer4 = null;
        this.testAnswer5 = null;

        this.clientMessage = "";

    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label testTitleLabel;
    @FXML
    private Label questionsQuantityLabel;

    @FXML
    private ListView<String> questionsListView;

    @FXML
    private Label selectedQuestionLabel;
    @FXML
    private RadioButton answer1RadioButton;
    @FXML
    private RadioButton answer2RadioButton;
    @FXML
    private RadioButton answer3RadioButton;
    @FXML
    private RadioButton answer4RadioButton;
    @FXML
    private RadioButton answer5RadioButton;

    @FXML
    private Button registerSolvedTestButton;

    @FXML
    void chooseAnswer1() {
        if (answer2RadioButton.isSelected()) {
            solvedTest.put(selectedQuestion, testAnswer1); // Misma clave => Se sustituye
            questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());
        }
    }

    @FXML
    void chooseAnswer2() {
        if (answer2RadioButton.isSelected()) {
            solvedTest.put(selectedQuestion, testAnswer2);
            questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());
        }
    }

    @FXML
    void chooseAnswer3() {
        if (answer3RadioButton.isSelected()) {
            solvedTest.put(selectedQuestion, testAnswer3);
            questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());
        }
    }

    @FXML
    void chooseAnswer4() {
        if (answer4RadioButton.isSelected()) {
            solvedTest.put(selectedQuestion, testAnswer4);
            questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());
        }
    }

    @FXML
    void chooseAnswer5() {
        if (answer5RadioButton.isSelected()) {
            solvedTest.put(selectedQuestion, testAnswer5);
            questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());
        }
    }

    @FXML
    void solveTest() {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.UP);

        double scorePerQuestion = (double) 10 / resourceTest.getQuestionsQuantity();
        double totalScore = 0.0;

        for (TestQuestion testQuestion : realTest.keySet()) {
            if (solvedTest.containsKey(testQuestion)) { // Si ha respondido
                if (solvedTest.get(testQuestion).isCorrect()) { // Y lo ha hecho correctamente
                    totalScore += scorePerQuestion;
                }
            }
        }
        String totalScoreString = decimalFormat.format(totalScore);
        totalScoreString = totalScoreString.replace(",", "."); // Para poder guardarlo correctamente en la BBDD

        clientMessage = sharedStore.getProtocolMessages().getClientArgument(46) + "#" + sharedStore.getSelectedResource().getIdResource() + "#" + totalScoreString;

        for (TestQuestion testQuestion : solvedTest.keySet()) {
            clientMessage += "#" + solvedTest.get(testQuestion).getIdAnswer(); // C46 # idTest # Nota # idRespuesta # idRespuesta # idRespuesta...
        }

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        if (sharedStore.getApp().checkResponseResult()) {
            App.setRoot("virtual_class");
        }
    }

    public void fillQuestionsList() { // Se realiza 1 sola vez

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
                fillQuestionAnswers(newValue);
            }
        });

    }

    public void fillQuestionAnswers(String question) {

        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> selectedQuestionWithAnswers = visualTest.get(question);

        if (selectedQuestionWithAnswers != null) {

            testAnswer1 = null; // Reinicio
            testAnswer2 = null;
            testAnswer3 = null;
            testAnswer4 = null;
            testAnswer5 = null;

            answer1RadioButton.setText("");
            answer1RadioButton.setSelected(false);
            answer1RadioButton.setVisible(false);

            answer2RadioButton.setText("");
            answer2RadioButton.setSelected(false);
            answer2RadioButton.setVisible(false);

            answer3RadioButton.setText("");
            answer3RadioButton.setSelected(false);
            answer3RadioButton.setVisible(false);

            answer4RadioButton.setText("");
            answer4RadioButton.setSelected(false);
            answer4RadioButton.setVisible(false);

            answer5RadioButton.setText("");
            answer5RadioButton.setSelected(false);
            answer5RadioButton.setVisible(false);

            for (TestQuestion testQuestion : selectedQuestionWithAnswers.keySet()) { // "Cargo"
                selectedQuestion = testQuestion;
                selectedQuestionLabel.setText(testQuestion.getText());

                int i = 0;
                for (TestAnswer answer : selectedQuestionWithAnswers.get(testQuestion)) {
                    if (i == 0) {
                        testAnswer1 = answer;
                        answer1RadioButton.setText(answer.getText());
                        answer1RadioButton.setVisible(true);
                        if (solvedTest.containsKey(selectedQuestion)) {
                            if (solvedTest.get(selectedQuestion).equals(testAnswer1)) {
                                answer1RadioButton.setSelected(true);
                            }
                        }
                    } else if (i == 1) {
                        testAnswer2 = answer;
                        answer2RadioButton.setText(answer.getText());
                        answer2RadioButton.setVisible(true);
                        if (solvedTest.containsKey(selectedQuestion)) {
                            if (solvedTest.get(selectedQuestion).equals(testAnswer2)) {
                                answer2RadioButton.setSelected(true);
                            }
                        }
                    } else if (i == 2) {
                        testAnswer3 = answer;
                        answer3RadioButton.setText(answer.getText());
                        answer3RadioButton.setVisible(true);
                        if (solvedTest.containsKey(selectedQuestion)) {
                            if (solvedTest.get(selectedQuestion).equals(testAnswer3)) {
                                answer3RadioButton.setSelected(true);
                            }
                        }
                    } else if (i == 3) {
                        testAnswer4 = answer;
                        answer4RadioButton.setText(answer.getText());
                        answer4RadioButton.setVisible(true);
                        if (solvedTest.containsKey(selectedQuestion)) {
                            if (solvedTest.get(selectedQuestion).equals(testAnswer4)) {
                                answer4RadioButton.setSelected(true);
                            }
                        }
                    } else if (i == 4) {
                        testAnswer5 = answer;
                        answer5RadioButton.setText(answer.getText());
                        answer5RadioButton.setVisible(true);
                        if (solvedTest.containsKey(selectedQuestion)) {
                            if (solvedTest.get(selectedQuestion).equals(testAnswer5)) {
                                answer5RadioButton.setSelected(true);
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    public void configComponents() {
        selectedQuestionLabel.setWrapText(true);
        answer1RadioButton.setWrapText(true);
        answer2RadioButton.setWrapText(true);
        answer3RadioButton.setWrapText(true);
        answer4RadioButton.setWrapText(true);
        answer5RadioButton.setWrapText(true);
    }

    public void LoadData() {
        resourceTest = (ResourceTest) sharedStore.getSelectedResource();
        this.realTest = resourceTest.getTest();

        answer1RadioButton.setVisible(false);
        answer2RadioButton.setVisible(false);
        answer3RadioButton.setVisible(false);
        answer4RadioButton.setVisible(false);
        answer5RadioButton.setVisible(false);

        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        testTitleLabel.setText(resourceTest.getTitleResource());
        questionsQuantityLabel.setText(solvedTest.size() + "/" + resourceTest.getQuestionsQuantity());

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
