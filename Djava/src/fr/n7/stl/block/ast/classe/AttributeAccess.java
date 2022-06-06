package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.AbstractAccess;
import java.util.ArrayList;
import java.util.List;
import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.classe.Signature;
import fr.n7.stl.block.ast.classe.TypeParameter;
import fr.n7.stl.block.ast.classe.AccessRight;
import fr.n7.stl.block.ast.classe.Instance;
import fr.n7.stl.block.ast.classe.ClassElement;
import fr.n7.stl.block.ast.classe.ConstructorDeclaration;
import fr.n7.stl.block.ast.classe.MethodDeclaration;
import fr.n7.stl.block.ast.classe.AttributeDeclaration;
import fr.n7.stl.block.ast.expression.accessible.IdentifierAccess;
import fr.n7.stl.block.ast.instruction.declaration.ClassDeclaration;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

public class AttributeAccess extends AbstractAccess implements Expression {
	
	protected String name;
	protected Expression objetBrut;
	protected AttributeDeclaration attributeDeclaration;

	public AttributeAccess(Expression objet, String name) {
		this.objetBrut = objet;
		this.name = name;
	}

	public Declaration getDeclaration(){
		return this.attributeDeclaration;
	}

	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
		return this.objetBrut.collectAndBackwardResolve(_scope);
	}
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		if (this.objetBrut instanceof IdentifierAccess) {
			IdentifierAccess id = (IdentifierAccess) this.objetBrut;
			if (_scope.knows(id.getName())) {
				Declaration idType = _scope.get(id.getName());
				if (idType instanceof Instance) {
					Instance inst = (Instance) idType;
					ClassDeclaration classDecl = inst.getDeclaration();
					boolean temp = false;
					for (AttributeDeclaration a : classDecl.getAttributes(_scope)) {
						if(a.getName().equals(name)) {
							this.attributeDeclaration = a;
							return true;
						}
					}
					Logger.error("Object is an AtomicType");
					return false;
				}
			}
			
			Logger.error("Object not known or of the wrong type");
				return false;
		}
		Logger.error("Expression is not the identifier for an attribute");
				return false;
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
