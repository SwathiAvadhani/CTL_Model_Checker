package model;

public class ExpressionsHolder {
    private String rightExp;
    private String leftExp;


    public ExpressionsHolder() {
        this.leftExp = "";
        this.rightExp = "";
    }

    public void setLeftExpression(String leftExp) {
        this.leftExp = leftExp;
    }

    public String getLeftExpression() {
        return this.leftExp;
    }

    public void setRightExpression(String rightExp) {
        this.rightExp = rightExp;
    }

    public String getRightExpression() {
        return this.rightExp;
    }
}
