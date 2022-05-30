package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Register;

public class AttributeDeclaration extends ClassElement {

    /**
	 * Name of the declared attribute.
	 */
	protected String name;

	/**
	 * AST node for the type of the declared attribute.
	 */
	protected Type type;

    /**
	 * AST node for the initial value of the declared attribute.
	 */
	protected Expression value;

    
    /**
	 * Address register that contains the base address used to store the declared attribute.
	 */
	protected Register register;
	
	/**
	 * Offset from the base address used to store the declared attribute
	 * i.e. the size of the memory allocated to the previous declared attribute
	 */
	protected int offset;

    public AttributeDeclaration(Type type, String name, Expression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public AttributeDeclaration(Type type, String name) {
        this(type, name, null);
    }

    
}
