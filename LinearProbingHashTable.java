// Gian Brazzini
// CE 3345.0U1
// Project 4: Creating a hashtable to store names and colors. The keys are the names and the color is the value.


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class LinearProbingHashTable<K, V> {
	
	private static class Entry<K,V>{
		private K key;
		private V value;
		private boolean deleted=false;
		
		Entry(K key, V value){
			this.key=key;
			this.value=value;
		}
		
		public void delete(){
			deleted=true;
		}
		
		public void unDelete(){
			deleted=false;
		}
		
		public boolean deleted(){
			return deleted;
		}
		
	}

	Entry<K,V> table[];
	int size;
	int count;
	
	@SuppressWarnings("unchecked")
	LinearProbingHashTable(){
		size=16;
		table = new Entry[size];
	}
	
	public boolean insert(K key, V value)throws NullPointerException{
		
		if(key==null)
			throw new NullPointerException("Invlaid key: null");
		
		StringBuilder str = new StringBuilder();
		str = str.append("Inserting <"+key+","+value+ ">; ");
		
		int i = hash(key);
		Entry<K,V> temp = new Entry<>(key, value);
		boolean unique=false;
		
		for(; ; i=(i+1)%size){
			if(table[i]==null){
				unique=insert(temp,i);
			}
			else if(table[i].value.equals(value) && table[i].key.equals(key)){
				unique = table[i].deleted() ? true : false;
				table[i].unDelete();
			}else{
				continue;
			}
			
			break;
		}
		
		str = str.append("Hashed to: " + i + "");
		if(unique)
			System.out.println(str.toString());
		
		count+= unique? 1:0;
		if(count>=size/2)
			rehash();
		
		return unique;
	}
	
	private boolean insert(Entry<K,V> e, int index){
		table[index]=e;
		return true;
	}
	
	public V find(K key){
		int index = getProbingLocation(key);
		return index==-1 ? null : table[index].value;
	}
	
	public boolean delete(K key){
		int index=getProbingLocation(key);
		if(index==-1)
			return false;
		
		if(table[index]!=null){
			table[index].delete();
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void rehash(){
		System.out.println("REHASHING THE TABLE");
		
		count=0;
		size*=2;
		Entry<K,V> temp[];
		temp = new Entry[size];

		for(int i=0; i<size/2; i++){
			if(table[i]!=null && !table[i].deleted()){
				
				for(int j=hash(table[i].key);; j=(j+1)%size){
					if(temp[j]!=null)
						continue;
						
					temp[j]=table[i];
					count++;
					break;
				}
				
			}
		}
		
		table=temp;
		temp=null;
	}
	
	private int hash(K key){
		int hash=0;
		String str = key.toString();
		byte[] bytes = str.getBytes();
		
		for(int i: bytes)
			hash += (37*i + hash)>>i;

		hash%=size;
		
		return hash;
	}
	
	public int getHashLocation(K key){
		int index = hash(key);
		return (table[index]!=null && (table[index].key.equals(key))) ? index : -1;
	}
	
	public int getProbingLocation(K key){

		int index = getHashLocation(key);
		if(index != -1)
			return index;

		index = hash(key);
		
		for(; table[index]!=null; index=(index+1)%size){
			if(table[index].key==key)
				return index;
		}
		
		
		return -1;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		str = str.append( "INDEX\tVALUE\t   KEY\t\t\t SIZE:"+ size + "  VALUES:"+ count + "\n" );
		
		for(int i=0; i<size; i++){
			str = str.append(i+"\t");
			str = table[i]==null ? str.append("null") :str.append( table[i].value + "\t   " + table[i].key);
			str = str.append("\n");
		}
			
		return str.toString();
	}
	
	// Table that stores people's favorite colors using the first name as key and the color as value
	public static void main(String[] args) throws FileNotFoundException {
		
		// Create a table with the default starting size being 16
		LinearProbingHashTable<String, String> hashTable = new LinearProbingHashTable<>();
		Scanner line = new Scanner(new FileReader("import.txt"));

		// Insert 6 values from 'import.txt' in the zip file
		for(int i=0; i<6; i++)
			try{
				hashTable.insert(line.next(), line.next());
			}
			catch (NullPointerException e){
				System.out.println(e.getMessage());
			}
			
		line.close();
		
		// Outputting the hash table
		System.out.println("\n"+hashTable.toString());
	
		Scanner input = new Scanner(System.in);
		
		// Testing insert
		System.out.print("Would you like insert a custom value? (y/n)");
		
		char c = input.nextLine().charAt(0);
		
		String newKey = "Steve";
		String newVal = "Maroon";
		String yes = "default";
				
		if(c=='y'||c=='Y'){
			System.out.print("Enter a name(key): ");
			newKey = input.nextLine();
			
			System.out.print("Enter a color(value): ");
			newVal = input.nextLine();
			
			yes = "user inputted";
		}
		input.close();
		
		System.out.print("\nInserting " + yes + " entry of name(key) '" + newKey + "' of color(value) '" + newVal + "'...\n");
		try{
			hashTable.insert(newVal, newKey);
		}
		catch (NullPointerException e){
			System.out.println(e.getMessage());
		}
		
		// Outputting the hash table
		System.out.println(hashTable.toString());
		
		// Deleting a value
		System.out.print("Deleting the index for John...");
		hashTable.delete("John");
		
		// Outputting the hash table.
		System.out.print(" The value is marked as deleted but will still appear when toString() is called.\n");
		System.out.println(hashTable.toString());
		
		System.out.println("\nLet's insert another to test if the table will rehash when it fills 8 of its 16 indexes.");
		System.out.println("We're also testing if the deleted value will be gone next time the toString() function is called.");
		System.out.println("Inserting key 'Jack' with the value 'White'...");
		hashTable.insert("Jack", "White");
		System.out.print(hashTable.toString());
		
		System.out.print("The deleted value is gone and now the table is of size 32\n\n");
		
		System.out.println("Testing what happens when an object is inserted that has the same key but different value...");
		System.out.println("Inserting key 'Jack' with the value 'Black'...");
		hashTable.insert("Jack", "Black");
		
		System.out.print(hashTable.toString());
		System.out.println("The system tests if both value AND key are identical. Otherwise it probes as normal until it reaches an empty index.");
		
		System.out.println("\n\nFinding the value for key 'Jack': " + hashTable.find("Jack"));
		System.out.println("It finds the first instance of that key and returns the value");
		
		System.out.println("\n\nTesting the getHashLocation and getProbingLocation functions:");
		System.out.println("The hash location of key 'Sophia' is " + hashTable.getHashLocation("Sophia"));
		System.out.println("The probing location of key 'Sophia' is " + hashTable.getProbingLocation("Sophia"));

		System.out.println("\n\nTesting if duplicate will insert. Using key Gian with value Green.");
		System.out.println("Insert?: " + hashTable.insert("Gian", "Green"));
		
		System.out.println("\n\nForcing an insert to probe.");
		hashTable.insert("Poshia", "Blue");
		System.out.println(hashTable.toString());
		System.out.println("Testing the getHashLocation and getProbingLocation functions:");
		System.out.println("The hash location of key 'Poshia' is " + hashTable.getHashLocation("Poshia"));
		System.out.println("The probing location of key 'Poshia' is " + hashTable.getProbingLocation("Poshia"));
		
		System.out.println("Since it was probed, hashlocation function doesn't find it. Only probing does.");
		
		
	}

}
