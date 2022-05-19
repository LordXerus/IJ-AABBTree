package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.internals.AABB;

import javax.annotation.Nullable;

import java.util.*;
import java.util.function.Function;

@NotNullByDefault
final class AABBTreeHandle implements IAABBParent, IAABBTreeHandle {
	
	@Nullable private IAABBChild root;

	@Override //IAABBHandle
	public void insert(AABBLeaf leaf) {
		assert leaf.getParent().isEmpty();
		if(root == null) {
			root = leaf;
			leaf.attachParent(this);
			return;
		}

		//insertionCount += 1;



		// ### stage 1: determine the best sibling by traversing down the stack

		IAABBChild sib = root;
		while(!sib.isLeaf()) {

			AABBInternal sibling = sib.asInternal().orElseThrow();

			// cost before leaf insertion I think
			float oldCost = 2.0f * sibling.getAABB().perimeter;

			// immutability is great
			AABB combined = AABB.merge(leaf.getAABB(), sibling.getAABB());

			// cost after leaf insertion
			float newCost = 2.0f * combined.perimeter;

			float inheritCost = 2.0f * (newCost - oldCost);

			// #############

			// Cost of Descending into Child 1
			float cost1;

			{
				AABB child_combined = AABB.merge(leaf.getAABB(), sibling.getChild1().getAABB());

				if (sibling.getChild1().isLeaf()) {
					cost1 = child_combined.perimeter + inheritCost;
				} else {
					float child_oldCost = sibling.getChild1().getAABB().perimeter;
					float child_newCost = child_combined.perimeter;

					// no idea what this does
					cost1 = (child_newCost - child_oldCost) + inheritCost;
				}
			}

			float cost2;

			{
				AABB child_combined = AABB.merge(leaf.getAABB(), sibling.getChild2().getAABB());

				if (sibling.getChild2().isLeaf()) {
					cost2 = child_combined.perimeter + inheritCost;
				} else {
					float child_oldCost = sibling.getChild2().getAABB().perimeter;
					float child_newCost = child_combined.perimeter;

					// no idea what this does
					cost2 = (child_newCost - child_oldCost) + inheritCost;
				}
			}

			if (newCost < cost1 && newCost < cost2) {
				// plug leaf with parent here...
				break;
			}

			if (cost1 < cost2) {
				// descend
				sib = sibling.getChild1();
			} else {
				sib = sibling.getChild2();
			}
		}

		// ### Stage 2: Create the new parent and re-link nodes

		sib.join(leaf);

		// ### Stage 3: Walk back up fixing the AABB

		IAABBParent parent = leaf.getParent().orElseThrow();
		while (true) {
			Optional<AABBInternal> optionalInternal = parent.asInternal();
			if(optionalInternal.isEmpty()) break;

			AABBInternal internal = optionalInternal.orElseThrow();

			internal = internal.balanceHeight();


			internal.updateHeight();
			internal.updateAABB();


			// step back up the tree
			parent = internal.getParent().orElseThrow();
		}

		//validate();

	}

	@Override //IAABBTreeHandle
	public <T> List<T> query(AABB query_aabb, Function<Integer, T> converter) {

		List<T> result = new ArrayList<>();

		Deque<IAABBChild> stack = new ArrayDeque<>();

		stack.push(this.root);

		while(stack.size() > 0)
		{
			IAABBChild this_node = stack.pop();

			if(this_node.getAABB().intersects(query_aabb)){
				Optional<AABBLeaf> leaf = this_node.asLeaf();


				if(this_node.isLeaf())
				{
					result.add(converter.apply(this_node.asLeaf().orElseThrow().getHandler()));
				}
				else
				{
					// push 2 and 1 so search order is 1 and 2
					stack.push(this_node.asInternal().orElseThrow().getChild2());
					stack.push(this_node.asInternal().orElseThrow().getChild1());
				}

			}

		}
		return result;
	}

	@Override // IAABBTreeHandle
	public boolean isEmpty() {
		return root == null;
	}
	

	@Override // IAABBParent
	public boolean isParentOf(IAABBChild child) {
		return this.root == child;
	}
	
	@Override // IAABBParent
	public boolean isRoot() {
		return true;
	}
	
	@Override // IAABBParent
	public Optional<AABBTreeHandle> asRoot() {
		return Optional.of(this);
	}

	@Override // IAABBParent
	public Optional<AABBInternal> asInternal() {
		return Optional.empty();
	}

	
	@Override // IAABBParent
	public void replaceChild(IAABBChild oldChild, IAABBChild newChild) {
		if(this.root == null) throw new IllegalStateException("Cannot replace empty tree. Insert first.");

		// oldchild parent is not this
		assert oldChild.getParent().map((p) -> p != this).orElse(true);
		if(!this.isParentOf(oldChild)) throw new IllegalArgumentException("Who replaced root with wrong child?");
		
		root = newChild;
	}

	@Override // IAABBParent
	public void unbranch(IAABBChild child) {

		assert child.getParent().map((p) -> p != this).orElse(true);
		if(!this.isParentOf(child)) throw new IllegalArgumentException("Who unbranched root with wrong child?");
		
		root = null;
	}

	
}
