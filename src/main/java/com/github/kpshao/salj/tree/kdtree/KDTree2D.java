package com.github.kpshao.salj.tree.kdtree;

import java.util.Arrays;
import java.util.PriorityQueue;

public class KDTree2D {
    private final double[] xPoints;
    private final double[] yPoints;
    private int maxDepth = 10;

    private final Node root;

    public Node getRoot() {
        return root;
    }

    static class Node {
        int[] index;
        Node left, right;
        int depth;

        Node(int[] index, int depth) {
            this.index = index;
            this.depth = depth;
        }
    }

    public KDTree2D(double[] xPoints, double[] yPoints, int maxDepth) {
        if (xPoints == null || yPoints == null || xPoints.length != yPoints.length) {
            throw new IllegalArgumentException("坐标数组不能为空且长度必须相等");
        }
        
        this.xPoints = xPoints.clone();
        this.yPoints = yPoints.clone();
        this.maxDepth = maxDepth;
        
        int[] indices = new int[xPoints.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        
        root = buildTree(indices, 0, indices.length - 1, 0);
    }
    
    private Node buildTree(int[] indices, int start, int end, int depth) {
        if (start > end) {
            return null;
        }
        
        // 如果点数小于阈值，直接作为叶子节点
        int pointCount = end - start + 1;
        if (depth >= maxDepth - 1) {
            return new Node(Arrays.copyOfRange(indices, start, end + 1), depth);
        }
        
        // 根据深度决定使用x还是y坐标进行分割
        boolean useX = (depth % 2 == 0);
        
        // 使用三路划分找到中位数区间
        int mid = (start + end) / 2;
        int[] equalRange = quickSelect(indices, start, end, mid, useX);
        int leftEnd = mid-1;
        int rightStart = mid + 1;
        
        // 创建当前节点，包含所有相等的值
        // int[] currentIndices = Arrays.copyOfRange(indices, equalRange[0], equalRange[1] + 1);
        Node node = new Node(new int[]{indices[mid]}, depth);
        
        // 递归构建左右子树
        node.left = buildTree(indices, start, leftEnd, depth + 1);
        node.right = buildTree(indices, rightStart, end, depth + 1);
        
        return node;
    }
    
    /**
     * 使用三路划分的快速选择算法
     * @param indices 索引数组
     * @param start 起始位置
     * @param end 结束位置
     * @param k 要找的第k小的元素位置
     * @param useX true表示使用x坐标比较，false表示使用y坐标比较
     * @return 返回等于pivot的区间[left, right]
     */
    int[] quickSelect(int[] indices, int start, int end, int k, boolean useX) {
        while (start <= end) {
            // 使用三路划分，返回等于pivot的区间[left, right]
            int[] equalRange = threeWayPartition(indices, start, end, useX);
            int left = equalRange[0], right = equalRange[1];
            
            if (k >= left && k <= right) {
                // k在等于pivot的区间内，找到目标
                return equalRange;
            } else if (k < left) {
                // 在左半部分继续查找
                end = left - 1;
            } else {
                // 在右半部分继续查找
                start = right + 1;
            }
        }
        return new int[]{start, start};
    }
    
    /**
     * 三路划分，将数组分成小于、等于、大于pivot三部分
     * @param indices 索引数组
     * @param start 起始位置
     * @param end 结束位置
     * @param useX true表示使用x坐标比较，false表示使用y坐标比较
     * @return 返回等于pivot的区间[left, right]
     */
    private int[] threeWayPartition(int[] indices, int start, int end, boolean useX) {
        // 选择pivot
        int mid = (start + end) / 2;
        int pivotIdx = medianOfThree(indices, start, mid, end, useX);
        double pivotValue = useX ? xPoints[indices[pivotIdx]] : yPoints[indices[pivotIdx]];
        
        // lt: 小于区域的右边界
        // gt: 大于区域的左边界
        // i: 当前扫描位置
        int lt = start, i = start, gt = end;
        
        while (i <= gt) {
            double value = useX ? xPoints[indices[i]] : yPoints[indices[i]];
            int cmp = Double.compare(value, pivotValue);
            
            if (cmp < 0) {
                // 当前值小于pivot，放入左边
                swap(indices, lt++, i++);
            } else if (cmp > 0) {
                // 当前值大于pivot，放入右边
                swap(indices, i, gt--);
            } else {
                // 当前值等于pivot，保持在中间
                i++;
            }
        }
        
        // 返回等于pivot的区间[lt, gt]
        return new int[]{lt, gt};
    }
    
    /**
     * 三数取中法选择枢轴值（pivot）
     * 从数组的起始、中间、结束位置选择三个元素，返回其中值的索引位置
     * 这种方法可以避免在已经排序或接近排序的数组上出现最差性能
     * 
     * @param indices 索引数组
     * @param a 第一个位置的索引（通常是起始位置）
     * @param b 第二个位置的索引（通常是中间位置）
     * @param c 第三个位置的索引（通常是结束位置）
     * @param useX true表示使用x坐标比较，false表示使用y坐标比较
     * @return 返回三个值中中值所在的索引位置
     */
    private int medianOfThree(int[] indices, int a, int b, int c, boolean useX) {
        // 获取三个位置对应的坐标值
        double va = useX ? xPoints[indices[a]] : yPoints[indices[a]];
        double vb = useX ? xPoints[indices[b]] : yPoints[indices[b]];
        double vc = useX ? xPoints[indices[c]] : yPoints[indices[c]];
        
        // 通过比较找出三个值中的中值
        // 使用决策树方法，最多需要3次比较就能找到中值
        if (va < vb) {
            if (vb < vc) return b;    // va < vb < vc，中值是vb
            if (va < vc) return c;    // va < vc <= vb，中值是vc
            return a;                 // vc <= va < vb，中值是va
        } else {
            if (va < vc) return a;    // vb <= va < vc，中值是va
            if (vb < vc) return c;    // vb < vc <= va，中值是vc
            return b;                 // vc <= vb <= va，中值是vb
        }
    }
    
    private void swap(int[] indices, int i, int j) {
        int temp = indices[i];
        indices[i] = indices[j];
        indices[j] = temp;
    }

    /**
     * 查找距离目标点(x,y)最近的k个点的索引
     * @param x 目标点x坐标
     * @param y 目标点y坐标
     * @param k 需要返回的最近点数量
     * @return 返回k个最近点的索引数组，按照距离从近到远排序
     * @throws IllegalArgumentException 当k <= 0时抛出
     */
    public int[] findKNearest(double x, double y, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k必须大于0");
        }
        if (k > xPoints.length) {
            k = xPoints.length;
        }
        
        // 使用优先队列存储k个最近的点，按距离从大到小排序（最大堆）
        // 队列中存储的是点的索引和到目标点的距离
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            double distA = squareDistance(x, y, xPoints[a[0]], yPoints[a[0]]);
            double distB = squareDistance(x, y, xPoints[b[0]], yPoints[b[0]]);
            return Double.compare(distB, distA);  // 最大堆，距离最大的在堆顶
        });
        
        // 递归搜索k个最近的点
        searchKNearest(root, x, y, k, pq);
        
        // 构建结果数组，由于优先队列是最大堆，需要倒序存储以保证结果按距离从近到远排序
        int[] result = new int[pq.size()];
        int i = result.length - 1;
        while (!pq.isEmpty()) {
            result[i] = pq.poll()[0];
            i--;
        }
        return result;
    }

    /**
     * 递归搜索k个最近的点
     * @param node 当前节点
     * @param x 目标点x坐标
     * @param y 目标点y坐标
     * @param k 需要查找的点数
     * @param pq 存储最近k个点的优先队列
     */
    private void searchKNearest(Node node, double x, double y, int k, PriorityQueue<int[]> pq) {
        if (node == null) return;
        
        // 根据节点深度决定使用x还是y坐标进行比较
        boolean useX = (node.depth % 2 == 0);
        double nodeValue = useX ? xPoints[node.index[0]] : yPoints[node.index[0]];
        double searchValue = useX ? x : y;
        
        // 处理当前节点中的所有点
        for (int idx : node.index) {
            double dist = squareDistance(x, y, xPoints[idx], yPoints[idx]);
            
            // 如果队列未满或当前点比队列中最远的点更近，则更新队列
            if (pq.size() < k) {
                pq.offer(new int[]{idx});
            } else {
                assert pq.peek() != null;
                if (dist < squareDistance(x, y, xPoints[pq.peek()[0]], yPoints[pq.peek()[0]])) {
                    pq.poll();  // 移除最远的点
                    pq.offer(new int[]{idx});  // 添加当前点
                }
            }
        }
        
        // 如果是叶子节点，直接返回
        if (node.left == null && node.right == null) {
            return;
        }
        
        // 决定先搜索哪个子树
        Node first = searchValue < nodeValue ? node.left : node.right;
        Node second = searchValue < nodeValue ? node.right : node.left;
        
        // 递归搜索更可能包含近邻的子树
        searchKNearest(first, x, y, k, pq);
        
        // 判断是否需要搜索另一个子树
        // 计算查询点到分割线的距离
        double splitDist = useX ? (x - nodeValue) * (x - nodeValue) : (y - nodeValue) * (y - nodeValue);
        
        // 如果队列未满，或者到分割线的距离小于等于当前k个点中的最大距离，则需要搜索另一个子树
        if (pq.size() < k || splitDist <= squareDistance(x, y, xPoints[pq.peek()[0]], yPoints[pq.peek()[0]])) {
            searchKNearest(second, x, y, k, pq);
        }
    }

    /**
     * 计算两点间距离的平方
     * @param x1 第一个点的x坐标
     * @param y1 第一个点的y坐标
     * @param x2 第二个点的x坐标
     * @param y2 第二个点的y坐标
     * @return 两点间距离的平方
     */
    private double squareDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return dx * dx + dy * dy;
    }
}
