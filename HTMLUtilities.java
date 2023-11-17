/**
 *	Utilities for handling HTML
 * 	Breaks down HTML code into tokens consisting of HTML tags, words, 
 * 	punctuation, and numbers.
 *
 *	@author	Charles Chang
 *	@since	October 31, 2023
 */
public class HTMLUtilities {
	/* the stat of the tokenizer. Is this a comment in or preformatted 
	 * text?
	 * NONE means normal
	 * COMMENT means comment
	 * PRE means preformatted */
	private enum tokenizerState {NONE, COMMENT, PRE};
	private tokenizerState state = tokenizerState.NONE;
	/**
	 *	Break the HTML string into tokens. The array returned is
	 *	exactly the size of the number of tokens in the HTML string.
	 *	Example:	HTML string = "Goodnight moon goodnight stars"
	 *				returns { "Goodnight", "moon", "goodnight", "stars" }
	 *	@param str			the HTML string
	 *	@return				the String array of tokens
	 */
	public String[] tokenizeHTMLString (String str) {
		// make the size of the array large to start
		String[] result = new String[10000];
		// where along the token array we are
		int tokenNum = 0;
		// keep track of str from last tag to counter
		String tempToken = "";
		// is the token complete?
		boolean isComplete = false;
		// where along str we are
		int counter = 0;
		// comment edge cases
		boolean isCommEdgeCase = false;
		
		// Check for pre
		// check to see if code is preformatted
			if (str.indexOf("<pre>") != -1) {
				state = tokenizerState.PRE;
			}
			// check for end of preformatting
			if (str.indexOf("</pre>") != -1) {
				state = tokenizerState.NONE;
			}
			if (state == tokenizerState.PRE) {
				result[0] = str;
				return result;
			}
		
		// scan all characters while scanning next for spaces
		while (counter < str.length()) {
			tempToken += str.charAt(counter);
			// is the token an html tag
			if (checkTag(tempToken)) isComplete = true;
			
			// check to see if a comment has started
			if (tempToken.trim().indexOf("<!--") == 0) { 
				state = tokenizerState.COMMENT;
			}
			// check to see of the comment has ended
			if (tempToken.trim().indexOf("-->") >= 0 && state == 
											tokenizerState.COMMENT) {
				isCommEdgeCase = true;
			}
			
			/* only check for the rest of the cases if tempToken doesnt 
			 * start with < and state is not COMMENT */
			else if (tempToken.charAt(0) != '<' && state != tokenizerState.COMMENT) {
				isComplete = isFullToken(str, counter, tempToken);
			}
			// check if it is the last char of str
			if (counter == str.length() - 1) isComplete = true;
			
			// if the token is valid, add it to result and clear temp
			if (isComplete) {
				/* do not count the token if it is not just a whitespace 
				 * or tokenizerState is COMMENT */
				if ((tempToken.length() != 1 || 
						!Character.isWhitespace(tempToken.charAt(0))) && 
						state != tokenizerState.COMMENT) {
					result[tokenNum] = tempToken.trim();
					tokenNum ++;
				}
				// Check edge cases
				if (isCommEdgeCase) {
					state = tokenizerState.NONE;
					isCommEdgeCase = false;
				}
				// Reset tempToken, so code can scan for next tag
				tempToken = "";
				isComplete = false;
			}
			counter ++;
		}
		// return the correctly sized array
		return result;
	}
	
	/**
	 *	Print the tokens in the array to the screen
	 *	Precondition: All elements in the array are valid String objects.
	 *				(no nulls)
	 *	@param tokens		an array of String tokens
	 */
	public void printTokens (String[] tokens) {
		if (tokens == null) return;
		for (int a = 0; a < tokens.length; a++) {
			if (tokens[a] != null) {
				if (a % 5 == 0) System.out.print("\n  ");
				System.out.print("[token " + a + "]: " + tokens[a] + " ");
			}
		}
		System.out.println();
	}
	
	/**
	 * Whether the given String starts and ends with < and > respectively
	 * 
	 * @param	token	token to check
	 * @return			boolean true for if token starts and ends with
	 * 					< and > respectively, false if not
	 */
	private boolean checkTag (String token) {
		// Check first and last char
		if (token.charAt(0) == '<' && token.charAt(token.length() - 1) == '>') {
			return true;
		}
		return false;
	}
	/**
	 * Checks to see if char is punctuation
	 * - and . treated differently, so not included here
	 * 
	 * @param 	c	character to check
	 * @return		boolean true if punctuation
	 */
	private boolean isPunctuation (char c) {
		switch (c) {
			case ',': case ';': case ':': case '(': case ')':
			case '?': case '!': case '=': case '&': case '~': case '+':
			return true; 
			default: return false;
		}
	}
	/**
	 * Checks for all possible tokens, and return whether to end token 
	 * or keep scanning
	 * 
	 * @param str			string to tokenize
	 * @param counter		int to keep track of wehre along the str we are
	 * @param temptoken		string from the last token to where counter
	 * @return				boolean true if there tempToken is a token
	 */
	private boolean isFullToken (String str, int counter, String tempToken) {
		/* keep as boolean as some cases may overlap, so a false might turn 
		 * into a true after considring other cases */
		boolean isComplete = false;
		/* check if hyphen is next, but not if letter before 
		 * and after or if before an int*/
		if (counter < str.length() - 1 && str.charAt(counter + 1) == '-'
					&& !Character.isLetterOrDigit(str.charAt(counter + 2)) 
					&& !Character.isLetter(str.charAt(counter)))
			isComplete = true;
		// check if . is between 2 numbers
		if (counter < str.length() - 2 && str.charAt(counter + 1) == '.' 
					&& Character.isDigit(str.charAt(counter)) 
					&& Character.isDigit(str.charAt(counter + 2)))
			isComplete = false;
		/* check if . is after a letter or num, but before a 
		 * non-letter/non-num */
		if (counter < str.length() - 2 && str.charAt(counter + 1) == '.' 
					&& Character.isLetterOrDigit(str.charAt(counter)) 
					&& !Character.isLetterOrDigit(str.charAt(counter + 2)))
			isComplete = true;
		if (counter == str.length() - 2 && str.charAt(counter + 1) == '.')
			isComplete = true;
		// check if next char is non-hyphen punctuation
		if (counter< str.length() - 1 && isPunctuation(str.charAt(counter + 1)))
			isComplete = true;
		// check if temp is a single punctuation
		if (tempToken.length() == 1 && isPunctuation(tempToken.charAt(0)))
			isComplete = true;
		// check if next char is a <
		if (counter < str.length() - 1 && str.charAt(counter + 1) == '<')
			isComplete = true;
		// check if space is next
		if (counter < str.length() - 1 && str.charAt(counter + 1) == ' ') 
			isComplete = true;
		// check if tab is next
		if (counter < str.length() - 1 && str.charAt(counter + 1) == '\t') 
			isComplete = true;
		// check to see if current char is a space
		if (tempToken.length() == 1 && tempToken.charAt(0) == ' ')
			isComplete = true;
		// check if current is - and .digit is next
		if (counter < str.length() - 3 && str.charAt(counter) == '-' &&
					str.charAt(counter + 1) == '.' && 
					Character.isDigit(str.charAt(counter + 2)))
			isComplete = false;
		return isComplete;
	}
}
