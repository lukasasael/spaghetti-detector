package com.spaghettidetector;

import java.util.ArrayList;
import java.util.List;

public class MetricsReport {
    private String analyzedFile;
    private int totalMethods;
    private List<MethodMetrics> methods = new ArrayList<>();

    public MetricsReport(String analyzedFile) {
        this.analyzedFile = analyzedFile;
    }

    public void addMethod(MethodMetrics method) {
        this.methods.add(method);
        this.totalMethods = this.methods.size();
    }

    public static class MethodMetrics {
        private String methodName;
        private int lines;
        private int parameters;
        private int exits;
        private int depth;
        private int jumps;
        private int conditionals;
        private int loops;

        public MethodMetrics(String methodName, int lines, int parameters, int exits, 
                             int depth, int jumps, int conditionals, int loops) {
            this.methodName = methodName;
            this.lines = lines;
            this.parameters = parameters;
            this.exits = exits;
            this.depth = depth;
            this.jumps = jumps;
            this.conditionals = conditionals;
            this.loops = loops;
        }
    }
}