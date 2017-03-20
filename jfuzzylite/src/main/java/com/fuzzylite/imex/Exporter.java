/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

/**
 The Exporter class is the abstract class for exporters to translate an Engine
 into different formats.

 @todo declare methods for exporting other components (e.g., Variable)

 @author Juan Rada-Vilela, Ph.D.
 @see Importer
 @since 4.0
 */
public abstract class Exporter implements Op.Cloneable {

    /**
     Returns a string representation of the engine

     @param engine is the engine to export
     @return a string representation of the engine
     */
    public abstract String toString(Engine engine);

    /**
     Stores the string representation of the engine into the specified file

     @param file is the file to export the engine to
     @param engine is the engine to export
     @throws IOException if any problem occurs upon creation or writing to the
     file
     */
    public void toFile(File file, Engine engine) throws IOException {
        if (!file.createNewFile()) {
            FuzzyLite.logger().log(Level.FINE, "Replacing file: {0}", file.getAbsolutePath());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), FuzzyLite.UTF_8));
        try {
            writer.write(toString(engine));
        } catch (IOException ex) {
            throw ex;
        } finally {
            writer.close();
        }
    }

    /**
     Creates a clone of the exporter

     @return a clone of the exporter
     */
    @Override
    public Exporter clone() throws CloneNotSupportedException {
        return (Exporter) super.clone();
    }

}
