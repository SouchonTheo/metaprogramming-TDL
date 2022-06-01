/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAccess extends AbstractField {

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
		Type realType = this.record.getType();
		if (realType instanceof NamedType) {
			realType = ((NamedType) this.record.getType()).getType();
		}
		RecordType rec = (RecordType) realType;
		
		int deplToField = 0;
		int totalDepl = 0;
		int fieldSize = 0;
		for (FieldDeclaration f : rec.getFields()){
			if (f.getName().equals(this.field.getName())) {
				fieldSize = f.getType().length();
				deplToField = totalDepl;
			} else {
				totalDepl += f.getType().length();
			}
		}
		Fragment _result = _factory.createFragment();
		// On ajoute tout le record
		_result.append(this.record.getCode(_factory));
		// On enlève ce qu'il y a en dessous et au dessus
		_result.add(_factory.createPop(0,deplToField));
		_result.add(_factory.createPop(fieldSize, totalDepl - deplToField - fieldSize));
		return _result;
	}
}
