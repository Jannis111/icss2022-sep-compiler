package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {

		ast.root = (Stylesheet) currentContainer.pop();
	}

	@Override
	public void enterIdselector(ICSSParser.IdselectorContext ctx) {
		IdSelector selector = new IdSelector(ctx.getText());
		currentContainer.push(selector);
	}

	@Override
	public void exitIdselector(ICSSParser.IdselectorContext ctx) {
		IdSelector selector = (IdSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterTagselector(ICSSParser.TagselectorContext ctx) {
		TagSelector tagSelector = new TagSelector(ctx.getText());
		currentContainer.push(tagSelector);
	}

	@Override
	public void exitTagselector(ICSSParser.TagselectorContext ctx) {
		TagSelector selector = (TagSelector) currentContainer.pop();
		currentContainer.peek().addChild(selector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyname(ICSSParser.PropertynameContext ctx) {
		PropertyName propertyName = new PropertyName(ctx.getText());
		currentContainer.push(propertyName);
	}

	@Override
	public void exitPropertyname(ICSSParser.PropertynameContext ctx) {
		PropertyName propertyName = (PropertyName) currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}

	@Override
	public void enterClasselector(ICSSParser.ClasselectorContext ctx) {
		ClassSelector classSelector = new ClassSelector(ctx.getText());
		currentContainer.push(classSelector);
	}

	@Override
	public void exitClasselector(ICSSParser.ClasselectorContext ctx) {
		ClassSelector classSelector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(classSelector);
	}

	@Override
	public void enterColorliteral(ICSSParser.ColorliteralContext ctx) {
		ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	@Override
	public void exitColorliteral(ICSSParser.ColorliteralContext ctx) {
		ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	@Override
	public void enterPixelliteral(ICSSParser.PixelliteralContext ctx) {
		PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}

	@Override
	public void exitPixelliteral(ICSSParser.PixelliteralContext ctx) {
		PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
	}
}