package fr.n7.stl.block.ast.classe;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.Type;

public class MethodType implements Type {
    
    private Type result;
	private List<Type> parameters;

	public MethodType(Type _result, Iterable<Type> _parameters) {
		this.result = _result;
		this.parameters = new LinkedList<Type>();
		for (Type _type : _parameters) {
			this.parameters.add(_type);
		}
	}
	/*Ajout des getters pour le function type */
	public Type getResultType(){
		return this.result;
	}

	public List<Type> getArgsType(){
		return this.parameters;
	}
	

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if (_other instanceof NamedType) {
			_other = ((NamedType) _other).getType();
		}
		boolean res = false;
		if (_other instanceof MethodType){
			MethodType other = (MethodType) _other;
			if (other != null && other.getArgsType().size() == this.parameters.size()) {
				res = this.result.equalsTo(other.getResultType());
				for (int i =0; i < this.parameters.size(); i++){
					res = res && this.parameters.get(i).equalsTo(other.getArgsType().get(i));
				}
			}
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof NamedType) {
			_other = ((NamedType) _other).getType();
		}
		boolean res = false;
		if (_other instanceof MethodType){
			MethodType other = (MethodType) _other;
			if (other != null && other.getArgsType().size() == this.parameters.size()) {
				res = other.getResultType().compatibleWith(this.result);
				for (int i =0; i < this.parameters.size(); i++){
					res = res && this.parameters.get(i).compatibleWith(other.getArgsType().get(i));
				}
			}
		}
		return res;
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
		return this.result.resolve(_scope);
	}
}
