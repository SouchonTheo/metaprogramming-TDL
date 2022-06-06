/**
 * 
 */
package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
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

	protected ClassDeclaration classe;

	Type typeToInstance;
	/**
	 * Declaration of the called function after name resolution.
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
	public ConstructorCall(Type typeToInstance, List<Expression> _arguments) {
		this.typeToInstance = typeToInstance;
		this.constructor = null;
		this.arguments = _arguments;
	}
	public ConstructorCall(Type typeToInstance) {
		this.typeToInstance = typeToInstance;
		this.constructor = null;
		this.arguments = new ArrayList<Expression>();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = ((this.constructor == null)?this.name:this.constructor) + "( ";
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
		if(typeToInstance instanceof Instance){
			Declaration declaration = ((Instance)typeToInstance).instanciate(_scope);
			if(declaration instanceof ClassDeclaration) {
				this.classe = (ClassDeclaration)declaration;
				if(!this.classe.isAbstract()) {
					List<ConstructorDeclaration> classConstructors = this.classe.getConstructors();
					for (ConstructorDeclaration c : classConstructors){
						boolean temp = true;
						List<ParameterDeclaration> check = c.getParameters();
						if (check.size() == this.arguments.size()) {
							for(int i = 0; i< check.size(); i++) {
								temp = temp && check.get(i).getType().compatibleWith(this.arguments.get(i).getType());
							}
						} else {
							Logger.error("size not matching");
							temp = false;
						}
						if(temp){
							this.constructor = c;
							return true;
						} else {
							Logger.error("temp = false");
						}
					}
					Logger.error("No constructor corresponds to the one called");
					
				} else {
					Logger.error("Can't call a constructor on an abstract class");
				}
			} else {
				Logger.error("Constructor called with not the name of a class");
			}
		} else {
			Logger.error("Atomic types don't have constructors");
		}
		return false;
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
		frag.add(_factory.createCall(this.name, this.constructor.getRegister()));
		return frag;
	}

}
