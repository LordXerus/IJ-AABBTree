package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.annotation.NotNullByDefault;
import lordxerus.aabbtest.engine.AABB;

import javax.annotation.Nullable;

@NotNullByDefault
public final class AABBItem<T> {
    @Nullable private T data;
    private AABB aabb;
    @Nullable public T getData() {
        return data;
    }
    public void setData(@Nullable T data) {
        this.data = data;
    }

    public AABB getAABB() {
        return aabb;
    }
    void setAABB(AABB aabb) {
        this.aabb = aabb;
    }

    static <T> AABBItem<T> createAABBItem(IAABBItemHandle handle, AABBTree<T> tree, int ndx, AABB aabb) {
        return new AABBItem<>(handle, tree, ndx, aabb);
    }


    private final IAABBItemHandle handle;
    private final AABBTree<T> tree;
    private final int tree_ndx;

    private AABBItem(IAABBItemHandle handle, AABBTree<T> tree, int ndx, AABB aabb) {
        this.handle = handle;
        this.tree = tree;
        this.tree_ndx = ndx;
        this.aabb = aabb;
    }



    public boolean move(AABB newAABB) {
        tree.changeAABB(tree_ndx, newAABB);
        return handle.move(newAABB);
    }
    public void destroy() {
        handle.remove();
        tree.free(tree_ndx);
    }
}
