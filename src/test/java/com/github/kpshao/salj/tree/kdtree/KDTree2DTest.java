package com.github.kpshao.salj.tree.kdtree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KDTree2DTest {

    /**
     * 测试基本的快速选择功能
     */
    @Test
    void testQuickSelectBasic() {
        // 准备测试数据
        double[] xPoints = {7, 2, 9, 4, 5, 6, 3};
        double[] yPoints = {1, 2, 3, 4, 5, 6, 7};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);

        // 创建测试用的indices数组
        int[] indices = new int[xPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // 测试找中位数
        int mid = indices.length / 2;
        int[] result = kdTree.quickSelect(indices, 0, indices.length - 1, mid, true);

        // 验证结果：中位数应该是5
        assertEquals(5.0, xPoints[indices[result[0]]], 0.001);

        // 验证划分是否正确
        for (int i = 0; i < result[0]; i++) {
            assertTrue(xPoints[indices[i]] <= xPoints[indices[result[0]]]);
        }
        for (int i = result[1] + 1; i < indices.length; i++) {
            assertTrue(xPoints[indices[i]] >= xPoints[indices[result[0]]]);
        }
    }

    /**
     * 测试包含重复值的情况
     */
    @Test
    void testQuickSelectWithDuplicates() {
        double[] xPoints = {4, 2, 4, 3, 5, 2, 4};
        double[] yPoints = {1, 2, 3, 4, 5, 6, 7};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);

        int[] indices = new int[xPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // 测试找中位数
        int mid = indices.length / 2;
        int[] result = kdTree.quickSelect(indices, 0, indices.length - 1, mid, true);

        // 验证结果：应该返回所有5的范围
        assertEquals(4.0, xPoints[indices[result[0]]], 0.001);
        assertEquals(4.0, xPoints[indices[result[1]]], 0.001);

        // 验证返回的区间长度（应该有3个5）
        assertEquals(3, result[1] - result[0] + 1);
    }

    /**
     * 测试已排序数组的情况
     */
    @Test
    void testQuickSelectWithSortedArray() {
        double[] xPoints = {1, 2, 3, 4, 5, 6, 7};
        double[] yPoints = {1, 2, 3, 4, 5, 6, 7};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);

        int[] indices = new int[xPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // 测试找中位数
        int mid = indices.length / 2;
        int[] result = kdTree.quickSelect(indices, 0, indices.length - 1, mid, true);

        // 验证结果：中位数应该是4
        assertEquals(4.0, xPoints[indices[result[0]]], 0.001);
    }

    /**
     * 测试边界情况
     */
    @Test
    void testQuickSelectEdgeCases() {
        // 测试只有两个元素的数组
        double[] xPoints = {2, 1};
        double[] yPoints = {1, 2};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);

        int[] indices = new int[xPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // 测试找最小值
        int[] result = kdTree.quickSelect(indices, 0, indices.length - 1, 0, true);
        assertEquals(1.0, xPoints[indices[result[0]]], 0.001);

        // 测试找最大值
        result = kdTree.quickSelect(indices, 0, indices.length - 1, 1, true);
        assertEquals(2.0, xPoints[indices[result[0]]], 0.001);
    }

    /**
     * 测试使用y坐标进行划分
     */
    @Test
    void testQuickSelectUsingYCoordinate() {
        double[] xPoints = {1, 2, 3, 4, 5};
        double[] yPoints = {5, 2, 1, 4, 3};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);

        int[] indices = new int[yPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // 测试找中位数
        int mid = indices.length / 2;
        int[] result = kdTree.quickSelect(indices, 0, indices.length - 1, mid, false);

        // 验证结果：y坐标的中位数应该是3
        assertEquals(3.0, yPoints[indices[result[0]]], 0.001);

        // 验证划分是否正确
        for (int i = 0; i < result[0]; i++) {
            assertTrue(yPoints[indices[i]] <= yPoints[indices[result[0]]]);
        }
        for (int i = result[1] + 1; i < indices.length; i++) {
            assertTrue(yPoints[indices[i]] >= yPoints[indices[result[0]]]);
        }
    }
    
    /**
     * 测试基本的KDTree构建
     */
    @Test
    void testBasicConstruction() {
        double[] xPoints = {2, 5, 9, 4, 8, 7};
        double[] yPoints = {3, 4, 6, 7, 1, 2};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);
        
        // 验证根节点不为空
        assertNotNull(kdTree.getRoot());
        
        // 验证树的结构（通过遍历验证每个节点的分割是否正确）
        verifyKDTreeStructure(kdTree.getRoot(), 0, xPoints, yPoints);

        xPoints = new double[]{5, 2, 5, 3, 5, 2, 4};
        yPoints = new double[]{1, 2, 3, 4, 5, 6, 7};
        kdTree = new KDTree2D(xPoints, yPoints, 10);

        // 验证根节点不为空
        assertNotNull(kdTree.getRoot());

        // 验证树的结构（通过遍历验证每个节点的分割是否正确）
        verifyKDTreeStructure(kdTree.getRoot(), 0, xPoints, yPoints);
    }
    
    /**
     * 测试最大深度限制
     */
    @Test
    void testMaxDepthLimit() {
        // 创建一个较大的数据集
        double[] xPoints = new double[20];
        double[] yPoints = new double[20];
        for (int i = 0; i < 20; i++) {
            xPoints[i] = i;
            yPoints[i] = i;
        }
        
        int maxDepth = 3;
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, maxDepth);
        System.out.println(getTreeDepth(kdTree.getRoot()));
        
        // 验证树的深度不超过maxDepth
        assertTrue(getTreeDepth(kdTree.getRoot()) <= maxDepth);
    }
    
    /**
     * 测试边界情况
     */
    @Test
    void testEdgeCases() {
        // 测试空数组
        assertThrows(IllegalArgumentException.class, () -> {
            new KDTree2D(null, new double[0], 10);
        });
        
        // 测试长度不匹配的数组
        assertThrows(IllegalArgumentException.class, () -> {
            new KDTree2D(new double[1], new double[2], 10);
        });
        
        // 测试单个点
        double[] xPoints = {1};
        double[] yPoints = {1};
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);
        assertNotNull(kdTree.getRoot());
        assertEquals(1, kdTree.getRoot().index.length);
    }
    
    /**
     * 测试大规模数据集
     */
    @Test
    void testLargeDataset() {
        int size = 300000;
        double[] xPoints = new double[size];
        double[] yPoints = new double[size];
        
        // 生成随机点
        for (int i = 0; i < size; i++) {
            xPoints[i] = Math.random() * 100;
            yPoints[i] = Math.random() * 100;
        }

        long start = System.currentTimeMillis();
        KDTree2D kdTree = new KDTree2D(xPoints, yPoints, 10);
        System.out.println(System.currentTimeMillis() - start);
        
        // 验证树的基本属性
        assertNotNull(kdTree.getRoot());
        verifyKDTreeStructure(kdTree.getRoot(), 0, xPoints, yPoints);
    }
    
    // 辅助方法：验证KD树的结构
    private void verifyKDTreeStructure(KDTree2D.Node node, int depth, double[] xPoints, double[] yPoints) {
        if (node == null) return;
        
        boolean useX = (depth % 2 == 0);
        
        // 验证节点的分割是否正确
        if (node.left != null) {
            for (int leftIndex : node.left.index) {
                for (int nodeIndex : node.index) {
                    if (useX) {
                        assertTrue(xPoints[leftIndex] <= xPoints[nodeIndex]);
                    } else {
                        assertTrue(yPoints[leftIndex] <= yPoints[nodeIndex]);
                    }
                }
            }
        }
        
        if (node.right != null) {
            for (int rightIndex : node.right.index) {
                for (int nodeIndex : node.index) {
                    if (useX) {
                        assertTrue(xPoints[rightIndex] >= xPoints[nodeIndex]);
                    } else {
                        assertTrue(yPoints[rightIndex] >= yPoints[nodeIndex]);
                    }
                }
            }
        }
        
        // 递归验证子节点
        verifyKDTreeStructure(node.left, depth + 1, xPoints, yPoints);
        verifyKDTreeStructure(node.right, depth + 1, xPoints, yPoints);
    }
    
    // 辅助方法：获取树的深度
    private int getTreeDepth(KDTree2D.Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }
}