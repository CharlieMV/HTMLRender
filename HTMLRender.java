/**
 *	HTMLRender
 *	This program renders HTML code into a JFrame window.
 *	It requires your HTMLUtilities class and
 *	the SimpleHtmlRenderer and HtmlPrinter classes.
 * 
 * 	javac -cp SimpleHtmlRenderer.jar *.java
 *  java -cp ".;SimpleHtmlRenderer.jar" HTMLRender
 *
 *	The tags supported:
 *		<html>, </html> - start/end of the HTML file
 *		<body>, </body> - start/end of the HTML code
 *		<p>, </p> - Start/end of a paragraph.
 *					Causes a newline before and a blank line after. Lines are restricted
 *					to 80 characters maximum.
 *		<hr>	- Creates a horizontal rule on the following line.
 *		<br>	- newline (break)
 *		<b>, </b> - Start/end of bold font print
 *		<i>, </i> - Start/end of italic font print
 *		<q>, </q> - Start/end of quotations
 *		<hX>, </hX> - Start/end of heading with size X = 1, 2, 3, 4, 5, 6
 *		<pre>, </pre> - Preformatted text
 *
 *	@author		Charles Chang
 *	@version	November 16, 2023
 */
import java.io.*;
import java.util.*;
 
public class HTMLRender {
	
	// Enum for how to print words
	private enum modifiers {
		NONE, BOLD, ITALICS, PRE, H1, H2, H3, H4, H5, H6
	}
	private modifiers mod;
	
	// Character count to make sure a line never goes over 80 characters
	private int charCount;
	/* Token Count to keep track of what part of the tokens array has
	 * been read */
	private int tokenCount;
	// Filename to read
	private String fileName;
	
	// String containing the entire file as input for HTMLUtils
	private String input;
	
	// SimpleHtmlRenderer fields
	private SimpleHtmlRenderer render;
	private HtmlPrinter browser;
	
	// HTMLUtilities fields
	private HTMLUtilities HTMLutils;
	
		
	public HTMLRender() {
		// Initialize Simple Browser
		render = new SimpleHtmlRenderer();
		browser = render.getHtmlPrinter();
		HTMLutils = new HTMLUtilities();
		input = "";
		// Initiate formatting vars
		mod = modifiers.NONE;
		charCount = 0;
		tokenCount = 0;
		fileName = "";
	}
	
	
	public static void main(String[] args) {
		HTMLRender hf = new HTMLRender();
		hf.fileName = args[0];
		// Run
		hf.run();
	}
	
	public void run() {
		// Is the token a tag
		boolean isTag = false;
		boolean isPunct = false;
		charCount = 0;
		// Read input file line by line
		try {
			File HTMLIn = new File(fileName);
			Scanner reader = new Scanner(HTMLIn);
			while (reader.hasNextLine()) {
				// Read line
				input = reader.nextLine();
				// Tokenize line
				String[] tokens = HTMLutils.tokenizeHTMLString(input);
				// Print stuff from tokens
				for (int i = 0; i < tokens.length; i++) {
					if (tokens[i] != null) {
						/* Check for <html> and <body> we can ignore these since
						 * code is assumed to be syntactically correct*/
						if (isNotImportant(tokens[i]))	isTag = true;
						// Account for bold, italics, and headers
						if (checkModifiers(tokens[i]))	isTag = true;
						// Check if the next token is a singular punctuation
						if (HTMLCheckPunct(tokens, i))	isPunct = true;
						// Print the token if it is not a tag
						HTMLPrintTokens(tokens[i], isTag, isPunct);
						isTag = false;
						isPunct = false;
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found");
		}
	}
	
	/**
	 *	Check whether token is <html> </html> <body> or </body>
	 * 	Code can ignore this because syntax is assumed to be correct
	 * 	
	 * 	@param 	String	the current token
	 * 	@return	boolean true if those tags present false otherwise
	 */
	private boolean isNotImportant (String token) {
		switch (token.toLowerCase()) {
			case "<html>":	case "</html>":	case "<body>":	case "</body>":
			return true;
			default:	return false;
		}
	}
	
	/**
	 * 	Check if token is <b>, <i>, <h#>, or any of their closing tags
	 * 
	 * 	@param	String	token to check the tag of
	 * 	@return boolean	was it a bold, italics, or header
	 */
	private boolean checkModifiers (String token) {
		switch (token.toLowerCase()) {
			// Check for bolds and headers
			case "<b>":		mod = modifiers.BOLD;		return true;
			case "</b>":	mod = modifiers.NONE;		return true;
			case "<i>":		mod = modifiers.ITALICS;	return true;
			case "</i>":	mod = modifiers.NONE;		return true;
			case "<pre>":	mod = modifiers.PRE;		return true;
			case "</pre>":	mod = modifiers.NONE;		return true;
			case "<h1>":	mod = modifiers.H1;			return true;
			case "<h2>":	mod = modifiers.H2;			return true;
			case "<h3>":	mod = modifiers.H3;			return true;
			case "<h4>":	mod = modifiers.H4;			return true;
			case "<h5>":	mod = modifiers.H5;		return true;
			case "<h6>":	mod = modifiers.H6;		return true;
			case "</h1>":	case "</h2>":	case "</h3>":	case "</h4>":
			case "</h5>":	case "</h6>":	mod = modifiers.NONE;
			return true;
		};
		return false;
	}
	
	/**
	 * Check if the next String in array is a char
	 * If the next String is a tag, check the one after that
	 * 
	 * @param String[]	tokens to check
	 * @param int		where along tokens we are
	 * @return boolean	true if the next non-tag token is a punctuation
	 */
	private boolean HTMLCheckPunct (String[] tokens, int index) {
		/* Check the next index */
		int i = index + 1;
		boolean isTag = true;
		/* if the next token is a tag, check the one after that
		 * keep repeating until there is a token that is not a tag */
		while (isTag) {
			if (tokens[i] == null) return false;
			/* We only want to use the method to check, not change mod,
			 * so have a temp and change mod back*/
			modifiers temp = mod;
			if (tokens[i].length() != 1 && !checkModifiers(tokens[i]))
				return false;
			mod = temp;
			switch (tokens[i].charAt(0)) {
				case '.': case ',': case ';': case ':': case '(': case ')':
				case '?': case '!': case '=': case '&': case '~': case '+':
				return true; 
			}
			i ++;
		}
		return false;
	}
	
	/**
	 * Print the given token with tht given modification
	 * 
	 * @param String	token to print
	 * @param boolean	is the token a tag
	 * @param boolean	is the token a punctuation char
	 */
	private void HTMLPrintTokens (String token, boolean isTag, boolean isPunct) {
		// Check for </p>, println if it is
		// Check for <hr>, print horizontal rule if it is
		// Check for <br>, print page break if it is
		// Check for <q> or </q>, print quotes
		switch (token.toLowerCase()) {
			case "<p>":	browser.println();	case "</p>":	browser.println();	
			isTag = true;	charCount = 0;	break;
			case "<hr>":	browser.printHorizontalRule();	charCount = 0;
			isTag = true;	break;
			case "<br>":	browser.printBreak(); charCount = 0;	
			isTag = true;	break;
			case "<q>":	case "</q>":	browser.print("\"");	charCount++;
			isTag = true;	break;
		}
		// Print token
		if (!isTag) {
			int addChar = 0;
			addChar = tokenLength(token.length());
			if (charCount + addChar > 2400) {
				browser.println();
				charCount = 0;
			}
			charCount += addChar;
			switch (mod) {
				case NONE: browser.print(token);			break;
				case BOLD: browser.printBold(token);		break;
				case ITALICS: browser.printItalic(token);	break;
				case H1: browser.printHeading1(token);		break;
				case H2: browser.printHeading2(token);		break;
				case H3: browser.printHeading3(token);		break;
				case H4: browser.printHeading4(token);		break;
				case H5: browser.printHeading5(token);		break;
				case H6: browser.printHeading6(token);		break;
				case PRE:browser.print(token);	
				browser.println();							break;
			}
		}
		// Add a space if token printed is not punctuation or preformatted
		if (!isPunct && mod != modifiers.PRE && !isTag) {
			browser.print(" ");
			charCount += tokenLength(1);
		}
	}
	
	/**
	 * Calculate token length based off current modifiers
	 * 
	 * @param int token length
	 * @return line length to add
	 */
	private int tokenLength (int tokenChars) {
		switch (mod) {
			// Length 80; 2400/80 = 30
			case NONE: 	case BOLD:	case ITALICS:	case H4:	default:
			return 30 * tokenChars;
			// Length 40; 2400/40 = 60
			case H1: return 60 * tokenChars;
			// Length 50; 2400/50 = 48
			case H2: return 48 * tokenChars;
			// Length 60; 2400/60 = 40
			case H3: return 40 * tokenChars;
			// Length 100; 2400/100 = 24
			case H5: return 24 * tokenChars;
			// Length 120; 2400/120 = 20
			case H6: return 20 * tokenChars;
			// PRE doesn't care
		}
	}
}
