/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class RScriptExporter extends Exporter {

    private String minimumColor;
    private String maximumColor;
    private String contourColor;

    public RScriptExporter() {
        this.minimumColor = "yellow";
        this.maximumColor = "red";
        this.contourColor = "black";
    }

    public String getMinimumColor() {
        return minimumColor;
    }

    public void setMinimumColor(String minimumColor) {
        this.minimumColor = minimumColor;
    }

    public String getMaximumColor() {
        return maximumColor;
    }

    public void setMaximumColor(String maximumColor) {
        this.maximumColor = maximumColor;
    }

    public String getContourColor() {
        return contourColor;
    }

    public void setContourColor(String contourColor) {
        this.contourColor = contourColor;
    }

    @Override
    public String toString(Engine engine) {
        if (engine.getInputVariables().isEmpty()) {
            throw new RuntimeException("[exporter error] engine has no input variables to export the surface");
        }
        if (engine.getOutputVariables().isEmpty()) {
            throw new RuntimeException("[exporter error] engine has no output variables to export the surface");
        }
        InputVariable a = engine.getInputVariables().get(0);
        InputVariable b = engine.getInputVariables().get(1 % engine.numberOfInputVariables());
        return toString(engine, a, b,
                1024, FldExporter.ScopeOfValues.AllVariables, engine.getOutputVariables());
    }

    public String toString(Engine engine, InputVariable a, InputVariable b,
            int values, FldExporter.ScopeOfValues scope,
            List<OutputVariable> outputVariables) {
        StringWriter writer = new StringWriter();
        try {
            writeScriptExportingDataFrame(engine, writer, a, b, values, scope, outputVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

    public String toString(Engine engine, InputVariable a, InputVariable b,
            Reader reader, List<OutputVariable> outputVariables) {
        StringWriter writer = new StringWriter();
        try {
            writeScriptExportingDataFrame(engine, writer, a, b, reader, outputVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

    @Override
    public void toFile(File file, Engine engine) throws IOException {
        if (engine.getInputVariables().isEmpty()) {
            throw new RuntimeException("[exporter error] engine has no input variables to export the surface");
        }
        if (engine.getOutputVariables().isEmpty()) {
            throw new RuntimeException("[exporter error] engine has no output variables to export the surface");
        }
        InputVariable a = engine.getInputVariables().get(0);
        InputVariable b = engine.getInputVariables().get(1 % engine.numberOfInputVariables());
        toFile(file, engine, a, b,
                1024, FldExporter.ScopeOfValues.AllVariables, engine.getOutputVariables());
    }

    public void toFile(File file, Engine engine,
            InputVariable a, InputVariable b,
            int values, FldExporter.ScopeOfValues scope,
            List<OutputVariable> outputVariables) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), FuzzyLite.UTF_8));
        try {
            writeScriptExportingDataFrame(engine, writer, a, b, values, scope, outputVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            writer.close();
        }
    }

    public void toFile(File file, Engine engine,
            InputVariable a, InputVariable b, Reader reader,
            List<OutputVariable> outputVariables) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), FuzzyLite.UTF_8));
        try {
            writeScriptExportingDataFrame(engine, writer, a, b, reader, outputVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            writer.close();
        }
    }

    public void writeScriptImportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, String dataFramePath,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer);

        writer.append("engine.name = \"" + engine.getName() + "\"\n");
        writer.append("engine.fll = \"" + new FllExporter().toString(engine) + "\"\n\n");
        writer.append("engine.fldFile = \"" + dataFramePath + "\"\n");
        writer.append("if (require(data.table)) {\n"
                + "    engine.df = data.table::fread(engine.fldFile, sep=\"auto\", header=\"auto\")\n"
                + "} else {\n"
                + "    engine.df = read.table(engine.fldFile, header=TRUE)\n"
                + "}\n");
        writer.append("\n");

        writeScriptPlots(writer, a, b, outputVariables);
    }

    public void writeScriptExportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, int values, FldExporter.ScopeOfValues scope,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer);

        writer.append("engine.name = \"" + engine.getName() + "\"\n");
        writer.append("engine.fll = \"" + new FllExporter().toString(engine) + "\"\n\n");

        List<InputVariable> activeVariables = new ArrayList<InputVariable>(engine.getInputVariables());
        for (int i = 0; i < activeVariables.size(); ++i) {
            if (activeVariables.get(i) != a && activeVariables.get(i) != b) {
                activeVariables.set(i, null);
            }
        }
        writer.append("engine.fldFile = \"");
        new FldExporter().write(engine, writer, values, scope, activeVariables);
        writer.append("\"\n\n");

        writer.append("engine.df = read.delim(textConnection(engine.fld), header=TRUE, "
                + "sep=\" \", strip.white=TRUE)\n\n");

        writeScriptPlots(writer, a, b, outputVariables);
    }

    public void writeScriptExportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, Reader reader,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer);

        writer.append("engine.name = \"" + engine.getName() + "\"\n");
        writer.append("engine.fll = \"" + new FllExporter().toString(engine) + "\"\n\n");

        writer.append("engine.fldFile = \"");
        new FldExporter().write(engine, writer, reader);
        writer.append("\"\n\n");

        writer.append("engine.df = read.delim(textConnection(engine.fld), header=TRUE, "
                + "sep=\" \", strip.white=TRUE)\n\n");

        writeScriptPlots(writer, a, b, outputVariables);
    }

    protected void writeScriptHeader(Writer writer) throws IOException {
        writer.append("#R script generated with " + FuzzyLite.LIBRARY + ".\n\n");
        writer.append("library(ggplot2);\n");
        writer.append("\n");
    }

    protected void writeScriptPlots(Writer writer,
            InputVariable a, InputVariable b,
            List<OutputVariable> outputVariables) throws IOException {
        StringWriter arrangeGrob = new StringWriter();
        arrangeGrob.append("arrangeGrob(");
        for (int i = 0; i < outputVariables.size(); ++i) {
            OutputVariable z = outputVariables.get(i);
            if (a != b) {
                writer.append("engine.plot.i1i2_o" + (i + 1) + " = ggplot(engine.df, aes(" + a.getName() + ", " + b.getName() + ")) + \n"
                        + "    geom_tile(aes(fill=" + z.getName() + ")) + \n"
                        + "    scale_fill_gradient(low=\"" + minimumColor + "\", high=\"" + maximumColor + "\") + \n"
                        + "    stat_contour(aes(x=" + a.getName() + ", y=" + b.getName() + ", z=" + z.getName() + "), color=\"" + contourColor + "\") + \n"
                        + "    ggtitle(\"(" + a.getName() + ", " + b.getName() + ") = " + z.getName() + "\")\n\n");

                writer.append("engine.plot.i2i1_o" + (i + 1) + " = ggplot(engine.df, aes(" + b.getName() + ", " + a.getName() + ")) + \n"
                        + "    geom_tile(aes(fill=" + z.getName() + ")) + \n"
                        + "    scale_fill_gradient(low=\"" + minimumColor + "\", high=\"" + maximumColor + "\") + \n"
                        + "    stat_contour(aes(x=" + b.getName() + ", y=" + a.getName() + ", z=" + z.getName() + "), color=\"" + contourColor + "\") + \n"
                        + "    ggtitle(\"(" + b.getName() + ", " + a.getName() + ") = " + z.getName() + "\")\n\n");

                arrangeGrob.append("engine.plot.i1i2_o" + (i + 1) + ", " + "engine.plot.i2i1_o" + (i + 1) + ", ");
            } else {
                writer.append("engine.plot.i1_o" + (i + 1) + " = ggplot(engine.df, aes(" + a.getName() + ", " + z.getName() + ")) + \n"
                        + "    geom_line(aes(color=" + z.getName() + "), size=3, lineend=\"round\", linejoin=\"mitre\") + \n"
                        + "    scale_color_gradient(low=\"" + minimumColor + "\", high=\"" + maximumColor + "\") + \n"
                        + "    ggtitle(\"" + a.getName() + " vs " + z.getName() + "\")\n\n");
                writer.append("engine.plot.o" + (i + 1) + "_i1 = ggplot(engine.df, aes(" + a.getName() + ", " + z.getName() + ")) + \n"
                        + "    geom_line(aes(color=" + z.getName() + "), size=3, lineend=\"round\", linejoin=\"mitre\") + \n"
                        + "    scale_color_gradient(low=\"" + minimumColor + "\", high=\"" + maximumColor + "\") + \n"
                        + "    coord_flip() + \n"
                        + "    ggtitle(\"" + a.getName() + " vs " + z.getName() + "\")\n\n");
                arrangeGrob.append("engine.plot.i1_o" + (i + 1) + ", " + "engine.plot.o" + (i + 1) + "_i1, ");
            }
        }

        arrangeGrob.append("ncol=2, top=engine.name)");
        writer.append("if (require(gridExtra)) {\n"
                + "    engine.plots = " + arrangeGrob.toString() + "\n"
                + "    ggsave(paste0(engine.name, \".pdf\"), engine.plots)\n"
                + "    if (require(grid)) {\n"
                + "        grid.newpage()\n"
                + "        grid.draw(engine.plots)\n"
                + "    }\n"
                + "}\n");
    }

    @Override
    public RScriptExporter clone() throws CloneNotSupportedException {
        return (RScriptExporter) super.clone();
    }

}
