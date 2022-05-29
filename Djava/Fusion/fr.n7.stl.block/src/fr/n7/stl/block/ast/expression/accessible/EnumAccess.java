package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractAccess;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.EnumerationType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;


public class EnumAccess extends AbstractAccess implements AccessibleExpression  {
    
    protected EnumerationType type;
    public EnumAccess(EnumerationType _type) {
        this.type = _type;
    }
    @Override
    public Type getType(){
        return this.type;
    }
    @Override
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
        Logger.error("Ne devrait pas être appelé");
        return false;
    }
    @Override
    public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> _scope) {
        Logger.error("Ne devrait pas être appelé");
        return false;
    }
    @Override
    public Fragment getCode(TAMFactory _factory) {
        Logger.error("Ne devrait pas être appelé");
        return _factory.createFragment();
    }
    @Override
    protected Declaration getDeclaration() {
        Logger.error("Ne devrait pas être appelé");
        return null;
    }
}
