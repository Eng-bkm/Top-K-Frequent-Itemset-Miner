# Top-K Frequent Itemset Miner (Java Implementation)

This Java project implements the **Top-K Frequent Itemsets (Top-KFI)** mining algorithm, designed to identify the most frequent item combinations in a transactional dataset. It uses a priority queue to dynamically maintain and extend the most frequent itemsets, making it suitable for data mining applications in market basket analysis, recommendation systems, and pattern discovery.

## 📁 Project Structure

- **TopKFI.java** – Main implementation file.
- **Entry Class** – Internal class used for storing and sorting itemsets.
- **Input Dataset** – A text file with each line representing a transaction, containing space-separated item IDs (integers).

## 🚀 Features

- Mines the top-K most frequent itemsets from a dataset.
- Supports customizable parameters:
  - `K` = number of top frequent itemsets to find.
  - `M` = maximum number of itemsets to output.
- Efficient mining using:
  - Priority queues for sorting by support.
  - Pruning via singleton extension logic.
  - Optimized support calculation using binary search.

## 🔧 Requirements

- Java JDK 8 or higher

