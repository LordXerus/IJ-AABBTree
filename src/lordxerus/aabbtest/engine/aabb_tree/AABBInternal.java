package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.internals.AABB;

import javax.annotation.Nullable;
import java.util.Optional;

@NotNullByDefault
final class AABBInternal extends AABBNode
implements IAABBChild, IAABBParent {

	private AABB aabb;
	private int height;
	
	private IAABBChild child1;
	private IAABBChild child2;


	AABBInternal(IAABBChild c1, IAABBChild c2, @Nullable IAABBParent parent) {
		super(parent);
		
		this.child1 = c1;
		this.child2 = c2;

		this.aabb = AABB.merge(child1.getAABB(), child2.getAABB());
		// updateAABB();
		updateHeight();
	}
	
	@Override // IAABBChild
	public AABB getAABB() {
		assert aabb.contains(child1.getAABB()) && aabb.contains(child2.getAABB());
		return aabb;
	}

	public void updateAABB() {
		this.aabb = AABB.merge(child1.getAABB(), child2.getAABB());
		// this.aabb = new AABB(aabb.center, aabb.extents.x + 10, aabb.extents.y + 10);
	}

	@Override // IAABBChild
	public int getHeight() {
		return height;
	}

	public void updateHeight() {
		this.height = Math.max(child1.getHeight(), child2.getHeight());
	}


	@Override // IAABBChild
	public boolean isLeaf() {
		return false;
	}

	@Override  // IAABBParent
	public boolean isRoot() {
		return false;
	}

	@Override // IAABBChild
	public Optional<AABBLeaf> asLeaf() {
		return Optional.empty();
	}

	@Override // IAABBParent
	public Optional<AABBTreeHandle> asRoot() {
		return Optional.empty();
	}

	@Override // IAABBChild, IAABBParent
	public Optional<AABBInternal> asInternal() {
		return Optional.of(this);
	}

	@Override // IAABBParent
	public boolean isParentOf(IAABBChild child) {
		return child == child1 || child == child2;
	}

	@Override // IAABBParent
	public void replaceChild(IAABBChild oldChild, IAABBChild newChild) {
		// assert parent of old child is not this
		assert oldChild.getParent().map((p) -> p != this).orElse(true);

		if(oldChild == child1) {
			child1 = newChild;
		}
		else
		if (oldChild == child2) {
			child2 = newChild;
		}


		else throw new AssertionError("Child not present in AABBInternal");

		updateAABB();
		updateHeight();
	}

	// destroys this
	// child that is being unbranched should not reference this
	// it is the child's responsibility to dereference this
	@Override // IAABBParent?
	public void unbranch(IAABBChild child) {
		assert isParentOf(child); // child once belonged to this
		// child does not think this is parent anymore
		assert child.getParent().map(p -> p != this).orElse(true);
		// or else parent is null which means true

		// the child that is bridged upwards
		IAABBChild other;

		if (child == child1) other = child2; else
		if (child == child2) other = child1;
		else throw new AssertionError();

		other.detachParent(); // other's parent should be this

		// bridge so other's new parent is this's parent, if this has a parent
		getParent().ifPresent(parent -> {
			detachParent();
			parent.replaceChild(this, other);
			other.attachParent(parent);
		});

		// in either case, one child is disconnected
		// other child has different parent
		// this should be GC-ed

		// At any given time there should only be 3 references to this
		// parent, to-be-unbranched child, and other
		// those three references are removed, so this should be GC-ed
	}

	AABBInternal balanceHeight2() {
		if(this.height < 2) return this;

		int balance = child2.getHeight() - child1.getHeight();

		AABBInternal newRoot;

		if(balance > 1) {
			newRoot = child2.asInternal().orElseThrow();
		}
		else
		if(balance < 1) {
			newRoot = child1.asInternal().orElseThrow();
		}
		else return this;

		// regardless of which child newRoot is, it should be detached first
		newRoot.detachParent();

		getParent().ifPresent(p -> {
			detachParent();
			p.replaceChild(this, newRoot);
			newRoot.attachParent(p);
		});

		IAABBChild movedSubtree = (newRoot.child2.getHeight() > newRoot.child1.getHeight()) ?
				newRoot.child1 : newRoot.child2;


		movedSubtree.detachParent();
		newRoot.replaceChild(movedSubtree, this);
		this.attachParent(newRoot);

		this.replaceChild(newRoot, movedSubtree);
		movedSubtree.attachParent(this);

		this.updateHeight();
		this.updateAABB();

		newRoot.updateHeight();
		newRoot.updateAABB();

		return newRoot;
	}
	AABBInternal balanceHeight() {
		if(this.height < 2) {
			return this;
		}

		int balance = child2.getHeight() - child1.getHeight();

		// child2-heavy
		// rotate c2 up
		if(balance > 1) {
			AABBInternal c2 = child2.asInternal().orElseThrow();

			IAABBChild c2c1 = c2.child1;
			IAABBChild c2c2 = c2.child2;

			// swap c2 and this
			Optional<IAABBParent> parent = getParent();

			c2.detachParent();

			parent.ifPresent(p -> {
				detachParent();


				p.replaceChild(this, c2);
				c2.attachParent(p);
			});

			if(c2c1.getHeight() > c2c2.getHeight()) {
				c2c2.detachParent();

				c2.replaceChild(c2c2, this);
				this.attachParent(c2);

				this.replaceChild(c2, c2c2);
				c2c2.attachParent(this);
			}
			else
			{
				c2c1.detachParent();

				c2.replaceChild(c2c1, this);
				this.attachParent(c2);

				this.replaceChild(c2, c2c1);
				c2c1.attachParent(this);
			}

			this.updateHeight();
			c2.updateHeight();


			return c2;
		}


		if(balance < 1) {
			AABBInternal c1 = child2.asInternal().orElseThrow();

			IAABBChild c1c1 = c1.child1;
			IAABBChild c1c2 = c1.child2;

			// swap c2 and this
			Optional<IAABBParent> parent = getParent();

			c1.detachParent();

			parent.ifPresent(p -> {
				detachParent();


				p.replaceChild(this, c1);
				c1.attachParent(p);
			});

			if(c1c1.getHeight() > c1c2.getHeight()) {
				c1c2.detachParent();

				c1.replaceChild(c1c2, this);
				this.attachParent(c1);

				this.replaceChild(c1, c1c2);
				c1c2.attachParent(this);
			}
			else
			{
				c1c1.detachParent();

				c1.replaceChild(c1c1, this);
				this.attachParent(c1);

				this.replaceChild(c1, c1c1);
				c1c1.attachParent(this);
			}

			this.updateHeight();
			c1.updateHeight();

			return c1;
		}

		return this;
	}

	IAABBChild getChild1() { return child1; }
	IAABBChild getChild2() { return child2; }
	
}
