package com.lucadev.dbfacade.util;

/**
 * Class with some help methods such as string validation etc...
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public class Helper {

    /**
     * Check the given string against null and empty.
     * @param string
     * @return
     */
    public static boolean isNotNullOrEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    /**
     * Check a list of strings against null and emptyness, returns false if only one string fails.
     * @param strings
     * @return
     */
    public static boolean isNotNullOrEmpty(String... strings) {
        for (String string : strings) {
            if(!isNotNullOrEmpty(string)) {
                return false;
            }
        }
        return true;
    }
}
