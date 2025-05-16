// This file implements the Top-K Frequent Itemsets mining algorithm
// Author: Biruk Kiros Meles
// Project: Data structures and algorithms AA 2022-23

import java.io.*;
import java.util.*;

public class TopKFI {
    // Priority queue to store itemsets sorted by their support counts (descending order)
    static PriorityQueue<Entry> q = new PriorityQueue<Entry>();
    // HashMap to store itemsets (not directly used in current implementation)
    static HashMap<Integer, HashMap> item_sets = new HashMap<Integer, HashMap>();
    
    // Debug flag - set to true for debugging output
    public static boolean DEBUG = false;

    public static void main(String args[]) {
        // HashMap to store all transactions from input file
        HashMap<Integer, int[]> transactions1 = new HashMap<Integer, int[]>();

        // Parse input arguments
        if (args.length != 3) {
            System.out.println("The arguments are not correct!");
            System.out.println("Please use \njava TopKFI datasetpath K M");
            return;
        }

        String db_path = args[0];  // Path to dataset file
        int K = Integer.parseInt(args[1]);  // Number of top itemsets to find
        int M = Integer.parseInt(args[2]);  // Maximum number of itemsets to output

        if (K < 0 || M < 0) {
            System.out.println("K and M should be positive!");
            return;
        }

        if (DEBUG) {
            System.out.println("Path to dataset: " + db_path);
            System.out.println("K: " + K);
            System.out.println("M: " + M);
        }

        // Read the input file and populate transactions
        try {
            File file_db = new File(db_path);
            Scanner db_reader = new Scanner(file_db);
            int transaction_id = 0;
            while (db_reader.hasNextLine()) {
                transaction_id++;
                String transaction = db_reader.nextLine();
                if (DEBUG) {
                    System.out.println("transaction " + transaction_id + " is " + transaction);
                }
                // Split transaction line into individual items
                String[] items_str = transaction.split("\\s+");
                int[] items = new int[items_str.length];
                
                // Convert string items to integers
                for (int i = 0; i < items_str.length; i++) {
                    try {
                        items[i] = Integer.parseInt(items_str[i]);
                        if (DEBUG) {
                            System.out.println("  item " + items[i]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input format of transaction is wrong!");
                        System.out.println("transaction " + transaction_id + " is " + transaction);
                        e.printStackTrace();
                        return;
                    }
                }
                // Store transaction in HashMap
                transactions1.put(Integer.valueOf(transaction_id), items);
            }
            db_reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file " + db_path + " does not exist!");
            e.printStackTrace();
            return;
        }

        // Start mining frequent itemsets
        HashMap<Integer, Integer> HashBig = new HashMap<Integer, Integer>();
        
        // Step 1: Find all singleton items and their supports
        singletonItems(transactions1, q, HashBig);
        
        // Extract all singleton items from the priority queue
        int[] singletons = new int[q.size()];
        Iterator value = q.iterator();
        int l = 0;
        while (value.hasNext()) {
            Entry g = (Entry) value.next();
            singletons[l] = (int) g.getItem().peek();
            l++;
        }
        
        // Step 2: Initialize list to store top-K itemsets
        LinkedList<Entry> S = new LinkedList<Entry>();
        Entry kth_extract = new Entry();
        int kth_support = 0;
        
        // Step 3: Mine top-K frequent itemsets
        for (int i = 0; i < K; i++) {
            // 4A: Get itemset with next highest support
            if (q.size() == 0) break;
            kth_extract = (Entry) q.poll();
            kth_support = kth_extract.getKey();
            
            // 4B: Add itemset to result list S
            Entry kth_extract3 = new Entry(kth_extract.getKey(), kth_extract.getItem(), kth_extract.getValue());
            if (kth_extract3.getItem().size() != 0)    
                S.add(kth_extract3);
            
            // 4C: Generate new candidate itemsets by extending current itemset
            adder4c(transactions1, kth_extract3, singletons, q);
        }
        
        // Handle itemsets with support equal to the K-th itemset's support
        while ((q.size() != 0) && q.peek().getKey() == kth_support && q.peek().getItem().size() != 0) {
            for (int i = 0; i < K; i++) {
                if (q.size() == 0) break;
                Entry kth_extract1 = (Entry) q.poll();
                Entry kth_extract4 = new Entry(kth_extract1.getKey(), kth_extract1.getItem(), kth_extract1.getValue());

                if (kth_extract4.getItem().size() != 0)    
                    S.add(kth_extract4);
                
                adder4c(transactions1, kth_extract4, singletons, q);
            }
        }
        
        // Output results
        if (S.size() <= M) {
            // If we have fewer itemsets than M, output all
            int sizet = S.size();
            int supportf = 0;
            System.out.println("  " + S.size());
            while (S.size() != 0) {
                Entry last = S.remove();
                String str = " ";
                PriorityQueue<Integer> D = last.getItem();
                int supportf1 = last.getKey();
                int dsize = D.size();
                
                // Format the itemset output
                while (D.size() != 0 && !str.contains(" " + D.peek().toString() + " ")) {
                    supportf = supportf1;
                    str = str + " " + D.poll().toString() + " ";
                }
                if (str != " ")
                    System.out.println(str + " " + "(" + supportf + ")");
            }
        } else {
            // If we have more itemsets than M, just output the count
            System.out.println("   " + S.size());
        }
    }
    
    // Helper method to print contents of priority queue (for debugging)
    public static void Printer(PriorityQueue<Entry> M) {
        for (int i = 0; i < 100; i++) {
            Entry g = (Entry) M.poll();
            System.out.println(g.getKey());
        }
    }
    
    // Finds all singleton items and their support counts
    public static void singletonItems(HashMap<Integer, int[]> M, PriorityQueue<Entry> Q, HashMap<Integer, Integer> HashBig) {
        int support_number;    
        ArrayList<Integer> list = new ArrayList<Integer>();

        // Process each transaction
        for (int i = 1; i <= M.size(); i++) {
            int[] trans_i = M.get(Integer.valueOf(i));
            
            // Process each item in the transaction
            for (int j = 0; j < trans_i.length; j++) {
                int[] m = new int[1];
                m[0] = trans_i[j];
                // Calculate support for this item
                list = support(m, M);
                support_number = list.size();
                PriorityQueue<Integer> X = new PriorityQueue<Integer>(Collections.reverseOrder());
                X.add(Integer.valueOf(m[0]));
                
                // Handle duplicates - only add if not already present or with different support
                if (HashBig.get(m[0]) != null && HashBig.get(m[0]) != support_number) {
                    HashBig.put(m[0], support_number);
                    Q.add(new Entry(Integer.valueOf(support_number), X, list));
                } else if ((HashBig.get(m[0]) == null)) {
                    HashBig.put(m[0], support_number);
                    Q.add(new Entry(Integer.valueOf(support_number), X, list));
                }
            }
        }
    }
    
    // Entry class to store itemsets, their supports, and transaction lists
    public static class Entry implements Comparable<Entry> {
        private Integer key;  // Support count
        private ArrayList value;  // List of supporting transactions
        private PriorityQueue<Integer> item;  // The itemset itself
        
        public Entry() {
            this.key = null;
            this.value = null;
            this.item = null;
        }

        public Entry(Integer key, PriorityQueue<Integer> item, ArrayList<Integer> value) {
            this.key = key;
            this.value = value;
            this.item = item;
        }
        
        // Getters
        public Integer getKey() { return this.key; }
        public ArrayList<Integer> getValue() { return this.value; }
        public PriorityQueue<Integer> getItem() { return this.item; }

        // Compare entries by support count (descending order)
        @Override
        public int compareTo(Entry other) {
            return (other.getKey()).compareTo(this.getKey()); 
        }
    }
    
    // Binary search helper method
    public static int binarySearch(int arr[], int first, int last, int key) {  
        if (last >= first) {  
            int mid = first + (last - first) / 2;  
            if (arr[mid] == key) {  
                return mid;  
            }  
            if (arr[mid] > key) {  
                return binarySearch(arr, first, mid - 1, key);  // Search left subarray  
            } else {  
                return binarySearch(arr, mid + 1, last, key);  // Search right subarray  
            }  
        }  
        return -1;  
    }  
    
    // Calculates the support (list of transactions containing) an itemset
    public static ArrayList support(int[] sub, HashMap<Integer, int[]> M) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (M.size() != 0) {
            // Check each transaction
            for (int i = 1; i < M.size() + 1; i++) {
                int check_hit = 0;
                int[] array1 = new int[M.get(Integer.valueOf(i)).length];
                array1 = M.get(Integer.valueOf(i));
                int s = sub.length;
                
                // Check if all items in subset are present in transaction
                for (int j = 0; j < s; j++) {
                    if (binarySearch(array1, 0, array1.length - 1, sub[j]) != -1)
                        check_hit++;
                    else break;
                }
                
                // If all items present, add transaction to support list
                if (check_hit == s)
                    list.add(i);
            }
            return list;
        } else {
            System.out.println("*****************************No transactions found**************************************************");
            return null;
        }
    }
    
    // Generates new candidate itemsets by extending a given itemset with singletons
    public static void adder4c(HashMap<Integer, int[]> M, Entry X, int[] singles, PriorityQueue<Entry> Q) {
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        int one = X.getItem().peek();  // Get first item of current itemset
        PriorityQueue<Integer> X_new = new PriorityQueue<Integer>(Collections.reverseOrder());
        ArrayList<Integer> list = X.getValue();  // Supporting transactions
        
        // Try extending with each singleton
        for (int i = 0; i < singles.length; i++) {
            // Only consider singletons greater than current item (to avoid duplicates)
            if (one < singles[i] && one != singles[i]) {
                for (int j = 0; j < list.size(); j++) {
                    int support = 0;
                    int ti = list.size();
                    int ti2 = list.remove(ti - 1);
                    int[] array1 = M.get(ti2);
                    
                    // Check if transaction contains the singleton
                    if (binarySearch(array1, 0, array1.length - 1, singles[i]) != -1) {
                        list1.add(ti2);
                        support++;
                    }
                    
                    // If extended itemset has support, add to queue
                    if (support > 0) {
                        X_new = X.getItem();
                        X_new.add(singles[i]);
                        Q.add(new Entry(Integer.valueOf(support), X_new, list1));
                    }
                }
            }
        }
    }
}
