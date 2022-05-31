package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    public Register getRegister() {
        return this.register;
    }

    public int getOffset() {
        return this.offset;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        throw new SemanticsUndefinedException("collectAndBackwardResolve");
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        throw new SemanticsUndefinedException("fullResolve");
    }

    @Override
    public boolean checkType() {
        throw new SemanticsUndefinedException("checkType");
    }

    @Override
    public Type returnsTo() {
        throw new SemanticsUndefinedException("returnsTo");
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
    throw new SemanticsUndefinedException("allocateMemory");
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode");
    }

    
}
