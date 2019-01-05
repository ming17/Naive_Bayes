import java.util.Scanner;
import java.io.*;

public class Topic {
	private int numDocs;
	private Words w;
	private String name;
	
	public Topic(String n)
	{
		name = n;
		w = new Words();
		numDocs = 0;
	}
	
	public double contains(String word)
	{
		return w.contains(word);
	}
	
	//Adds document and all its words, updating w accordingly
	public void addDoc(File f) throws IOException
	{
		numDocs++;
		
		Scanner scanner = new Scanner(f);
		scanner.useDelimiter(" |,|!|\\|/|@|\\.|_|\\n");
		
		String temp;
		
		while(scanner.hasNext())
		{
			temp = scanner.next();
			temp = temp.toLowerCase();
			
			//Check token to see if should be ignored/skipped
			switch(temp)
			{
			//if any of these cases, skip and move to the next token
			case "xref:":
			case "path:":
			case "newsgroups:":
			case "subject:":
			case "message-id:":
			case "article-i:":
			case "organization:":
				if(scanner.hasNext())
				{
					temp = scanner.next();
					temp = temp.toLowerCase();
					temp = temp.replaceAll("[^a-z]", "");
					
					//only add word if it isn't the empty string
					if(!temp.equals("") && !temp.equals("'"))
						w.addWord(temp);
				}
				break;
				
			//if any of these cases, skip the entire line
			case "from:":
			case "date:":
			case "references:":
			case "nntp-posting-host:":
			case "lines:":
			case "reply-to:":
			case "sender:":
				scanner.nextLine();
				if(scanner.hasNext())
				{
					temp = scanner.next();
					temp = temp.toLowerCase();
					temp = temp.replaceAll("[^a-z]", "");
					
					//only add word if it isn't the empty string
					if(!temp.equals(""))
						w.addWord(temp);
				}
				break;
				
			default:
				temp = temp.replaceAll("[^a-z]", "");
				
				//only add word if it isn't the empty string
				if(!temp.equals(""))
					w.addWord(temp);
			}
		}
		
		scanner.close();
	}
	
	public String toString()
	{
		return ("Topic " + getName() + " word list\n" + w.toString() + "\n\n");
	}
	
	public void sort()
	{
		w.sort();
	}
	
	public int getNumDocs()
	{
		return numDocs;
	}
	
	public String getName()
	{
		return name;
	}
}
