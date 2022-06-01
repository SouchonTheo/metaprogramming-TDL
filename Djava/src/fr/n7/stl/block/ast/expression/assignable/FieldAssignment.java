/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAssignment extends AbstractField implements AssignableExpression {

	/**
	 * Construction for the implementation of a record field assignment expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field assignment expression.
	 * @param _name Name of the field in the record field assignment expression.
	 */
	public FieldAssignment(AssignableExpression _record, String _name) {
		super(_record, _name);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.FieldAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Type realType = this.record.getType();
		if (realType instanceof NamedType) {
			realType = ((NamedType) this.record.getType()).getType();
		}
		RecordType rec = (RecordType) realType;

		int deplToField = 0;
		int fieldSize = 0;
		for (FieldDeclaration f : rec.getFields()) {
			if (f.getName().equals(this.field.getName())) {
				fieldSize = f.getType().length();
				break;
			} else {
				deplToField += f.getType().length();
			}
		}

		Fragment _result = _factory.createFragment();
		_result.add(_factory.createStore(Register.SB, deplToField, fieldSize));
		return _result;
	}
}
