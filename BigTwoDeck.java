/**
 * This class is used to model a deck of cards used in a Big Two card game.
 * It is a subclass of the Deck class.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class BigTwoDeck extends Deck {
	
	/**
	 * This method initializes a deck of Big Two cards.
	 * 
	 */
	public void initialize() {
		// remove all cards from the deck
		removeAllCards();
		// create 52 Big Two cards and add them to the deck
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				Card bigTwoCard = new BigTwoCard(i, j);
				addCard(bigTwoCard);
			}
		}
	}
}
