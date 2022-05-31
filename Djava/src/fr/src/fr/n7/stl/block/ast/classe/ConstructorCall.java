/**
 * 
 */
package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.FunctionType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function call expression.
 * @author Marc Pantel
 *
 */
public class ConstructorCall implements Expression {

	/**
	 * Name of the called function.
	 */
	protected String name;
	
	/**
	 * Declaration of the called function after name resolution.
	 * TODO : Should rely on the VariableUse class.
	 */
	protected ConstructorDeclaration constructor;
	
	/**
	 * List of AST nodes that computes the values of the parameters for the function call.
	 */
	protected List<Expression> arguments;
	
	/**
	 * @param _name : Name of the called function.
	 * @param _arguments : List of AST nodes that computes the values of the parameters for the function call.
	 */
	public ConstructorCall(String _name, List<Expression> _arguments) {
		this.name = _name;
		this.method = null;
		this.arguments = _arguments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = ((this.method == null)?this.name:this.method) + "( ";
		Iterator<Expression> _iter = this.arguments.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
		}
		while (_iter.hasNext()) {
			_result += " ," + _iter.next();
		}
		return  _result + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
			Declaration _declaration = _scope.get(this.name);
			if (_declaration instanceof ConstructorDeclaration) {
				this.constructor = ((ConstructorDeclaration) _declaration);
				return true;
			} else {
				Logger.error("The call for " + this.name + " is of the wrong kind.");
				return false;
			}
		} else {
			Logger.error("The identifier " + this.name + " has not been found.");
			return false;	
		}
	}
	/*
	@Override
	public Type getType() {
		Type type = this.function.getType();
		if (type instanceof FunctionType) {
			FunctionType functype = (FunctionType) type;
			return functype.getResultType();
		} else  {
			//Branche morte, car erreur attrapée avant au resolve. Laissé par cohérence au niveau des instanceof.
			Logger.error("Error : not a function type");
			return AtomicType.ErrorType;
		}
	}
*/
	@Override
	public Type getType() {
		if (this.constructor.getType() instanceof ConstructorType) {
			ArrayList<Type> typeList = new ArrayList<Type>();
			for (int i = 0; i < arguments.size(); i++) {
				Type argType = this.arguments.get(i).getType();
				Type paramType = this.constructor.getParameters().get(i).getType();
				if (argType.compatibleWith(paramType)) {
					typeList.add(argType);					
				} else {
					Logger.error("Type des arguments différents des paramètres");
					return AtomicType.ErrorType;
				}
			}
			FunctionType fType = (FunctionType) this.constructor.getType();
			FunctionType callType = new FunctionType(this.constructor.getBody().returnsTo(), typeList);
			if (callType.compatibleWith(fType)) {
				return fType.getResultType();
			} else {
				Logger.error("Types incompatibles");
				return AtomicType.ErrorType;
			}
		}
		Logger.error(this.name + " n'est pas un constructeur");
		return AtomicType.ErrorType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		// charger les paramètres
		for(Expression e : this.arguments) {
			frag.append(e.getCode(_factory));
		}
		// On rajoute un call
		frag.add(_factory.createCall(this.name, this.method.getRegister()));
		return frag;
	}

}
