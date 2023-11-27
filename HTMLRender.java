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
		NONE, BOLD, ITALICS, H1, H2, H3, H4, H5, H6
	}
	private modifiers mod;
	
	// Character count to make sure a line never goes over 80 characters
	private int charCount;
	/* Token Count to keep track of what part of the tokens array has
	 * been read */
	private int tokenCount;
	// Filename to read
	private String fileName;
	
	
	// the array holding all the tokens of the HTML file
	private String [] tokens;
	private final int TOKENS_SIZE = 100000;	// size of array
	
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
		tokens = readTokenize(fileName);
		HTMLutils.printTokens(tokens);
		// Is the token a tag
		boolean isTag = false;
		// Read token by token
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] != null) {
				/* Check for <html> and <body> we can ignore these since
				 * code is assumed to be syntactically correct*/
				if (isNotImportant(tokens[i])) i++;
				isTag = checkModifiers(tokens[i]);
				// Check for </p>, println if it is
				// Check for <hr>, print horizontal rule if it is
				// Check for <br>, print page break if it is
				// Check for <q> or </q>, print quotes
				switch (tokens[i]) {
					case "</p>":	browser.println();	isTag = true;	
					break;
					case "<hr>":	borwser.printHorizontalRule(); 
					isTag = true;	break;
					case "<br>":	browser.printBreak(); isTag = true;	
					break;
					case "<q>":	case "</q>":	browser.print("\""); 
					isTag = true;	break;
				}
			}
		}
		
		/*
		// Sample renderings from HtmlPrinter class
		
		// Print plain text without line feed at end
		browser.print("First line");
		
		// Print line feed
		browser.println();
		
		// Print bold words and plain space without line feed at end
		browser.printBold("bold words");
		browser.print(" ");
		
		// Print italic words without line feed at end
		browser.printItalic("italic words");
		
		// Print horizontal rule across window (includes line feed before and after)
		browser.printHorizontalRule();
		
		// Print words, then line feed (printBreak)
		browser.print("A couple of words");
		browser.printBreak();
		browser.printBreak();
		
		// Print a double quote
		browser.print("\"");
		
		// Print Headings 1 through 6 (Largest to smallest)
		browser.printHeading1("Heading1");
		browser.printHeading2("Heading2");
		browser.printHeading3("Heading3");
		browser.printHeading4("Heading4");
		browser.printHeading5("Heading5");
		browser.printHeading6("Heading6");
		
		// Print pre-formatted text (optional)
		browser.printPreformattedText("Preformat Monospace\tfont");
		browser.printBreak();
		browser.print("The end");
		*/
	}
	/**
	 * 	Read and tokenize file
	 * 	
	 * 	@param	String 	file name
	 * 	@return String[] tokenized list
	 */
	public String[] readTokenize (String name) {
		// Read input file
		try {
			File HTMLIn = new File(name);
			Scanner reader = new Scanner(HTMLIn);
			while (reader.hasNextLine()) {
				input += reader.nextLine() + " ";
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found");
		}
		// Tokenize input
		tokens = HTMLutils.tokenizeHTMLString(input);
		HTMLutils.printTokens(tokens);
		return tokens;
	}
	
	/**
	 *	Check whether token is <html> </html> <body> or </body>
	 * 	Code can ignore this because syntax is assumed to be correct
	 * 	
	 * 	@param 	String	the current token
	 * 	@return	boolean true if those tags present false otherwise
	 */
	public boolean isNotImportant (String token) {
		switch (token) {
			case "<html>":	case "</html>":	case "<body>":	case "</body>":
			return true;
			default:	return false;
		}
		/*if (token.length() < 5) return false;
		if (token.charAt(0) != '<' || token.charAt(token.length() - 1) != '>')
			return false;
		if (token.indexOf("html") != -1) return true;
		if (token.indexOf("body") != -1) return true;
		return false;
		*/
	}
	
	/**
	 * 	Check if token is <b>, <i>, <h#>, or any of their closing tags
	 * 
	 * 	@param	String	token to check the tag of
	 * 	@return boolean	was it a bold, italics, or header
	 */
	public boolean checkModifiers (String token) {
		switch (token) {
			case "<b>":		isB = true;		return true;
			case "</b>":	isB = false;	return true;
			case "<i>":		isI = true;		return true;
			case "</i>":	isI = false;	return true;
			case "<h1>":	isH = true;		headerSize = 1;		return true;
			case "<h2>":	isH = true;		headerSize = 2;		return true;
			case "<h3>":	isH = true;		headerSize = 3;		return true;
			case "<h4>":	isH = true;		headerSize = 4;		return true;
			case "<h5>":	isH = true;		headerSize = 5;		return true;
			case "<h6>":	isH = true;		headerSize = 6;		return true;
			case "</h1>":	case "</h2>":	case "</h3>":	case "</h4>":
			case "</h5>":	case "</h6>":	isH = false; headerSize = -1;
			return true;
		};
		return false;
	}
}
