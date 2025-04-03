package nl.han.ica.icss.generator;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

	public String generate(AST ast) {


		return generateStyleSheet(ast.root);


	}

	private String generateStyleSheet(Stylesheet root)
	{
		StringBuilder styleSheetString = new StringBuilder();
		for (ASTNode child :root.getChildren())
		{
			if(child instanceof Stylerule)
			{
				styleSheetString.append(generateStyleRule((Stylerule)child));
				styleSheetString.append("\n");
			}
		}
		return styleSheetString.toString();
	}

	private StringBuilder generateStyleRule(Stylerule stylerule)
	{
		StringBuilder styleRuleString = new StringBuilder();
		for (ASTNode child : stylerule.getChildren())
		{

			if(child instanceof Selector)
			{
				styleRuleString.append(child.toString());
				styleRuleString.append(" {");
			}
			else if(child instanceof Declaration)
			{
				styleRuleString.append("  ");
				styleRuleString.append(((Declaration) child).property.name);
				styleRuleString.append(": ");

				Expression expression = ((Declaration) child).expression;
				if(expression instanceof ColorLiteral)
				{
					styleRuleString.append(((ColorLiteral) expression).value);
				}
				else if(expression instanceof PercentageLiteral)
				{
					styleRuleString.append(((PercentageLiteral) expression).value);
					styleRuleString.append("%");
				}
				else if(expression instanceof ScalarLiteral)
				{
					styleRuleString.append(((ScalarLiteral) expression).value);
				}
				else if (expression instanceof PixelLiteral)
				{
					styleRuleString.append(((PixelLiteral) expression).value);
					styleRuleString.append("px");
				}
				styleRuleString.append(";");
			}

			styleRuleString.append("\n");
		}
		styleRuleString.append("}");
		styleRuleString.append("\n");
		return styleRuleString;
	}


}
