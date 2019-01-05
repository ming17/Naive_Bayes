import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class NB_Runner {
	
	//Define constant array of all topics and names
	public static final Topic[] TOPICS = {
		new Topic("comp.graphics"), new Topic("comp.os.ms-windows.misc"), new Topic("comp.sys.ibm.pc.hardware"), 
		new Topic("comp.sys.mac.hardware"), new Topic("comp.windows.x"), new Topic("misc.forsale"), 
		new Topic("rec.autos"), new Topic("rec.motorcycles"), new Topic("rec.sport.baseball"), 
		new Topic("rec.sport.hockey"), new Topic("soc.religion.christian"), new Topic("talk.politics.guns"), 
		new Topic("talk.politics.mideast"), new Topic("talk.politics.misc"), new Topic("talk.religion.misc"), 
		new Topic("alt.atheism"), new Topic("sci.space"), new Topic("sci.crypt"), new Topic("sci.electronics"), new Topic("sci.med") };
	
	public static final int NUM_TRAIN_DOCS = 500;
	public static final int NUM_TEST_DOCS = 500;
	
	public static int totalTrainDocs = 0;
	
	public static void main(String[] args) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("processed_data.txt"));
		File folder;
		File[] listOfFiles;
		int totalTestDocs = 0;
		int numCorrect = 0;
		
		//Training Data to get word counts for half of all documents
		for(Topic t : TOPICS)
		{
			folder = new File("20_newsgroups/" + t.getName());
			listOfFiles = folder.listFiles();
			
			for(int i = 0; i < NUM_TRAIN_DOCS; i++)
			{
				t.addDoc(listOfFiles[i]);
				totalTrainDocs++;
			}
			//Sort each topic's word list in alphabetical order so that it can be searched with binarySearch
			t.sort();
			
			//DEBUGGING : print word lists
			writer.write(t.toString());
		}
		
		//Testing Data to find accuracy
		for(Topic t : TOPICS)
		{
			folder = new File("20_newsgroups/" + t.getName());
			listOfFiles = folder.listFiles();
			
			writer.write("Testing Documents from topic " + t.getName() + ":\n");
			
			for(int i = NUM_TRAIN_DOCS; i < NUM_TRAIN_DOCS+NUM_TEST_DOCS; i++)
			{
				//tests document to see which newsgroup it is categorized under and returns true if accurate
				if(testDoc(listOfFiles[i], t.getName(), writer))
					numCorrect++;
				totalTestDocs++;
			}
			
			writer.write("\n\n");
			System.out.println(NUM_TEST_DOCS + " more documents tested");
		}
		
		System.out.println("The system has an accuracy of " + (100 * numCorrect/totalTestDocs) + "%");
		writer.close();
	}
	
	public static boolean testDoc(File f, String correctTopic, BufferedWriter writer) throws IOException
	{
		double[] runningProds = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		double predictedProd = Double.POSITIVE_INFINITY;
		String predictedTopic = "";
		String temp;
		double val;
		
		Scanner scanner = new Scanner(f);
		scanner.useDelimiter(" |,|!|\\|/|\\.|@|_|\\n");
				
		//Set all products to P(Y)
		for(int i = 0; i < 20; i++)
		{
			runningProds[i] *= (TOPICS[i].getNumDocs()/(double)totalTrainDocs);
			//System.out.print("prod " + i + ": " + runningProds[i] + "\t");
		}
		//System.out.println();
		
		//parse through document and calculate probabilities of finding each word in a document of each topic
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
				}
				break;
			}
			
			temp = temp.replaceAll("[^a-z]", "");
			
			//only update probabilities if temp isn't empty string
			if(!temp.equals("") && !temp.equals("'"))
			{
				for(int i = 0; i < 20; i++)
				{
					val = TOPICS[i].contains(temp);
					//Adjusts product values to be lower by a factor of 10^200 if any of them would reach infinity
					if (runningProds[i] * val == Double.POSITIVE_INFINITY)
					{
						//DEBUGGING : Infinity Checks
						//System.out.println("DEALING WITH INFINITY\n\n\n");
						for(int j = 0; j < 20; j++)
						{
							runningProds[j] /= Math.pow(10, 200);
						}
					}
					runningProds[i] *= val;
				}
			}
		}
		
		//find the highest product to find the predicted value
		for(int i = 0; i < 20; i++)
		{
			if(runningProds[i] < predictedProd)
			{
				predictedProd = runningProds[i];
				predictedTopic = TOPICS[i].getName();
			}
			//System.out.print("prod " + i + ": " + runningProds[i] + "\t");
		}
		//System.out.println();
		
		scanner.close();
		
		//Returns true if predicted topic correctly
		if(predictedTopic.equals(correctTopic))
		{
			writer.write("Successfully classified " + f.toString().substring(14) + " as document of type " + correctTopic + "\n");
			return true;
		}
		else
		{
			writer.write("Classified " + f.toString().substring(14) + " as document of type " + predictedTopic + " when it should be " + correctTopic + "\n");
			return false;
		}
	}
	
}
