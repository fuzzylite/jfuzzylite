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
import com.fuzzylite.Op;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Exporter implements Op.Cloneable {

    public abstract String toString(Engine engine);

    public void toFile(File file, Engine engine) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(toString(engine));
        writer.flush();
        writer.close();
    }

    @Override
    public Exporter clone() throws CloneNotSupportedException {
        return (Exporter) super.clone();
    }

}
