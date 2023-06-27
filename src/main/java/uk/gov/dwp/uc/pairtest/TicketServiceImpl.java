package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private static final int MAX_TICKETS = 20;

    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    private void validateAccountId(Long accountId){
        if(accountId == null || accountId <= 0){
            throw new InvalidPurchaseException();
        }
    }

    private void validatePurchase(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException{
        if(ticketTypeRequests == null || ticketTypeRequests.length == 0 ){
            throw new InvalidPurchaseException();
        }
    }

    private void validateTicket(TicketTypeRequest request) {
        if(request == null){
            throw new InvalidPurchaseException();
        }

        if(request.getTicketType() == null){
            throw new InvalidPurchaseException();
        }

        if(request.getNoOfTickets() <= 0){
            throw new InvalidPurchaseException();
        }
    }

    private boolean isSeatsRequired(TicketType ticketType) {
        return ticketType != TicketType.INFANT;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

    }

}
