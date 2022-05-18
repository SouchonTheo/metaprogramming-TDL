/**
 * 
 */
package fr.n7.stl.block.ast;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;
import fr.n7.stl.util.Logger;

/**
 * Represents a Block node in the Abstract Syntax Tree node for the Bloc language.
 * Declares the various semantics attributes for the node.
 * 
 * A block contains declarations. It is thus a Scope even if a separate SymbolTable is used in
 * the attributed semantics in order to manage declarations.
 * 
 * @author Marc Pantel
 *
 */
public class Block {

	/**
	 * Sequence of instructions contained in a block.
	 */
	protected List<Instruction> instructions;

	/**
	 * Constructor for a block.
	 */
	public Block(List<Instruction> _instructions) {
		this.instructions = _instructions;
	}
	
	/**
	 * Size of the block in order to allocate memory
	 */
	private int size;
	
	/**
	 * Offset before of the block
	 */
	private int offset;
	
	private Register register;
	
	private HierarchicalScope<Declaration> local;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		for (Instruction _instruction : this.instructions) {
			_local += _instruction;
		}
		return "{\n" + _local + "}\n" ;
	}
	
	/**
	 * Inherited Semantics attribute to collect all the identifiers declaration and check
	 * that the declaration are allowed.
	 * @param _scope Inherited Scope attribute that contains the identifiers defined previously
	 * in the context.
	 * @return Synthesized Semantics attribute that indicates if the identifier declaration are
	 * allowed.
	 */
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		boolean ret = true;
		local = new SymbolTable(_scope);
		Iterator<Instruction> it = this.instructions.iterator();
		while (ret && it.hasNext()) {
			ret = it.next().collectAndBackwardResolve(local);
		}
		//System.out.println(local.toString());
		return ret;
	}
	
	/**
	 * Inherited Semantics attribute to check that all identifiers have been defined and
	 * associate all identifiers uses with their definitions.
	 * @param _scope Inherited Scope attribute that contains the defined identifiers.
	 * @return Synthesized Semantics attribute that indicates if the identifier used in the
	 * block have been previously defined.
	 */
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean ret = true;
		Iterator<Instruction> it = this.instructions.iterator();
		while (ret && it.hasNext()) {
			ret = it.next().fullResolve(local);
		}
		return ret;
	}

	/**
	 * Synthesized Semantics attribute to check that an instruction if well typed.
	 * @return Synthesized True if the instruction is well typed, False if not.
	 */	
	public boolean checkType() {
		boolean ret = true;
		Iterator<Instruction> it = this.instructions.iterator();
		while (ret && it.hasNext()) {
			ret = it.next().checkType();
		}
		return ret;
	}

	/**
	 * Inherited Semantics attribute to allocate memory for the variables declared in the instruction.
	 * Synthesized Semantics attribute that compute the size of the allocated memory. 
	 * @param _register Inherited Register associated to the address of the variables.
	 * @param _offset Inherited Current offset for the address of the variables.
	 */	
	public void allocateMemory(Register _register, int _offset) {
		int depl = _offset;
		this.register = _register;
		this.offset = _offset;
		this.size = 0;
		for (Instruction i : this.instructions) {
			depl = i.allocateMemory(_register, depl);
		}
		this.size = depl - _offset;
	}

	/**
	 * Inherited Semantics attribute to build the nodes of the abstract syntax tree for the generated TAM code.
	 * Synthesized Semantics attribute that provide the generated TAM code.
	 * @param _factory Inherited Factory to build AST nodes for TAM code.
	 * @return Synthesized AST for the generated TAM code.
	 */
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		for (Instruction i : this.instructions) {
			frag.append(i.getCode(_factory));
		}
		if (this.register.equals(Register.LB)) {
			frag.add(_factory.createPop(this.offset, 0));
		} else {
			frag.add(_factory.createPop(this.offset, this.size));			
		}
		return frag;
	}

	/**
	 * Allows for the returns to know what is the return type of the function.
	 */
	public Type returnsTo() {
		Type returnType = AtomicType.VoidType;
		Type instruType = AtomicType.VoidType;
		for (Instruction i : this.instructions) {
			instruType = i.returnsTo();
			if (returnType.equalsTo(AtomicType.VoidType)) {
				// Le type de retour n'est pas initialisé donc on l'initialise
				returnType = instruType;
			} else if (!returnType.equalsTo(AtomicType.VoidType) && !instruType.equalsTo(AtomicType.VoidType)) {
				// Il y a déjà un type de retour et pourtant l'instruction en a un
				Logger.error("Il y a plusieurs types de retour dans ce block");
				return AtomicType.ErrorType;
			}
		}
		return returnType;
	}
	
	
}
