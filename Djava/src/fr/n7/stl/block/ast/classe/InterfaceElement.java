package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;

public abstract class InterfaceElement implements Instruction, Declaration {
    public abstract Signature getSignature();
}
