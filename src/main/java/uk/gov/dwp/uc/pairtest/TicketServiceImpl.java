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
        validateAccountId(accountId);
        validatePurchase(ticketTypeRequests);

        int totalAdults = 0;
        int minRequiredAdults = 0;

        int totalTickets = 0;
        int totalSeats = 0;
        int totalCost = 0;

        for (TicketTypeRequest request : ticketTypeRequests){
            validateTicket(request);

            // Update totals
            totalTickets += request.getNoOfTickets();
            totalCost += request.getTotal();

            // Throw if max tickets exceeded
            if(totalTickets > MAX_TICKETS){
                throw new InvalidPurchaseException();
            }

            // Add seats if required
            if(isSeatsRequired(request.getTicketType())){
                totalSeats+= request.getNoOfTickets();
            }

            // Update totalAdults or minRequiredAdults for
            // future check to ensure there is a sufficient adults : none adults ratio
            if(request.getTicketType() == TicketType.ADULT){
                totalAdults+= request.getNoOfTickets();
            } else{
                minRequiredAdults+= request.getNoOfTickets();
            }
        }

        // Throw if there are less adults than children and infants
        if(totalAdults < minRequiredAdults){
            throw new InvalidPurchaseException();
        }

        // Reserve seats and make payment
        reservationService.reserveSeat(accountId, totalSeats);
        paymentService.makePayment(accountId, totalCost);
    }
}
