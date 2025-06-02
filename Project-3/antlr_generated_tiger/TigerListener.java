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
	 * Enter a parse tree produced by {@link TigerParser#varDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclarationList(TigerParser.VarDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#varDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclarationList(TigerParser.VarDeclarationListContext ctx);
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
	 * Enter a parse tree produced by {@link TigerParser#optionalInit}.
	 * @param ctx the parse tree
	 */
	void enterOptionalInit(TigerParser.OptionalInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#optionalInit}.
	 * @param ctx the parse tree
	 */
	void exitOptionalInit(TigerParser.OptionalInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#functDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterFunctDeclarationList(TigerParser.FunctDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#functDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitFunctDeclarationList(TigerParser.FunctDeclarationListContext ctx);
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
	 * Enter a parse tree produced by {@link TigerParser#retType}.
	 * @param ctx the parse tree
	 */
	void enterRetType(TigerParser.RetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#retType}.
	 * @param ctx the parse tree
	 */
	void exitRetType(TigerParser.RetTypeContext ctx);
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
	 * Enter a parse tree produced by {@link TigerParser#typeId}.
	 * @param ctx the parse tree
	 */
	void enterTypeId(TigerParser.TypeIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#typeId}.
	 * @param ctx the parse tree
	 */
	void exitTypeId(TigerParser.TypeIdContext ctx);
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
	 * Enter a parse tree produced by the {@code assignStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssignStat(TigerParser.AssignStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssignStat(TigerParser.AssignStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifNoElse}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfNoElse(TigerParser.IfNoElseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifNoElse}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfNoElse(TigerParser.IfNoElseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifWithElse}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfWithElse(TigerParser.IfWithElseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifWithElse}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfWithElse(TigerParser.IfWithElseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWhileStat(TigerParser.WhileStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWhileStat(TigerParser.WhileStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterForStat(TigerParser.ForStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitForStat(TigerParser.ForStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallStat(TigerParser.FuncCallStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallStat(TigerParser.FuncCallStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBreakStat(TigerParser.BreakStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBreakStat(TigerParser.BreakStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(TigerParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(TigerParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code letBlockStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterLetBlockStat(TigerParser.LetBlockStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code letBlockStat}
	 * labeled alternative in {@link TigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitLetBlockStat(TigerParser.LetBlockStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#optPrefix}.
	 * @param ctx the parse tree
	 */
	void enterOptPrefix(TigerParser.OptPrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#optPrefix}.
	 * @param ctx the parse tree
	 */
	void exitOptPrefix(TigerParser.OptPrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#funcCallWithOptPrefix}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallWithOptPrefix(TigerParser.FuncCallWithOptPrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#funcCallWithOptPrefix}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallWithOptPrefix(TigerParser.FuncCallWithOptPrefixContext ctx);
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
	 * Enter a parse tree produced by the {@code topLevel}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTopLevel(TigerParser.TopLevelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code topLevel}
	 * labeled alternative in {@link TigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTopLevel(TigerParser.TopLevelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orChain}
	 * labeled alternative in {@link TigerParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrChain(TigerParser.OrChainContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orChain}
	 * labeled alternative in {@link TigerParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrChain(TigerParser.OrChainContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andChain}
	 * labeled alternative in {@link TigerParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndChain(TigerParser.AndChainContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andChain}
	 * labeled alternative in {@link TigerParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndChain(TigerParser.AndChainContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compare}
	 * labeled alternative in {@link TigerParser#compExpr}.
	 * @param ctx the parse tree
	 */
	void enterCompare(TigerParser.CompareContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compare}
	 * labeled alternative in {@link TigerParser#compExpr}.
	 * @param ctx the parse tree
	 */
	void exitCompare(TigerParser.CompareContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link TigerParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(TigerParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addSub}
	 * labeled alternative in {@link TigerParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(TigerParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mulDiv}
	 * labeled alternative in {@link TigerParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(TigerParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mulDiv}
	 * labeled alternative in {@link TigerParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(TigerParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negate}
	 * labeled alternative in {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterNegate(TigerParser.NegateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negate}
	 * labeled alternative in {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitNegate(TigerParser.NegateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prim}
	 * labeled alternative in {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrim(TigerParser.PrimContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prim}
	 * labeled alternative in {@link TigerParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrim(TigerParser.PrimContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constLit}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterConstLit(TigerParser.ConstLitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constLit}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitConstLit(TigerParser.ConstLitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lVal}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterLVal(TigerParser.LValContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lVal}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitLVal(TigerParser.LValContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(TigerParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(TigerParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(TigerParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link TigerParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(TigerParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayIndex}
	 * labeled alternative in {@link TigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterArrayIndex(TigerParser.ArrayIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayIndex}
	 * labeled alternative in {@link TigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitArrayIndex(TigerParser.ArrayIndexContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(TigerParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(TigerParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#compOp}.
	 * @param ctx the parse tree
	 */
	void enterCompOp(TigerParser.CompOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#compOp}.
	 * @param ctx the parse tree
	 */
	void exitCompOp(TigerParser.CompOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#addOp}.
	 * @param ctx the parse tree
	 */
	void enterAddOp(TigerParser.AddOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#addOp}.
	 * @param ctx the parse tree
	 */
	void exitAddOp(TigerParser.AddOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link TigerParser#mulOp}.
	 * @param ctx the parse tree
	 */
	void enterMulOp(TigerParser.MulOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link TigerParser#mulOp}.
	 * @param ctx the parse tree
	 */
	void exitMulOp(TigerParser.MulOpContext ctx);
}