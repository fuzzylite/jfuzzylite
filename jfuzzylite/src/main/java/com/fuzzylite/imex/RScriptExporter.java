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

/**
 The RScriptExporter class is an Exporter that creates an R script to plot one
 or more surfaces of an engine for two input variables and any number of output
 variables.

 @author Juan Rada-Vilela, Ph.D.
 @see FldExporter
 @see Exporter
 @since 6.0
 */
public class RScriptExporter extends Exporter {

    private String minimumColor;
    private String maximumColor;
    private String contourColor;

    public RScriptExporter() {
        this.minimumColor = "yellow";
        this.maximumColor = "red";
        this.contourColor = "black";
    }

    /**
     Gets the color to represent the minimum values.

     @return the color to represent the minimum values
     */
    public String getMinimumColor() {
        return minimumColor;
    }

    /**
     Sets the color to represent the minimum values.

     @param minimumColor is the color to represent the minimum values
     */
    public void setMinimumColor(String minimumColor) {
        this.minimumColor = minimumColor;
    }

    /**
     Gets the color to represent the maximum values.

     @return maximumColor is the color to represent the maximum values
     */
    public String getMaximumColor() {
        return maximumColor;
    }

    /**
     Sets the color to represent the maximum values.

     @param maximumColor is the color to represent the maximum values
     */
    public void setMaximumColor(String maximumColor) {
        this.maximumColor = maximumColor;
    }

    /**
     Gets the color to draw the contour lines

     @return the color to draw the contour lines
     */
    public String getContourColor() {
        return contourColor;
    }

    /**
     Sets the color to draw the contour lines

     @param contourColor is the color to draw the contour lines
     */
    public void setContourColor(String contourColor) {
        this.contourColor = contourColor;
    }

    /**
     Returns an R script plotting multiple surfaces based on a data frame
     generated with 1024 values in the scope of FldExporter::AllVariables for
     the first two input variables.

     @param engine is the engine to export
     @return an R script plotting multiple surfaces for the first two input
     variables in the engine.
     */
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

    /**
     Returns an R script plotting multiple surfaces based on a data frame
     generated with the given number of values and scope for the two input
     variables.

     @param engine is the engine to export
     @param a is the first input variable
     @param b is the second input variable
     @param values is the number of values to evaluate the engine
     @param scope is the scope of the number of values to evaluate the engine
     @param outputVariables are the output variables to create the surface for
     @return an R script plotting multiple surfaces for the two input variables
     on the output variables.
     */
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

    /**
     Returns an R script plotting multiple surfaces based on the input stream of
     values for the two input variables.

     @param engine is the engine to export
     @param a is the first input variable
     @param b is the second input variable
     @param reader is an input stream of data whose lines contain
     space-separated input values
     @param outputVariables are the output variables to create the surface for
     @return an R script plotting multiple surfaces for the two input variables
     on the output variables
     */
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

    /**
     Creates an R script file plotting multiple surfaces based on a data frame
     generated with 1024 values in the scope of FldExporter::AllVariables for
     the two input variables

     @param file is the R script file
     @param engine is the engine to export
     @throws IOException if any error occurs during writing the file
     */
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

    /**
     Creates an R script file plotting multiple surfaces based on a data frame
     generated with the given number of values and scope for the two input
     variables

     @param file is the R script file
     @param engine is the engine to export
     @param a is the first input variable
     @param b is the second input variable
     @param values is the number of values to evaluate the engine
     @param scope is the scope of the number of values to evaluate the engine
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing the file
     */
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

    /**
     Creates an R script file plotting multiple surfaces based on the input
     stream of values for the two input variables.

     @param file is the R script file
     @param engine is the engine to export
     @param a is the first input variable
     @param b is the second input variable
     @param reader is an input stream of data whose lines contain
     space-separated input values
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing the file
     */
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

    /**
     Writes an R script plotting multiple surfaces based on a manually imported
     data frame containing the data for the two input variables on the output
     variables.

     @param engine is the engine to export
     @param writer is the output where the engine will be written to
     @param a is the first input variable
     @param b is the second input variable
     @param dataFramePath is the path where the data frame should be located
     (the path will not be accessed, it will only be written to script)
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing on the writer

     */
    public void writeScriptImportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, String dataFramePath,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer, engine);

        writer.append("engine.fldFile = \"" + dataFramePath + "\"\n");
        writer.append("if (require(data.table)) {\n"
                + "    engine.df = data.table::fread(engine.fldFile, sep=\"auto\", header=\"auto\")\n"
                + "} else {\n"
                + "    engine.df = read.table(engine.fldFile, header=TRUE)\n"
                + "}\n");
        writer.append("\n");

        writeScriptPlots(writer, a, b, outputVariables);
    }

    /**
     Writes an R script plotting multiple surfaces based on a data frame
     generated with the given number of values and scope for the two input
     variables on the output variables.

     @param engine is the engine to export
     @param writer is the output where the engine will be written to
     @param a is the first input variable
     @param b is the second input variable
     @param values is the number of values to evaluate the engine
     @param scope is the scope of the number of values to evaluate the engine
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing on the writer
     */
    public void writeScriptExportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, int values, FldExporter.ScopeOfValues scope,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer, engine);

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

    /**
     Writes an R script plotting multiple surfaces based on a data frame
     generated with the given number of values and scope for the two input
     variables on the output variables.

     @param engine is the engine to export
     @param writer is the output where the engine will be written to
     @param a is the first input variable
     @param b is the second input variable
     @param reader is an input stream of data whose lines contain
     space-separated input values
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing on the writer

     */
    public void writeScriptExportingDataFrame(Engine engine, Writer writer,
            InputVariable a, InputVariable b, Reader reader,
            List<OutputVariable> outputVariables) throws IOException {
        writeScriptHeader(writer, engine);

        writer.append("engine.fldFile = \"");
        new FldExporter().write(engine, writer, reader);
        writer.append("\"\n\n");

        writer.append("engine.df = read.delim(textConnection(engine.fld), header=TRUE, "
                + "sep=\" \", strip.white=TRUE)\n\n");

        writeScriptPlots(writer, a, b, outputVariables);
    }

    /**
     Writes the header of the R script (e.g., import libraries)

     @param writer is the output where the header will be written to
     @param engine is the engine to export
     @throws IOException if any error occurs upon writing on the writer

     */
    protected void writeScriptHeader(Writer writer, Engine engine) throws IOException {
        writer.append("#Code automatically generated with " + FuzzyLite.LIBRARY + ".\n\n");
        writer.append("library(ggplot2);\n");
        writer.append("\n");
        writer.append("engine.name = \"" + engine.getName() + "\"\n");
        if (!Op.isEmpty(engine.getDescription())) {
            writer.append(String.format(
                    "engine.description = \"%s\"", engine.getDescription()));
        }
        writer.append("engine.fll = \"" + new FllExporter().toString(engine) + "\"\n\n");
    }

    /**
     Writes the code to generate the surface plots for the input variables on
     the output variables.

     @param writer is the output where the engine will be written to
     @param a is the first input variable
     @param b is the second input variable
     @param outputVariables are the output variables to create the surface for
     @throws IOException if any error occurs upon writing on the writer
     */
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
