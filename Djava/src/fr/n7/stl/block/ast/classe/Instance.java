
package fr.n7.stl.block.ast.classe;

import java.util.List;
import java.util.ArrayList;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class Instance implements Expression {
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

    public void addInstances(List<Instance> instances){
        this.generiques.addAll(instances);
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

    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        if (_scope.knows(this.name)) {
            boolean result = true;
            for (Instance i : this.generiques) {
                result = result && i.collectAndBackwardResolve(_scope);
            }
            this.declaration = (ClassDeclaration) _scope.get(this.name);
            return result && this.declaration.collectAndBackwardResolve(_scope);
        } else {
            Logger.error("Instance non enregistrée dans la table des symboles");
            return false;
        }
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        for (Instance i : this.generiques) {
            result = result && i.fullResolve(_scope);
        }
        return result && this.declaration.fullResolve(_scope);
    }

    @Override
    public Type getType() {
        return this.declaration.getType();
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode");
    }
}
