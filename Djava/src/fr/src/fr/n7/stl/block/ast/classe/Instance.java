
package fr.n7.stl.block.ast.classe;

import java.util.List;

import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;

public class Instance {
    protected String name;
    protected List<Instance> generiques = new ArrayList<Instance>();

    public Instance (String name){
        this.name = name();
    }

    public void addInstance(Instance instance){
        this.generiques.add(instance);
    }

    public Declaration instanciate(HierarchicalScope<Declaration> _scope) {
        if (_scope.knows(this.name)) {
            return _scope.get(this.name);
        } else {
            Logger.error("Instance non enregistrée dans la table des symboles");
            return null;
        }
    }
}
