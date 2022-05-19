package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;

import java.util.Optional;

@NotNullByDefault
sealed interface IAABBParent permits AABBTreeHandle, AABBInternal {
	Optional<AABBTreeHandle> asRoot();
	Optional<AABBInternal> asInternal();
	
	boolean isRoot();
	
	default boolean isAncestorOf(IAABBChild descendant) {
		return descendant.isDescendantOf(this);
	}

	boolean isParentOf(IAABBChild child);

	void replaceChild(IAABBChild oldChild, IAABBChild newChild);
	
	// function called by child when child is removed
	void unbranch(IAABBChild child);
}
