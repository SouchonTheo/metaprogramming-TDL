
package fr.n7.stl.block.ast.classe;

import java.util.List;
import java.util.ArrayList;

import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.util.Logger;

public class Instance {
    protected String name;
    protected List<Instance> generiques = new ArrayList<Instance>();
    protected ClassDeclaration declaration; 


    public Instance (String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public ClassDeclaration getDeclaration(){
        return this.declaration;
    }

    public void addInstance(Instance instance){
        this.generiques.add(instance);
    }

    public Declaration instanciate(HierarchicalScope<Declaration> _scope) {
        if (_scope.knows(this.name)) {
            this.declaration = (ClassDeclaration) _scope.get(this.name);
            return this.declaration;
        } else {
            Logger.error("Instance non enregistrée dans la table des symboles");
            return null;
        }
    }
}
