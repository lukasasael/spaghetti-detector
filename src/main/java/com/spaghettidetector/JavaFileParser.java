package com.spaghettidetector;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JavaFileParser {

    public CompilationUnit parse(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("Arquivo não encontrado ou inválido: " + filePath);
        }

        try {
            return StaticJavaParser.parse(file);
        } catch (Exception e) {
            throw new IOException("Falha crítica na análise sintática (Erro de Sintaxe Java) para o arquivo: " + filePath, e);
        }
    }
}