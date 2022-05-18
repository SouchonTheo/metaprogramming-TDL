/**
 * 
 */
package fr.n7.stl.block.ast.instruction;


import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
import fr.n7.stl.block.ast.type.*;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		boolean result_condition = this.condition.collectAndBackwardResolve(_scope);
		boolean result_then = this.thenBranch.collectAndBackwardResolve(_scope);
		if (this.elseBranch != null) {
			boolean result_else = this.elseBranch.collectAndBackwardResolve(_scope);
			return result_condition && result_else && result_then;
		}
		return result_condition && result_then;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result_condition = this.condition.fullResolve(_scope);
		boolean result_then = this.thenBranch.fullResolve(_scope);
		if (this.elseBranch != null) {
			boolean result_else = this.elseBranch.fullResolve(_scope);
			return result_condition && result_else && result_then;
		}
		return result_condition && result_then;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		boolean condition_result = this.condition.getType().compatibleWith(AtomicType.BooleanType);
		if (condition_result) {
			boolean then_result = this.thenBranch.checkType();
			if (this.elseBranch != null) {
				boolean else_result = this.elseBranch.checkType();
				return condition_result && then_result && else_result;
			}
			return then_result;
		} else {
			Logger.error("Condition not boolean");
			return false;
		}
	}

	@Override
	public Type returnsTo(){
		Type return_then = thenBranch.returnsTo();
		if (elseBranch == null) {
			return return_then;
		} else {
			Type return_else = elseBranch.returnsTo();
			if (return_else == AtomicType.VoidType || return_then == AtomicType.VoidType) {
				return AtomicType.VoidType;
			} else if (return_else == return_then) {
				return return_else;
			} else {
				
				Logger.error("Return types incompatible");
				return AtomicType.ErrorType;
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.thenBranch.allocateMemory(_register, _offset);
		if (elseBranch != null) {
			this.elseBranch.allocateMemory(_register, _offset);
		}
		return _offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		// On met en place les labels et les fragments
		int ifNum = _factory.createLabelNumber();
		String labelElse = "etiq_sinon_conditionnelle_"+ ifNum;;
		String labelEnd = "etiq_fin_conditionnelle_" + ifNum;
		Fragment elseFrag = null;
		Fragment thenFrag = this.thenBranch.getCode(_factory);
		
		// On crée le fragment principal au fur et à mesure
		Fragment frag = _factory.createFragment();
		frag.append(this.condition.getCode(_factory));
		if (this.elseBranch == null) { // 0 -> end
			frag.add(_factory.createJumpIf(labelEnd, 0));
		} else { // 0 -> else
			frag.add(_factory.createJumpIf(labelElse, 0));
			elseFrag = this.elseBranch.getCode(_factory);
			elseFrag.addPrefix(labelElse);
		}
		frag.append(thenFrag);
		if (elseFrag != null) {
			frag.add(_factory.createJump(labelEnd));
			frag.append(elseFrag);
		}
		frag.addSuffix(labelEnd);
		return frag;
	}
	/*
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = _factory.createFragment();
		int num = _factory.createLabelNumber();
		_result.append(this.condition.getCode(_factory));
		_result.add(_factory.createJumpIf("else" + num, 0));
		_result.append(this.thenBranch.getCode(_factory));
		_result.add(_factory.createJump("end" + num));
		if (elseBranch != null) {
			_result.addSuffix("else" + num);
			_result.append(this.elseBranch.getCode(_factory));
		}
		_result.addSuffix("end" + num);
		return _result;
	}
	*/
}
