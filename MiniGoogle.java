/*
 * MiniGoogle.java
 * 
 * This program allows for searching the text of an article using keywords, 
 * using Cosine Similarity
 * 
 * Author: Jake Bloomfeld (jtbloom@bu.edu)
 * Date: 4/18/16
 */

import java.util.*;

public class MiniGoogle {
    
    private static final String [] blackList = { "the", "of", "and", "a", "to", "in", "is", 
    "you", "that", "it", "he", "was", "for", "on", "are", "as", "with", 
    "his", "they", "i", "at", "be", "this", "have", "from", "or", "one", 
    "had", "by", "word", "but", "not", "what", "all", "were", "we", "when", 
    "your", "can", "said", "there", "use", "an", "each", "which", "she", 
    "do", "how", "their", "if", "will", "up", "other", "about", "out", "many", 
    "then", "them", "these", "so", "some", "her", "would", "make", "like", 
    "him", "into", "time", "has", "look", "two", "more", "write", "go", "see", 
    "number", "no", "way", "could", "people",  "my", "than", "first", "water", 
    "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day", 
    "did", "get", "come", "made", "may", "part" }; 
    
    private static String preprocess(String s) {
        char[]charsToRemove = {'.',',',':',';','!','?','/','-','(',')','~'
                                                                    ,'\'','"'};
        s = s.toLowerCase();
        for (int i = 0; i < s.length(); i++) {
            char eachChar;
            eachChar = s.charAt(i);
            for (int j = 0; j < charsToRemove.length; j++ ) {
                    if (eachChar == charsToRemove[j]){
                        String noPunctuation = Character.toString(eachChar);
                        s = s.replace(noPunctuation, "");
                    }
            }
        }
        return s;
    }
    
    private static boolean blacklisted(String s) {
        s = preprocess(s);
        for (int i = 0; i < blackList.length; i++) {
            if (s.equals(blackList[i])) {
                return true;
            }
        }
        return false;
    }
    
    //discussed approach for this method on whiteboard with Francis Zamora
    private static double getCosineSimilarity(String s, String t) {
        s = preprocess(s);
        t = preprocess(t);
        StringTokenizer sTokenized = new StringTokenizer(s);
        StringTokenizer tTokenized = new StringTokenizer(t);
        TermFrequencyTable A = new TermFrequencyTable();
        while (sTokenized.hasMoreTokens()) {
            String sTokenized1 = sTokenized.nextToken();
            if (!blacklisted(sTokenized1)) {
                A.insert(sTokenized1, 0);
            }
        }
        while (tTokenized.hasMoreTokens()) {
            String tTokenized1 = tTokenized.nextToken();
            if (!blacklisted(tTokenized1)) {
                A.insert(tTokenized1, 1);
            }
        }
        return A.cosineSimilarity();
    }


    public static String phraseSearch(String phrase, ArticleTable T) {
        String S = "";
        T.reset();
        MaxHeap H = new MaxHeap();
        while (T.hasNext()) {
           Article a = T.next(); 
           double cosineSim = getCosineSimilarity(phrase, a.body);
           a.putCS(cosineSim);
           if (a.getCS() > 0.001) {
               H.insert(a);  
           }
        }
        if (H.isEmpty()) {
            return ("\nNo matching articles found!\n");
        }
        else if (H.size() == 1) {
            Article b = H.getMax();
            S += "Top Match:\n\n";
            S += "Match 1 with a cosine similarity of " + b.getCS() + ":\n\n" + b.title + "\n===\n" + b.body + "\n\n";
            return S;
        }
        else if (H.size() == 2) {
            Article b = H.getMax();
            Article c = H.getMax();
            S += "Top 2 Matches:\n\n";
            S += "Match 1 with a cosine similarity of " + b.getCS() + ":\n\n" + b.title + "\n===\n" + b.body + "\n\n";
            S += "Match 2 with a cosine similarity of " + c.getCS() + ":\n\n" + c.title + "\n===\n" + c.body + "\n\n";
            return S;
                
        }
        else {           
            Article b = H.getMax();
            
            Article c = H.getMax();
            Article d = H.getMax();
            S += "Top 3 Matches:\n\n";
            S += "Match 1 with a cosine similarity of " + b.getCS() + ":\n\n" + b.title + "\n===\n" + b.body + "\n\n";
            S += "Match 2 with a cosine similarity of " + c.getCS() + ":\n\n" + c.title + "\n===\n" + c.body + "\n\n";
            S += "Match 3 with a cosine similarity of " + d.getCS() + ":\n\n" + d.title + "\n===\n" + d.body + "\n\n";        
            return (S); 
        }
    }
    
    
    private static Article[] getArticleList(DatabaseIterator db) {
    
    // count how many articles are in the directory
        int count = db.getNumArticles(); 
    
    // now create array
        Article[] list = new Article[count];
        for(int i = 0; i < count; ++i)
            list[i] = db.next();
    
        return list; 
    }
  
    private static DatabaseIterator setupDatabase(String path) {
        return new DatabaseIterator(path);
    }
  
    private static void insert(Scanner s, ArticleTable D) {
        System.out.println();
        System.out.println("Add an article");
        System.out.println("==============");
    
        System.out.print("Enter article title: ");
        String title = s.nextLine();
    
        System.out.println("You may now enter the body of the article.");
        System.out.println("Press return two times when you are done.");
    
        String body = "";
        String line = "";
        do {
            line = s.nextLine();
            body += line + "\n";
        } while (!line.equals(""));
    
        D.insert(new Article(title, body));
    }
  
  
    private static void delete(Scanner s, ArticleTable D) {
        System.out.println();
        System.out.println("Remove an article");
        System.out.println("=================");
        System.out.print("Enter article title: ");
        
        String title = s.nextLine();
       
        D.delete(title);
        System.out.println("Deleted " + title);
  }
  
  
    private static void lookup(Scanner s, ArticleTable D) {
        System.out.println();
        System.out.println("Search by article title");
        System.out.println("=======================");
        System.out.print("Enter article title: ");
   
        String title = s.nextLine();
    
        Article a = D.lookup(title);
    
        if (a != null)
            System.out.println(a);
        else {
            System.out.println("Article not found!"); 
            return; 
        }
    
        System.out.println("Press return when finished reading.");
        s.nextLine();
    }
  
    private static void phraseSearch2(Scanner s, ArticleTable D) {
        System.out.println();
        System.out.println("Search by article content");
        System.out.println("=======================");
        System.out.print("Enter a search phrase: ");
        
        String title = s.nextLine();
    
        System.out.println(phraseSearch(title, D));
        System.out.println("Press return when finished reading.");
        
        s.nextLine();
    }
    
    public static void main (String[] args) {
                         
        ArticleTable D = new ArticleTable(); 
        
        Scanner user = new Scanner(System.in);
    
        String dbPath = "articles/";
    
        DatabaseIterator db = setupDatabase(dbPath);
    
        System.out.println("Read " + db.getNumArticles() + 
                       " articles from disk.");
    
        ArticleTable L = new ArticleTable(); 
        
        Article[] A = getArticleList(db);
        
        L.initialize(A);
        
        System.out.println("Created in-memory hash table of articles.");
    
        int choice = -1;
        
        do {
            System.out.println();
            System.out.println("Welcome to Mini-Google!");
            System.out.println("=====================");
            System.out.println("Make a selection from the " +
                         "following options:");
            System.out.println();
            System.out.println("    1. add a new article");
            System.out.println("    2. remove an article");
            System.out.println("    3. Search by article title");
            System.out.println("    4. Search by phrase (list of keywords)");
      
            System.out.print("\nEnter a selection (1-4, or 0 to quit): ");
      
            choice = user.nextInt();
            user.nextLine();
      
            switch (choice) {
                case 0:
                    System.out.println("Bye!");
                    return;
          
                case 1:
                    insert(user, L);
                    break;
          
                case 2:
                    delete(user, L);
                    break;
          
                case 3:
                    lookup(user, L);
                    break;
                    
                case 4:
                    phraseSearch2(user, L);
                    break;
          
                default:
                    break;
            }
      
            choice = -1;
      
        } while (choice < 0 || choice > 5);
    
    }
        
}
            
            
