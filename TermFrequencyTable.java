/*
 * TermFrequencyTable.java
 * 
 * This program stores the words from two Strings (i.e., documents) in such a way 
 * that it can easily calculate the "cosine" similarity of the two.
 *
 * Author: Jake Bloomfeld (jtbloom@bu.edu)
 * Date: 4/16/16
 */

public class TermFrequencyTable {
    
    
    private final int SIZE = 103;
        
    private Node[] T = new Node[SIZE];
    
    private int ASum = 0;
    private int BSum = 0;
    
    private class Node {
        String term;
        int[] termFreq = new int[2];
        Node next;
        
        public Node() {}
        
        public Node(String t) {
            term = t;
        }
        
        public Node(String t, int[] a,  Node p) {
            term = t;
            termFreq = a;
            next = p;
        }
        
        
    }
    
    private int hash(String k) {          // hash function from http://research.cs.vt.edu/AVresearch/hashing/strings.php
        char ch[];
        ch = k.toCharArray();
        int klength = k.length();

        int i, sum;
        for (sum = 0, i = 0; i < k.length(); i++) {
            sum += ch[i];
        }
        return sum % SIZE;
    }
   
    public void insert(String term, int docNum) {
        T[hash(term)] = insertHelper(term, docNum, T[hash(term)]);   
    }
        
    private Node insertHelper(String term, int docNum, Node p) {   
       if (p == null) {
           Node k = new Node(term);
           k.termFreq[docNum] += 1;
           return k;
       }
       else if (term.equals(p.term)) {
           p.termFreq[docNum] += 1;
           return p;
       }
       else {
           p.next = insertHelper(term, docNum, p.next);
           return p;
       }
    }
       
    
    public double cosineSimilarity() {
        int total = 0;
        for (int i = 0; i < T.length; i++) {
            for (Node q = T[i]; q != null; q = q.next) {
                total += q.termFreq[0] * q.termFreq[1];
                ASum += q.termFreq[0] * q.termFreq[0];
                BSum += q.termFreq[1] * q.termFreq[1];          
            }
        }
        int numerator = total;
        double denominator = Math.sqrt(ASum) * Math.sqrt(BSum);
        return numerator/denominator;
    }
    
    public void printTable() {
        for(int i = 0; i < SIZE; ++i) {
            System.out.println(i);
            for(Node p = T[i]; p != null; p = p.next) {
                System.out.println(p.term + "\t" + p.termFreq[0] + "\t" + p.termFreq[1]);
            }
        }
    }
          
                
    
    public static void main (String[] args) {
        
        TermFrequencyTable T = new TermFrequencyTable();
        
        String[] test0 = {"CS112", "HW10"};
        String[] test1 = {"CS112", "HW10", "HW10"};
        
        for (int i = 0; i < test0.length; i++) {
            T.insert(test0[i],0);
        }
        for (int i = 0; i < test1.length; i++) {
            T.insert(test1[i],1);
        }
        
        
        System.err.println("Testing cosine similarities");
        System.out.println("\nTest 1:\nCS112, HW10\n&\nCS112, HW10, HW10");
        System.out.println("\nShould print out 0.9487 (or very close to it):");
        System.out.println(T.cosineSimilarity());
        
        TermFrequencyTable S = new TermFrequencyTable();
        
        String[] test2 = {"Dog", "Cat"};
        String[] test3 = {"Dog", "Cat", "Dog", "Cat"};
        
        for (int i = 0; i < test2.length; i++) {
            S.insert(test2[i],0);
        }
        for (int i = 0; i < test3.length; i++) {
            S.insert(test3[i],1);
        }

        System.out.println("\n\nTest 2:\nDog, Cat\n&\nDog, Cat, Dog, Cat");
        System.out.println("\nShould print out 1.0 (or very close to it):");
        System.out.println(S.cosineSimilarity());
        
        TermFrequencyTable R = new TermFrequencyTable();
        
        String[] test4 = {"Hey", "my", "name", "is", "Jake"};
        String[] test5 = {"Hey", "my", "name", "is", "Jake"};
        
        for (int i = 0; i < test4.length; i++) {
            R.insert(test4[i],0);
        }
        for (int i = 0; i < test5.length; i++) {
            R.insert(test5[i],1);
        }
        
        System.out.println("\n\nTest 3:\nHey, my, name, is, Jake\n&\nHey, my, name, is, Jake");
        System.out.println("\nShould print out 1.0 (or very close to it):");
        System.out.println(R.cosineSimilarity());
        
        TermFrequencyTable U = new TermFrequencyTable();
        
        String[] test6 = {"Hello"};
        String[] test7 = {"Goodbye"};
        
        for (int i = 0; i < test6.length; i++) {
            U.insert(test6[i],0);
        }
        for (int i = 0; i < test7.length; i++) {
            U.insert(test7[i],1);
        }
        
        System.out.println("\n\nTest 4:\nHello\n&\nGoodbye");
        System.out.println("\nShould print out 0.0 (or very close to it):");
        System.out.println(U.cosineSimilarity());
        
    }       
    
    
}
    
    