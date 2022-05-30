package fr.n7.stl.block.ast.classe;

public abstract class ClassElement {

    /**
	 * AST node for the initial access right of the declared classAttribute.
	 */
	protected AccessRight access = AccessRight.PUBLIC;

    protected boolean isStatic = false;

    protected boolean isFinal = false;

    public void setStatic() {
        this.isStatic = true;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setFinal() {
        this.isFinal = true;
    }

    public boolean isFinal() {
        return isFinal;
    }

}
