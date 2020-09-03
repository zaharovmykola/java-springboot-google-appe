package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.utils;

/* Извлечение текста стека ошибок, если сообщение ошибки пусто */
public class ErrorsGetter {
    public static String get(Exception _ex) {
        String errorString = "";
        if (_ex.getMessage() == null) {
            String errorTrace = "";
            for (StackTraceElement el : _ex.getStackTrace()) {
                errorTrace += el.toString() + "\n";
            }
            if (errorTrace.equals("")) {
            } else {
                errorString = errorTrace;
            }
        } else {
            errorString = _ex.getMessage();
        }
        return errorString;
    }
}
