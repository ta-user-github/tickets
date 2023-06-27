package tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.*;

import uk.gov.dwp.uc.pairtest.TicketServiceImpl;

public class TicketServiceImplTest {
    private TicketServiceImpl ticketService;

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    @Test
    public void testPurchaseTickets_ValidPurchase() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 2),
                new TicketTypeRequest(TicketType.CHILD, 1)
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 50 payment 2 * 20 + 1 * 10
        // 3 seats 2 + 1
        verify(paymentService, times(1)).makePayment(accountId, 50);
        verify(reservationService, times(1)).reserveSeat(accountId, 3);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_InvalidAccountId0() throws InvalidPurchaseException {
        // Create test data
        Long accountId = Long.valueOf(0);
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to account id being 0
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_InvalidAccountIdMinus() throws InvalidPurchaseException {
        // Create test data
        Long accountId = Long.valueOf(-1);
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to account id being minus
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_InvalidAccountIdNull() throws InvalidPurchaseException {
        // Create test data
        Long accountId = null;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to account id being null
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_InsufficientAdultsWithChild() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
                new TicketTypeRequest(TicketType.CHILD, 2)
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to there being less adults than children
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void testPurchaseTickets_Valid_SufficientAdultsWithChildren() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 2),
                new TicketTypeRequest(TicketType.CHILD, 2)
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 60 payment 2 * 20 + 2 * 10
        // 4 seats 2 + 2
        verify(paymentService, times(1)).makePayment(accountId, 60);
        verify(reservationService, times(1)).reserveSeat(accountId, 4);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_Invalid_InsufficientSeatsWithInfants() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
                new TicketTypeRequest(TicketType.INFANT, 2)
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to there being less adults than infants
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void testPurchaseTickets_Valid_SufficientAdultsWithInfants() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 2),
                new TicketTypeRequest(TicketType.INFANT, 2)
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 40 payment 2 * 20 + 2 * 0
        // 2 seats 2 + 0
        verify(paymentService, times(1)).makePayment(accountId, 40);
        verify(reservationService, times(1)).reserveSeat(accountId, 2);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_Invalid_InsufficientSeatsWithInfantAndChild() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
                new TicketTypeRequest(TicketType.CHILD, 1),
                new TicketTypeRequest(TicketType.INFANT, 1),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to there being less adults than infants and children
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void testPurchaseTickets_Valid_SufficientAdultsWithInfantAndChild() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 2),
                new TicketTypeRequest(TicketType.CHILD, 1),
                new TicketTypeRequest(TicketType.INFANT, 1)
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 60 payment 2 * 20 + 1 * 10 + 1 * 0
        // 4 seats 2 + 1 + 0
        verify(paymentService, times(1)).makePayment(accountId, 50);
        verify(reservationService, times(1)).reserveSeat(accountId, 3);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_ExceedsMaxSeats() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 21),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due to exceeding max seats
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void testPurchaseTickets_Valid_MaximumTickets() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 20)
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 400 payment 20 * 20
        // 20 seats 20
        verify(paymentService, times(1)).makePayment(accountId, 400);
        verify(reservationService, times(1)).reserveSeat(accountId, 20);
    }

    @Test
    public void testPurchaseTickets_Valid_CorrectAdultCost() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 20 payment 1 * 20
        // 1 seat
        verify(paymentService, times(1)).makePayment(accountId, 20);
        verify(reservationService, times(1)).reserveSeat(accountId, 1);
    }

    // Where above test passes adult cost is confirmed to be 20, add 10 for child to test its cost
    @Test
    public void testPurchaseTickets_Valid_CorrectChildCost() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
                new TicketTypeRequest(TicketType.CHILD, 1),
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 30 payment 1 * 20 + 1 * 10
        // 2 seat 1 + 1
        verify(paymentService, times(1)).makePayment(accountId, 30);
        verify(reservationService, times(1)).reserveSeat(accountId, 2);
    }

    // Where above test passes adult cost is confirmed to be 20, add 0 for infant to test its cost
    @Test
    public void testPurchaseTickets_Valid_CorrectInfantCost() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(TicketType.ADULT, 1),
                new TicketTypeRequest(TicketType.INFANT, 1),
        };

        // Set up necessary mocks
        doNothing().when(paymentService).makePayment(anyLong(), anyInt());
        doNothing().when(reservationService).reserveSeat(anyLong(), anyInt());

        // Call the method under test
        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        // Verify interactions with the paymentService and reservationService
        // 20 payment 1 * 20 + 1 * 0
        // 1 seat 1 + 0 (infant)
        verify(paymentService, times(1)).makePayment(accountId, 20);
        verify(reservationService, times(1)).reserveSeat(accountId, 1);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTickets_InvalidPurchase_TicketTypeNull() throws InvalidPurchaseException {
        // Create test data
        Long accountId = 123L;
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[] {
                new TicketTypeRequest(null, 21),
        };

        // Call the method under test, expect an InvalidPurchaseException to be thrown,
        // due null ticket type provided
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }
}