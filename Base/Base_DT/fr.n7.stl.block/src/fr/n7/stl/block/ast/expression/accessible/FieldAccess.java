/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAccess extends AbstractField implements Expression {

	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public FieldAccess(Expression _record, String _name) {
		super(_record, _name);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		int depl = 0;
		int size = 0;
		Type realType = this.record.getType();
		if (realType instanceof NamedType) {
			realType = ((NamedType) this.record.getType()).getType();
		}
		RecordType type = (RecordType) realType;
		for (FieldDeclaration field : type.getFields()) {
			if (!field.getName().equals(this.name)) {
				System.out.println("fields diff : " + field);
				depl += field.getType().length();
			} else {
				System.out.println("fields eg : " + field);
				size = field.getType().length();
				break;
			}
		}
		Fragment frag = _factory.createFragment();
		System.out.println(this.record.getClass());
		//frag.add(_factory.createLoad(record.register, record.offset + depl, size));
		return frag;
	}

}
