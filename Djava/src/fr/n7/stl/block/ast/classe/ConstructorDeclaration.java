package fr.n7.stl.block.ast.classe;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
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

public class ConstructorDeclaration extends ClassElement implements DeclarationWithParameters{
    

	protected Block body;

    protected String name;

    private Register register;

    private Type type;

    /**
	 * Scope
	 */
	protected HierarchicalScope<Declaration> tds;

   /**
    * List of AST nodes for the formal parameters of the function
    */
   protected List<ParameterDeclaration> parameters;
    
    public ConstructorDeclaration(String name, Block body, List<ParameterDeclaration> parameters) {
        this.name = name;
        this.body = body;
        this.type = new Instance(this.name);
        this.parameters = parameters;
    }

    public ConstructorDeclaration(String name, Block body) {
        this(name,body, new ArrayList<ParameterDeclaration>());
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    public Block getBody() {
        return this.body;
    }
    
    public Register getRegister() {
        return this.register;
    }

    @Override
    public Type getType() {
        if (!this.body.returnsTo().compatibleWith(this.type)) {
			Logger.error("Return type incorrect");
			return AtomicType.ErrorType;
		}

		List<Type> parametersType = new ArrayList<Type>();
		for (ParameterDeclaration p : this.parameters){
			parametersType.add(p.getType());
		}
        return new ConstructorType(this.type , parametersType);
    }
    
    @Override
    public List<ParameterDeclaration> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (_scope.accepts(this)) {
            _scope.register(this);
            this.tds = new SymbolTable(_scope);
            for (ParameterDeclaration p : this.getParameters()) {
                this.tds.register(p);
            }
            return body.collectAndBackwardResolve(this.tds);
        } else {
            Logger.error("Déjà existant");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean b = true;
		for (ParameterDeclaration p : this.getParameters()) {
			b = b && p.getType().resolve(_scope);
		}
        return this.body.fullResolve(this.tds) && b;
    }

    @Override
    public boolean checkType() {
        return body.checkType();
    }

    @Override
    public Type returnsTo() {
        return AtomicType.VoidType;
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

    @Override
    public Fragment getCode(TAMFactory _factory) {
        String labelEnd;
		Fragment _result = _factory.createFragment();
		Fragment funcBody = _factory.createFragment();
		String labelBegin = this.getName();
		for (ParameterDeclaration param : this.getParameters()) {
			labelBegin = labelBegin + param.getType().toString();
		}
		labelEnd = labelBegin + "_end";
        funcBody.addPrefix(labelBegin);
		funcBody.append(this.body.getCode(_factory));
		funcBody.addSuffix(labelEnd);
        _result.add(_factory.createJump(labelEnd));
		_result.append(funcBody);
        return _result;
    }

}
