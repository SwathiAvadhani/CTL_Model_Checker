package controller;

import model.ExpressionsHolder;
import model.State;
import model.Transition;

import java.util.LinkedHashSet;
import java.util.Set;

public class ExpressionType {
    protected Set<State> SAT(String expression) {
        System.out.println(String.format("Original Formula: %s", expression));
        Set<State> states = new LinkedHashSet<>();
        ExpressionsHolder expressionsHolder = new ExpressionsHolder();
        expression = expression.trim();
        CTLFormulaFileObj.TypeSAT typeSAT = getTypeSAT(expression, expressionsHolder);

        System.out.println(String.format("Type SAT: %s", typeSAT.toString()));
        System.out.println(String.format("Left Expression: %s", expressionsHolder.getLeftExpression()));
        System.out.println(String.format("Right Expression: %s", expressionsHolder.getRightExpression()));

        System.out.println("**************************************************");

        switch (typeSAT) {
            case AllTrue:
                states.addAll(CTLFormulaFileObj.kripkeStructure._state);
                break;
            case AllFalse:
                break;
            case Atomic:
                for (State state : CTLFormulaFileObj.kripkeStructure._state) {
                    if (state.atoms.contains(expressionsHolder.getLeftExpression()))
                        states.add(state);
                }
                break;
            case Not:
                states.addAll(CTLFormulaFileObj.kripkeStructure._state);
                Set<State> f1States = SAT(expressionsHolder.getLeftExpression());

                for (State state : f1States) {
                    if (states.contains(state))
                        states.remove(state);
                }
                break;
            case And:
                Set<State> andF1States = SAT(expressionsHolder.getLeftExpression());
                Set<State> andF2States = SAT(expressionsHolder.getRightExpression());

                for (State state : andF1States) {
                    if (andF2States.contains(state))
                        states.add(state);
                }
                break;
            case Or:
                Set<State> orF1States = SAT(expressionsHolder.getLeftExpression());
                Set<State> orF2States = SAT(expressionsHolder.getRightExpression());

                states = orF1States;
                for (State state : orF2States) {
                    if (!states.contains(state))
                        states.add(state);
                }
                break;
            case Implies:
                String impliesFormula = "!" + expressionsHolder.getLeftExpression() + "|" + expressionsHolder.getRightExpression();
                states = SAT(impliesFormula);
                break;
            case AX:
                String axFormula = "!" + "EX" + "!" + expressionsHolder.getLeftExpression();

                states = SAT(axFormula);

                Set<State> tempStates = new LinkedHashSet<>();
                for (State sourceState : states) {
                    for (Transition transition : CTLFormulaFileObj.kripkeStructure._transition) {
                        if (sourceState.equals(transition.fromState)) {
                            tempStates.add(sourceState);
                            break;
                        }
                    }
                }
                states = tempStates;
                break;
            case EX:
                String exFormula = expressionsHolder.getLeftExpression();
                states = SAT_EX(exFormula);
                break;
            case AU:
                StringBuilder auFormulaBuilder = new StringBuilder();
                auFormulaBuilder.append("!(E(!");
                auFormulaBuilder.append(expressionsHolder.getRightExpression());
                auFormulaBuilder.append("U(!");
                auFormulaBuilder.append(expressionsHolder.getLeftExpression());
                auFormulaBuilder.append("&!");
                auFormulaBuilder.append(expressionsHolder.getRightExpression());
                auFormulaBuilder.append("))|(EG!");
                auFormulaBuilder.append(expressionsHolder.getRightExpression());
                auFormulaBuilder.append("))");
                states = SAT(auFormulaBuilder.toString());
                break;
            case EU:
                states = SAT_EU(expressionsHolder.getLeftExpression(), expressionsHolder.getRightExpression());
                break;
            case EF:
                String efFormula = "E(TU" + expressionsHolder.getLeftExpression() + ")";
                states = SAT(efFormula);
                break;
            case EG:
                String egFormula = "!AF!" + expressionsHolder.getLeftExpression();
                states = SAT(egFormula);
                break;
            case AF:
                String afFormula = expressionsHolder.getLeftExpression();
                states = SAT_AF(afFormula);
                break;
            case AG:
                String agFormula = "!EF!" + expressionsHolder.getLeftExpression();
                states = SAT(agFormula);
                break;
            case Unknown:
                throw new IllegalArgumentException("Invalid CTL expression");
        }
        return states;
    }

    private CTLFormulaFileObj.TypeSAT getTypeSAT(String expression, ExpressionsHolder expressionsHolder)
    {
        //remove extra brackets
        expression = CTLFormulaFileObj.removeExtraBrackets(expression);

        //looking for binary implies, and. or, AU, EU
        if (expression.contains(">")) {
            if (CTLFormulaFileObj.isBinaryOp(expression, ">", expressionsHolder))
                return CTLFormulaFileObj.TypeSAT.Implies;
        }

        if (expression.contains("&")) {
            if (CTLFormulaFileObj.isBinaryOp(expression, "&", expressionsHolder))
                return CTLFormulaFileObj.TypeSAT.And;
        }

        if (expression.contains("|")) {
            if (CTLFormulaFileObj.isBinaryOp(expression, "|", expressionsHolder))
                return CTLFormulaFileObj.TypeSAT.Or;
        }

        if (expression.startsWith("A(")) {
            String strippedExpression = expression.substring(2);
            if (CTLFormulaFileObj.isBinaryOp(strippedExpression, "U", expressionsHolder))
                return CTLFormulaFileObj.TypeSAT.AU;
        }

        if (expression.startsWith("E(")) {
            String strippedExpression = expression.substring(2);
            if (CTLFormulaFileObj.isBinaryOp(strippedExpression, "U", expressionsHolder))
                return CTLFormulaFileObj.TypeSAT.EU;
        }

        if (expression.equals("T")) {
            expressionsHolder.setLeftExpression(expression);
            return CTLFormulaFileObj.TypeSAT.AllTrue;
        }
        if (expression.equals("F")) {
            expressionsHolder.setLeftExpression(expression);
            return CTLFormulaFileObj.TypeSAT.AllFalse;
        }
        if (CTLFormulaFileObj.isAtomic(expression)) {
            expressionsHolder.setLeftExpression(expression);
            return CTLFormulaFileObj.TypeSAT.Atomic;
        }
        if (expression.startsWith("!")) {
            expressionsHolder.setLeftExpression(expression.substring(1));
            return CTLFormulaFileObj.TypeSAT.Not;
        }
        if (expression.startsWith("AX")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.AX;
        }
        if (expression.startsWith("EX")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.EX;
        }
        if (expression.startsWith("EF")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.EF;
        }
        if (expression.startsWith("EG")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.EG;
        }
        if (expression.startsWith("AF")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.AF;
        }
        if (expression.startsWith("AG")) {
            expressionsHolder.setLeftExpression(expression.substring(2));
            return CTLFormulaFileObj.TypeSAT.AG;
        }
        if (expression.equals("")) {
            expressionsHolder.setLeftExpression(expression);
            return CTLFormulaFileObj.TypeSAT.AllTrue;
        }

        return CTLFormulaFileObj.TypeSAT.Unknown;
    }

    private Set<State> SAT_EX(String expression) {
        Set<State> x = SAT(expression);
        Set<State> y = ValidateStates.PreE(x);
        return y;
    }

    private Set<State> SAT_EU(String leftExpression, String rightExpression) {
        Set<State> w = SAT(leftExpression);
        Set<State> x = new LinkedHashSet<>();
        Set<State> y = SAT(rightExpression);;

        x.addAll(CTLFormulaFileObj.kripkeStructure._state);
        while (!AreListStatesEqual(x, y)) {
            x = y;
            Set<State> newY = new LinkedHashSet<>();
            Set<State> preEStates = ValidateStates.PreE(y);

            newY.addAll(y);
            Set<State> wAndPreE = new LinkedHashSet<>();
            for (State state: w) {
                if (preEStates.contains(state))
                    wAndPreE.add(state);
            }
            for (State state: wAndPreE) {
                if (!newY.contains(state))
                    newY.add(state);
            }
            y = newY;
        }
        return y;
    }

    private Set<State> SAT_AF(String expression) {
        Set<State> x = new LinkedHashSet<>();
        x.addAll(CTLFormulaFileObj.kripkeStructure._state);
        Set<State> y = SAT(expression);
        while (!AreListStatesEqual(x, y)) {
            x = y;
            Set<State> newY = new LinkedHashSet<>();
            Set<State> preAStates = ValidateStates.PreA(y);
            newY.addAll(y);
            for (State state: preAStates) {
                if (!newY.contains(state))
                    newY.add(state);
            }
            y = newY;
        }
        return y;
    }

    private boolean AreListStatesEqual(Set<State> set1, Set<State> set2) {
        if (set1.size() != set2.size())
            return false;
        for (State state: set1) {
            if (!set2.contains(state))
                return false;
        }
        return true;
    }
}
