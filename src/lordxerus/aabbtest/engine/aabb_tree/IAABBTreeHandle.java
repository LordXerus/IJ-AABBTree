package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.AABB;

import java.util.List;
import java.util.function.Function;

interface IAABBTreeHandle {
    void insert(AABBLeaf leaf);

    // ??? raycast(???);


    //IAABBTreeHandle
    <T> List<T> query(AABB query_aabb, Function<Integer, T> converter);

    boolean isEmpty();
}
