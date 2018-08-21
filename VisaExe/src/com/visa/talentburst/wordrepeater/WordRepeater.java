package com.visa.talentburst.wordrepeater;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * 
 * @author sugan
 *
 */
public class WordRepeater {

	// space, tab, comma (,), colon (:), semicolon (;), dash (-), and period (.).
	private static final String delimeters = "- ,;//.:\t";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String sentence = scanner.nextLine();
		String repeatedWord = firstRepeatedWord(sentence, delimeters);
		System.out.println(repeatedWord);
		scanner.close();
	}

	private static String firstRepeatedWord(String sentence, String delimeters) {
		List<String> tokenizedString = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(sentence, delimeters);
		String foundWord = "none found";
		while (st.hasMoreTokens()) {
			tokenizedString.add(st.nextToken());
		}
		for (String word : tokenizedString) {
			//JAVA 8 Feature
			List<String> repeatedWord = tokenizedString.stream().filter(wordInSentence -> wordInSentence.equals(word))
					.collect(Collectors.toList());
			if (repeatedWord != null && repeatedWord.size() > 1) {
				foundWord = repeatedWord.get(0);
				//If the List is greater than 1 which means the word is repeated, so break the loop
				break;
			}
		}
		return foundWord;
	}

}
