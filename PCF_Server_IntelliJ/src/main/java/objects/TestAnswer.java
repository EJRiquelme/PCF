package objects;

public class TestAnswer {
    /*
    CREATE TABLE pcf_test_answer(
        id_test_answer INT AUTO_INCREMENT PRIMARY KEY,
        text_test_answer VARCHAR(1000) NOT NULL,
        correct_test_answer BOOLEAN NOT NULL,
        id_test_question INT NOT NULL -- FOREIGN KEY id_test
    );
    ALTER TABLE pcf_test_answer ADD FOREIGN KEY(id_test_question) REFERENCES pcf_test_question(id_test_question) ON DELETE CASCADE;
    */

    private int idAnswer;
    private String textAnswer;
    private boolean correct;
    private int idQuestion;

    public TestAnswer() {
    }

    public TestAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public TestAnswer(String textAnswer, boolean correct, int idQuestion) { // INSERT
        this.textAnswer = textAnswer;
        this.correct = correct;
        this.idQuestion = idQuestion;
    }

    public TestAnswer(String textAnswer, boolean correct) {
        this.textAnswer = textAnswer;
        this.correct = correct;
    }

    public TestAnswer(int idAnswer, String textAnswer) {
        this.idAnswer = idAnswer;
        this.textAnswer = textAnswer;
    }

    public TestAnswer(int idAnswer, String textAnswer, boolean correct) {
        this.idAnswer = idAnswer;
        this.textAnswer = textAnswer;
        this.correct = correct;
    }

    public TestAnswer(int idAnswer, String textAnswer, boolean correct, int idQuestion) {
        this.idAnswer = idAnswer;
        this.textAnswer = textAnswer;
        this.correct = correct;
        this.idQuestion = idQuestion;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }
}
