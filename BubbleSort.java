/**
 * 冒泡排序实现类
 * 
 * @author Qoder
 */
public class BubbleSort {
    
    /**
     * 对整数数组进行冒泡排序（升序）
     * 
     * 算法原理：
     * 1. 比较相邻的两个元素，如果前一个比后一个大，则交换位置
     * 2. 对每一对相邻元素做同样的工作，从开始第一对到结尾的最后一对
     * 3. 重复以上步骤，直到没有任何一对数字需要比较
     * 
     * 时间复杂度：O(n²)
     * 空间复杂度：O(1)
     * 
     * @param arr 待排序的整数数组
     * @throws IllegalArgumentException 当输入数组为null时抛出
     */
    public static void bubbleSort(int[] arr) {
        // 检查输入参数
        if (arr == null) {
            throw new IllegalArgumentException("输入数组不能为null");
        }
        
        int n = arr.length;
        
        // 外层循环控制排序轮数
        for (int i = 0; i < n - 1; i++) {
            // 优化标志，如果某一轮没有发生交换，说明数组已经有序
            boolean swapped = false;
            
            // 内层循环进行相邻元素比较和交换
            for (int j = 0; j < n - 1 - i; j++) {
                // 如果前一个元素大于后一个元素，则交换
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            
            // 如果这一轮没有发生交换，说明数组已经有序，可以提前结束
            if (!swapped) {
                break;
            }
        }
    }
    
    /**
     * 交换数组中两个位置的元素
     * 
     * @param arr 数组
     * @param i 第一个位置
     * @param j 第二个位置
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    /**
     * 打印数组内容
     * 
     * @param arr 要打印的数组
     */
    public static void printArray(int[] arr) {
        if (arr == null) {
            System.out.println("数组为null");
            return;
        }
        
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    
    /**
     * 主方法，用于测试冒泡排序功能
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 测试用例1：普通无序数组
        int[] arr1 = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("原始数组:");
        printArray(arr1);
        
        bubbleSort(arr1);
        
        System.out.println("排序后数组:");
        printArray(arr1);
        
        // 测试用例2：已排序数组
        int[] arr2 = {1, 2, 3, 4, 5};
        System.out.println("\n已排序数组:");
        printArray(arr2);
        
        bubbleSort(arr2);
        
        System.out.println("排序后数组:");
        printArray(arr2);
        
        // 测试用例3：逆序数组
        int[] arr3 = {5, 4, 3, 2, 1};
        System.out.println("\n逆序数组:");
        printArray(arr3);
        
        bubbleSort(arr3);
        
        System.out.println("排序后数组:");
        printArray(arr3);
    }
}