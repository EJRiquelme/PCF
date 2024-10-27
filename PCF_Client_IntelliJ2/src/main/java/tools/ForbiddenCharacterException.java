/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import javafx.scene.control.Label;

/**
 *
 * @author william
 */
public class ForbiddenCharacterException extends Exception {

    public ForbiddenCharacterException(String errorMessage) {
        super(errorMessage);
    }
}
