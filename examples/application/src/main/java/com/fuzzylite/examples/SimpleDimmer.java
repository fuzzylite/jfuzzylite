/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.examples;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllImporter;
import java.io.File;
import java.net.URL;

public class SimpleDimmer {

    private Engine engine;

    public SimpleDimmer() {
        String configurationFile = "/SimpleDimmer.fll";
        URL url = SimpleDimmer.class.getResource(configurationFile);
        try {
            engine = new FllImporter().fromFile(new File(url.toURI()));
        } catch (Exception ex) {
            FuzzyLite.log().severe(ex.toString());
        }
    }

    public void run() {
        FldExporter exporter = new FldExporter();
        FuzzyLite.log().info(exporter.toString(engine, 10 * engine.getInputVariables().size()));
    }

    public static void main(String[] args) {
        new SimpleDimmer().run();
    }

}
