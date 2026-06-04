package com.spaghettidetector;

public class TestSample {

    // MÉTODO 1: Linear e simples
    public int simpleMethod(int x) {
        return x * 2;
    }

    // MÉTODO 2: Lógica aninhada e desvios
    public String classifyScore(int score) {
        if (score >= 90) {
            return "A";
        } else if (score >= 80) {
            return "B";
        } else if (score >= 70) {
            for (int i = 0; i < 3; i++) {
                if (i == 2) {
                    continue; // <- Jump!
                }
            }
            return "C";
        } else {
            throw new IllegalArgumentException("Score inválido");
        }
    }

    // MÉTODO 3: Loops e interrupções
    public void processItems(String[] items) {
        for (String item : items) {
            if (item == null) {
                continue;
            }
            for (int i = 0; i < item.length(); i++) {
                if (item.charAt(i) == 'x') {
                    break; 
                }
            }
        }
    }
}