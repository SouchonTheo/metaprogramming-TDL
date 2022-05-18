/**
 * 
 */
package fr.n7.stl.block.ast.type;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;

/**
 * Implementation of the Abstract Syntax Tree node for a pointer type.
 * @author Marc Pantel
 *
 */
public class PointerType implements Type {

	protected Type element;

	public PointerType(Type _element) {
		this.element = _element;
	}
	
	public Type getPointedType() {
		return this.element;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		boolean result = false;
		if (_other instanceof PointerType) {
			PointerType other = (PointerType) _other;
			if (other != null) {
				result = this.element.equalsTo(other.getPointedType());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof NamedType) {
			_other = ((NamedType) _other).getType();
		}
		boolean result = false;
		if (_other instanceof PointerType) {
			PointerType other = (PointerType) _other;
			if (other != null) {
				result = this.element.compatibleWith(other.getPointedType());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		// A vérifier
		if (_other instanceof PointerType) {
			PointerType other = (PointerType) _other;
			return this.getPointedType().merge(other.getPointedType());
		} else {
			System.out.println("Types non égaux");
			return AtomicType.VoidType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		return 1; // ?
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.element + " *)";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.element.resolve(_scope);
	}

}
