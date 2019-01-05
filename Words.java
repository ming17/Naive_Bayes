import java.lang.Math;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Words {
	//List of Words in documents of this topic
	private ArrayList<Word> wordList;
	private int numWords;
	
	//Define comparator in order to use sort and search for an ArrayList of type Word
	private static Comparator<Word> c = new Comparator<Word>()
	{
		public int compare(Word w1, Word w2)
		{
			return w1.name.compareTo(w2.name);
		}
	};
	
	private class Word
	{
		private String name;
		private int count;
		
		private Word(String n)
		{
			name = n;
			count = 1;
		}
		
		private void add()
		{
			count++;
		}
	}
	
	public Words()
	{
		wordList = new ArrayList<Word>();
		numWords = 0;
	}
	
	//Return the logarithmic probability to avoid values that are too high
	public double contains(String w)
	{
		int index = Collections.binarySearch(wordList, new Word(w), c);
		if(index >= 0)
			return -1*Math.log((double)wordList.get(index).count/numWords);
		else
		{
			//if word not found, return 1
			return -1*Math.log(1.0/(numWords+1));
		}
	}
	
	public void addWord(String w)
	{
		//if empty string, don't add a word
		if(w.equals(""))
			return;
		
		numWords++;
		
		for(int i = 0; i < wordList.size(); i++)
		{
			if(wordList.get(i).name.equals(w))
			{
				wordList.get(i).add();
				return;
			}
		}
				
		//word wasn't found, so add new word
		wordList.add(new Word(w));
		return;
	}
	
	public void sort()
	{
		wordList.sort(c);
	}
	
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		for(int i = 0; i < wordList.size(); i++)
		{
			s.append(wordList.get(i).name + ": " + wordList.get(i).count + "\t");
			if(i%10 == 0)
				s.append("\n");
		}
		
		return s.toString();
	}
}
