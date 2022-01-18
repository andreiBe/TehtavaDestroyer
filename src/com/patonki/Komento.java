package com.patonki;

import com.patonki.virheet.KomentoVirhe;

/**
 * Komento, jota käyttäjä voi käyttää koodissaan #-merkillä
 */
public interface Komento {
    String run(String[] parameters, KoodiParser parser) throws KomentoVirhe;
}
