package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.AbstractAccess;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

public class AttributeAccess extends AbstractAccess {
	
	protected String name;
	protected Expression objetBrut;

	public AttributeAccess(Expression objet, String name) {
		this.objetBrut = objet;
		this.name = name;
	}

	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		return this.objetBrut.collectAndBackwardResolve(_scope);
	}
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		if (expr instanceof IdentifierAccess) {
			IdentifierAccess id = (IdentifierAccess) expr;
			Type idType = id.getType();
			if (idType instanceof Instance) {
				Instance inst = (Instance) idType;
				ClassDeclaration classDecl = inst.getDeclaration();
				if (classDecl.getA().contains(this.method)) {
					// On est ok
				} else {
					Logger.error("Method not known for this object");
					return false;
				}
			} else {
				Logger.error("Object is an AtomicType");
				return false;
			}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	public Fragment getCode(TAMFactory _factory) {
		Fragment _result = _factory.createFragment();
		/*_result.add(_factory.createLoad(
				this.declaration.getRegister(), 
				this.declaration.getOffset(),
				this.declaration.getType().length()));*/
		_result.addComment(this.toString());
		return _result;
	}

}
