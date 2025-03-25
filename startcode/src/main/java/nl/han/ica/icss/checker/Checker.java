package nl.han.ica.icss.checker;

import javafx.scene.paint.Color;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet root)
    {
        for (ASTNode child : root.getChildren())
        {
            if(child instanceof Stylerule)
            {
                checkStylerule((Stylerule) child);
            }

        }

    }

    private void checkStylerule(Stylerule stylerule)
    {
        for (ASTNode child : stylerule.getChildren())
        {
            if(child instanceof Declaration)
            {
                checkDeclaration((Declaration)child);
            }
        }
    }

    private void checkDeclaration(Declaration node)
    {
        if(node.property.name.equals("width")|| node.property.name.equals("height"))
        {
            if (node.expression instanceof ColorLiteral)
            {
              node.setError("Width may not be a color");
            }
        }else if(node.property.name.equals("color") || node.property.name.equals("background-color"))
        {
            if (node.expression instanceof PixelLiteral)
            {
                node.setError("Color may not consists of pixel");
            }
        }
    }


}
