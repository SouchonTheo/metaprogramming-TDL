package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.List;
import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.classe.Signature;
import fr.n7.stl.block.ast.classe.TypeParameter;
import fr.n7.stl.block.ast.classe.AccessRight;
import fr.n7.stl.block.ast.classe.Instance;
import fr.n7.stl.block.ast.classe.ClassElement;
import fr.n7.stl.block.ast.classe.ConstructorDeclaration;
import fr.n7.stl.block.ast.classe.MethodDeclaration;
import fr.n7.stl.block.ast.classe.AttributeDeclaration;
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
 * Abstract Syntax Tree node for a class declaration.
 * @author Basile Gros, Pablo Neyens, Diégo Rogard, Théo Souchon
 */
public class ClassDeclaration implements Instruction, Declaration {	
    /**
    * Name of the class
    */
    protected String name;

    /**
     * Attribute that determçines whether the class is abstract or not. false by default
     */
    protected boolean isAbstract = false;

    /**
     * Attribute that determçines whether the class is final or not. false by default
     */
    protected boolean isFinal = false;

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
    List<Instance> interfaces;

    protected List<ClassElement> classElements;

    /**
    * Scope
    */
    protected HierarchicalScope<Declaration> tds;

    public ClassDeclaration(String _name, List<TypeParameter> _generiques, Instance _heritage, List<Instance> _interfaces, List<ClassElement> _classElements) {
        this.name = _name;
        this.generiques = _generiques;
        this.heritage = _heritage;
        this.interfaces = _interfaces;
        this.classElements = _classElements;
    }

    public Instance getHeritage() {
        return this.heritage;
    }

    public List<Instance> getInterfaces() {
        return this.interfaces;
    }

    public List<ClassElement> getClassElements() {
        return this.classElements;
    }

    public void setAbstract(){
        this.isAbstract = true;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public void setFinal(){
        this.isFinal = true;
    }

    public boolean isFinal() {
        return this.isFinal;
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

    public List<MethodDeclaration> getMethods(HierarchicalScope<Declaration> _scope) {
        List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
        if(this.heritage != null) {
            Declaration tempDeclaration = this.heritage.instanciate(_scope);
            //Vérifier qu'il s'agit bien d'une classe
            if (tempDeclaration instanceof ClassDeclaration) {
                ClassDeclaration superClass = (ClassDeclaration) tempDeclaration;
                //Récupérer les méthodes de la classe.
                methods = superClass.getMethods(_scope);
            } else {
                Logger.error("Class "+ this.name + " extends something else than a class");
                return new ArrayList<MethodDeclaration>();
            }
        }
        for(ClassElement c: this.classElements) {
            if(c instanceof MethodDeclaration) {
                methods.add((MethodDeclaration)c);
            }
        }
        return methods;
    }

    public List<AttributeDeclaration> getAttributes(HierarchicalScope<Declaration> _scope) {
        List<AttributeDeclaration> attributes = new ArrayList<AttributeDeclaration>();
        if(this.heritage != null) {
            Declaration tempDeclaration = this.heritage.instanciate(_scope);
            //Vérifier qu'il s'agit bien d'une classe
            if (tempDeclaration instanceof ClassDeclaration) {
                ClassDeclaration superClass = (ClassDeclaration) tempDeclaration;
                //Récupérer les méthodes de la classe.
                attributes = superClass.getAttributes(_scope);
            } else {
                Logger.error("Class "+ this.name + " extends something else than a class");
                return new ArrayList<AttributeDeclaration>();
            }
        }
        for(ClassElement c: this.classElements) {
            if(c instanceof AttributeDeclaration) {
                attributes.add((AttributeDeclaration)c);
            }
        }
        return attributes;
    }

    public List<ConstructorDeclaration> getConstructors() {
        List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();

        for(ClassElement c: this.classElements) {
            if(c instanceof ConstructorDeclaration) {
                constructors.add((ConstructorDeclaration) c);
            }
        }
        return constructors;
    }


    /* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (this.heritage != null && this.heritage.getDeclaration().isFinal()){
            Logger.error("Heritage d'une classe finale");
            return false;
        }
		if (_scope.accepts(this)) {
			_scope.register(this);
			boolean retour = true;
            this.tds = new SymbolTable(_scope);
            if (this.generiques != null) {
                for (TypeParameter g : this.generiques){
                    retour = retour && g.collectAndBackwardResolve(tds);
                }
            }
            if (this.interfaces != null) {
                for (Instance i : this.interfaces){
                    retour = retour && i.collectAndBackwardResolve(tds);
                }
            }

            
            for(ClassElement c : this.classElements) {
                if(this.heritage != null) {
                    for(ClassElement h : this.heritage.getDeclaration().getClassElements()) {
                        if (c.getName().equals(h.getName()) && h.isFinal() && c instanceof MethodDeclaration && h instanceof MethodDeclaration) {
                            List<ParameterDeclaration> cTab = ((MethodDeclaration) c).getParameters();
                            List<ParameterDeclaration> hTab = ((MethodDeclaration) h).getParameters();
                            Boolean condHeritage = true;
                            if (cTab.size() == hTab.size()) {
                                for(int i = 0; i < cTab.size(); i++) {
                                    if (cTab.get(i).getType().equals(hTab.get(i).getType())){
                                        condHeritage = false;
                                    }
                                }
                                if (!condHeritage){
                                    Logger.error("Final method redifine");
                                    return false;
                                }
                                retour = retour && condHeritage;
                            }
                        }
                    }
                }
		// On vérifie que les constructeurs ont le nom de la classe et que les méthodes et attributs non
                if (c instanceof ConstructorDeclaration) {
                    if (!c.getName().equals(this.name)){
                        Logger.error("Nom de constructeur incorrect");
                        return false;
                    }
                } else {
                    if(c.getName().equals(this.name)){
                        Logger.error("Nom de methode/attribut incorrect");
                        return false;
                    }
                }
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
        boolean result = false;
        //Récupérer les méthodes de la classe
        List<MethodDeclaration> methods = this.getMethods(_scope);
        //Si la classe est concrète
        if(!this.isAbstract) {
            //Séparer les méthodes abstraites des méthodes concrètes
            List<MethodDeclaration> concreteMethods = new ArrayList<MethodDeclaration>();
            List<MethodDeclaration> abstractMethods = new ArrayList<MethodDeclaration>();
            for(MethodDeclaration m : methods) {
                if (m.isAbstract()) {
                    abstractMethods.add(m);
                } else {
                    concreteMethods.add(m);
                }
            }
            //Vérifier que les méthodes abstraites sont concrétisées
            for(MethodDeclaration am : abstractMethods) {
                boolean temp = false;
                for(MethodDeclaration cm : concreteMethods) {
                    temp = temp || (am.getEntete().equals(cm.getEntete()) && (
                    //Les accès de l'abstrait sont plus restrictifs que le concret
                    cm.getAccess() == AccessRight.PUBLIC
                    || (cm.getAccess() == AccessRight.PROTECTED && am.getAccess() !=AccessRight.PUBLIC)
                    || (cm.getAccess() == AccessRight.PRIVATE && am.getAccess() == AccessRight.PRIVATE)));
                }
                if(!temp) {
                    Logger.error("Abstract method " + am + " not implemented in class " + this);
                    return false;
                }
                result = result && temp;
            }

            List<ConstructorDeclaration> constructors = this.getConstructors();
            boolean constrDefault = true;
            for(ConstructorDeclaration c : constructors) {
                if (c.getParameters().size() == 0) {
                    constrDefault = false;
                }
            }

            if (constrDefault) {
                Block block = new Block();
                this.classElements.add(new ConstructorDeclaration(this.name, block));
            }

            return result;
        }

        //Récupérer les méthodes des interfaces
        List<Signature> methodsInterface = new ArrayList<Signature>();
        for(Instance i : this.interfaces) {
            Declaration tempDeclaration = i.instanciate(_scope);
            //Vérifier qu'il s'agit bien d'une interface
            if (tempDeclaration instanceof InterfaceDeclaration) {
                InterfaceDeclaration inter = (InterfaceDeclaration) tempDeclaration;
                //Récupérer les méthodes de l'interface.
                methodsInterface.addAll(inter.getMethods(_scope));
            } else {
                Logger.error("Class "+ this.name + " implements "+ tempDeclaration.getName() + ", something else than an interface");
                return false;
            }
        }

        //Vérifier que les méthodes des interfaces sont bien toutes implémentées
        for(Signature s : methodsInterface) {
            boolean temp = false;
            for(MethodDeclaration m : methods) {
                temp = temp || s.equals(m.getEntete());
            }
            if(!temp) {
                Logger.error("Interface method " + s + " not implemented in class " + this);
                return false;
            }
            result = result && temp;
            
        }

        //Faire un fullResolve sur les éléments de la classe
        for (TypeParameter g : this.generiques){
            result = result && g.fullResolve(tds);
        }
        for(ClassElement c : this.classElements){
            
            result = result && c.fullResolve(tds);
        }
        return result;
        
    }
        
        
    public boolean checkType(){
        boolean result = true;
        for (ClassElement c : classElements) {
            result = result && c.checkType();
        }
        return result;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        Logger.error("allocateMemory not implemented for ClassDeclaration");
        return 0;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        Logger.error("getCode not implemented for ClassDeclaration");
        return _factory.createFragment();
    }


}
