/*

Author: Anurag Saini
        anuragsn7@gmail.com

** CodeConvert **

This program was developed as a part of compiler-design course.
It is a translator from mathematical expression to 3-Address intermediate code
using principles of compiler design (lexer, parser, code generator, optimizer etc)

The language in consideration consists of mathematical expression of simple
form such as
    x = 7 + 9/2
    y = (x + x) + tan(5)
    ...

each line contains one expression with clealy defined LHS and RHS. LHS must
be a variable and RHS can be: number, function, expression


With this language description in mind, this program implements following
    - Lexer
    - Parser
    - Intermediate Code Generator
    - Intermediate Code Optimizer

*/


import Builder.*;
import Elements.*;
import Optimize.Optimizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Test {

    public static int MAX_ROUNDS = 25;
    
    public static void main(String[] args) throws FileNotFoundException{
        
        File file = null;
        Scanner scan = new Scanner(System.in);
        
        // Get input file. Contains one expression per line
        while(file == null){
            System.out.print("Instruction File Path: ");
            file = new File(scan.nextLine());
            
            if(!file.exists() || !file.isFile()){ 
                file = null; 
                continue;
            }
        }
        
        // Scan file lines (expressions) in ArrayList
        scan = new Scanner(file);
        ArrayList<String> lines = new ArrayList<>();
        
        System.out.println("Input File Content:");
        while(scan.hasNextLine()){
            lines.add(scan.nextLine());
            System.out.println(lines.get(lines.size()-1));
        }
        
        
        // Convert expressions in 3-Address code (unoptimized)
        ArrayList<Instruction> ins =  ThreeAddressBuilder.Build(lines);
        
        
        // Print unoptimized 3-Address code
        System.out.println("\n\nUnoptimized 3-Address Code:");
        for(Instruction i : ins){
            System.out.println(i.toString());
        }
        
        Optimizer op = new Optimizer();
        
        
        // Perform successive round of optimizations
        // Stop when no more reduction in complexity or too many rounds
        boolean optimized = true;
        int i = 1;
        
        while(optimized && i < MAX_ROUNDS){   
            optimized = false;
            
            System.out.println("\n\nCode after " + i + " optimization round:");
            optimized = op.Optimize(ins);
            System.out.println(Optimizer.ToText(ins, 1));
            i++;
        }
        
        if(optimized) System.out.println("\n\nMaximum number of rounds is limited to " + MAX_ROUNDS);
    }
}
