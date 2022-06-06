package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;


public class ObjectDeclaration implements Declaration, Instruction {

    /**
	 * Name of the declared variable.
	 */
	protected String name;
	
	/**
	 * AST node for the type of the declared variable.
	 */
	protected Type type;
	
	/**
	 * AST node for the initial value of the declared variable.
	 */
	protected Expression value;


    public ObjectDeclaration(String _name, Type _type, Expression _value){
        this.name = _name;
        this.type = _type;
        this.value = _value;
    }
    
    public ObjectDeclaration(String _name, Type _type){
        this(_name,_type,null);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return this.type;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean checkType() {
        if (this.value == null) {
            return true;
        } else {
            return this.value.getType().compatibleWith(this.type);
        }
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
			_scope.register(this);
            if(this.value != null) {
			    return this.value.collectAndBackwardResolve(_scope);
            }
		} else {
			Logger.error("Error : Multiple declarations.");
			return false;
		}    
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        if(this.value != null) {
            return this.value.fullResolve(_scope) && this.type.resolve(_scope);
        } else {
            return this.type.resolve(_scope);
        }
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode");
    }

    @Override
    public Type returnsTo() {
        return AtomicType.VoidType;
    }
    
}
