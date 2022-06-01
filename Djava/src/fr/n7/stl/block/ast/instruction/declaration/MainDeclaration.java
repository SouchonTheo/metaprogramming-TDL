package fr.n7.stl.block.ast.instruction.declaration;

import java.util.Iterator;
import java.util.List;
import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
/**
 * Abstract Syntax Tree node for a main program declaration.
 * @author Basile Gros, Pablo Neyens, Diégo Rogard, Théo Souchon
 */
public class MainDeclaration implements Instruction, Declaration {	

    protected List<ParameterDeclaration> parameters;
    protected Block body;
    protected HierarchicalScope<Declaration> tds;
    private Register register;


    public MainDeclaration(List<ParameterDeclaration> _parameters, Block _block){
        this.parameters = _parameters;
        this.body = _block;
    }

    @Override
	public String toString() {
        return this.getName();
    }

    public String getName() {
        String _result = "void main( ";
        Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
        if (_iter.hasNext()) {
            _result += _iter.next();
            while (_iter.hasNext()) {
                _result += " ," + _iter.next();
            }
		}
		return _result + " )" + this.body.toString();
    }


    public Type getType() {
        return AtomicType.VoidType;
    }

    public Type returnsTo() {
        return AtomicType.VoidType;
    }


	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope){
        this.tds = new SymbolTable(_scope);
        for (ParameterDeclaration p : this.parameters){
            this.tds.register(p);
        }
        return this.body.collectAndBackwardResolve(this.tds);
    }
	

	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean b = true;
		for (ParameterDeclaration p : this.parameters) {
			b = b && p.getType().resolve(_scope);
		}
		return this.body.fullResolve(this.tds) && b;
	}

	@Override
	public boolean checkType() {
		return this.body.checkType();
	}

	// En fait c'est pareil qu'une fonction classique non ?
    @Override
    public int allocateMemory(Register _register, int _offset) {
        this.register = Register.LB;
		// On commence par calculer le déplacement total
		int depl = 0;
		for (ParameterDeclaration param : this.parameters) {
			depl -= param.type.length();
		}
		// Ensuite on remonte pour affecter petit a petit l'espace des paramètres
		for (ParameterDeclaration param : this.parameters) {
			param.offset = depl;
			depl += param.type.length();
		}
		this.body.allocateMemory(this.register, 0);
		return 0;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        String labelEnd = "main_end";
		Fragment _result = _factory.createFragment();
		Fragment funcBody = _factory.createFragment();
		// On prépare le corps de la fonction entouré de labels
		funcBody.addPrefix("main");
		funcBody.append(this.body.getCode(_factory));
		funcBody.addSuffix(labelEnd);
		// On y rajoute le jump
		_result.add(_factory.createJump(labelEnd));
		_result.append(funcBody);
		return _result;
    }

}