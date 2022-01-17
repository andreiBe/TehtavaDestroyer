package com.patonki;

/**
 * Komento, jota käyttäjä voi käyttää koodissaan #-merkillä
 */
public interface Komento {
    String run(String[] parameters, KoodiParser parser);
}
