package com.jitterted.ebp.blackjack;

public class ConsoleHand {
    static String displayFirstCard(Hand hand) {
        return ConsoleCard.display(hand.dealerFaceUpCard());
    }
}
