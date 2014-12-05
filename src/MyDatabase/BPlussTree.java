package MyDatabase;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/* 
 * Unlike a binary search tree, each node of a B+-tree may have a variable number of keys and children.
 * The keys are stored in non-decreasing order. Each node either is a leaf node or
 * it has some associated children that are the root nodes of subtrees.
 * The left child node of a node's element contains all nodes (elements) with keys less than or equal to the node element's key
 * but greater than the preceding node element's key (except for duplicate internal node elements).
 * If a node becomes full, a split operation is performed during the insert operation.
 * The split operation transforms a full node with 2*T-1 elements into two nodes with T-1 and T elements
 * and moves the median key of the two nodes into its parent node.
 * The elements left of the median (middle) element of the splitted node remain in the original node.
 * The new node becomes the child node immediately to the right of the median element that was moved to the parent node.
 * 
 * Example (T = 4):
 * 1.  R = | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
 * 
 * 2.  Add key 8
 *   
 * 3.  R =         | 4 |
 *                 /   \
 *     | 1 | 2 | 3 | -> | 4 | 5 | 6 | 7 | 8 |
 *
 */

public class BPlussTree {
        class Node {
                public int mNumKeys = 0;
                public String[] mKeys = new String[2 * T - 1];
                public Object[] mObjects = new Object[2 * T - 1];
                public Node[] mChildNodes = new Node[2 * T];
                public boolean mIsLeafNode;
                public Node mNextNode;
         //       public  String[] ref = new String[2 * T - 1];
        }
        
        private Node mRootNode;
        private static final int T =22;
        
        public BPlussTree() {
                mRootNode = new Node();
                mRootNode.mIsLeafNode = true;           
        }
        
        public void add(String key, Object object){//, String addr) {
                Node rootNode = mRootNode;
                if (rootNode.mNumKeys == (2 * T - 1)) {
                        Node newRootNode = new Node();
                        mRootNode = newRootNode; //making empty
                        newRootNode.mIsLeafNode = false; // making new root node
                    //    System.out.println("Split Tree");
                        mRootNode.mChildNodes[0] = rootNode; // all 7 keys are transfered
                        splitChildNode(newRootNode, 0, rootNode); // Split rootNode and move its median (middle) key up into newRootNode.
                        insertIntoNonFullNode(newRootNode, key, object);//addr); // Insert the key into the B-Tree with root newRootNode.
                } else {
                        insertIntoNonFullNode(rootNode, key, object);//, addr); // Insert the key into the B-Tree with root rootNode.
                }
        }
        
        // Split the node, node, of a B-Tree into two nodes that contain T-1 (and T) elements and move node's median key up to the parentNode.
        // This method will only be called if node is full; node is the i-th child of parentNode.
        // All internal keys (elements) will have duplicates within the leaf nodes.
        void splitChildNode(Node parentNode, int i, Node node) {
                Node newNode = new Node();
                newNode.mIsLeafNode = node.mIsLeafNode;
                newNode.mNumKeys = T;
             //   System.out.println("Split Child");
                for (int j = 0; j < T; j++) { // Copy the last T elements of node into newNode. Keep the median key as duplicate in the first key of newNode.
                        newNode.mKeys[j] = node.mKeys[j + T - 1];
                        newNode.mObjects[j] = node.mObjects[j + T - 1];
                       // newNode.mObjects[j] = node.mObjects[j + T - 1][1];
                }
                if (!newNode.mIsLeafNode) {
                        for (int j = 0; j < T + 1; j++) { // Copy the last T + 1 pointers of node into newNode.
                                newNode.mChildNodes[j] = node.mChildNodes[j + T - 1];
                        }
                        for (int j = T; j <= node.mNumKeys; j++) {
                                node.mChildNodes[j] = null;
                        }
                } else {
                        // Manage the linked list that is used e.g. for doing fast range queries.
                        newNode.mNextNode = node.mNextNode;
                        node.mNextNode = newNode;
                }
                for (int j = T - 1; j < node.mNumKeys; j++) {
                        node.mKeys[j] = "0";
                        node.mObjects[j] = null;
                }
                node.mNumKeys = T - 1;
                
                // Insert a (child) pointer to node newNode into the parentNode, moving other keys and pointers as necessary.
                for (int j = parentNode.mNumKeys; j >= i + 1; j--) {
                        parentNode.mChildNodes[j + 1] = parentNode.mChildNodes[j];
                }
                parentNode.mChildNodes[i + 1] = newNode;        
                for (int j = parentNode.mNumKeys - 1; j >= i; j--) {
                        parentNode.mKeys[j + 1] = parentNode.mKeys[j];
                        parentNode.mObjects[j + 1] = parentNode.mObjects[j];
                }
                parentNode.mKeys[i] = newNode.mKeys[0];
                parentNode.mObjects[i] = newNode.mObjects[0];
                parentNode.mNumKeys++;
        }
        
        // Insert an element into a B-Tree. (The element will ultimately be inserted into a leaf node). 
        void insertIntoNonFullNode(Node node, String key, Object object){ //, String addr) {
                int i = node.mNumKeys - 1;
                if (node.mIsLeafNode) {
                        // Since node is not a full node insert the new element into its proper place within node.
                        while (i >= 0 &&  key.compareTo(node.mKeys[i]) < 0) {//key.compareTo(node.mKeys[i] < 0 key < node.mKeys[i]
                                node.mKeys[i + 1] = node.mKeys[i];
                                node.mObjects[i + 1] = node.mObjects[i];
                                //node.ref[i+1]=node.ref[i];
                                i--;
                        }
                        i++;
                        node.mKeys[i] = key;
                        node.mObjects[i] = object;
                     //   node.ref[i]= addr;
                        node.mNumKeys++;
                       // System.out.println(node.mKeys[i]+"\t"+node.ref[i]);
                } else {
                        // Move back from the last key of node until we find the child pointer to the node
                        // that is the root node of the subtree where the new element should be placed.
                        while (i >= 0 &&  key.compareTo(node.mKeys[i]) < 0) { //key < node.mKeys[i]
                                i--;
                        }
                        i++;
                        if (node.mChildNodes[i].mNumKeys == (2 * T - 1)) {
                                splitChildNode(node, i, node.mChildNodes[i]);
                                if (key.compareTo(node.mKeys[i]) > 0 ){ //key > node.mKeys[i]
                                        i++;
                                }
                        }
                        insertIntoNonFullNode(node.mChildNodes[i], key, object);//, addr);
                }
        }       
        
        // Recursive search method.
        public Object search(Node node, String key) {              
                int i = 0;
                while (i < node.mNumKeys && key.compareTo(node.mKeys[i]) > 0) { // key > node.mKeys[i]
                        i++;
                }
                if (i < node.mNumKeys && key.compareTo(node.mKeys[i]) == 0) { //key == node.mKeys[i]
                        return node.mObjects[i];
                                 }
                if (node.mIsLeafNode) {
                        return null;
                } else {
                        return search(node.mChildNodes[i], key);
                }       
        }
        
        public Object search(String key) {
                return search(mRootNode, key);
        }
        
        // Iterative search method.
        public Object search2(Node node, String key) {
                while (node != null) {
                        int i = 0;
                        while (i < node.mNumKeys && key.compareTo(node.mKeys[i]) > 0) {// key > node.mKeys[i]
                                i++;
                        }
                        if (i < node.mNumKeys && key.compareTo(node.mKeys[i]) == 0) {//key == node.mKeys[i]
                                return node.mObjects[i];//node.ref[i];
                        }
                        if (node.mIsLeafNode) {
                                return null;//node.ref[i];
                        } else {
                                node = node.mChildNodes[i];
                        }
                }
                return null;
        }
        
        public Object search2(String key) {
                return search2(mRootNode, key);
        }
        
        // Inorder walk over the tree.
        public String toString() {
                String string = "";
                Node node = mRootNode;          
                while (!node.mIsLeafNode) {                     
                        node = node.mChildNodes[0];
                }               
                while (node != null) {
                		string+="{"+node.mKeys+"::";
                        for (int i = 0; i < node.mNumKeys; i++) {
                                string += node.mObjects[i] + ", ";
                        }
                        string+="}\n";
                        node = node.mNextNode;
                }
                return string;
        }
        
        // Inorder walk over parts of the tree.
       public String toString(String fromKey, String toKey) throws NullPointerException {
                String string = "";
                Node node = getLeafNodeForKey(fromKey);
                while (node != null) {
                        for (int j = 0; j < node.mNumKeys; j++) {
                                string += node.mObjects[j] + ", ";
                                if (node.mKeys[j].compareTo(toKey) == 0 ) { //node.mKeys[j] == toKey
                                        return string;
                                }
                        }
                        node = node.mNextNode;
                }
                return string;
        }
        
        Node getLeafNodeForKey(String key) {
                Node node = mRootNode;
                while (node != null) {
                        int i = 0;
                        while (i < node.mNumKeys && key.compareTo(node.mKeys[i]) > 0) { //key > node.mKeys[i]
                                i++;
                        }
                        if (i < node.mNumKeys && key.compareTo(node.mKeys[i]) == 0 ) { //key == node.mKeys[i]
                                node = node.mChildNodes[i + 1];
                                while (!node.mIsLeafNode) {                     
                                      node = node.mChildNodes[0];
                                }
                                return node;
                        }
                        if (node.mIsLeafNode) {
                                return null;
                        } else {
                                node = node.mChildNodes[i];
                        }
                }
                return null;
        }
        
        public void btreestart() throws Exception  {
                BPlussTree bPlusTree = new BPlussTree();
                //long primeNumbers[] = new long[10000]; //{ 2, 3, 5, 7, 11, 13, 19, 23, 37, 41, 43, 47, 53, 59, 67, 71, 61, 73, 79, 89,
                                //97, 101, 103, 109, 29, 31, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 17, 83, 107 };
                
                
               FileInputStream fstream = new FileInputStream("C:/Users/POOJA/Desktop/db/index.txt");
         	   BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

         	   String strLine;
 			   int count=0;
         	   //Read File Line By Line
 			  String data[][]=new String[10000][10000];
 			   while ((strLine = br.readLine()) != null)   {
     

         		   if (strLine.contains("   ")) {
 
 						String string = strLine;
 						String[] parts = string.split("   ");
 						String part1 = parts[0]; // 004
 						String part2 = parts[1];
 						
 						data[count][0]=part1;
 						data[count][1]=part2; 
 					count++;	
 						} else {
 						    throw new IllegalArgumentException(/*"String " + string + " does not contain -"*/);
 						}
         		   
         	     
         	   }

         	   //Close the input stream
         	   br.close();
         	   //working code end         
                
                
                for (int i = 0; i < count ; i++) {
                       bPlusTree.add(data[i][1], data[i][0]);//,data[i][0]);
                //	System.out.println(data[i][0]+"\t"+data[i][1]);
             
                }               
                
                for (int i = 0; i < count; i++) {
                        //String value = String.valueOf(primeNumbers[i]);
                        Object searchResult =  (Object)bPlusTree.search(data[i][1]);//primeNumbers[i]                       
                        System.out.println(" Key  " + data[i][1] + " retrieved offset " + searchResult);
                     /*   if (!data[i][1].equals(searchResult)) {
                                System.out.println("Oops: Key " + data[i][1] + " retrieved object " + searchResult);
                     */   }
                System.out.println("SEARCH-----------------------------------1");
                      System.out.println(bPlusTree.search("38417813544394A"));
                      System.out.println(bPlusTree.toString());
                        
        }               
      //  System.out.println("SEARCH-----------------1");
        // System.out.println("SEARCH-----------------------------------1");
          //      System.out.println(bPlusTree.search("38417813544394A"));
                /*        String abc=(String)bPlusTree.search("38417813544394A");
                System.out.println("SEARCH-----------------------------------2");
                System.out.println(bPlusTree.search("11627047200100A"));
                System.out.println("SEARCH-----------------------------------3");
                System.out.println(bPlusTree.toString());
                String display = bPlusTree.toString();
                String[] part1=null;
                if (display.contains(", ")) 
                {
                	part1 = display.split(", ");
               	}
                for(int i=0; i<part1.length;i++ ){
                if(abc.equals(part1[i]))
                {
                	int k=i+10;
                	for(int j=i;j<k;j++)
                		System.out.println(part1[j]);
                }
                	}
                System.out.println("SEARCH-----------------------------------from --to    11627047200100A 11627047200100A 64541668700164A");
          //      System.out.println(bPlusTree.toString("1","2"));
            //  System.out.println(bPlusTree.toString("00007542528470A","00188761001112A"));
               // System.out.println(bPlusTree.toString( "60520117982800A", "62961182282731A"));
*/        }
