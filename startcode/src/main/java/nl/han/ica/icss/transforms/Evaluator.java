package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet root)
    {
        for (ASTNode child : root.getChildren())
        {
            if(child instanceof Stylerule)
            {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyStylerule(Stylerule node)
    {
        for (ASTNode child : node.getChildren())
        {
            if(child instanceof Declaration)
            {
                applyDeclartion((Declaration) child);
            }
        }
    }

    private void applyDeclartion(Declaration node)
    {
        node.expression = evalExpression(node.expression);
    }

    private Expression evalExpression(Expression expression)
    {
        if(expression instanceof Literal)
        {
            return (Literal)expression;
        } else
        {
            return evalAddOperation((AddOperation) expression);
        }
    }

    private Expression evalAddOperation(AddOperation addOperation)
    {
        PixelLiteral left = (PixelLiteral) evalExpression(addOperation.lhs);
        PixelLiteral right = (PixelLiteral) evalExpression(addOperation.rhs);

        return new PixelLiteral(left.value + right.value);
    }
}
