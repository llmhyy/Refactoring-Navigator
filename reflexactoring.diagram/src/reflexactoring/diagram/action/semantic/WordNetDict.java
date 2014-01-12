package reflexactoring.diagram.action.semantic;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import reflexactoring.diagram.util.ReflexactoringUtil;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.SimpleStemmer;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * This class is used for singleton pattern.
 * @author linyun
 *
 */

public class WordNetDict 
{
	private IDictionary wordNetDict = null;
	private static WordNetDict instance = null;
	private WordnetStemmer wmStemmer = null;

	private WordNetDict()
	{
		initial();
		wordNetDict.open();
	}

	public static WordNetDict getInstance()
	{
		if (instance == null) instance = new WordNetDict();
		return instance;
	}

	public void closeDictionary()
	{
		wordNetDict.close();
	}

	public void initial()
	{
		
		String defaultPath = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
		String path = ReflexactoringUtil.getDictPath();
		path = (path == null || path.trim() == ""? defaultPath : path);
		URL url = null;
		try
		{
			url = new URL("file", null, path);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		wordNetDict = new Dictionary(url);
		wmStemmer = new WordnetStemmer(wordNetDict);

	}

	//get a word just as nouns or verbs   add by 2011-11-28
	
	public String getNounsOrVerbs(String word)
	{
		String resultword=null;		
		IIndexWord idxWord = wordNetDict.getIndexWord(word, POS.NOUN);
		if (idxWord == null)
		{
			idxWord = wordNetDict.getIndexWord(word, POS.VERB);
		}
		if (idxWord != null)
		{
			IWordID wordID = (IWordID) idxWord.getWordIDs().get(0);
			IWord iWord = wordNetDict.getWord(wordID);
		    resultword=iWord.getLemma().toString();
		 //   System.out.println("the word is : <"+ word + "> and it's nouns or verbs are : " + resultword);
		}
		return resultword;
	}
	
	// get a stem of a word 
	
	public String getStem(String word)
	{
		String resultword;
		WordnetStemmer wnStemmer = new WordnetStemmer(wordNetDict);

		if (wnStemmer.findStems(word).size() > 0) 
		{		
			resultword= wnStemmer.findStems(word).get(0);
			//System.out.println("This is : "+word +"  stem=>"+ resultword );
			return resultword;
		}
		else{
			return word;
		}

	}
	
	public boolean isCorrectWord(String word)
	{
		 
		return wmStemmer.findStems(word).size() >0;
	}

	/**
	 * a reference class, which is copied from previous member is FDSE lab
	 * @param isDeep
	 * @param words
	 * @param syntaxRelatedWords
	 * @param semanticRelatedWords
	 */
	public void getRelatedWords(boolean isDeep, ArrayList<String> words,
			HashSet<String> syntaxRelatedWords,
			HashSet<String> semanticRelatedWords)
	{
		
		for (String word : words)
		{
			IIndexWord idxWord = wordNetDict.getIndexWord(word, POS.NOUN);
			if (idxWord == null)
			{
				idxWord = wordNetDict.getIndexWord(word, POS.VERB);
				if (idxWord == null)
				{
					idxWord = wordNetDict.getIndexWord(word, POS.ADJECTIVE);
					if (idxWord == null)
					{
						idxWord = wordNetDict.getIndexWord(word, POS.ADVERB);
					}
				}
			}
			if (idxWord != null)
			{
				IWordID wordID = (IWordID) idxWord.getWordIDs().get(0);
				IWord iWord = wordNetDict.getWord(wordID);

				List<IWordID> wl = iWord.getRelatedWords();
				for (IWordID iw : wl)
				{
					IWord wd = wordNetDict.getWord(iw);
					syntaxRelatedWords.add(wd.getLemma());
				}

				for (IWord iw : iWord.getSynset().getWords())
				{
					semanticRelatedWords.add(iw.getLemma());
				}

				if (isDeep)
				{
					List<ISynsetID> ssl = iWord.getSynset().getRelatedSynsets();
					for (ISynsetID ssid : ssl)
					{
						ISynset ss = wordNetDict.getSynset(ssid);
						for (IWord iw : ss.getWords())
						{
							semanticRelatedWords.add(iw.getLemma());
						}
					}
				}

			}

		}
	}

}
