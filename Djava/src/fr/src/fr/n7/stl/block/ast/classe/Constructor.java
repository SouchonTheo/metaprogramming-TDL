package fr.n7.stl.block.ast.classe;

import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;

public class Constructor extends ClassElement {
    

	protected Block block;

    protected String name; // ?

   /**
    * List of AST nodes for the formal parameters of the function
    */
   protected List<ParameterDeclaration> parameters;
    
    public Constructor(String name, Block block, List<ParameterDeclaration> parameters) {
        this.name = name;
        this.block = block;
        this.parameters = parameters;
    }

    public Constructor(String name, Block block) {
        this(name,block, null);
    }
}
