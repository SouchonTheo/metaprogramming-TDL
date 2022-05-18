/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a binary expression.
 * @author Marc Pantel
 *
 */
/**
 * @author Marc Pantel
 *
 */
public class BinaryExpression implements Expression {

	/**
	 * AST node for the expression whose value is the left parameter for the binary expression.
	 */
	protected Expression left;
	
	/**
	 * AST node for the expression whose value is the left parameter for the binary expression.
	 */
	protected Expression right;
	
	/**
	 * Binary operator computed by the Binary Expression.
	 */
	protected BinaryOperator operator;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public BinaryExpression(Expression _left, BinaryOperator _operator, Expression _right) {
		this.left = _left;
		this.right = _right;
		this.operator = _operator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.left + " " + this.operator + " " + this.right + ")";
	}
	
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		boolean _left = this.left.collectAndBackwardResolve(_scope);
		boolean _right = this.right.collectAndBackwardResolve(_scope);
		return _left && _right;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean _left = this.left.fullResolve(_scope);
		boolean _right = this.right.fullResolve(_scope);
		return _left && _right;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		Type _left = this.left.getType();
		Type _right = this.right.getType();
		switch (this.operator) {
			case Add: {
				if (_left.compatibleWith(AtomicType.IntegerType) && _right.compatibleWith(AtomicType.IntegerType)) {
					return AtomicType.IntegerType;
				} else if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.FloatingType;
				} else if (_left.compatibleWith(AtomicType.StringType) && _right.compatibleWith(AtomicType.StringType)) {
					return AtomicType.StringType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}
			}
			case Substract: {
				if (_left.compatibleWith(AtomicType.IntegerType) && _right.compatibleWith(AtomicType.IntegerType)) {
					return AtomicType.IntegerType;
				} else if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.FloatingType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}
			}
			case Multiply: {
				if (_left.compatibleWith(AtomicType.IntegerType) && _right.compatibleWith(AtomicType.IntegerType)) {
					return AtomicType.IntegerType;
				} else if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.FloatingType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}
			}
			case Divide: {
				if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.FloatingType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}
			}
			case Modulo: {
				if (_left.compatibleWith(AtomicType.IntegerType) && _right.compatibleWith(AtomicType.IntegerType))  {
					return AtomicType.IntegerType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}
			}
			case Lesser: {
				if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			case Greater: {
				if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			case LesserOrEqual: {
				if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			case GreaterOrEqual: {
				if (_left.compatibleWith(AtomicType.FloatingType) && _right.compatibleWith(AtomicType.FloatingType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			case Equals: {
				if (_left.compatibleWith(AtomicType.ErrorType) || _right.compatibleWith(AtomicType.ErrorType)) {
					return AtomicType.ErrorType;
				} else {
					return AtomicType.BooleanType;
				}
			} 
			case Different: {
				if (_left.compatibleWith(AtomicType.ErrorType) || _right.compatibleWith(AtomicType.ErrorType)) {
					
					return AtomicType.ErrorType;
				} else {
					return AtomicType.BooleanType;
				}
			}
			case And: {
				if (_left.compatibleWith(AtomicType.BooleanType) && _right.compatibleWith(AtomicType.BooleanType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			case Or: {
				if (_left.compatibleWith(AtomicType.BooleanType) && _right.compatibleWith(AtomicType.BooleanType)) {
					return AtomicType.BooleanType;
				} else {
					Logger.error("Type error in binary expression : " + this.operator + " parameters " + _left + " " + _right);
					return AtomicType.ErrorType;
				}				
			}
			default : 
			//Branche non accessible : on a fait un switch exhaustif. Laissé en cas de problème au niveau du Lexer er Parser
			Logger.error("Error : Binary operation not known");
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = this.left.getCode(_factory);
		_result.addComment(this.toString());
		if (this.left instanceof AccessibleExpression) {
			_result.add(_factory.createLoadI(this.left.getType().length()));
		}
		_result.append(this.right.getCode(_factory));
		if (this.right instanceof AccessibleExpression) {
			_result.add(_factory.createLoadI(this.right.getType().length()));
		}
		_result.add(TAMFactory.createBinaryOperator(this.operator));
		return _result;
	}

}
