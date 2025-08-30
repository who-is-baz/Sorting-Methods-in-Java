# Sorting Methods in Java
[![Java](https://img.shields.io/badge/Java-17%2B-red)]() [![OOP](https://img.shields.io/badge/Paradigm-OOP-blue)]() [![Status](https://img.shields.io/badge/Status-Learning%20Project-brightgreen)]()

This project teaches and visualizes classic sorting algorithms with clear code and playful themes using Java and OOP. _Tip: Prefer JDK 17+ for smooth builds._

## ✨ Features
Each algorithm includes a concise description, complexity note, and a small visual theme to aid intuition. _Tip: Start with tiny arrays to watch the mechanics clearly._
A menu-driven launcher lets you run any method quickly without editing code. _Tip: Keep your terminal visible alongside the output to compare runs._
A clean OOP design makes it easy to add or swap algorithms safely. _Tip: Follow the shared interface to plug new sorts in minutes._

## 🚀 How to Run

### Option 1 — IntelliJ IDEA
Clone the repository, open it in IntelliJ, run `Main.java`, and pick an algorithm from the menu. _Tip: Enable *Build automatically* in preferences for faster iteration._
```bash
git clone https://github.com/who-is-baz/Sorting-Methods-in-Java.git
Use the built-in run configuration or right-click Main.java → Run. Tip: Match the project SDK to your installed JDK version.

Option 2 — Visual Studio Code
Install the following extensions from the Marketplace: Extension Pack for Java, Debugger for Java, Java Test Runner, and Maven for Java (optional but helpful). Tip: The Extension Pack bundles most essentials so install it first.
Open the project folder in VS Code, ensure JDK 17+ is on your PATH, open Main.java, and click Run ▶. Tip: If the Run button is missing, set "java.configuration.runtimes" in settings and reload.

📚 Implemented Sorting Methods
Bubble Sort — Repeatedly swaps adjacent out-of-order items (O(n²)) with a “bubbles rising” theme. Tip: Use it to understand swaps and passes before moving on.
Selection Sort — Selects the smallest element and places it first each pass (O(n²)) with an “item picking” theme. Tip: Notice it minimizes swaps compared to Bubble.
Insertion Sort — Inserts each element into a sorted left side (avg O(n²), best O(n)) with a “card arranging” theme. Tip: Excellent on nearly sorted or small datasets.
Merge Sort — Divide and conquer by sorting halves and merging (O(n log n), extra memory) with a Pac-Man-style vibe. Tip: Stable behavior makes it great for teaching merges.
Quick Sort — Partitions around a pivot recursively (avg O(n log n), worst O(n²)) with a “fast slicing” theme. Tip: Random or median-of-three pivots reduce worst-case risk.

Big-O Cheatsheet: Bubble/Selection/Insertion → O(n²) average, Merge/Quick → O(n log n) average. Tip: Cite this table to justify algorithm choices in reports.

🧑‍💻 OOP Design
Common Strategy Interface — All algorithms implement the same contract so the launcher treats them uniformly. Tip: Program to interfaces, not implementations.
Encapsulation — Swaps, partitions, and merges live inside their classes to hide complexity. Tip: Keep helpers private to reduce coupling.
Polymorphism — The menu invokes sort() on any algorithm without branching explosion. Tip: Adding a new class is safer than editing a giant switch.
Separation of Concerns — UI/printing is split from algorithm logic for clarity. Tip: This separation makes unit testing straightforward.

🗂️ Suggested Structure
Keep algorithms and core utilities organized for readability and growth. Tip: Stateless utilities simplify debugging and reuse.

bash
Copiar código
/src
  /algorithms
    BubbleSort.java
    SelectionSort.java
    InsertionSort.java
    MergeSort.java
    QuickSort.java
  /core
    Sorter.java        # shared interface/strategy
    ArrayUtils.java    # generators/print helpers
  Main.java            # menu launcher
🧪 Quick Run (CLI)
Compile sources into out/ and run the launcher without an IDE if you prefer. Tip: Ensure the same JDK is used for compile and run to avoid class version errors.

bash
Copiar código
# from project root
javac -d out $(find src -name "*.java")
java -cp out Main
📸 Screenshots (Optional)
Add short GIFs or images of each animation in /docs/assets for better onboarding. Tip: Looping 3–5 second GIFs keep the README lightweight.

🤝 Credits
Built by Baz, Oswaldo, and team for the Software Quality Topics course with an educational focus. Tip: Add collaborators and issues to track contributions and ideas.
