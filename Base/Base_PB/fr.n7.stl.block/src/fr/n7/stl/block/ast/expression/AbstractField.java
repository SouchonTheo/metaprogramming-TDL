package fr.n7.stl.block.ast.expression;

import javax.naming.directory.AttributeModificationException;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.EnumerationType;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.util.Logger;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of assignments. These elements
 * share attributes, toString and getType methods.
 * @author Marc Pantel
 *
 */
public abstract class AbstractField implements Expression {

	protected Expression record;
	protected String name;
	protected FieldDeclaration field;
	
	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public AbstractField(Expression _record, String _name) {
		this.record = _record;
		this.name = _name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.record + "." + this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		return this.record.collectAndBackwardResolve(_scope);
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		return this.record.fullResolve(_scope);
	}

	/**
	 * Synthesized Semantics attribute to compute the type of an expression.
	 * @return Synthesized Type of the expression.
	 */
	public Type getType() {
		Type type =  this.record.getType();
		RecordType recordType;
		EnumerationType enumType;
		if (type instanceof NamedType) {
			type = ((NamedType) type).getType();
		}
		if (type instanceof RecordType) {
			recordType = (RecordType) type;
			if (recordType.contains(this.name)) {
				this.field = recordType.get(this.name);
				return this.field.getType();
			}	else {
				Logger.error("Error : Record field not known");
				return AtomicType.ErrorType;
			}
		} else if (type instanceof EnumerationType) {
			enumType = (EnumerationType) type;
			if (enumType.contains(this.name)) {
				return enumType;
			}	else {
				Logger.error("Error : Enum field not known");
				return AtomicType.ErrorType;
			}
		} {
			Logger.error("Error : Type not a record or enum type");
			return AtomicType.ErrorType;
		}
	}

}