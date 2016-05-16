/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

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

public abstract class Exporter implements Op.Cloneable {

    public abstract String toString(Engine engine);

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

    @Override
    public Exporter clone() throws CloneNotSupportedException {
        return (Exporter) super.clone();
    }

}
