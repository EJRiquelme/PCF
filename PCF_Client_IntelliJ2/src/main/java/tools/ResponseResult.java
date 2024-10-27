/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

/**
 *
 * @author william
 */
public class ResponseResult {

    private String resultType;
    private String alertType;
    private String title;
    private String header;
    private String content;

    // CONSTRUCTORES
    //
    public ResponseResult() { // Reset
        this.resultType = "none";
    }

    public ResponseResult(String type, String content) { // Notifications
        this.resultType = type;
        this.content = content;
    }

    public ResponseResult(String resultType, String alertType, String title, String header, String content) { // Alerts
        this.resultType = resultType;
        this.alertType = alertType;
        this.title = title;
        this.header = header;
        this.content = content;
    }

    // GETTERS & SETTERS
    //
    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
