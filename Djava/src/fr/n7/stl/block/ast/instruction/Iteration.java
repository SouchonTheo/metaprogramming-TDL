/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.Block;
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
public class Iteration implements Instruction {

	protected Expression condition;
	protected Block body;

	public Iteration(Expression _condition, Block _body) {
		this.condition = _condition;
		this.body = _body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "while (" + this.condition + " )" + this.body;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		boolean result_condition = this.condition.collectAndBackwardResolve(_scope);
		boolean result_block = this.body.collectAndBackwardResolve(_scope);
		return result_block && result_condition;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result_condition = this.condition.fullResolve(_scope);
		boolean result_block = this.body.fullResolve(_scope);
		return result_block && result_condition;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		boolean result_condition = this.condition.getType().compatibleWith(AtomicType.BooleanType);
		boolean result_body = this.body.checkType();
		if(result_condition) {
			return result_body;
		} else {
			Logger.error("Error : Condition not boolean.");
			return false;
		}
	}

	@Override
	public Type returnsTo(){
		return this.body.returnsTo();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.body.allocateMemory(_register, _offset);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		int num = _factory.createLabelNumber();
		String labelStart = "etiq_debut_tantque_" + num;
		String labelEnd = "etiq_fin_tantque_" + num;
		frag.append(this.condition.getCode(_factory));
		frag.addPrefix(labelStart);
		frag.add(_factory.createJumpIf(labelEnd, 0));
		Fragment block = this.body.getCode(_factory);
		block.add(_factory.createJump(labelStart));
		frag.append(block);
		frag.addSuffix(labelEnd);
		return frag;
	}
}
