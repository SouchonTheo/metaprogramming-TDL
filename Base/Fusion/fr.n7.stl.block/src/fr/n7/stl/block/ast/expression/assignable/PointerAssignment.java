/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.expression.AbstractPointer;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns the content of a pointed cell.
 * @author Marc Pantel
 *
 */
public class PointerAssignment extends AbstractPointer implements AssignableExpression {

	/**
	 * Construction for the implementation of a pointer content assignment expression Abstract Syntax Tree node.
	 * @param _pointer Abstract Syntax Tree for the pointer expression in a pointer content assignment expression.
	 */
	public PointerAssignment(Expression _pointer) {
		super(_pointer);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.PointerAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = _factory.createFragment();
		int s = this.pointer.getType().length();
		VariableAssignment va = (VariableAssignment) this.pointer;
		if (va.declaration instanceof VariableDeclaration) {
			VariableDeclaration d = (VariableDeclaration) va.declaration;
			_result.add(_factory.createLoad(
				d.getRegister(), 
				d.getOffset(),
				d.getType().length()));
		} else if(va.declaration instanceof ParameterDeclaration) {
			Logger.error("ParameterDeclaration not implemented yet.");
		}
		_result.add(_factory.createStoreI(s));
		return _result;
	}

	/* (non-Javadoc)
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		VariableAssignment realPointer = (VariableAssignment) this.pointer;
		frag.add(_factory.createLoad(realPointer.declaration.getRegister(), realPointer.declaration.getOffset(), realPointer.declaration.getType().length()));
		frag.add(_factory.createStoreI(this.pointer.getType().length()));
		return frag;
	}
	*/
	
}
