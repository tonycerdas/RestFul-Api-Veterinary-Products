/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package validation;

import java.util.regex.Pattern;

public class Validation {

    public Validation() {
    }

    public boolean evaluarInputNumber(String value) {
        return Pattern.matches(justNumber, value);
    }

    public boolean evaluarInputText(String value) {
        return Pattern.matches(justWord, value);
    }

    public boolean evaluarInputUrl(String value) {
        return Pattern.matches(justUrl, value);
    }
    String justNumber = "^[0-9.-]+$";
    String justUrl = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    String justWord = "[a-zA-Z ]{2,254}";
}
