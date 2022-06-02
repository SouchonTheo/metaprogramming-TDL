package fr.n7.stl.block.ast.classe;
import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;


public class TypeParameter implements Expression, Type, Instruction {

    private String name;

    private List<Instance> constraints;


    public TypeParameter(String name) {
        this(name, new ArrayList<Instance>());
    }

    public TypeParameter(String name, List<Instance> constraints) {
        this.name = name;
        this.constraints = constraints;
    }

    
    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        for (Instance i : this.constraints) {
            result &= i.collectAndBackwardResolve(_scope);
        }
        return result;
    }

    @Override
    public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        for (Instance i : this.constraints) {
            result &= i.fullResolve(_scope);
        }
        return result;
    }

    @Override
    public Type getType() {
        Type returnType = this.constraints.get(0).getType();
        for (Instance instance : this.constraints) {
            returnType = returnType.merge(instance.getType());
        }
        return returnType;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {
        throw new SemanticsUndefinedException("Semantics getCode");
    }

    
    @Override
    public boolean equalsTo(Type _other) {
        return this.getType().equalsTo(_other);
    }

    @Override
    public boolean compatibleWith(Type _other) {
        return this.getType().compatibleWith(_other);
    }
    
    @Override
    public boolean checkType() {
        return !this.getType().equalsTo(AtomicType.VoidType);
    }

    @Override
    public Type merge(Type _other) {
        throw new SemanticsUndefinedException("merge");
    }

    @Override
    public int length() {
        throw new SemanticsUndefinedException("length");
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        throw new SemanticsUndefinedException("resolve");
    }

    @Override
    public Type returnsTo() {
        return AtomicType.VoidType;
    }

    @Override
    public int allocateMemory(Register _register, int _offset) {
        throw new SemanticsUndefinedException("allocateMemory");
    }
    
}
