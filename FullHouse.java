/**
 * This class is used to model a hand of FullHouse in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class FullHouse extends Hand {
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return topCard The top card of this hand.
	 */
	public Card getTopCard() {
		Card topCard = null;
		
		// the case when the triplet locates at the left hand side
		if (getCard(0).rank == getCard(1).rank && getCard(0).rank == getCard(2).rank) {
			topCard = getCard(0);
			// find the card with the highest suit
			for (int i = 1; i < 3; i++) {
				if (topCard.suit < getCard(i).suit) {
					topCard = getCard(i);
				}
			}
		// the case when the triplet locates at the right hand side
		} else {
			topCard = getCard(2);
			// find the card with the highest suit
			for (int i = 3; i < 5; i++) {
				if (topCard.suit < getCard(i).suit) {
					topCard = getCard(i);
				}
			}
		}
		
		return topCard;
	}
	
	
	/**
	 * This method checks if this is a valid hand.
	 * 
	 * @return valid Whether this hand is a valid hand or not.
	 */
	public boolean isValid() {
		boolean valid;
		
		// check whether it is a five-cards hand
		if (size() == 5) {
			// one of the full house pattern : AAABB
			if (getCard(0).rank == getCard(1).rank && getCard(0).rank == getCard(2).rank && getCard(3).rank == getCard(4).rank) {
				valid = true;
			// another full house pattern : AABBB
			} else if (getCard(0).rank == getCard(1).rank && getCard(2).rank == getCard(3).rank && getCard(2).rank == getCard(4).rank) {
				valid = true;
			} else {
				valid = false;
			}
		} else {
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "FullHouse" The name of this class as a String object.
	 */
	public String getType() {
		return "FullHouse";
	}
}
