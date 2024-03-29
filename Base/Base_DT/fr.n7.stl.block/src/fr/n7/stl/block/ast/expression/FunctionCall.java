/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.FunctionType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function call expression.
 * @author Marc Pantel
 *
 */
public class FunctionCall implements Expression {

	/**
	 * Name of the called function.
	 * TODO : Should be an expression.
	 */
	protected String name;
	
	/**
	 * Declaration of the called function after name resolution.
	 * TODO : Should rely on the VariableUse class.
	 */
	protected FunctionDeclaration function;
	
	/**
	 * List of AST nodes that computes the values of the parameters for the function call.
	 */
	protected List<Expression> arguments;
	
	/**
	 * @param _name : Name of the called function.
	 * @param _arguments : List of AST nodes that computes the values of the parameters for the function call.
	 */
	public FunctionCall(String _name, List<Expression> _arguments) {
		this.name = _name;
		this.function = null;
		this.arguments = _arguments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = ((this.function == null)?this.name:this.function) + "( ";
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
		if (_scope.knows(name)) {
			boolean result = true;
			for (Expression e : arguments) {
				result = e.collectAndBackwardResolve(_scope);
			}
			return result;
		} else {
			Logger.error("Fonction inconnue");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		for (Expression e : arguments) {
			result = e.fullResolve(_scope);
		}
		this.function = (FunctionDeclaration) _scope.get(name);
		if (this.arguments.size() != this.function.getParameters().size()) {
			Logger.error("Nombre de paramètres incorrects par rapport à la définition");
			return false;
		}
		return result && (this.function != null);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if (this.function.getType() instanceof FunctionType) {
			ArrayList<Type> typeList = new ArrayList<Type>();
			for (int i = 0; i < arguments.size(); i++) {
				Type argType = this.arguments.get(i).getType();
				Type paramType = this.function.getParameters().get(i).getType();
				if (argType.compatibleWith(paramType)) {
					typeList.add(argType);					
				} else {
					Logger.error("Type des arguments différents des paramètres");
					return AtomicType.ErrorType;
				}
			}
			FunctionType fType = (FunctionType) this.function.getType();
			FunctionType callType = new FunctionType(this.function.getBody().returnsTo(), typeList);
			if (callType.compatibleWith(fType)) {
				return fType.getResultType();
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
		frag.add(_factory.createCall(this.name, this.function.getRegister()));
		
		frag.addSuffix(this.name);
		// On écrit le code du bloc
		frag.append(this.function.getBody().getCode(_factory));
		return frag;
	}
}
