/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.FunctionType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;

	/**
	 * Scope
	 */
	protected HierarchicalScope<Declaration> tds;
	
	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}

	private Register register;
	private int paramOffset;
	/**
	 * AST node for the body of the function
	 */
	protected Block body;

	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	//N'est jamais appelée, car les fonctions
	public Type getType() {
		if (!this.body.returnsTo().compatibleWith(this.type)) {
			Logger.error("Return type incorrect");
			return AtomicType.ErrorType;
		}

		List<Type> parametersType = new ArrayList<Type>();
		for (ParameterDeclaration p : this.parameters){
			parametersType.add(p.getType());
		}
		return new FunctionType(this.type, parametersType);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		if (_scope.accepts(this)) {
			_scope.register(this);
			this.tds = new SymbolTable(_scope);
			for (ParameterDeclaration p : this.parameters){
				this.tds.register(p);
			}
			return this.body.collectAndBackwardResolve(this.tds);
		} else {
			Logger.error("Error : Multiple declarations.");
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean b = true;
		for (ParameterDeclaration p : this.parameters) {
			b = b && p.getType().resolve(_scope);
		}
		return this.body.fullResolve(this.tds) && b;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return this.body.checkType();
	}

	@Override
	public Type returnsTo(){
		return AtomicType.VoidType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.register = _register;
		for (ParameterDeclaration param : this.parameters) {
			param.offset = this.paramOffset;
			this.paramOffset += param.type.length();
		}
		this.body.allocateMemory(Register.LB, this.paramOffset);
		return _offset;

		//this.body.allocateMemory(_register, _offset);
		//return _offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = _factory.createFragment();
		_result.addPrefix(this.name);
		_result.append(this.body.getCode(_factory));
		return _result;

		//return _factory.createFragment();
		
	}

	public Block getBody() {
		return this.body;
	}

	public Register getRegister() {
		return this.register;
	}

}
