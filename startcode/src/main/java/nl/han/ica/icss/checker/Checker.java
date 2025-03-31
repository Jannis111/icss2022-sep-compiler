package nl.han.ica.icss.checker;

import javafx.scene.paint.Color;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet root)
    {
        variableTypes.add(new HashMap<>());
        for (ASTNode child : root.getChildren())
        {
            if (child instanceof VariableAssignment)
            {
                checkVariableAssignment((VariableAssignment) child);
            }else if(child instanceof Stylerule)
            {
                checkStylerule((Stylerule) child);
            }else if (child instanceof VariableReference)
            {
                checkVariableReference((VariableReference) child);
            }
        }
    }

    private void checkStylerule(Stylerule stylerule)
    {
        for (ASTNode child : stylerule.getChildren())
        {
            if (child instanceof VariableAssignment)
            {
                checkVariableAssignment((VariableAssignment) child);

            }else if(child instanceof Declaration)
            {
                checkDeclaration((Declaration)child);
            } else if (child instanceof VariableReference)
            {
                checkVariableReference((VariableReference) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);


            }
        }
    }

    private void checkDeclaration(Declaration node)
    {
        if(node.property.name.equals("width")|| node.property.name.equals("height"))
        {
            if (getExpressionType(node.expression) == ExpressionType.COLOR)
            {
              node.setError("Width may not be a color");
            }
        }else if(node.property.name.equals("color") || node.property.name.equals("background-color"))
        {
            if (getExpressionType(node.expression) == ExpressionType.PIXEL)
            {
                node.setError("Color may not consists of pixel");
            }
        }

        if (node.expression instanceof VariableReference)
        {
            checkVariableReference((VariableReference) node.expression);
        }

        if(node.expression instanceof Operation)
        {
            checkOperation((Operation) node.expression);
        }
    }

    private void checkOperation(Operation operation)
    {
        if(getExpressionType(operation) == ExpressionType.UNDEFINED)
        {
            operation.setError("This operation is wrong.");
        }
    }

    private ExpressionType getExpressionType(Expression expression)
    {
        if (expression instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expression instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expression instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expression instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expression instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if(expression instanceof AddOperation || expression instanceof SubtractOperation)
        {
           ExpressionType leftType = getExpressionType(((Operation) expression).lhs);
           ExpressionType rightType = getExpressionType(((Operation) expression).rhs);

           if(leftType == rightType)
           {
               return leftType;
           }else
           {
               return ExpressionType.UNDEFINED;
           }
        }
        else if(expression instanceof MultiplyOperation)
        {
            ExpressionType leftType = getExpressionType(((Operation) expression).lhs);
            ExpressionType rightType = getExpressionType(((Operation) expression).rhs);

            if (leftType == ExpressionType.SCALAR || rightType == ExpressionType.SCALAR) {
                if (leftType == ExpressionType.SCALAR) {
                    return rightType;
                } else {
                    return leftType;
                }
            } else {
                return ExpressionType.UNDEFINED;
            }

        }

        if(expression instanceof VariableReference)
        {
            for (HashMap<String, ExpressionType> hashMap : variableTypes)
            {
                if(hashMap.containsKey(((VariableReference)expression).name))
                {
                    return hashMap.get(((VariableReference)expression).name);
                }
            }
        }

        return ExpressionType.UNDEFINED;
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment)
    {
        ExpressionType expressionType = getExpressionType(variableAssignment.expression) ;
        if(expressionType == ExpressionType.UNDEFINED)
        {
            variableAssignment.setError("is not a literal");
        }else
        {
            variableTypes.getLast().put(variableAssignment.name.name, expressionType);
        }
    }

    private void checkVariableReference(VariableReference variableReference)
    {
        boolean definedVariableReference = false;
        for (HashMap<String, ExpressionType> scope : variableTypes)
        {
            if (scope.containsKey(variableReference.name)) {
                definedVariableReference = true;
                break;
            }
        }
        if(!(definedVariableReference)){
            variableReference.setError("Variable " + variableReference.name + " does not exist in this scope");
        }
    }

    private void checkIfClause(IfClause ifClause)
    {
        if(ifClause.conditionalExpression instanceof VariableReference)
        {
            for(HashMap<String, ExpressionType> hashMap: variableTypes)
            {
                if(hashMap.get(((VariableReference) ifClause.conditionalExpression).name) != ExpressionType.BOOL)
                {
                    ifClause.setError("Conditional expression is not a boolean");
                }
            }
        }
        else if (!(ifClause.conditionalExpression instanceof BoolLiteral))
        {
            ifClause.setError("Conditional expression is not a boolean");
        }
        if(!(ifClause.body.isEmpty()))
        {
            checkIfClauseBody(ifClause.body);
        }
    }

    private void checkIfClauseBody(ArrayList<ASTNode> body)
    {
        for (ASTNode bodyPart : body)
        {
            if(bodyPart instanceof IfClause)
            {
                checkIfClause((IfClause) bodyPart);
            }
        }
    }

}
