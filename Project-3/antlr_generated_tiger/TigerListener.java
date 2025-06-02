// Generated from Tiger.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TigerParser}.
 */
public interface TigerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TigerParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(TigerParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(TigerParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterLvalue(TigerParser.LvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitLvalue(TigerParser.LvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(TigerParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(TigerParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(TigerParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funcCall}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(TigerParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#arrayCreation}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreation(TigerParser.ArrayCreationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#arrayCreation}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreation(TigerParser.ArrayCreationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#recordCreation}.
	 * @param ctx the parse tree
	 */
	void enterRecordCreation(TigerParser.RecordCreationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#recordCreation}.
	 * @param ctx the parse tree
	 */
	void exitRecordCreation(TigerParser.RecordCreationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#ifExp}.
	 * @param ctx the parse tree
	 */
	void enterIfExp(TigerParser.IfExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#ifExp}.
	 * @param ctx the parse tree
	 */
	void exitIfExp(TigerParser.IfExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#whileExp}.
	 * @param ctx the parse tree
	 */
	void enterWhileExp(TigerParser.WhileExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#whileExp}.
	 * @param ctx the parse tree
	 */
	void exitWhileExp(TigerParser.WhileExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#forExp}.
	 * @param ctx the parse tree
	 */
	void enterForExp(TigerParser.ForExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#forExp}.
	 * @param ctx the parse tree
	 */
	void exitForExp(TigerParser.ForExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#letExp}.
	 * @param ctx the parse tree
	 */
	void enterLetExp(TigerParser.LetExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#letExp}.
	 * @param ctx the parse tree
	 */
	void exitLetExp(TigerParser.LetExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#exprSeq}.
	 * @param ctx the parse tree
	 */
	void enterExprSeq(TigerParser.ExprSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#exprSeq}.
	 * @param ctx the parse tree
	 */
	void exitExprSeq(TigerParser.ExprSeqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpr(TigerParser.AssignExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpr(TigerParser.AssignExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orAsTop}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOrAsTop(TigerParser.OrAsTopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orAsTop}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOrAsTop(TigerParser.OrAsTopContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(TigerParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(TigerParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(TigerParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(TigerParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#compExpr}.
	 * @param ctx the parse tree
	 */
	void enterCompExpr(TigerParser.CompExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#compExpr}.
	 * @param ctx the parse tree
	 */
	void exitCompExpr(TigerParser.CompExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(TigerParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(TigerParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(TigerParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(TigerParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(TigerParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(TigerParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#decList}.
	 * @param ctx the parse tree
	 */
	void enterDecList(TigerParser.DecListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#decList}.
	 * @param ctx the parse tree
	 */
	void exitDecList(TigerParser.DecListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(TigerParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(TigerParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#typeDec}.
	 * @param ctx the parse tree
	 */
	void enterTypeDec(TigerParser.TypeDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#typeDec}.
	 * @param ctx the parse tree
	 */
	void exitTypeDec(TigerParser.TypeDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#typeExp}.
	 * @param ctx the parse tree
	 */
	void enterTypeExp(TigerParser.TypeExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#typeExp}.
	 * @param ctx the parse tree
	 */
	void exitTypeExp(TigerParser.TypeExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#typeFields}.
	 * @param ctx the parse tree
	 */
	void enterTypeFields(TigerParser.TypeFieldsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#typeFields}.
	 * @param ctx the parse tree
	 */
	void exitTypeFields(TigerParser.TypeFieldsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#varDec}.
	 * @param ctx the parse tree
	 */
	void enterVarDec(TigerParser.VarDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#varDec}.
	 * @param ctx the parse tree
	 */
	void exitVarDec(TigerParser.VarDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funcDec}.
	 * @param ctx the parse tree
	 */
	void enterFuncDec(TigerParser.FuncDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funcDec}.
	 * @param ctx the parse tree
	 */
	void exitFuncDec(TigerParser.FuncDecContext ctx);
}