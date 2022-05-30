package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.Block;

public class MethodDeclaration extends ClassElement {

    protected Signature entete;

	protected Block block;

	private boolean isAbstract = false;

	public MethodDeclaration(Signature entete, Block block) {
		this.entete = entete;
		this.block = block;
	}

	public void setAbstract() {
		this.isAbstract = true;
	}

	public boolean isAbstract() {
		return isAbstract;
	}


}
