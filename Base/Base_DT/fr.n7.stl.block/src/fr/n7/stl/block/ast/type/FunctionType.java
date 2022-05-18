/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;

/**
 * Implementation of the Abstract Syntax Tree node for a function type.
 * @author Marc Pantel
 *
 */
public class FunctionType implements Type {

	private Type result;
	private List<Type> parameters;

	public FunctionType(Type _result, Iterable<Type> _parameters) {
		this.result = _result;
		this.parameters = new LinkedList<Type>();
		for (Type _type : _parameters) {
			this.parameters.add(_type);
		}
	}

	
	public Type getResultType() {
		return this.result;
	}
	
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		Type otherType = _other;
		if (otherType instanceof NamedType) {
			otherType = ((NamedType) otherType).getType();
		}
		if (otherType instanceof FunctionType) {
			FunctionType other = (FunctionType) otherType;
			if (other != null && other.result.equals(this.result) && other.parameters.size() == this.parameters.size()) {
				for (int i = 0 ; i < this.parameters.size(); i++) {
					if (!this.parameters.get(i).equals(other.parameters.get(i))) {
						return false;
					}
				}
				return true;
			}
		} else {
			System.out.println("Type invalide.");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		Type otherType = _other;
		if (otherType instanceof NamedType) {
			otherType = ((NamedType) otherType).getType();
		}
		if (otherType instanceof FunctionType) {
			FunctionType other = (FunctionType) otherType;
			if (other != null && other.result.compatibleWith(this.result) && other.parameters.size() == this.parameters.size()) {
				for (int i = 0 ; i < this.parameters.size(); i++) {
					if (!this.parameters.get(i).compatibleWith(other.parameters.get(i))) {
						return false;
					}
				}
				return true;
			}
		} else {
			System.out.println("Type invalide.");
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		throw new SemanticsUndefinedException( "merge is undefined in FunctionType.");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		throw new SemanticsUndefinedException("Semantics length is undefined in FunctionType.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "(";
		Iterator<Type> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + ") -> " + this.result;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		throw new SemanticsUndefinedException("Semantics resolve is undefined in FunctionType.");
	}

}
