package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.AbstractAccess;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

public class AttributeAccess extends AbstractAccess {
	
	protected AttributeDeclaration declaration;
	
	/**
	 * Creates a variable use expression Abstract Syntax Tree node.
	 * @param _name Name of the used variable.
	 */
	public AttributeAccess(AttributeDeclaration _declaration) {
		this.declaration = _declaration;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getDeclaration()
	 */
	public Declaration getDeclaration() {
		return this.declaration;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = _factory.createFragment();
		_result.add(_factory.createLoad(
				this.declaration.getRegister(), 
				this.declaration.getOffset(),
				this.declaration.getType().length()));
		_result.addComment(this.toString());
		return _result;
	}

}
