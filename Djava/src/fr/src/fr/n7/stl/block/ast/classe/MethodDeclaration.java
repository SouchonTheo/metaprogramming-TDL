package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
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
		throw new SemanticsUndefinedException( "collectAndBackwardResolve");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		throw new SemanticsUndefinedException( "fullResolve");
	}

	
	@Override
	public int allocateMemory(Register _register, int _offset) {
		throw new SemanticsUndefinedException("allocate Memory");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		throw new SemanticsUndefinedException("Semantics getCode");
	}

	public Block getBody() {
		return this.body;
	}

	public Register getRegister() {
		return this.register;
	}



}
