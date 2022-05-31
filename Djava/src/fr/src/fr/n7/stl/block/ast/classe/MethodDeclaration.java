package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class MethodDeclaration extends ClassElement implements DeclarationWithParameters {

    protected Signature entete;

	protected Block body;

	private boolean isAbstract = false;

	private Register register;

	/**
	 * Scope
	 */
	protected HierarchicalScope<Declaration> tds;
	


	public MethodDeclaration(Signature entete, Block body) {
		this.entete = entete;
		this.body = body;
	}

	public void setAbstract() {
		this.isAbstract = true;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public Signature getEntete() {
		return this.entete;
	}

	public List<ParameterDeclaration> getParameters() {
		return this.entete.parameters;
	}

/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.entete.toString() + this.body.toString();
	}

	/* (non-Javadoc)
	* @see fr.n7.stl.block.ast.Declaration#getName()
	*/
	public String getName() {
		return this.entete.name;
	}

	
	@Override
	public Type returnsTo(){
		return AtomicType.VoidType;
	}
	
	/* (non-Javadoc)
	* @see fr.n7.stl.block.ast.Declaration#getType()
	*/
	@Override
	public Type getType() {
		if (!this.body.returnsTo().compatibleWith(this.entete.type)) {
			Logger.error("Return type incorrect");
			return AtomicType.ErrorType;
		}

		List<Type> parametersType = new ArrayList<Type>();
		for (ParameterDeclaration p : this.entete.parameters){
			parametersType.add(p.getType());
		}
		return new MethodType(this.entete.type, parametersType);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return this.body.checkType();
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		if (_scope.accepts(this)) {
			_scope.register(this);
			this.tds = new SymbolTable(_scope);
			for (ParameterDeclaration p : this.getParameters()) {
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
		for (ParameterDeclaration p : this.getParameters()) {
			b = b && p.getType().resolve(_scope);
		}
		return this.body.fullResolve(this.tds) && b;
	}

	
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.register = Register.LB;
		// On commence par calculer le déplacement total
		int depl = 0;
		for (ParameterDeclaration param : this.getParameters()) {
			depl -= param.getType().length();
		}
		// Ensuite on remonte pour affecter petit a petit l'espace des paramètres
		for (ParameterDeclaration param : this.getParameters()) {
			param.setOffset(depl);
			depl += param.getType().length();
		}
		this.body.allocateMemory(this.register, 0);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		String labelEnd;
		Fragment _result = _factory.createFragment();
		Fragment funcBody = _factory.createFragment();
		// On prépare le corps de la fonction entouré de labels
		String labelBegin = this.getName();
		for (ParameterDeclaration param : this.getParameters()) {
			labelBegin = labelBegin + param.getType().toString();
		}
		labelEnd = labelBegin + "_end";
		funcBody.addPrefix(labelBegin);
		funcBody.append(this.body.getCode(_factory));
		funcBody.addSuffix(labelEnd);
		// On y rajoute le jump
		_result.add(_factory.createJump(labelEnd));
		_result.append(funcBody);
		return _result;
	}

	public Block getBody() {
		return this.body;
	}

	public Register getRegister() {
		return this.register;
	}



}
