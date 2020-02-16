* Rebecca Castillo
* Geoff Knox
* Krishna Marentes
* Elijah Orozco

### **Project Assignment 1** *(Front End)*:
* Implementation of scanner and parser using Java and Antlr, a third-party tool.
  * Antlr serves as both a scanner and parser all at once.
* Grammar is written specifically for the use of Antlr, as kregGrammar.g4, but a .txt file is also included.
* Necessary documentation is included.
  * Command line arguments are supported.
  * Error messages are also displayed.
* Usage
  ** Run instructions **
  * Clone repository
  * Windows
    Compile .g4: java -jar antlr-4.8-complete.jar kregGrammar.g4 -o out 
    Compile java: javac -cp “.;antlr-4.8-complete.jar;out” *.java
    Run Example: java -cp “.;antlr-4.8-complete.jar;out” SCC tests\ex1.c 
    Print tokens: java -cp “.;antlr-4.8-complete.jar;out” SCC -t tests\ex1.c
  * Mac / Linux
    Compile .g4: java -jar antlr-4.8-complete.jar kregGrammar.g4 -o out 
    Compile java: javac -cp “.:antlr-4.8-complete.jar:out” *.java
    Run Example: java -cp “.:antlr-4.8-complete.jar:out” SCC tests/ex1.c 
    Print tokens: java -cp “.:antlr-4.8-complete.jar:out” SCC -t tests/ex1.c
