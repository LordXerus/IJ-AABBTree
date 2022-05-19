package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@NotNullByDefault
sealed abstract class AABBNode
implements IAABBChild
permits AABBInternal, AABBLeaf
{

	private @Nullable IAABBParent parent;

	AABBNode(@Nullable IAABBParent parent) {
		this.parent = parent;
	}

	@Override // IAABBChild
	public final Optional<IAABBParent> getParent(){
		return Optional.ofNullable(this.parent);
	}
	
	@Override // IAABBChild
	public final void attachParent(IAABBParent parent) {
		if(this.parent != null) throw new IllegalStateException("attempt to overwrite node parent?");

		assert parent.isParentOf(this);
		this.parent = parent;
	}

	@Override // IAABBChild
	public final void detachParent() {
		if(parent == null) throw new IllegalStateException("detaching a node with no parent?");
		this.parent = null;
	}

	@Override // IAABBChild
	public final boolean isChildOf(IAABBParent parent) {
		return this.parent == parent;
	}

	@Override // IAABBChild
	public final boolean isDescendantOf(IAABBParent ancestor) {

		@Nullable IAABBParent parent = this.parent;
		while(parent != null) {
			if(parent == ancestor) return true;
			parent = parent.asInternal().flatMap(IAABBChild::getParent).orElse(null);
			// roots cannot be the descendant of anything return false
			//
		}
		return false;
	}

	@Override // IAABBChild
	public final void join(IAABBChild other) {
		if(other.getParent().isPresent()) throw new IllegalArgumentException("Cannot join an owned subtree");
		
		// with joint as center
		
		AABBInternal joint = new AABBInternal(
			this,       // c1 out
			other,    // c2 out
			this.parent // p out
		);

		if (joint.getParent().isPresent())
			joint.getParent().orElseThrow().replaceChild(this, joint);

		this.detachParent();
		this.attachParent(joint); // c1 in
		other.attachParent(joint); // c2 in

		
		//return joint;
	}


	
	// IAABBChild
	
}
