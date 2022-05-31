package fr.n7.stl.block.ast.classe;

import java.util.Iterator;
import java.util.List;


import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.type.Type;

public class Signature extends InterfaceElement {
    /**
	 * Name of the function
	 */
	protected String name;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;

    public Signature (Type type, String name, List<ParameterDeclaration> parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    public Signature (Type type, String name) {
        this(type, name, null);
    }

    public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
        return _result + ")";
    }
}
