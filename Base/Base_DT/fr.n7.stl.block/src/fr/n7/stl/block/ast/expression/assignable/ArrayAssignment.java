/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.BinaryOperator;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.expression.value.IntegerValue;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a cell in an array.
 * @author Marc Pantel
 */
public class ArrayAssignment extends AbstractArray implements AssignableExpression {

	/**
	 * Construction for the implementation of an array element assignment expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element assignment expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element assignment expression.
	 */
	public ArrayAssignment(AssignableExpression _array, Expression _index) {
		super(_array, _index);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.ArrayAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		int elementSize = this.array.getType().length();
		Fragment frag = _factory.createFragment();
		VariableAssignment realArray = (VariableAssignment) this.array;
		frag.add(_factory.createLoad(realArray.declaration.getRegister(), realArray.declaration.getOffset(), realArray.declaration.getType().length()));
		IntegerValue realIndex = (IntegerValue) this.index;
		frag.add(_factory.createLoadL(realIndex.getValue()));
		frag.add(_factory.createLoadL(elementSize));
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Multiply));
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
		frag.add(_factory.createStoreI(elementSize));
		return frag;
	}	
}
