package objects;

public class TestQuestion {
    /*
    CREATE TABLE pcf_test_question(
        id_test_question INT AUTO_INCREMENT PRIMARY KEY,
        text_test_question VARCHAR(1000) NOT NULL,
        id_test INT NOT NULL -- FOREIGN KEY id_test
    );
    ALTER TABLE pcf_test_question ADD FOREIGN KEY(id_test) REFERENCES pcf_test(id_test) ON DELETE CASCADE;
    */

    private int idQuestion;
    private String textQuestion;
    private int idTest;

    public TestQuestion() {

    }

    public TestQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public TestQuestion(String textQuestion, int idTest) {
        this.textQuestion = textQuestion;
        this.idTest = idTest;
    }

    public TestQuestion(int idQuestion, String textQuestion) {
        this.idQuestion = idQuestion;
        this.textQuestion = textQuestion;
    }

    public TestQuestion(int idQuestion, String textQuestion, int idTest) {
        this.idQuestion = idQuestion;
        this.textQuestion = textQuestion;
        this.idTest = idTest;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public int getIdTest() {
        return idTest;
    }

    public void setIdTest(int idTest) {
        this.idTest = idTest;
    }
}
