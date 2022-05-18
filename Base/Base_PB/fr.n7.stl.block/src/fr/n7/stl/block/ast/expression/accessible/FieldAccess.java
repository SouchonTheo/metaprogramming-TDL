/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
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
		Fragment _result = _factory.createFragment();
		RecordType rec = (RecordType) this.record.getType();
		List<FieldDeclaration> fields = rec.getFields();
		List<String> names = new LinkedList<String>();
		for (FieldDeclaration f : fields){
			names.add(f.getName());
		} 
		int pos = names.indexOf(this.name);
		int size = rec.getFields().size();
		int sizeField = this.field.getType().length();

		_result.append(this.record.getCode(_factory));
		_result.add(_factory.createPop(0,(size-pos)*sizeField));
		_result.add(_factory.createPop(sizeField,(pos-1)*sizeField));
		return _result; 
	}

}
