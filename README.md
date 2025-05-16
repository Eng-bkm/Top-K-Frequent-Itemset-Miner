# Top-K Frequent Itemsets Mining (TopKFI)

This project implements the **Top-K Frequent Itemsets (TopKFI)** mining algorithm in Java. It reads a transaction dataset, identifies frequent itemsets, and outputs the top-K itemsets based on support.

## 📌 Author

**Biruk Kiros Meles**
Course Project: *Data Structures and Algorithms AA 2022-23*

---

## 🚀 Features

* Reads transactions from a dataset file.
* Computes support for singleton and extended itemsets.
* Uses a **priority queue** (max-heap) to track top-K itemsets.
* Allows limiting the output size via a maximum output threshold `M`.
* Supports debugging via a simple debug flag.

---

## 🧾 Input Format

The dataset file must contain **one transaction per line**, with **space-separated integers** representing item IDs.

**Example:**

```
1 2 3
2 3 4
1 2
```

---

## 📥 Usage

### Compile

```bash
javac TopKFI.java
```

### Run

```bash
java TopKFI <dataset_path> <K> <M>
```

* `dataset_path`: Path to the dataset file.
* `K`: Number of top frequent itemsets to mine.
* `M`: Maximum number of itemsets to output.

### Example

```bash
java TopKFI data/transactions.txt 5 10
```

This command will output up to 10 itemsets among the top 5 most frequent.

---

## 📦 Output

* If the number of mined itemsets is **≤ M**, it prints all itemsets with their support count.
* If more than M itemsets are found, it only prints the total count.

**Example Output:**

```
5
 1 2 (3)
 2 3 (2)
 ...
```

---

## ⚙️ Configuration

You can enable debug mode to see internal processing and support computation by setting:

```java
public static boolean DEBUG = true;
```

---

## 📁 File Structure

* `TopKFI.java` – Main file containing all logic.

  * `Entry` class – Represents an itemset with support and transaction list.
  * `singletonItems()` – Computes support for individual items.
  * `adder4c()` – Generates candidate itemsets.
  * `support()` – Computes transactions supporting a given itemset.
  * `binarySearch()` – Used to check for item presence in transactions.

---

## ✅ Dependencies

* Java Standard Library only (`java.util`, `java.io`).

---

## 🛠️ Known Limitations

* Transactions are indexed starting from 1 (not 0).
* Singleton and itemset support counting can be inefficient for large datasets.
* The `adder4c()` logic modifies shared objects, which might introduce bugs or unintended behavior in extended itemsets.

---

## 📜 License

This project is shared as part of an academic course and is intended for educational purposes. No commercial license is provided.

