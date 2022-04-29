/**
 * This class is a subclass of the Card class.
 * It is used to model a card used in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class BigTwoCard extends Card {
	
	/**
	 * This constructor builds a card with the specified suit and rank. 
	 * 
	 * @param suit It is an integer between 0 and 3.
	 * @param rank It is an integer between 0 and 12.
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * This method compares the order of this card with the specified card.
	 * 
	 * @return a negative integer, zero, or a positive integer as this card is less than, equal to, or greater than the specified card.
	 */
	public int compareTo(Card card) {
		int thisCardRank = this.rank;
		int specifiedCardRank = card.rank;
		
		// if hand rank is 0(A) or 1(2), then add 13, because 2 is the largest, A is the second largest
		if (thisCardRank == 1 || thisCardRank == 0) {
			thisCardRank+=13;
		}
		// if hand rank is 0(A) or 1(2), then add 13, because 2 is the largest, A is the second largest
		if (specifiedCardRank == 1 || specifiedCardRank == 0) {
			specifiedCardRank+=13;
		}
		
		// compare their order
		if (thisCardRank > specifiedCardRank) {
			return 1;
		} else if (thisCardRank < specifiedCardRank) {
			return -1;
		} else {
			// the case when their rank is same, then compare their suit
			if (this.suit > card.suit) {
				return 1;
			} else if (this.suit < card.suit) {
				return -1;
			} else {
				return 0;
			}
			
		}
		
		
	}
}
