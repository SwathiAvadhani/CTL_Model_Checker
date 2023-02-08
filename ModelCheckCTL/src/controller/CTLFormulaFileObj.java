package controller;

import java.util.*;

import model.ExpressionsHolder;
import model.Kripke;
import model.State;

public class CTLFormulaFileObj {
    public enum TypeSAT {
    	Unknown,
        AllTrue,
        AllFalse,
        Atomic,
        Not,
        And,
        Or,
        Implies,
        AX,
        EX,
        AU,
        EU,
        EF,
        EG,
        AF,
        AG
    }
    public static Kripke kripkeStructure;
    public Map<String, String> convertedSymbol;
    public State state;
    public String expression;

    public CTLFormulaFileObj(String expression, State state, Kripke kripke) {
        this.convertedSymbol = new HashMap<>();
        this.convertedSymbol.put("->", ">");
        this.convertedSymbol.put("not", "!");
        this.convertedSymbol.put("and", "&");
        this.convertedSymbol.put("or", "|");
        this.kripkeStructure = kripke;
        this.state = state;
        this.expression = ConvertToSystemFormula(expression);
    }

    public String ConvertToSystemFormula(String expression) {
        for (String key : this.convertedSymbol.keySet()) {
            expression = expression.replace(key, this.convertedSymbol.get(key));
        }

        return expression;
    }

    public boolean IsSatisfy() {
        ExpressionType satClass = new ExpressionType();
        Set<State> states = satClass.SAT(expression);
        for(State returnedStates: states){
            if(returnedStates.equals(state))
                return true;
        }
        return false;
    }

    protected static String removeExtraBrackets(String expression) {
        String newExpression = expression;
        if (expression.startsWith("(") && expression.endsWith(")")) {
            newExpression = expression.substring(1);
        } else if (!expression.startsWith("(") && expression.endsWith(")")) {
            newExpression = expression.substring(0, expression.length() - 1);
        } else if (expression.startsWith("(") && !expression.endsWith(")")) {
            newExpression = expression.substring(1);
        }

        return newExpression;
    }

    static boolean isAtomic(String expression) {
        if (kripkeStructure._atom.contains(expression))
            return true;
        return false;
    }

    static boolean isBinaryOp(String expression, String symbol, ExpressionsHolder expressionsHolder) {
        boolean isBinaryOp = false;
        if (expression.contains(symbol)) {
            int openParanthesisCount = 0;
            int closeParanthesisCount = 0;

            int i=0;
            while (i < expression.length() - 1) {
                String currentChar = expression.substring(i, i + 1);
                if (currentChar.equals(symbol) && openParanthesisCount == closeParanthesisCount) {
                    expressionsHolder.setLeftExpression(expression.substring(0, i));
                    expressionsHolder.setRightExpression(expression.substring(i + 1));
                    isBinaryOp = true;
                    break;
                } else if (currentChar.equals("(")) {
                    openParanthesisCount++;
                } else if (currentChar.equals(")")) {
                    closeParanthesisCount++;
                }
                i++;
            }
        }
        return isBinaryOp;
    }


}
