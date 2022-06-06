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
	 * Name of the called function.
	 */
	protected String name;
	
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
	public MethodCall(String _name, List<Expression> _arguments) {
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
		if (_scope.knows(this.name)) {
			boolean result = true;
			if (this.arguments != null){
				
				for (Expression arg : this.arguments) {
					result = result && arg.collectAndBackwardResolve(_scope);
				}
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
		for (Expression arg : this.arguments) {
			result = result && arg.fullResolve(_scope);
		}
		Declaration decl = _scope.get(name);
		if ( decl instanceof MethodDeclaration) {
			this.method = (MethodDeclaration) decl;
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
