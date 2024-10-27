/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

/**
 *
 * @author william
 */
public class User {

    private int idUser;
    private boolean photo;
    private String name;
    private String email;
    private String password;
    private String province;
    private String township;
    private String shortPresentation;
    private String longPresentation;

    private double score;

    // CONSTRUCTOR
    public User() { // Vacio -> No Logeado
        this.idUser = 0;
        this.photo = false;
        this.name = "Cliente Anónimo";
        this.email = "";
        this.password = "";
        this.province = "";
        this.township = "";
        this.shortPresentation = "";
        this.longPresentation = "";
    }

    public User(String email) { // Para comprobaciones de existencia en BBDD
        this.email = email;
    }

    public User(int idUser, String name, double score) { // Exclusivo de Calificaciones
        this.idUser = idUser;
        this.name = name;
        this.score = score;
    }

    public User(String name, String email, String password) { // Para el Login
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(boolean photo, String name, String email, String password, String province, String township, String shortPresentation, String longPresentation) { // Para el Registro
        this.photo = photo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }

    public User(int idUser, boolean photo, String name, String email, String password, String province, String township, String shortPresentation, String longPresentation) { // Para el Registro
        this.idUser = idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }

    public User(int idUser, boolean photo, String name, String email, String province, String township, String shortPresentation, String longPresentation) { // Motor de Busqueda
        this.idUser = idUser;
        this.photo = photo;
        this.name = name;
        this.email = email;
        this.province = province;
        this.township = township;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public double getScore() {
        return score;
    }
}
