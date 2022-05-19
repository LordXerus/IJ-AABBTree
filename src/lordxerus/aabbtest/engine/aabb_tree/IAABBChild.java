package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.internals.AABB;

import java.util.Optional;

sealed interface IAABBChild
permits AABBNode, AABBLeaf, AABBInternal {

	AABB getAABB();

	int getHeight();

    boolean isLeaf();
	Optional<AABBLeaf> asLeaf();
	Optional<AABBInternal> asInternal();

	Optional<IAABBParent> getParent();
	void attachParent(IAABBParent parent);
	void detachParent();
	
	boolean isDescendantOf(IAABBParent ancestor);
	boolean isChildOf(IAABBParent parent);
	
	// function called by createProxy to plug other in with us
	void join(IAABBChild other);
}
