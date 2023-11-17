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
		// Initialize token array
		tokens = new String[TOKENS_SIZE];
		
		// Initialize Simple Browser
		render = new SimpleHtmlRenderer();
		browser = render.getHtmlPrinter();
		HTMLutils = new HTMLUtilities();
		input = "";
	}
	
	
	public static void main(String[] args) {
		HTMLRender hf = new HTMLRender();
		// Read input file
		try {
			File HTMLIn = new File(args[0]);
			Scanner reader = new Scanner(HTMLIn);
			while (reader.hasNextLine()) {
				hf.input += reader.nextLine();
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found");
		}
		// Tokenize input
		System.out.print(hf.tokens.toString());
		hf.tokens = hf.HTMLutils.tokenizeHTMLString(hf.input);
		System.out.print(hf.tokens.toString());
		hf.run();
	}
	
	public void run() {
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
		
	}
	
	
}