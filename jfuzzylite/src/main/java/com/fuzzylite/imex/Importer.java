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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Importer implements Op.Cloneable {

    public abstract Engine fromString(String text);

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
