package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.FunctionType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
/**
 * Abstract Syntax Tree node for a class declaration.
 * @author Basile Gros, Pablo Neyens, Diégo Rogard, Théo Souchon
 */
public class ClassDeclaration implements Instruction, Declaration {	
    /**
    * Name of the class
    */
    protected String name;

    /**
    * Generic parameters of the class
    */
    protected List<TypeParameter> generiques;

    /**Interfaces
     * Class inherited from by the class.
     */
    Instance heritage;

    /**
     * Interfaces de la classe.
     */
    List<Instance> _interfaces;

    /**
    * Scope
    */
    protected HierarchicalScope<Declaration> tds;

    public ClassDeclaration(String _name, List<TypeParameter> _generiques, Instance _heritage, List<Instance> _interfaces, List<ClassElement> _classeElements) {
        this.name = _name;
        this.generiques = _generiques;
        this.heritage = _heritage;
        this.interfaces = _interfaces;
        this.classeElements = _classeElements;
    }

    public String toString() {
       return this.name;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return AtomicType.VoidType;
    }
    
    public Type returnsTo() {
        return AtomicType.VoidType;
    }


    /* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		if (_scope.accepts(this)) {
			_scope.register(this);
			boolean retour = true;
			for (TypeParameter g : this.generiques){
				retour = retour && g.collectAndBackwardResolve(tds);
			}
            for (Instance i : this.interfaces){
				retour = retour && g.collectAndBackwardResolve(tds);
			}
            retour = retour && this.heritage.collectAndBackwardResolve(tds);
            for(ClassElement c : this.classeElements){
                
                retour = retour && c.collectAndBackwardResolve(tds);
            }
			return retour;
		} else {
			Logger.error("Error : Multiple declarations of same interface.");
			return false;
		}
	}

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        Logger.error("fullResolve not implemented for ClassDeclaration");
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        Logger.error("allocateMemory not implemented for ClassDeclaration");
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Logger.error("getCode not implemented for ClassDeclaration");
        return _factory.createFragment();



}