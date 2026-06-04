package com.spaghettidetector;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MetricsVisitor extends VoidVisitorAdapter<MetricsReport> {

    @Override
    public void visit(MethodDeclaration md, MetricsReport report) {
        super.visit(md, report);
        analyzeBody(md, md.getNameAsString(), report);
    }

    @Override
    public void visit(ConstructorDeclaration cd, MetricsReport report) {
        super.visit(cd, report);
        analyzeBody(cd, cd.getNameAsString(), report);
    }

    private void analyzeBody(Node node, String name, MetricsReport report) {
        BodyAnalyzerVisitor bodyAnalyzer = new BodyAnalyzerVisitor();
        node.accept(bodyAnalyzer, null);

        int lines = 0;
        if (node.getRange().isPresent()) {
            var range = node.getRange().get();
            lines = range.end.line - range.begin.line + 1;
        }

        int parameters = 0;
        if (node instanceof MethodDeclaration) {
            parameters = ((MethodDeclaration) node).getParameters().size();
        } else if (node instanceof ConstructorDeclaration) {
            parameters = ((ConstructorDeclaration) node).getParameters().size();
        }

        MetricsReport.MethodMetrics metrics = new MetricsReport.MethodMetrics(
                name,
                lines,
                parameters,
                bodyAnalyzer.exits,
                bodyAnalyzer.maxDepth,
                bodyAnalyzer.jumps,
                bodyAnalyzer.conditionals,
                bodyAnalyzer.loops
        );
        report.addMethod(metrics);
    }

    /**
     * Visitor interno para computar métricas isoladas do corpo do método/construtor.
     */
    private static class BodyAnalyzerVisitor extends VoidVisitorAdapter<Void> {
        int exits = 0;
        int currentDepth = 0;
        int maxDepth = 0;
        int jumps = 0;
        int conditionals = 0;
        int loops = 0;

        // Auxiliar para detectar se estamos dentro de um loop
        private int loopNestingLevel = 0;

        private void enterStructuralNode() {
            currentDepth++;
            if (currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }
        }

        private void exitStructuralNode() {
            currentDepth--;
        }

        @Override
        public void visit(IfStmt n, Void arg) {
            conditionals++;
            enterStructuralNode();
            super.visit(n, arg);
            exitStructuralNode();
        }

        private void handleLoop(Node n, Void arg) {
            loops++;
            loopNestingLevel++;
            enterStructuralNode();
            
            // Força a navegação manual dos filhos para garantir a ordem léxica correta
            for (Node child : n.getChildNodes()) {
                child.accept(this, arg);
            }

            exitStructuralNode();
            loopNestingLevel--;
        }

        @Override
        public void visit(ForStmt n, Void arg) { handleLoop(n, arg); }

        @Override
        public void visit(ForEachStmt n, Void arg) { handleLoop(n, arg); }

        @Override
        public void visit(WhileStmt n, Void arg) { handleLoop(n, arg); }

        @Override
        public void visit(DoStmt n, Void arg) { handleLoop(n, arg); }

        @Override
        public void visit(SwitchStmt n, Void arg) {
            enterStructuralNode();
            super.visit(n, arg);
            exitStructuralNode();
        }

        @Override
        public void visit(TryStmt n, Void arg) {
            enterStructuralNode();
            super.visit(n, arg);
            exitStructuralNode();
        }

        @Override
        public void visit(ReturnStmt n, Void arg) {
            exits++;
            super.visit(n, arg);
        }

        @Override
        public void visit(ThrowStmt n, Void arg) {
            exits++;
            super.visit(n, arg);
        }

        @Override
        public void visit(BreakStmt n, Void arg) {
            exits++; // break sempre conta como saída prematura de bloco linear
            if (loopNestingLevel > 0) {
                jumps++; // Se estiver dentro de loop, conta como desvio (jump)
            }
            super.visit(n, arg);
        }

        @Override
        public void visit(ContinueStmt n, Void arg) {
            jumps++; // continue é puramente um jump estrutural de loop
            super.visit(n, arg);
        }
    }
}