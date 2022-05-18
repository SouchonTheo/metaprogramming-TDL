/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

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
		boolean result_cond = this.condition.collectAndBackwardResolve(_scope);
		boolean result_then = this.thenBranch.collect(_scope);
		if (this.elseBranch != null) {
			boolean result_else = this.elseBranch.collect(_scope);
			return result_cond && result_then && result_else;
		} else {
			return result_cond && result_then;
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result_cond = this.condition.fullResolve(_scope);
		boolean result_then = this.thenBranch.resolve(_scope);
		if (this.elseBranch != null) {
			boolean result_else = this.elseBranch.resolve(_scope);
			return result_cond && result_then && result_else;
		} else {
			return result_cond && result_then;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (this.condition.getType().compatibleWith(AtomicType.BooleanType)) {
			boolean res_then = this.thenBranch.checkType();
			boolean res_else = true;
			if (this.elseBranch != null) {
				res_else = this.elseBranch.checkType();
			}
			return res_then && res_else;
		} else {
			System.out.println("Conditionelle non booléenne.");
			return false;
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

	@Override
	public Type returnsTo() {
		Type thenReturn = this.thenBranch.returnsTo();
		if (this.elseBranch == null) {
			return thenReturn;
		} else {
			Type elseReturn = this.elseBranch.returnsTo();
			// Faire attention avec les VoidType
			if (thenReturn.equalsTo(elseReturn)) {
				return thenReturn;
			} else {
				Logger.error("Type de retour des deux branches du if incompatibles");
				return AtomicType.ErrorType;
			}
		}
	}
	
}
