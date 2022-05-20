package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.AABB;

import java.util.*;

@NotNullByDefault
public final class AABBTree<T> {

	private final IAABBTreeHandle root = new AABBTreeHandle();

	private final List<AABBItem<T>> items = new ArrayList<>();
	private final Queue<Integer> free_ndxs = new ArrayDeque<>();

	void free(int ndx) {
		items.set(ndx, null);
		free_ndxs.add(ndx);
	}
	void changeAABB(int ndx, AABB aabb) {
		items.get(ndx).setAABB(aabb);
	}

	public AABBItem<T> create(AABB aabb, T data) {

		int free_ndx = free_ndxs.isEmpty() ? items.size() : free_ndxs.remove();
		AABBLeaf leaf = new AABBLeaf(
				new AABB(aabb.center, aabb.width + 100, aabb.height + 100),
				free_ndx,
				root
		);

		AABBItem<T> item = AABBItem.createAABBItem(leaf, this, free_ndx, aabb);

		item.setData(data);

		if(free_ndxs.isEmpty()) items.add(item);
		else items.set(free_ndx, item);

		root.insert(leaf);

		return item;
	}

	public List<AABBItem<T>> query(AABB aabb) {
		return root.query(aabb, items::get);
	}

	boolean isEmpty() {
		return root.isEmpty();
	}

}
