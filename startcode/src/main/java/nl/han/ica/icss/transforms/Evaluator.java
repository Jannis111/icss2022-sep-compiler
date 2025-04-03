package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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
            else if (child instanceof IfClause)
            {
                ArrayList<ASTNode> children = node.getChildren();
                int index = children.indexOf((IfClause) child);
                applyIfClause((IfClause) child, node, index);
            }
        }

        variableValues.removeFirst();
    }

    private void applyDeclartion(Declaration node)
    {
        node.expression = evalExpression(node.expression);

    }


    private void applyIfClause(IfClause ifClause, ASTNode parentNode, int index)
    {
        if(ifClause.conditionalExpression instanceof VariableReference)
        {
            ifClause.conditionalExpression = evalExpression((VariableReference)ifClause.conditionalExpression);
        }
        if(((BoolLiteral)ifClause.conditionalExpression).value)
        {
            if (!(ifClause.body.isEmpty()))
            {
                evalClauseBody(ifClause.body, parentNode, index);
                parentNode.removeChild(ifClause);
            }
        }
        else if(ifClause.elseClause != null)
        {
            if (!(ifClause.elseClause.body.isEmpty()))
            {
                evalClauseBody(ifClause.elseClause.body, parentNode, index);
                parentNode.removeChild(ifClause);
            }
        }

    }

    private void applyVariableAssignment(VariableAssignment node)
    {
        String variableReferenceName = node.name.name;
        Literal literal = (Literal) evalExpression(node.expression);

        node.expression = literal;

        variableValues.getFirst().put(variableReferenceName, literal);
    }


    private void evalClauseBody(ArrayList<ASTNode> body, ASTNode parentNode, int index)
    {
        variableValues.add(0, new HashMap<>());

        for (ASTNode bodyPart : body)
        {
            if (bodyPart instanceof IfClause)
            {
                applyIfClause((IfClause) bodyPart, parentNode, index);
            }
            else if (bodyPart instanceof VariableAssignment)
            {
                applyVariableAssignment((VariableAssignment) bodyPart);
            }
            else if (bodyPart instanceof Declaration)
            {

              //  parentNode.removeChild(bodyPart);
                applyDeclartion((Declaration) bodyPart);
                ((Stylerule) parentNode).addChild(bodyPart, index);
                index++;
            }
        }

        variableValues.removeFirst();
    }

    private Expression evalExpression(Expression expression)
    {
        if(expression instanceof Literal)
        {
            return (Literal)expression;
        }
        else if (expression instanceof Operation)
        {
            if(expression instanceof AddOperation)
            {
                Expression left =  evalExpression(((AddOperation)expression).lhs);
                Expression right =  evalExpression(((AddOperation)expression).rhs);

                if(left instanceof PixelLiteral && right instanceof PixelLiteral)
                {
                    return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
                } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
                    return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
                } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
                    return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
                }
            }
            else if (expression instanceof SubtractOperation)
            {
                Expression left = evalExpression(((SubtractOperation) expression).lhs);
                Expression right = evalExpression(((SubtractOperation) expression).rhs);

                if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
                    return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
                } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
                    return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
                } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
                    return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
                }
            }
            else if (expression instanceof MultiplyOperation)
            {
                Expression left =  evalExpression(((MultiplyOperation)expression).lhs);
                Expression right =  evalExpression(((MultiplyOperation)expression).rhs);

                // Scalar * Pixel
                if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
                    return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
                }
                else if (right instanceof ScalarLiteral && left instanceof PixelLiteral) {
                    return new PixelLiteral(((ScalarLiteral) right).value * ((PixelLiteral) left).value);
                }
                // Scalar * Percentage
                else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
                    return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
                }
                else if (right instanceof ScalarLiteral && left instanceof PercentageLiteral) {
                    return new PercentageLiteral(((ScalarLiteral) right).value * ((PercentageLiteral) left).value);
                }
                // Scalar * Scalar
                else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
                    return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
                }
            }
        }
        else if (expression instanceof VariableReference)
        {
            return evalVariableReference((VariableReference) expression);
        }
    return null;
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

}
