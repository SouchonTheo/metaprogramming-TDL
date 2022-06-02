package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.List;
import fr.n7.stl.block.ast.classe.Instance;
import fr.n7.stl.block.ast.classe.Signature;
import fr.n7.stl.block.ast.classe.TypeParameter;
import fr.n7.stl.block.ast.classe.InterfaceElement;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;
/**
 * Abstract Syntax Tree node for an interface declaration.
 * @author Basile Gros, Pablo Neyens, Diégo Rogard, Théo Souchon
 */
public class InterfaceDeclaration implements Instruction, Declaration {	
    /**
    * Name of the interface
    */
    protected String name;

    /**
    * Generic parameters of the interface
    */
    protected List<TypeParameter> generiques;

    /**
     * Interfaces inherited from by the interface.
     */
    List<Instance> heritages;

    /**
     * Elements de l'interface.
     */
    List<InterfaceElement> interfaceElements;

    /**
    * Scope
    */
    protected HierarchicalScope<Declaration> tds;

    public InterfaceDeclaration(String _name, List<TypeParameter> _generiques, List<Instance> _heritages, List<InterfaceElement> _interfaceElements) {
        this.name = _name;
        this.generiques = _generiques;
        this.heritages = _heritages;
        this.interfaceElements = _interfaceElements;
    }

    public List<Signature> getMethods(HierarchicalScope<Declaration> _scope) {
        List<Signature> methods = new ArrayList<Signature>();
        for(Instance i : this.heritages) {
            Declaration tempDeclaration = i.instanciate(_scope);
            //Vérifier qu'il s'agit bien d'une interface
            if (tempDeclaration instanceof InterfaceDeclaration) {
                InterfaceDeclaration inter = (InterfaceDeclaration) tempDeclaration;
                //Récupérer les méthodes de l'interface.
                methods.addAll(inter.getMethods(_scope));
            } else {
                Logger.error("Class "+ this.name + " implements "+ tempDeclaration.getName() + ", something else than an interface");
                return new ArrayList<Signature>();
            }
        }
        for(InterfaceElement i: this.interfaceElements) {
            methods.add(i.getSignature());
        }
        return methods;
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
			this.tds = new SymbolTable(_scope);

            boolean retour = true;
			for (TypeParameter g : this.generiques){
				retour = retour && g.collectAndBackwardResolve(tds);
			}
            for(InterfaceElement i : this.interfaceElements){
                
                retour = retour && i.collectAndBackwardResolve(tds);
            }
			return retour;
		} else {
			Logger.error("Error : Multiple declarations of same interface.");
			return false;
		}
	}

    /* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean retour = true;
        for (TypeParameter g : this.generiques){
            retour = retour && g.fullResolve(tds);
        }
        for(InterfaceElement i : this.interfaceElements){
            
            retour = retour && i.fullResolve(tds);
        }
        return retour;
	}

    @Override
	public boolean checkType() {
        boolean retour = true;
        for (TypeParameter g : this.generiques){
            retour = retour && g.checkType();
        }
        for(InterfaceElement i : this.interfaceElements){
            
            retour = retour && i.checkType();
        }
        return retour;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        Logger.error("allocateMemory not implemented for InterfaceDeclaration");
        return 0;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Logger.error("getCode not implemented for InterfaceDeclaration");
        return _factory.createFragment();
    }

}