package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public final class TicketTypeRequest {
    private final int noOfTickets;
    private final TicketType type;

    public TicketTypeRequest(TicketType type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public TicketType getTicketType() {
        return type;
    }

    public double getTotal(){
        return this.type.getPrice() * this.noOfTickets;
    }
}
