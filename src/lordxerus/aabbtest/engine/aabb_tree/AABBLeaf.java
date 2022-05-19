package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.Vector2;
import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.internals.AABB;

import javax.annotation.Nullable;
import java.util.Optional;

@NotNullByDefault
final class AABBLeaf extends AABBNode implements IAABBChild, IAABBItemHandle {
	private AABB aabb;
	private final int handler;

	private final IAABBTreeHandle treehandle;


	AABBLeaf(AABB aabb, int handler, IAABBTreeHandle tree) { this(aabb, handler, tree, null); }
	AABBLeaf(AABB aabb, int handler, IAABBTreeHandle tree, @Nullable IAABBParent parent) {
		super(parent);
		this.handler = handler;
		this.treehandle = tree;

		//this.aabb = aabb;
		this.aabb = new AABB(aabb.center, aabb.extents.x + 20, aabb.extents.y + 20);
	}

	@Override // IAABBChild
	public AABB getAABB() {
		return aabb;
	}

	public int getHandler() {
		return handler;
	}

	@Override // IAABBChild
	public int getHeight() {
		return 0;
	}

	@Override // IAABBChild
	public boolean isLeaf() {
		return true;
	}

	@Override // IAABBChild
	public Optional<AABBLeaf> asLeaf() {
		return Optional.of(this);
	}

	@Override // IAABBChild
	public Optional<AABBInternal> asInternal() {
		return Optional.empty();
	}

	@Override // IAABBItemHandle
	public boolean move(AABB newAABB) {
		final float AABB_FATTEN = 10;

		AABB fatAABB = new AABB(
				aabb.center,
				aabb.extents.x + 2f * AABB_FATTEN,
				aabb.extents.y + 2f * AABB_FATTEN);

		Vector2 dd = Vector2.sub(newAABB.center, aabb.center);
		Vector2 d = Vector2.mul(dd, 1.1f);

		float vlx = fatAABB.lower.x;
		float vly = fatAABB.lower.y;
		float vux = fatAABB.upper.x;
		float vuy = fatAABB.upper.y;

		if (d.x < 0.0f) {
			vlx += d.x;
		} else {
			vux += d.x;
		}

		if (d.y < 0.0f) {
			vly += d.y;
		} else {
			vuy += d.y;
		}

		AABB newfatAABB = new AABB(
				new Vector2(vlx, vly),
				new Vector2(vux, vuy)
		);
		// new AABB --[FATTEN]-> fatAABB --[EXTEND WITH D]-> newfatAABB


		if(aabb.contains(newAABB)) {
			// The tree AABB still contains the object, but it might be too large.
			// Perhaps the object was moving fast but has since gone to sleep.
			// The huge AABB is larger than the new fat AABB.

			AABB hugeAABB = new AABB(
					newfatAABB.center,
					newfatAABB.width + 8.0f * AABB_FATTEN,
					newfatAABB.height + 8.0f * AABB_FATTEN
			);

			if(hugeAABB.contains(aabb)) {
				// The tree AABB contains the object AABB and the tree AABB is
				// not too large. No tree update needed.
				return false;
			}

		}

		this.remove();

		aabb = newfatAABB;

		treehandle.insert(this);

		//moved = true; ?? I don't see this used

		return true;

	}

	@Override // IAABBItemHandle
	public void remove() {
		getParent().ifPresent(
			parent -> {
				this.detachParent();
				parent.unbranch(this);
			}
		);
	}
	
}
