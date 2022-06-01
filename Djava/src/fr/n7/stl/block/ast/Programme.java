package fr.n7.stl.block.ast;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.InterfaceDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.MainDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
public class Programme {
    protected List<InterfaceDeclaration> interfaces = new ArrayList<InterfaceDeclaration>();
    protected List<ClassDeclaration> classes = new ArrayList<ClassDeclaration>();
    protected MainDeclaration principal;
	protected HierarchicalScope<Declaration> tds;

	public void addInterface(InterfaceDeclaration i) {
		interfaces.add(i);
	}

	public void addClass(ClassDeclaration c) {
		classes.add(c);
	}

	public void addPrincipalClass(MainDeclaration principal) {
		this.principal = principal;
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "Interfaces : ";
		for (InterfaceDeclaration _interface : this.interfaces) {
			_local += _interface;
		}
        _local += "\n" + "Classes : ";
        for (ClassDeclaration _classe : this.classes) {
			_local += _classe;
		}
        _local += "\nMain : " + principal;
		return "{\n" + _local + "}\n" ;
	}

    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		tds = new SymbolTable(_scope);
		for (InterfaceDeclaration _interface : this.interfaces) {
			result = result && _interface.collectAndBackwardResolve(tds);
		}
        for (ClassDeclaration _classe : this.classes) {
			result = result && _classe.collectAndBackwardResolve(tds);
		}
		return result && principal.collectAndBackwardResolve(tds);
	}

    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		boolean result = true;
		for (InterfaceDeclaration _interface : this.interfaces) {
			result = result && _interface.fullResolve(tds);
		}
        for (ClassDeclaration _classe : this.classes) {
			result = result && _classe.fullResolve(tds);
		}
		return result && principal.fullResolve(tds);
	}

    public boolean checkType() {
		boolean result = true;
		for (InterfaceDeclaration _interface : this.interfaces) {
			result = result && _interface.checkType();
		}
        for (ClassDeclaration _classe : this.classes) {
			result = result && _classe.checkType();
		}
		return result && principal.checkType();
	}
	
    public void allocateMemory(Register _register, int _offset) {
        Logger.error("allocateMemory not implemented for Programme");
    }

    public Fragment getCode(TAMFactory _factory) {
        Logger.error("getCode not implemented for Programme");
        return _factory.createFragment();
    }

    
}
