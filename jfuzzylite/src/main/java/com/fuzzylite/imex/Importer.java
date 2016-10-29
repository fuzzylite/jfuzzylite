/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 The Importer class is the abstract class for importers to configure an Engine
 and its components from different text formats.

 @todo declare methods to import specific components

 @author Juan Rada-Vilela, Ph.D.
 @see Exporter
 @since 4.0
 */
public abstract class Importer implements Op.Cloneable {

    /**
     Imports the engine from the given text

     @param text is the string representation of the engine to import from
     @return the engine represented by the text
     */
    public abstract Engine fromString(String text);

    /**
     Imports the engine from the given file

     @param file is the file containing the engine to import
     @return the engine represented by the file
     @throws IOException if any error occurs upon reading the file
     */
    public Engine fromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), FuzzyLite.UTF_8));
        String line;
        StringBuilder textEngine = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                textEngine.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            reader.close();
        }
        return fromString(textEngine.toString());
    }

    @Override
    public Importer clone() throws CloneNotSupportedException {
        return (Importer) super.clone();
    }
}
