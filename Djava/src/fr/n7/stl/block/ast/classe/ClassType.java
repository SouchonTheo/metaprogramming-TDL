package fr.n7.stl.block.ast.classe;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.util.Logger;

public class ClassType implements Type {

    private Instance instance;

    public ClassType(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return this.instance;
    }


    @Override
    public boolean equalsTo(Type _other) {
        if (_other instanceof ClassType) {
            ClassType other = (ClassType) _other;
            return this.instance.getName().equals(other.getInstance().getName());
        } else {
            Logger.error("Other is not a ClassType");
            return false;
        }
    }

    @Override
    public boolean compatibleWith(Type _other) {
        boolean result = true;
        if (!this.equalsTo(_other)) {
            ClassDeclaration classDecl = this.instance.getDeclaration();
            Instance heritage = classDecl.getHeritage();
            result = new ClassType(heritage).compatibleWith(_other);
            for (Instance interf : classDecl.getInterfaces()) {
                result = result || new ClassType(interf).compatibleWith(_other);
            }
        }
        return result;
    }

    @Override
    public Type merge(Type _other) {
        Type result = AtomicType.ErrorType;
        if (!this.equalsTo(_other)) {
            ClassDeclaration classDecl = this.instance.getDeclaration();
            Instance heritage = classDecl.getHeritage();
            result = new ClassType(heritage).merge(_other);
            if (result.equalsTo(AtomicType.ErrorType)) {
                for (Instance interf : classDecl.getInterfaces()) {
                    result = new ClassType(interf).merge(_other);
                    if (!result.equalsTo(AtomicType.ErrorType))
                        break;
                }
            }
        }
        return result;
    }

    @Override
    public int length() {
        int length = 0;
        for (ClassElement element : this.instance.getDeclaration().getClassElements()) {
            length += element.getType().length();
        }
        return length;
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        for (ClassElement element : this.instance.getDeclaration().getClassElements()) {
            result = result && element.getType().resolve(_scope);
        }
        return result;
    }

}