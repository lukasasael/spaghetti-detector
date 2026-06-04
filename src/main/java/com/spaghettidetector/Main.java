package com.spaghettidetector;

import com.github.javaparser.ast.CompilationUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Erro: Caminho do arquivo .java não foi fornecido.");
            System.err.println("Uso: java -jar spaghetti-detector.jar <caminho_do_arquivo.java>");
            System.exit(1);
        }

        String filePath = args[0];
        try {
            JavaFileParser parser = new JavaFileParser();
            CompilationUnit cu = parser.parse(filePath);

            String fileName = new File(filePath).getName();
            MetricsReport report = new MetricsReport(fileName);

            MetricsVisitor visitor = new MetricsVisitor();
            cu.accept(visitor, report);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(report);

            System.out.println(jsonOutput);

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            System.exit(1);
        }
    }
}