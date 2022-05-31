package fr.n7.stl.block.ast.classe;

import java.util.Iterator;
import java.util.List;

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

public class Signature extends InterfaceElement implements DeclarationWithParameters {
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

    public Signature (Type type, String name, List<ParameterDeclaration> parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    public Signature (Type type, String name) {
        this(type, name, null);
    }

    public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
        return _result + ")";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public List<ParameterDeclaration> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
            _scope.register(this);
            return true;
        } else {
            Logger.error("Multiple declarations of the same signature");
            return false;
        }
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
       return AtomicType.VoidType;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        return 0;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode");
        // return _factory.createFragment(); ?
    }
}
