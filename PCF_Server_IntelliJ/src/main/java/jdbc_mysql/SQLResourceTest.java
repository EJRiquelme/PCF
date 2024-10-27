package jdbc_mysql;

import objects.ResourceTest;
import objects.TestAnswer;
import objects.TestQuestion;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLResourceTest {
    /*
    CREATE TABLE pcf_test(
	    id_test INT PRIMARY KEY, -- FOREIGN KEY id_resource
	    questions_quantity INT NOT NULL,
	    init_date_test DATE NOT NULL,
        init_time_test TIME NOT NULL,
        end_date_test DATE NOT NULL,
        end_time_test TIME NOT NULL,
	    percentage_test INT NOT NULL
    );
    ALTER TABLE pcf_test ADD FOREIGN KEY(id_test) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Test - Recurso

    CREATE TABLE pcf_test_question(
        id_test_question INT AUTO_INCREMENT PRIMARY KEY,
        text_test_question VARCHAR(1000) NOT NULL,
        id_test INT NOT NULL -- FOREIGN KEY id_test
    );
    ALTER TABLE pcf_test_question ADD FOREIGN KEY(id_test) REFERENCES pcf_test(id_test) ON DELETE CASCADE;

    CREATE TABLE pcf_test_answer(
        id_test_answer INT AUTO_INCREMENT PRIMARY KEY,
        text_test_answer VARCHAR(1000) NOT NULL,
        correct_test_answer BOOLEAN NOT NULL,
        id_test_question INT NOT NULL -- FOREIGN KEY id_test
    );
    ALTER TABLE pcf_test_answer ADD FOREIGN KEY(id_test_question) REFERENCES pcf_test_question(id_test_question) ON DELETE CASCADE;

    -----------------------------

    CREATE TABLE pcf_solved_test(
        id_solved_test INT AUTO_INCREMENT PRIMARY KEY,
        title_solved_test VARCHAR(50) NOT NULL,
        id_original_test INT NOT NULL,
        id_student INT NOT NULL
    );

    CREATE TABLE pcf_solved_test_question(
        id_solved_test_question INT AUTO_INCREMENT PRIMARY KEY,
        text_solved_test_question VARCHAR(1000) NOT NULL,
        id_solved_test INT NOT NULL -- FOREIGN KEY id_solved_test
    );
    ALTER TABLE pcf_solved_test_question ADD FOREIGN KEY(id_solved_test) REFERENCES pcf_solved_test(id_solved_test) ON DELETE CASCADE;

    CREATE TABLE pcf_solved_test_answer(
        id_solved_test_answer INT AUTO_INCREMENT PRIMARY KEY,
        text_solved_test_answer VARCHAR(1000) NOT NULL,
        correct_solved_test_answer BOOLEAN NOT NULL,
        id_solved_test_question INT NOT NULL -- FOREIGN KEY id_solved_test_question
    );
    ALTER TABLE pcf_solved_test_answer ADD FOREIGN KEY(id_solved_test_question) REFERENCES pcf_solved_test_question(id_solved_test_question) ON DELETE CASCADE;

    CREATE TABLE pcf_user_solves_test(
	    id_test INT,
        id_solved_test INT,
        id_user INT
    );
    ALTER TABLE pcf_user_solves_test ADD FOREIGN KEY(id_test) REFERENCES pcf_test(id_test) ON DELETE CASCADE;
    ALTER TABLE pcf_user_solves_test ADD FOREIGN KEY(id_solved_test) REFERENCES pcf_solved_test(id_solved_test) ON DELETE CASCADE;
    ALTER TABLE pcf_user_solves_test ADD FOREIGN KEY(id_user) REFERENCES pcf_user(id_user) ON DELETE CASCADE;
    */

    private Connection connection;
    private PreparedStatement pstm;

    private String testTable;
    private String[] testColumns;

    private String testQuestionTable;
    private String[] testQuestionColumns;

    private String testAnswerTable;
    private String[] testAnswerColumns;

    private String solvedTestTable;
    private String[] solvedTestColumns;

    private String solvedTestAnswerTable;
    private String[] solvedTestAnswerColumns;

    /*
    private String solvedTestQuestionTable;
    private String[] solvedTestQuestionColumns;

    private String userSolvesTestTable;
    private String[] userSolvesTestColumns;
    */
    public SQLResourceTest(Connection connection) {
        this.connection = connection;

        this.testTable = "pcf_test";
        this.testColumns = new String[]{"id_test", // 0
                "questions_quantity", // 1
                "init_date_test", // 2
                "init_time_test", // 3
                "end_date_test", // 4
                "end_time_test", // 5
                "percentage_test"}; // 6

        this.testQuestionTable = "pcf_test_question";
        this.testQuestionColumns = new String[]{"id_test_question", // 0
                "text_test_question", // 1
                "id_test"}; // 2

        this.testAnswerTable = "pcf_test_answer";
        this.testAnswerColumns = new String[]{"id_test_answer", // 0
                "text_test_answer", // 1
                "correct_test_answer", // 2
                "id_test_question"}; // 3

        //----------------------------------------------------------

        this.solvedTestTable = "pcf_solved_test";
        this.solvedTestColumns = new String[]{"id_solved_test", // 0
                "id_original_test", // 1
                "id_student", // 2
                "score"}; // 3

        this.solvedTestAnswerTable = "pcf_solved_test_answer";
        this.solvedTestAnswerColumns = new String[]{"id_solved_test", // 0
                "id_test_answer", // 1
                "chosen"}; // 3

        /*
        this.solvedTestTable = "pcf_solved_test";
        this.solvedTestColumns = new String[]{"id_solved_test", // 0
                "title_solved_test", // 1
                "id_original_test", // 2
                "id_student"}; // 3

        this.solvedTestQuestionTable = "pcf_solved_test_question";
        this.solvedTestQuestionColumns = new String[]{"id_solved_test_question", // 0
                "text_solved_test_question", // 1
                "id_solved_test"}; // 2

        this.solvedTestAnswerTable = "pcf_solved_test_answer";
        this.solvedTestAnswerColumns = new String[]{"id_solved_test_answer", // 0
                "text_solved_test_answer", // 1
                "correct_solved_test_answer", // 2
                "id_solved_test_question"}; // 3

        this.userSolvesTestTable = "pcf_user_solves_test";
        this.userSolvesTestColumns = new String[]{"id_test", // 0
                "id_solved_test", // 1
                "id_user", // 2
                "score"}; // 3 -> Puede ser null
        */
    }

    // INSERT TEST -> 1.idRecurso
    // 2.cantidadPreguntas, 3.fechaApertura, 4.horaApertura, 5.fechaCierre, 6.horaCierre, 7.porcentajeNota
    public String registerTest(ResourceTest resourceTest) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + testTable + " ("
                    + testColumns[0] + ", "
                    + testColumns[1] + ", "
                    + testColumns[2] + ", "
                    + testColumns[3] + ", "
                    + testColumns[4] + ", "
                    + testColumns[5] + ", "
                    + testColumns[6] + ") " +
                    "VALUES ((SELECT id_resource FROM pcf_resource WHERE order_resource = ? AND id_unit = ?), ?, ?, ?, ?, ?, ?);");

            pstm.setInt(1, resourceTest.getOrder());
            pstm.setInt(2, resourceTest.getIdUnit());
            pstm.setInt(3, resourceTest.getQuestionsQuantity());
            pstm.setDate(4, resourceTest.getOpenDate());
            pstm.setTime(5, resourceTest.getOpenTime());
            pstm.setDate(6, resourceTest.getCloseDate());
            pstm.setTime(7, resourceTest.getCloseTime());
            pstm.setInt(8, resourceTest.getPercentage());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // UPDATE TEST -> ResourceTest(Resource(7.idRecurso), 1.cantidadPreguntas, 2.fechaApertura, 3.horaApertura, 4.fechaCierre, 5.horaCierre, 6.porcentajeNota)
    //
    public String updateTest(ResourceTest resourceTest) {
        try {
            pstm = connection.prepareStatement("UPDATE " + testTable + " SET "
                    + testColumns[1] + " = ?, "
                    + testColumns[2] + " = ?, "
                    + testColumns[3] + " = ?, "
                    + testColumns[4] + " = ?, "
                    + testColumns[5] + " = ?, "
                    + testColumns[6] + " = ? WHERE " + testColumns[0] + " = ?;");

            pstm.setInt(1, resourceTest.getQuestionsQuantity());
            pstm.setDate(2, resourceTest.getOpenDate());
            pstm.setTime(3, resourceTest.getOpenTime());
            pstm.setDate(4, resourceTest.getCloseDate());
            pstm.setTime(5, resourceTest.getCloseTime());
            pstm.setInt(6, resourceTest.getPercentage());

            pstm.setInt(7, resourceTest.getIdResource());

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // PREGUNTA
    //
    public String registerTestQuestion(TestQuestion testQuestion) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + testQuestionTable + " ("
                    + testQuestionColumns[1] + ", "
                    + testQuestionColumns[2] + ") VALUES (?, ?);");

            pstm.setString(1, testQuestion.getTextQuestion());
            pstm.setInt(2, testQuestion.getIdTest());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public String updateTestQuestion(TestQuestion testQuestion) {
        try {
            pstm = connection.prepareStatement("UPDATE " + testQuestionTable + " SET "
                    + testQuestionColumns[1] + " = ? WHERE " + testQuestionColumns[0] + " = ?;");

            pstm.setString(1, testQuestion.getTextQuestion());
            pstm.setInt(2, testQuestion.getIdQuestion());

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

    public String deleteQuestion(int idQuestion) { // ON DELETE CASCADE
        try {

            pstm = connection.prepareStatement("DELETE FROM " + testQuestionTable +
                    " WHERE " + testQuestionColumns[0] + " = ?;");

            pstm.setInt(1, idQuestion);

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

    // RESPUESTAS
    //
    public String registerTestAnswers(LinkedHashSet<TestAnswer> testAnswers) {
        try {
            for (TestAnswer testAnswer : testAnswers) {
                pstm = connection.prepareStatement("INSERT INTO " + testAnswerTable + " ("
                        + testAnswerColumns[1] + ", "
                        + testAnswerColumns[2] + ", "
                        + testAnswerColumns[3] + ") VALUES (?, ?, ?);");

                pstm.setString(1, testAnswer.getTextAnswer());
                pstm.setBoolean(2, testAnswer.isCorrect());
                pstm.setInt(3, testAnswer.getIdQuestion());

                pstm.executeUpdate();
                pstm.close();
            }
            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public String updateTestAnswers(LinkedHashSet<TestAnswer> testAnswers) {
        try {
            for (TestAnswer testAnswer : testAnswers) {
                pstm = connection.prepareStatement("UPDATE " + testAnswerTable + " SET "
                        + testAnswerColumns[1] + " = ?, "
                        + testAnswerColumns[2] + " = ? WHERE " + testAnswerColumns[0] + " = ?;");

                pstm.setString(1, testAnswer.getTextAnswer());
                pstm.setBoolean(2, testAnswer.isCorrect());
                pstm.setInt(3, testAnswer.getIdAnswer());

                pstm.executeUpdate();
                pstm.close();
            }
            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

    public String deleteTestAnswers(LinkedHashSet<Integer> idAnswers) { // ON DELETE CASCADE
        try {
            for (Integer idAnswer : idAnswers) {
                pstm = connection.prepareStatement("DELETE FROM " + testAnswerTable +
                        " WHERE " + testAnswerColumns[0] + " = ?;");

                pstm.setInt(1, idAnswer);

                pstm.executeUpdate();
                pstm.close();
            }
            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

    public int getIdTestQuestion(TestQuestion testQuestion) {
        int idTestQuestion = -1;
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + testQuestionTable
                    + " WHERE " + testQuestionColumns[1] + " = ?"
                    + " AND " + testQuestionColumns[2] + " = ?;");

            pstm.setString(1, testQuestion.getTextQuestion());
            pstm.setInt(2, testQuestion.getIdTest());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    idTestQuestion = rst.getInt(testQuestionColumns[0]);
                }
                return idTestQuestion;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idTestQuestion;
    }


    public LinkedHashSet<Integer> getIdTestAnswers(int idQuestion) {
        LinkedHashSet<Integer> idAnswers = new LinkedHashSet<>();
        int idAnswer = -1;
        try {
            pstm = connection.prepareStatement("SELECT " + testAnswerColumns[0] +
                    " FROM " + testAnswerTable +
                    " WHERE " + testAnswerColumns[3] + " = ?" +
                    " ORDER BY " + testAnswerColumns[0] + ";"); // el id es AUTOINCREMENT

            pstm.setInt(1, idQuestion);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    idAnswer = rst.getInt(testAnswerColumns[0]);
                    idAnswers.add(idAnswer);
                }
                return idAnswers;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idAnswers;
    }

    // S46 # questionsQuantity # openDate # openTime # closeDate # closeTime # percentage # test
    //
    public ResourceTest selectTest(int idTest) {
        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM " + testTable + ", " + "pcf_resource" +
                    " WHERE " + testColumns[0] + " = " + "pcf_resource.id_resource" +
                    " AND " + testColumns[0] + " = ?;"); // el id es AUTOINCREMENT

            pstm.setInt(1, idTest);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    String titleResource = rst.getString("title_resource");
                    String presentationResource = rst.getString("presentation_resource");
                    boolean isHidden = rst.getBoolean("hidden_resource");
                    int questionsQuantity = rst.getInt(testColumns[1]);
                    Date openDate = rst.getDate(testColumns[2]);
                    Time openTime = rst.getTime(testColumns[3]);
                    Date closeDate = rst.getDate(testColumns[4]);
                    Time closeTime = rst.getTime(testColumns[5]);
                    int percentaje = rst.getInt(testColumns[6]);

                    return new ResourceTest(titleResource, presentationResource, isHidden, questionsQuantity, openDate, openTime, closeDate, closeTime, percentaje);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    // idP # p # idR1 # r1 # true # idR2 # r2 # false # |X| #idP2 # p2 # idR1 # r1 # true # idR2 # r2 # false # idR3 # r3 # false # idR4 # r4 # false
    //
    public LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> selectCompleteTest(int idTest) {
        LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test = new LinkedHashMap<>();
        TestQuestion testQuestion = new TestQuestion();
        LinkedHashSet<TestAnswer> testAnswers = new LinkedHashSet<>();

        try {
            pstm = connection.prepareStatement("SELECT pcf_test_question.id_test_question , pcf_test_question.text_test_question," +
                    " pcf_test_answer.id_test_answer, pcf_test_answer.text_test_answer, pcf_test_answer.correct_test_answer" +
                    " FROM pcf_test, pcf_test_question, pcf_test_answer" +
                    " WHERE pcf_test_question.id_test_question = pcf_test_answer.id_test_question" +
                    " AND pcf_test.id_test = pcf_test_question.id_test" +
                    " AND pcf_test.id_test = ?" +
                    " ORDER BY pcf_test_question.id_test_question ASC," +
                    " pcf_test_answer.id_test_answer ASC;"); // ORDER BY porsiaca, que de sql no me fio

            pstm.setInt(1, idTest);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idQuestion = rst.getInt("pcf_test_question.id_test_question");
                    String textQuestion = rst.getString("pcf_test_question.text_test_question");

                    if (testQuestion.getIdQuestion() != idQuestion) {
                        testQuestion = new TestQuestion(idQuestion, textQuestion);
                        testAnswers = new LinkedHashSet<>();
                    }

                    int idAnswer = rst.getInt("pcf_test_answer.id_test_answer");
                    String textAnswer = rst.getString("pcf_test_answer.text_test_answer");
                    boolean correct = rst.getBoolean("pcf_test_answer.correct_test_answer");

                    TestAnswer testAnswer = new TestAnswer(idAnswer, textAnswer, correct);
                    testAnswers.add(testAnswer);

                    test.put(testQuestion, testAnswers);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return test;
    }

    public int getIdSolvedTest(int idOriginalTest, int idStudent) {
        try {
            pstm = connection.prepareStatement("SELECT " + solvedTestColumns[0] +
                    " FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[1] + " = ?" +
                    " AND " + solvedTestColumns[2] + " = ?;");

            pstm.setInt(1, idOriginalTest);
            pstm.setInt(2, idStudent);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getInt(solvedTestColumns[0]);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    // INSERT TEST RESUELTO (Inicial)
    //
    public String registerSolvedTest(int idOriginalTest, int idStudent) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + solvedTestTable + " ("
                    + solvedTestColumns[1] + ", "
                    + solvedTestColumns[2] + ") VALUES (?, ?);");

            pstm.setInt(1, idOriginalTest);
            pstm.setInt(2, idStudent);

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public boolean existsSolvedTest(int idTest, int idUser) {
        try {
            pstm = connection.prepareStatement("SELECT " + solvedTestColumns[0] +
                    " FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[1] + " = " + "?" +
                    " AND " + solvedTestColumns[2] + " = ?;");

            pstm.setInt(1, idTest);
            pstm.setInt(2, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return true; // Si lo encuentra el Alumno ya ha intentado resolver el Test
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String setScore(int idTest, int idUser, double score) {
        try {
            pstm = connection.prepareStatement("UPDATE " + solvedTestTable +
                    " SET " + solvedTestColumns[3] + " = ?" + // Score
                    " WHERE " + solvedTestColumns[1] + " = ?" + // IdTest
                    " AND " + solvedTestColumns[2] + " = ?;"); // IdUser

            pstm.setDouble(1, score);
            pstm.setInt(2, idTest);
            pstm.setInt(3, idUser);

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // INSERT RESPUESTA ESCOGIDA
    //
    public String registerSolvedTestAnswer(int idSolvedTest, LinkedHashSet<TestAnswer> solvedTestAnswers) {
        try {
            String statement = "INSERT INTO " + solvedTestAnswerTable + " (" +
                    solvedTestAnswerColumns[0] + ", " + // idSolvedTest
                    solvedTestAnswerColumns[1] + ")" + // idAnswer
                    " VALUES";

            for (TestAnswer answer : solvedTestAnswers) {
                statement += " (?, ?),";
            }
            statement = statement.substring(0, statement.length() - 1); // Elimina la coma final
            statement += ";";

            pstm = connection.prepareStatement(statement);

            int i = 1;
            for (TestAnswer answer : solvedTestAnswers) {
                pstm.setInt(i, idSolvedTest);
                pstm.setInt(i + 1, answer.getIdAnswer());
                i += 2;
            }

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    /*

    // INSERT TEST <-> TEST RESUELTO <-> USUARIO
    //
    public String registerSolvedTestRelation(int idOriginalTest, int idUser) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + userSolvesTestTable + " ("
                    + userSolvesTestColumns[0] + ", "
                    + userSolvesTestColumns[1] + ", "
                    + userSolvesTestColumns[2] + ", "
                    + userSolvesTestColumns[3] + ")" +
                    " VALUES (?, " +
                    "(SELECT " + solvedTestColumns[0] +
                    " FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[2] + " = ?" +
                    " AND " + solvedTestColumns[3] + " = ?)" +
                    ", ?, ?);");

            pstm.setInt(1, idOriginalTest);

            pstm.setInt(2, idOriginalTest);
            pstm.setInt(3, idUser);

            pstm.setInt(4, idUser);
            pstm.setInt(5, 0);

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public int getIdSolvedTestQuestion(TestQuestion testQuestion, int idOriginalTest, int idUser) {
        try {
            pstm = connection.prepareStatement("SELECT " + solvedTestQuestionColumns[0] +
                    " FROM " + solvedTestQuestionTable +
                    " WHERE " + solvedTestQuestionColumns[1] + " = ?" +
                    " AND " + solvedTestQuestionColumns[2] + " = " +
                    "(SELECT " + solvedTestColumns[0] +
                    " FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[2] + " = ?" +
                    " AND " + solvedTestColumns[3] + " = ?)" +
                    ";");

            System.out.println(testQuestion.getIdQuestion() + " + " + testQuestion.getTextQuestion() + ":");

            pstm.setString(1, testQuestion.getTextQuestion());
            pstm.setInt(2, idOriginalTest);
            pstm.setInt(3, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    System.out.println(rst.getInt(solvedTestQuestionColumns[0]));
                    return rst.getInt(solvedTestQuestionColumns[0]);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    // INSERT PREGUNTA RESUELTA
    //
    public String registerSolvedTestQuestions(LinkedHashSet<TestQuestion> solvedTestQuestions, int idTest, int idUser) {
        try {
            for (TestQuestion solvedTestQuestion : solvedTestQuestions) {
                pstm = connection.prepareStatement("INSERT INTO " + solvedTestQuestionTable + " ("
                        + solvedTestQuestionColumns[1] + ", "
                        + solvedTestQuestionColumns[2] + ") VALUES (?, " +
                        "(SELECT " + solvedTestColumns[0] +
                        " FROM " + solvedTestTable +
                        " WHERE " + solvedTestColumns[2] + " = ?" +
                        " AND " + solvedTestColumns[3] + " = ?)" +
                        ");");

                pstm.setString(1, solvedTestQuestion.getTextQuestion());
                pstm.setInt(2, idTest);
                pstm.setInt(3, idUser);

                pstm.executeUpdate();
                pstm.close();
            }
            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // INSERT RESPUESTA ESCOGIDA
    //
    public String registerSolvedTestAnswer(LinkedHashSet<TestAnswer> solvedTestAnswers) {
        try {
            for (TestAnswer solvedTestAnswer : solvedTestAnswers) {
                pstm = connection.prepareStatement("INSERT INTO " + solvedTestAnswerTable + " (" +
                        solvedTestAnswerColumns[1] + ", " +
                        solvedTestAnswerColumns[2] + ", " +
                        solvedTestAnswerColumns[3] + ")" +
                        " VALUES (?, ?, ?);");

                pstm.setString(1, solvedTestAnswer.getTextAnswer());
                pstm.setBoolean(2, solvedTestAnswer.isCorrect());
                pstm.setInt(3, solvedTestAnswer.getIdQuestion());

                pstm.executeUpdate();
                pstm.close();
            }
            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }
    */

    public String deleteSolvedTestByOriginalTest(int idOriginalTest) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[2] + " = ?;");

            pstm.setInt(1, idOriginalTest);

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

    public String deleteSolvedTestByStudentId(int idStudent) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM " + solvedTestTable +
                    " WHERE " + solvedTestColumns[3] + " = ?;");

            pstm.setInt(1, idStudent);

            pstm.executeUpdate();
            pstm.close();

            return "true";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }

}




















