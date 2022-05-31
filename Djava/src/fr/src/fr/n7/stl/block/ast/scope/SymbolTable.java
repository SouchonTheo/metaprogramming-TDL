/**
 * 
 */
package fr.n7.stl.block.ast.scope;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fr.n7.stl.block.ast.classe.DeclarationWithParameters;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;


/**
 * Implementation of a hierarchical scope using maps.
 * @author Marc Pantel
 *
 */
public class SymbolTable implements HierarchicalScope<Declaration> {
	
	private Map<String, Declaration> declarations;
	private Scope<Declaration> context;

	public SymbolTable() {
		this( null );
	}
	
	public SymbolTable(Scope<Declaration> _context) {
		this.declarations = new HashMap<String,Declaration>();
		this.context = _context;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#get(java.lang.String)
	 */
	@Override
	public Declaration get(String _name) {
		if (this.declarations.containsKey(_name)) {
			return this.declarations.get(_name);
		} else {
			if (this.context != null) {
				return this.context.get(_name);
			} else {
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String _name) {
		return (this.declarations.containsKey(_name));
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#accepts(fr.n7.stl.block.ast.scope.Declaration)
	 */
	@Override
	public boolean accepts(Declaration _declaration) {
		boolean result = true;
		if (this.contains(_declaration.getName()) && !(this.get(_declaration.getName()) instanceof DeclarationWithParameters)) {
			result = false;
		}
		return result;
	}

	public boolean accepts(DeclarationWithParameters _declaration) {
		boolean result = true;
		if (this.contains(_declaration.getName())) {
			if (this.get(_declaration.getName()) instanceof DeclarationWithParameters) {
				DeclarationWithParameters other = (DeclarationWithParameters) this.get(_declaration.getName());
				for (int i = 0 ; i < other.getParameters().size() ; i++) {
					ParameterDeclaration declaParam = _declaration.getParameters().get(i);
					ParameterDeclaration otherParam = other.getParameters().get(i); 
					result = result && !declaParam.getType().equals(otherParam.getType());
				}
			}
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#register(fr.n7.stl.block.ast.scope.Declaration)
	 */
	@Override
	public void register(Declaration _declaration) {
		if (this.accepts(_declaration)) {
			this.declarations.put(_declaration.getName(), _declaration);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.HierarchicalScope#knows(java.lang.String)
	 */
	@Override
	public boolean knows(String _name) {
		if (this.contains(_name)) {
			return true;
		} else {
			if (this.context != null) {
				if (this.context instanceof HierarchicalScope<?>) {
					return ((HierarchicalScope<?>)this.context).knows(_name);
				} else {
					return this.context.contains(_name);
				}
			} else {
				return false;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		if (this.context != null) {
			_local += "Hierarchical definitions :\n" + this.context.toString();
		}
		_local += "Local definitions : ";
		for (Entry<String,Declaration> _entry : this.declarations.entrySet()) {
			_local += _entry.getKey() + " -> " + _entry.getValue().toString() + "\n";
		}
		return _local;
	}

}
