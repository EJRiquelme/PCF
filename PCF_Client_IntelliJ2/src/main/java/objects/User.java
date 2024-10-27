package objects;

public class User {

    private int idUser;
    private boolean photo;
    private String name;
    private String email;
    private String province;
    private String township;
    private String shortPresentation;
    private String longPresentation;

    private boolean logged;

    private double score;

    // CONSTRUCTOR
    public User() {
        this.idUser = -1; // !NullPointer
        this.name = "Login";
        this.logged = false;
    }

    public User(int idUser, String name) {
        this.idUser = idUser;
        this.name = name;
    }

    public User(int idUser, String name, double score) {
        this.idUser = idUser;
        this.name = name;
        this.score = score;
    }

    public User(String name, String province, String township, String shortPresentation) {
        this.name = name;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
    }

    public User(boolean photo, String name, String email, String province, String township, String shortPresentation, String longPresentation) { // Logeado
        // idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        // password;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.logged = false; // Para logearse ha de hacerse el "setLogged(true)" a la fuerza
    }

    public User(int idUser, boolean photo, String name, String email, String province, String township, String shortPresentation, String longPresentation) { // Listas Usuarios
        this.idUser = idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        // password;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }

    /*
    public User(int idUser, boolean photo, String name, String email, String province, String township, String shortPresentation, String longPresentation) { // Listar Usuarios y Login Aceptado
        this.idUser = idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        this.province = province;
        this.township = township;
        //this.direction = direction;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }

    public User(int idUser, boolean photo, String name, String email, String province, String township, double coordianteX, double coordianteY, String shortPresentation, String longPresentation) { // Listar Usuarios y Login Aceptado
        this.idUser = idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        this.province = province;
        this.township = township;
        this.coordinateX = coordianteX;
        this.coordinateY = coordianteY;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }
     */
    // GETTERS & SETTERS
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public boolean hasPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getTownship() {
        return township;
    }

    public void setTownShip(String township) {
        this.township = township;
    }

    public String getShortPresentation() {
        return shortPresentation;
    }

    public void setShortPresentation(String shortPresentation) {
        this.shortPresentation = shortPresentation;
    }

    public String getLongPresentation() {
        return longPresentation;
    }

    public void setLongPresentation(String longPresentation) {
        this.longPresentation = longPresentation;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
