// Generated from Tiger.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TigerParser}.
 */
public interface TigerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TigerParser#tigerProgram}.
	 * @param ctx the parse tree
	 */
	void enterTigerProgram(TigerParser.TigerProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#tigerProgram}.
	 * @param ctx the parse tree
	 */
	void exitTigerProgram(TigerParser.TigerProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#declarationSegment}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationSegment(TigerParser.DeclarationSegmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#declarationSegment}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationSegment(TigerParser.DeclarationSegmentContext ctx);
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
	 * Enter a parse tree produced by {@link TigerParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(TigerParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(TigerParser.VarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#functDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctDeclaration(TigerParser.FunctDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#functDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctDeclaration(TigerParser.FunctDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(TigerParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(TigerParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(TigerParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(TigerParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(TigerParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(TigerParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#idList}.
	 * @param ctx the parse tree
	 */
	void enterIdList(TigerParser.IdListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#idList}.
	 * @param ctx the parse tree
	 */
	void exitIdList(TigerParser.IdListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#statSeq}.
	 * @param ctx the parse tree
	 */
	void enterStatSeq(TigerParser.StatSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#statSeq}.
	 * @param ctx the parse tree
	 */
	void exitStatSeq(TigerParser.StatSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(TigerParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(TigerParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#assignStat}.
	 * @param ctx the parse tree
	 */
	void enterAssignStat(TigerParser.AssignStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#assignStat}.
	 * @param ctx the parse tree
	 */
	void exitAssignStat(TigerParser.AssignStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#ifStat}.
	 * @param ctx the parse tree
	 */
	void enterIfStat(TigerParser.IfStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#ifStat}.
	 * @param ctx the parse tree
	 */
	void exitIfStat(TigerParser.IfStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#whileStat}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(TigerParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#whileStat}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(TigerParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#forStat}.
	 * @param ctx the parse tree
	 */
	void enterForStat(TigerParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#forStat}.
	 * @param ctx the parse tree
	 */
	void exitForStat(TigerParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funcCallStat}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallStat(TigerParser.FuncCallStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funcCallStat}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallStat(TigerParser.FuncCallStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(TigerParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(TigerParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(TigerParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(TigerParser.ExprListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDivExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDivExpr(TigerParser.MulDivExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDivExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDivExpr(TigerParser.MulDivExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(TigerParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(TigerParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParensExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParensExpr(TigerParser.ParensExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParensExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParensExpr(TigerParser.ParensExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(TigerParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(TigerParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSubExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSubExpr(TigerParser.AddSubExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSubExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSubExpr(TigerParser.AddSubExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FuncCallExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(TigerParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FuncCallExpr}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(TigerParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(TigerParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(TigerParser.LiteralContext ctx);
}