package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public enum TicketType {
    ADULT,
    CHILD,
    INFANT;

    private static final double ADULT_PRICE = 20.0;
    private static final double CHILD_PRICE = 10.0;
    private static final double INFANT_PRICE = 0.0;

    public double getPrice() throws InvalidPurchaseException {
        return switch (this) {
            case ADULT -> ADULT_PRICE;
            case INFANT -> INFANT_PRICE;
            case CHILD -> CHILD_PRICE;
        };
    }
}
