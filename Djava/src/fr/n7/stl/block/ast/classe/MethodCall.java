/**
 * 
 */
package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function call expression.
 * @author Marc Pantel
 *
 */
public class MethodCall implements Expression {

	/**
	 * Instance of the called method.
	 */
	protected Instance instance;
	
	/**
	 * Object or class on which it is called
	 */
	protected Expression objectOrClass;
	
	/**
	 * Declaration of the called function after name resolution.
	 * TODO : Should rely on the VariableUse class.
	 */
	protected MethodDeclaration method;
	
	/**
	 * List of AST nodes that computes the values of the parameters for the function call.
	 */
	protected List<Expression> arguments;
	
	/**
	 * @param _name : Name of the called function.
	 * @param _arguments : List of AST nodes that computes the values of the parameters for the function call.
	 */
	public MethodCall(Expression _objectOrClass, Instance _instance, List<Expression> _arguments) {
		this.instance = _instance;
		this.method = null;
		this.arguments = _arguments;
		this.objectOrClass = _objectOrClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = ((this.method == null)?this.instance.getName():this.method) + "( ";
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
		if (_scope.knows(this.instance.getName(), this.arguments)) {
			boolean result = true;
			for (Expression arg : this.arguments) {
				result = result && arg.collectAndBackwardResolve(_scope);
			}
			return result;
		}
		Logger.error("Method not known");
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		// On appelle d'abord sur les arguments
		for (Expression arg : this.arguments) {
			result = result && arg.fullResolve(_scope);
		}
		// On vérifie la MethodDeclaration
		Declaration decl = _scope.get(name, this.arguments);
		if ( decl instanceof MethodDeclaration) {
			this.method = (MethodDeclaration) decl;
			// On vérifie qu'on a le droit d'appeler cette méthode sur cet objet
			if (expr instanceof IdentifierAccess) {
				IdentifierAccess id = (IdentifierAccess) expr;
				Type idType = id.getType();
				if (idType instanceof Instance) {
					Instance inst = (Instance) idType;
					ClassDeclaration classDecl = inst.getDeclaration();
					if (classDecl.getMethods().contains(this.method)) {
						// On est ok
					} else {
						Logger.error("Method not known for this object");
						return false;
					}
				} else {
					Logger.error("Object is an AtomicType");
					return false;
				}
			// Ou alors c'est une méthode statique de classe
			} else if (expr instanceof Instance) {
				Instance inst = (Instance) idType;
				ClassDeclaration classDecl = inst.getDeclaration();
				if (classDecl.getMethods().contains(this.method)) {
					if (this.method.isStatic()) {
						// On est ok
					} else {
						Logger.error("Method is not static. You can't call it like this.);
						return false;
					}
				} else {
					Logger.error("Method not known for this class");
					return false;
				}
			} else {
				Logger.error("Expression isn't a class or an object");
				return false;
			}
			// Maintenant on peut vérifier le nombre de paramètres
			if (this.method.getParameters().size() == this.arguments.size()) {
				return result && (this.method != null);
			} else {
				Logger.error("Incorrect number of parameters");
				return false;
			}
		} else {
			Logger.error("Object not a method");
			return false;
		}
	}

	@Override
	public Type getType() {
		if (this.method.getType() instanceof MethodType) {
			ArrayList<Type> typeList = new ArrayList<Type>();
			for (int i = 0; i < arguments.size(); i++) {
				Type argType = this.arguments.get(i).getType();
				Type paramType = this.method.getParameters().get(i).getType();
				if (argType.compatibleWith(paramType)) {
					typeList.add(argType);
				} else {
					Logger.error("Type des arguments différents des paramètres");
					return AtomicType.ErrorType;
				}
			}
			MethodType mType = (MethodType) this.method.getType();
			MethodType callType = new MethodType(this.method.getBody().returnsTo(), typeList);
			if (callType.compatibleWith(mType)) {
				return mType.getResultType();
			} else {
				Logger.error("Types incompatibles");
				return AtomicType.ErrorType;
			}
		}
		Logger.error(this.name + " n'est pas une fonction");
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
