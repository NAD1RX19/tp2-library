package library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    private static final String ISBN = "978-0-00-000001-1";
    private static final LocalDate DAY = LocalDate.of(2026, 10, 1);
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book(ISBN, "Software Engineering", 2016));
        library.addCopy(ISBN, "C-001");
        library.registerMember(new Member("M-01", "First member"));
    }

    @Test
    void lendingMakesTheCopyUnavailable() {
        Loan loan = library.lend("M-01", "C-001", DAY);
        assertFalse(library.findCopy("C-001").isAvailable());
        assertEquals(DAY.plusDays(14), loan.getDueDate());
    }

    @Test
    void unavailableCopyCannotBeLent() {
        library.lend("M-01", "C-001", DAY);
        library.registerMember(new Member("M-02", "Second member"));
        assertThrows(IllegalStateException.class, () -> library.lend("M-02", "C-001", DAY));
    }

    @Test
    void unknownMemberOrCopyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> library.lend("X", "C-001", DAY));
        assertThrows(IllegalArgumentException.class, () -> library.lend("M-01", "X", DAY));
        assertThrows(IllegalArgumentException.class, () -> library.returnCopy("X", DAY));
    }

    @Test
    void memberCannotHaveMoreThanFiveLoans() {
        for (int i = 1; i <= 6; i++) library.addCopy(ISBN, "K-" + i);
        for (int i = 1; i <= 5; i++) library.lend("M-01", "K-" + i, DAY);
        assertThrows(IllegalStateException.class, () -> library.lend("M-01", "K-6", DAY));
    }

    @Test
    void returningMakesTheCopyAvailableAgain() {
        library.lend("M-01", "C-001", DAY);
        Loan loan = library.returnCopy("C-001", DAY.plusDays(3));
        assertTrue(library.findCopy("C-001").isAvailable());
        assertFalse(loan.isOpen());
    }

    @Test
    void returningACopyNotOnLoanIsRefused() {
        assertThrows(IllegalStateException.class, () -> library.returnCopy("C-001", DAY));
    }

    @Test
    void loanIsLateAfterTheDueDate() {
        Loan loan = library.lend("M-01", "C-001", DAY);
        assertFalse(loan.isLate(DAY.plusDays(14)));
        assertTrue(loan.isLate(DAY.plusDays(15)));
    }

    @Test
    void booksWithSameIsbnAreEqual() {
        assertEquals(new Book(ISBN, "A", 2000), new Book(ISBN, "B", 2001));
    }
    @Test
    void searchIgnoresCase() {
        assertEquals(1, library.searchByTitle("software").size());
        assertEquals(1, library.searchByTitle("ENGINEERING").size());
        assertTrue(library.searchByTitle("python").isEmpty());
    }
    @Test
    void returnedCopyCanBeLentAgain() {
        library.lend("M-01", "C-001", DAY);
        library.returnCopy("C-001", DAY.plusDays(1));
        assertDoesNotThrow(() -> library.lend("M-01", "C-001", DAY.plusDays(2)));
    }

    @Test
    void lateReturnIsDetected() {
        Loan loan = library.lend("M-01", "C-001", DAY);
        library.returnCopy("C-001", DAY.plusDays(20));
        assertTrue(loan.isLate(DAY.plusDays(20)));
    }
}
