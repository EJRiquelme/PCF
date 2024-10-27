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
    private String text;
    private int idTest;

    public TestQuestion() {
    }

    public TestQuestion(int idQuestion, String text, int idTest) {
        this.idQuestion = idQuestion;
        this.text = text;
        this.idTest = idTest;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIdTest() {
        return idTest;
    }

    public void setIdTest(int idTest) {
        this.idTest = idTest;
    }
}
