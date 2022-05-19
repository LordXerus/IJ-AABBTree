package lordxerus.aabbtest.engine.aabb_tree;

import lordxerus.aabbtest.engine.internals.AABB;

interface IAABBItemHandle {

    boolean move(AABB newAABB);
    void remove();

}
