/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.declaration.LabelDeclaration;
import fr.n7.stl.util.Logger;

/**
 * @author Marc Pantel
 *
 */
public class EnumerationType implements Type, Declaration{
	
	private String name;
	
	private List<LabelDeclaration> labels;

	/**
	 * 
	 */
	public EnumerationType(String _name, List<LabelDeclaration> _labels) {
		this.name = _name;
		this.labels = _labels;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "enum" + this.name + " { ";
		Iterator<LabelDeclaration> _iter = this.labels.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " }";
	}


	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#equalsTo(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		boolean result = false;
		Type otherType = _other;
		if (otherType instanceof NamedType) {
			otherType = ((NamedType) otherType).getType();
		}
		if(otherType instanceof EnumerationType){
			EnumerationType other =  (EnumerationType) otherType;
			if (other != null && this.labels.size() == other.labels.size()){
				result = true;
				for (int i = 0; i < this.labels.size(); i++){
					result = result & (this.labels.get(i).getType().equalsTo(other.labels.get(i).getType()));
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#compatibleWith(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		boolean result = false;
		Type otherType = _other;
		if (otherType instanceof NamedType) {
			otherType = ((NamedType) otherType).getType();
		}
		if(otherType instanceof EnumerationType){
			EnumerationType other =  (EnumerationType) otherType;
			if (other != null && this.labels.size() == other.labels.size()){
				result = true;
				for (int i = 0; i < this.labels.size(); i++){
					result = result & (this.labels.get(i).getType().compatibleWith(other.labels.get(i).getType()));
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#merge(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if ((_other instanceof EnumerationType) || (_other instanceof NamedType && _other.compatibleWith(this))) {
			return this;
		} else {
			Logger.error("Error : Type not EnumerationType or NamedType type");
			return AtomicType.ErrorType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#length()
	 */
	@Override
	public int length() {
		return this.labels.size(); 
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this;
	}

	public List<LabelDeclaration> getLabels() {
		return this.labels;
	}

	public boolean contains(String _name) {
		boolean _result = false;
		Iterator<LabelDeclaration> _iter = this.labels.iterator();
		while (_iter.hasNext() && (! _result)) {
			_result = _result || _iter.next().getName().contentEquals(_name);
		}
		return _result;
	}

}
