# About Me: Professional Self-Assessment

**Aaron Summers · B.S. Computer Science, Southern New Hampshire University**

## Where I Started and Where I Am Going

I came to computer science from outside the field. My first degree and early career were in a different line of work, and I moved into a more technical role because I wanted to work closer to how software and data are built. I now work as a Quality Analyst in data quality, which taught me to look for what can go wrong before it reaches users. My goal is to move from quality assurance into a software engineering role, and this ePortfolio is evidence that I can design, build and secure software, as well as test it.

## What This Portfolio Shows

Each artifact comes from a different stage of my coursework, and I returned to each one with a critical eye. In my code review I examined the original code for functionality, security and maintainability, and I found a specific gap in each project that a simple fix would not close. The enhancements target those gaps directly:

- **Software design and engineering (CS 320 Task Manager).** The original was a single in-memory class that threw one generic exception for every failure. I redesigned it around a `TaskRepository` interface with in-memory and file-based implementations, injected into `TaskService`, and added a custom exception hierarchy. This shows that I can apply SOLID principles and design for change, and it is where my testing background helped most.
- **Algorithms and data structures (CS 340 Grazioso).** The original relied on hard breed and age cutoffs in database queries. I replaced them with a weighted scoring algorithm and a min-heap that selects the top *k* candidates in O(n log k) time, and I benchmarked it against a full sort. This shows that I can choose a data structure deliberately, analyze its complexity and weigh the trade-off between simplicity and scalability.
- **Databases (DAD 220 RMA analysis).** The original grouped on dirty text, which forced duplicate queries. I normalized the schema with a `Products` table and a foreign key, defined the return-percentage logic once in a view, and added a parameterized stored procedure. This shows that I can fix a data problem at its source and write queries that resist injection.

## How I Meet the Five Program Outcomes

1. **Collaboration.** The code review process I followed is built for working with other developers: a checklist, small focused reviews and feedback that explains the issue, why it matters and how to fix it. I learned from SmartBear and OWASP guidance that review is most valuable when it is continuous and shares knowledge across a team. 
2. **Communication.** I explained my findings in a recorded code review for a technical audience, and I wrote a narrative for each artifact. My QA work means I routinely write findings that other people need to act on.
3. **Algorithmic problem solving.** The Grazioso enhancement is the clearest case: I designed the scoring algorithm, justified the heap, analyzed its cost and measured the result.
4. **Well-founded techniques and tools.** I used interfaces, dependency injection, repositories, normalization, views, stored procedures and unit testing, which are standard industry practice and not one-off tricks.
5. **Security mindset.** I looked for vulnerabilities in my own work, including hardcoded database credentials, ambiguous error handling and the SQL injection risk in query building, and I changed designs to remove them, not just warn about them.

## Looking Ahead

Working on this portfolio changed how I think about the technology around me. AI-assisted tools, CI/CD pipelines and low-code platforms are all raising the importance of testing, secure design and sound architecture, which are the skills I have been building. As I move into software engineering, I expect to work inside automated pipelines and to use AI-assisted tools daily, and my testing background gives me a good foundation for both. I plan to keep strengthening my security and data skills, because as more software is built quickly and by more people, the engineers who can secure and fix it will be the ones in demand.

[← Back to the ePortfolio](README.md)
