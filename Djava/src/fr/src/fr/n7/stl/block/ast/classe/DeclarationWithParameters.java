package fr.n7.stl.block.ast.classe;

import java.util.List;

import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;

public interface DeclarationWithParameters extends Declaration {
    
    List<ParameterDeclaration> getParameters();

}