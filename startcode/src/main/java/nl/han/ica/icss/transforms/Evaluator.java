package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet root)
    {
        variableValues.add(0, new HashMap<>());

        for (ASTNode child : root.getChildren())
        {
            if(child instanceof Stylerule)
            {
                applyStylerule((Stylerule) child);
            }
            else if (child instanceof VariableAssignment)
            {
                applyVariableAssignment((VariableAssignment) child);
            }
        }

        variableValues.removeFirst();
    }

    private void applyStylerule(Stylerule node)
    {
        variableValues.add(0, new HashMap<>());

        for (ASTNode child : node.getChildren())
        {
            if(child instanceof Declaration)
            {
                applyDeclartion((Declaration) child);
            }
            else if (child instanceof VariableAssignment)
            {
                applyVariableAssignment((VariableAssignment) child);
            }
        }

        variableValues.removeFirst();
    }

    private void applyDeclartion(Declaration node)
    {
        node.expression = evalExpression(node.expression);

    }

    private void applyVariableAssignment(VariableAssignment node)
    {
        String variableReferenceName = node.name.name;
        Literal literal = (Literal) evalExpression(node.expression);

        variableValues.getFirst().put(variableReferenceName, literal);
    }

    private Expression evalExpression(Expression expression)
    {
        if(expression instanceof Literal)
        {
            return (Literal)expression;
        }
        else if (expression instanceof VariableReference)
        {
            return evalVariableReference((VariableReference) expression);
        }
        else
        {
            return evalAddOperation((AddOperation) expression);
        }
    }

    private Literal evalVariableReference(VariableReference node)
    {
        for (HashMap<String, Literal> scope : variableValues)
        {
            if(scope.containsKey(node.name))
            {
                return scope.get(node.name);
            }
        }
        return null;
    }

    private Expression evalAddOperation(AddOperation addOperation)
    {
        PixelLiteral left = (PixelLiteral) evalExpression(addOperation.lhs);
        PixelLiteral right = (PixelLiteral) evalExpression(addOperation.rhs);

        return new PixelLiteral(left.value + right.value);
    }


}
