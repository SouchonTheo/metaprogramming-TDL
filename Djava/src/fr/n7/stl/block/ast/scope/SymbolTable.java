/**
 * 
 */
package fr.n7.stl.block.ast.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.n7.stl.block.ast.classe.AttributeDeclaration;
import fr.n7.stl.block.ast.classe.DeclarationWithParameters;
import fr.n7.stl.block.ast.instruction.declaration.ConstantDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.util.Pair;


/**
 * Implementation of a hierarchical scope using maps.
 * @author Marc Pantel
 *
 */
public class SymbolTable implements HierarchicalScope<Declaration> {
	
	private Map<Pair<String, List<Type>>, Declaration> declarations;
	private Scope<Declaration> context;

	public SymbolTable() {
		this( null );
	}
	
	public SymbolTable(Scope<Declaration> _context) {
		this.declarations = new HashMap<Pair<String, List<Type>>, Declaration>();
		this.context = _context;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#get(java.lang.String)
	 */
	@Override
	public Declaration get(String _name) {
		return this.get(_name, null);
	}

	@Override
	public Declaration get(String name, List<Type> parameterTypes) {
		Pair<String,List<Type>> pair = new Pair<String,List<Type>>(name, parameterTypes);
		if (this.declarations.containsKey(pair)) {
			return this.declarations.get(pair);
		} else {
			if (this.context != null) {
				return this.context.get(name, parameterTypes);
			} else {
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String name) {
		return this.contains(name, null);
	}
	
	@Override
	public boolean contains(String name, List<Type> parameterTypes) {
		Pair<String,List<Type>> pair = new Pair<String,List<Type>>(name, parameterTypes);
		return (this.declarations.containsKey(pair));
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

	// Les variables peuvent avoir le même non qu'un attribut ou qu'une fonction
	public boolean accepts(VariableDeclaration declaration) {
		boolean result = true;
		if (this.contains(declaration.getName())) {
			Declaration other = this.get(declaration.getName());
			result = other instanceof AttributeDeclaration;
		}
		return result;
	}

	// Les variables peuvent avoir le même non qu'un attribut ou qu'une fonction
	public boolean accepts(ConstantDeclaration declaration) {
		boolean result = true;
		if (this.contains(declaration.getName())) {
			Declaration other = this.get(declaration.getName());
			result = other instanceof AttributeDeclaration;
		}
		return result;
	}

	// Les attributs peuvent avoir le même non qu'un attribut ou qu'une variable
	public boolean accepts(AttributeDeclaration declaration) {
		boolean result = true;
		if (this.contains(declaration.getName())) {
			Declaration other = this.get(declaration.getName());
			if (!(other instanceof VariableDeclaration || other instanceof ConstantDeclaration)) {
				result = false;
			}
		}
		return result;
	}

	// Les fonctions/méthodes/constructeurs peuvent avoir le même non qu'un attribut ou qu'une variable
	public boolean accepts(DeclarationWithParameters declaration) {
		List<Type> types = new ArrayList<Type>();
		for (ParameterDeclaration param : declaration.getParameters()) {
			types.add(param.getType());
		}
		return !this.contains(declaration.getName(), types);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Scope#register(fr.n7.stl.block.ast.scope.Declaration)
	 */
	@Override
	public void register(Declaration _declaration) {
		String name = _declaration.getName();
		List<Type> parameterTypes = null;
		if (_declaration instanceof DeclarationWithParameters) {
			DeclarationWithParameters decl = (DeclarationWithParameters) _declaration;
			parameterTypes = new ArrayList<Type>();
			for (ParameterDeclaration p : decl.getParameters()) {
				parameterTypes.add(p.getType());
			}
		}
		Pair<String,List<Type>> pair = new Pair<String,List<Type>>(name, parameterTypes);
		if (this.accepts(_declaration)) {
			this.declarations.put(pair, _declaration);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.HierarchicalScope#knows(java.lang.String)
	 */
	@Override
	public boolean knows(String _name) {
		return this.knows(_name, null);
	}

	@Override
	public boolean knows(String _name, List<Type> parameterTypes) {
		if (this.contains(_name, parameterTypes)) {
			return true;
		} else {
			if (this.context != null) {
				if (this.context instanceof HierarchicalScope<?>) {
					return ((HierarchicalScope<?>)this.context).knows(_name, parameterTypes);
				} else {
					return this.context.contains(_name, parameterTypes);
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
		Set<Entry<Pair<String, List<Type>>, Declaration>> entries = this.declarations.entrySet();
		

		for (Entry<Pair<String, List<Type>>, Declaration> entry : entries) {
			Pair<String,List<Type>> pair = entry.getKey();
			_local += pair.getLeft() + " : ";
			for (Type t : pair.getRight()) {
				_local += t.toString() + ", ";
			}
			_local += " -> " +  entry.getValue().toString() + "\n";
		}
		return _local;
	}



}
