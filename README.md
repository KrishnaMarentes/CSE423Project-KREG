**Rebecca Castillo** <br />
**Geoff Knox** <br />
**Krishna Marentes** <br />
**Elijah Orozco** <br />


### **Project Assignment 2** *(IR Generation)*:
* Implementation of scanner and parser using Java and Antlr, a third-party tool
  * Antlr serves as both a scanner and parser all at once
* Grammar is written specifically for the use of Antlr, as kregGrammar.g4, but a .txt file is also included
* **NEW** The abstract syntax tree can be displayed with option -a
* **NEW** The symbol table can be displayed with -s
* **NEW** Linear IR be displayed with the option -i
* Necessary documentation is included
  * See documentation.pdf
  * Command line arguments are supported
  * Error messages are also displayed
* Usage <br />
  * See usage.pdf
  * Clone repository <br />
  usage: java [OPTS] FILENAME <br />
  OPTS: [t, p, a, s, i, w, r] <br />
  t: Print the tokens <br />
  p: Print the parse tree <br />
  a: Print the abstract syntax tree <br />
  s: Print the symbol table <br />
  i: Print the linear IR <br />
  w: Write the IR to a given filename. Ex: -w ir.out FILENAME <br />
  r: Read in an IR instead of C code <br />
  * Do not use -w or -r together <br />
  FILENAME: file path of input C source code (or IR file) <br />
  
  **Run instructions** <br />
  
  * Windows <br />
    Compile .g4: java -jar antlr-4.8-complete.jar kregGrammar.g4 -o out <br />
    Compile java: javac -cp “.;antlr-4.8-complete.jar;out” *.java <br />
    Run Example: java -cp “.;antlr-4.8-complete.jar;out” SCC tests\ex1.c <br />
    Print tokens: java -cp “.;antlr-4.8-complete.jar;out” SCC -t tests\ex1.c <br />
  * Mac / Linux <br />
    Compile .g4: java -jar antlr-4.8-complete.jar kregGrammar.g4 -o out <br />
    Compile java: javac -cp “.:antlr-4.8-complete.jar:out” *.java <br />
    Run Example: java -cp “.:antlr-4.8-complete.jar:out” SCC tests/ex1.c <br />
    Print tokens: java -cp “.:antlr-4.8-complete.jar:out” SCC -t tests/ex1.c <br />
