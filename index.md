---
layout: default
title: Aaron Summers | Computer Science ePortfolio
---
# Aaron Summers | Computer Science ePortfolio

**CS 499 Capstone · Southern New Hampshire University · B.S. Computer Science**

I am a Quality Analyst working in data quality and moving into software engineering. This ePortfolio shows how three projects from earlier coursework were reviewed, redesigned and improved, and what each enhancement says about my skills as a software engineer.

---

## Start Here: Professional Self-Assessment

My [Professional Self-Assessment](about.md) is the best introduction to this portfolio. It explains how my background in quality assurance shaped the way I design, test and secure software, and how each enhancement below supports the five Computer Science program outcomes: collaboration, communication, algorithmic problem solving, well-founded tools and techniques, and a security mindset.

**[Read the Professional Self-Assessment →](about.md)**

---

## Code Review

I reviewed all three original artifacts in a recorded walkthrough, using a checklist built on SmartBear and OWASP guidance. The review covers functionality and correctness, security, and maintainability, and it identifies the weaknesses that each enhancement then fixes.

- **Code review video:** [Watch on YouTube](https://youtu.be/Frq1Z9r_CVQ) *(about 18 minutes)*
- **Review script:** [Script_Code_Review.docx](docs/Script_Code_Review.docx)
- **Code review approach and benefits:** [Code_Review_Benefits.docx](docs/Code_Review_Benefits.docx)

---

## Artifacts

| Category | Course | Original Artifact | Enhanced Artifact | Narrative |
| --- | --- | --- | --- | --- |
| Software Design and Engineering | CS 320 | [Original](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/software-design-engineering/original) | [Enhanced](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/software-design-engineering/enhanced) | [Narrative](software-design-engineering/narrative.md) |
| Algorithms and Data Structures | CS 340 | [Original](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/algorithms-data-structures/original) | [Enhanced](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/algorithms-data-structures/enhanced) | [Narrative](algorithms-data-structures/narrative.md) |
| Databases | DAD 220 | [Original](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/databases/original) | [Enhanced](https://github.com/summers-lee/summers-lee.gitub.io/tree/main/databases/enhanced) | [Narrative](databases/narrative.md) |

### 1. Software Design and Engineering: Task Manager (CS 320, Java)

**Original:** a `Task` class and a `TaskService` class backed by an in-memory `HashMap`, with a JUnit 5 test suite. Every failure threw the same `IllegalArgumentException`, and tasks disappeared when the program ended.

**Enhancement:**
- Introduced a `TaskRepository` interface with `InMemoryTaskRepository` and `FileTaskRepository` implementations.
- `TaskService` now depends on the interface through constructor injection (the Dependency Inversion Principle).
- Replaced the single generic exception with a custom hierarchy: `TaskException`, `DuplicateTaskException`, `TaskNotFoundException` and `InvalidTaskDataException`.

**Skills shown:** abstraction and interface design, SOLID principles, structured exception handling, persistence design, unit testing.

### 2. Algorithms and Data Structures: Grazioso Rescue-Dog Dashboard (CS 340, Python, MongoDB)

**Original:** an `AnimalShelter` CRUD module and a Dash dashboard whose rescue-category filters used hard breed and age cutoffs inside MongoDB queries.

**Enhancement:**
- Replaced the hard filters with a weighted, multi-criteria scoring algorithm covering breed (with partial credit for similar breeds), age fit (with a gradual falloff instead of a hard cutoff) and sex.
- Used a fixed-size min-heap to select the top *k* candidates in O(n log k) time, compared with O(n log n) for a full sort.

**Skills shown:** algorithm design, data structure selection, complexity analysis, performance benchmarking, and a documented trade-off between simplicity and scalability.

### 3. Databases: RMA Returns Analysis (DAD 220, MySQL)

**Original:** queries that joined an `RMA` table to an `Orders` table and grouped by a raw product description. Inconsistent text and stray line breaks forced a second, nearly identical query to get clean results.

**Enhancement:**
- Normalized the schema by moving product data into a `Products` table with a primary key, and referenced it from `Orders` through a foreign key.
- Created the view `vw_ReturnsByProduct` so the return-percentage logic is defined once.
- Added the parameterized stored procedure `sp_GetTopReturnedProducts`, which takes the number of results as a bound parameter instead of building a query string.

**Skills shown:** schema normalization, reusable database objects, parameterized (injection-resistant) queries, data-quality thinking.

---

## Course Outcome Alignment

| Program Outcome | Where It Is Demonstrated |
| --- | --- |
| **1. Collaboration:** build collaborative environments that support decision making | The code review process: a structured, checklist-based review designed to give another developer clear, actionable feedback and share knowledge. |
| **2. Communication:** professional oral, written and visual communication | The recorded code review, the three artifact narratives, the draw.io and written documentation, and this ePortfolio. |
| **3. Algorithms and trade-offs:** design and evaluate solutions using algorithmic principles | Category 2: the weighted scoring algorithm, heap-based selection, complexity analysis and benchmark. |
| **4. Well-founded techniques and tools:** use industry-standard techniques and tools | Category 1: SOLID-based redesign, repository pattern, JUnit testing. Category 3: normalization, views and stored procedures. |
| **5. Security mindset:** anticipate vulnerabilities and mitigate design flaws | Category 3: parameterized queries. Category 2: the hardcoded-credentials finding from the code review. Category 1: typed exceptions in place of ambiguous errors. |

---

## About This Repository

```
├── README.md                          (this page)
├── about.md                           (Professional Self-Assessment)
├── docs/                              (code review script and notes)
├── software-design-engineering/
│   ├── original/
│   ├── enhanced/
│   └── narrative.md
├── algorithms-data-structures/
│   ├── original/
│   ├── enhanced/
│   └── narrative.md
└── databases/
    ├── original/
    ├── enhanced/
    └── narrative.md
```

---

## Contact

- **Email:** [summers.aaron.lee@gmail.com](mailto:summers.aaron.lee@gmail.com)
- **LinkedIn:** [linkedin.com/in/aaron-summers-643725216](https://www.linkedin.com/in/aaron-summers-643725216/)
- **GitHub:** [github.com/summers-lee](https://github.com/summers-lee)
