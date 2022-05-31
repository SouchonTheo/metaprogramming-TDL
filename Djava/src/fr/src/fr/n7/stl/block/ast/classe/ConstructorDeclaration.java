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

public class ConstructorDeclaration extends ClassElement implements DeclarationWithParameters{
    

	protected Block block;

    protected String name;

    /**
	 * Scope
	 */
	protected HierarchicalScope<Declaration> tds;

   /**
    * List of AST nodes for the formal parameters of the function
    */
   protected List<ParameterDeclaration> parameters;
    
    public ConstructorDeclaration(String name, Block block, List<ParameterDeclaration> parameters) {
        this.name = name;
        this.block = block;
        this.parameters = parameters;
    }

    public ConstructorDeclaration(String name, Block block) {
        this(name,block, new ArrayList<ParameterDeclaration>());
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Type getType() {
        return block.getType();
    }
    
    @Override
    public List<ParameterDeclaration> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
            _scope.register();
            return block.collectAndBackwardResolve(_scope);
        } else {
            Logger.error("Déjà existant");
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean b = true;
		for (ParameterDeclaration p : this.getParameters()) {
			b = b && p.getType().resolve(_scope);
		}
        return this.block.fullResolve(this.tds) && b;
    }

    @Override
    public boolean checkType() {
        return block.checkType();
    }

    @Override
    public Type returnsTo() {
        return AtomicType.VoidType;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        throw new SemanticsUndefinedException("allocateMemory");
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Fragment _result = _factory.createFragment();
        
        
        throw new SemanticsUndefinedException("Semantics getCode");
        return _result;
    }

}
