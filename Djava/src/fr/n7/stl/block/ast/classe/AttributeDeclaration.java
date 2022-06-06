package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.block.ast.type.PartialType;
import fr.n7.stl.util.Logger;
import fr.n7.stl.util.Pair;

public class AttributeDeclaration extends ClassElement {

    /**
	 * Name of the declared attribute.
	 */
	protected Pair<String,PartialType> ident;

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

    public AttributeDeclaration(Type type, Pair<String,PartialType> ident, Expression value) {
        this.type = type;
        this.ident = ident;
        this.value = value;
    }

    public AttributeDeclaration(Type type, Pair<String,PartialType> ident) {
        this(type, ident, null);
    }

    @Override
    public String getName() {
        return this.ident.getLeft();
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
        if (_scope.accepts(this)) {
            if (this.isFinal) {
                _scope.register(new VariableDeclaration(this.ident.getLeft(), this.type, this.value));
            } else {
                _scope.register(this);
            }
            return this.value.collectAndBackwardResolve(_scope);
        } else {
            Logger.error("This attribute already exists");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		return this.value.fullResolve(_scope) && this.type.resolve(_scope);
    }

    @Override
    public boolean checkType() {
        Type realType = this.type;
		if (this.type instanceof NamedType) {
			realType = ((NamedType) this.type).getType();
		}
		boolean result = this.value.getType().compatibleWith(realType);
		if (! result) {
			Logger.error("Error : Incompatible type.");
		}
		return result;
    }

    @Override
    public Type returnsTo() {
        return AtomicType.VoidType;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        this.register = _register;
		this.offset = _offset;
		return this.type.length();
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment _result = _factory.createFragment();
		_result.add(_factory.createPush(this.type.length()));
		_result.append(this.value.getCode(_factory));
		_result.add(_factory.createStore(this.register, this.offset, this.type.length()));
		return _result;
    }

    
}
