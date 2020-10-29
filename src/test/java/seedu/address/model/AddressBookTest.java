package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_CASHIER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_REQUIREMENT_CHEF;
import static seedu.address.testutil.AddressBookBuilder.getTypicalAddressBook;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalShifts.SHIFT_A;
import static seedu.address.testutil.TypicalWorkers.ALICE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.shift.Shift;
import seedu.address.model.shift.exceptions.DuplicateShiftException;
import seedu.address.model.tag.Role;
import seedu.address.model.worker.Worker;
import seedu.address.model.worker.exceptions.DuplicateWorkerException;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.ShiftBuilder;
import seedu.address.testutil.WorkerBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getWorkerList());
        assertEquals(Collections.emptyList(), addressBook.getShiftList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateWorkers_throwsDuplicateWorkerException() {
        // Two workers with the same identity fields
        Worker editedAlice = new WorkerBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withRoles(VALID_ROLE_CASHIER)
                .build();
        List<Worker> newWorkers = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = AddressBookStub.createAddressBookStubWithWorkers(newWorkers);

        assertThrows(DuplicateWorkerException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasWorker_nullWorker_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasWorker(null));
    }

    @Test
    public void hasWorker_workerNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasWorker(ALICE));
    }

    @Test
    public void hasWorker_workerInAddressBook_returnsTrue() {
        addressBook.addWorker(ALICE);
        assertTrue(addressBook.hasWorker(ALICE));
    }

    @Test
    public void hasWorker_workerWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addWorker(ALICE);
        Worker editedAlice = new WorkerBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withRoles(VALID_ROLE_CASHIER)
                .build();
        assertTrue(addressBook.hasWorker(editedAlice));
    }

    @Test
    public void getWorkerList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getWorkerList().remove(0));
    }

    @Test
    public void resetData_withDuplicateShifts_throwsDuplicateShiftException() {
        Shift editedShift = new ShiftBuilder(SHIFT_A).withRoleRequirements(VALID_ROLE_REQUIREMENT_CHEF)
                .build();
        List<Shift> newShifts = Arrays.asList(SHIFT_A, editedShift);
        AddressBookStub newData = AddressBookStub.createAddressBookStubWithShifts(newShifts);

        assertThrows(DuplicateShiftException.class, () -> addressBook.resetData(newData));
    }


    @Test
    public void hasShift_nullShift_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasShift(null));
    }

    @Test
    public void hasShift_shiftNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasShift(SHIFT_A));
    }

    @Test
    public void hasShift_shiftInAddressBook_returnsTrue() {
        addressBook.addShift(SHIFT_A);
        assertTrue(addressBook.hasShift(SHIFT_A));
    }

    @Test
    public void hasShift_workerWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addShift(SHIFT_A);
        Shift editedShift = new ShiftBuilder(SHIFT_A).withRoleRequirements(VALID_ROLE_REQUIREMENT_CHEF)
                .build();
        assertTrue(addressBook.hasShift(editedShift));
    }

    @Test
    public void getShiftList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getShiftList().remove(0));
    }

    @Test
    public void equals() {

        addressBook.addWorker(ALICE);
        addressBook.addShift(SHIFT_A);
        AddressBook noWorkerAddressBook = new AddressBookBuilder().withShift(SHIFT_A).build();
        AddressBook noShiftAddressBook = new AddressBookBuilder().withWorker(ALICE).build();
        AddressBook emptyAddressBook = new AddressBook();

        //same object returns true
        assertEquals(addressBook, addressBook);

        //different class object returns false
        assertNotEquals(addressBook, 123);

        //same content returns true
        assertEquals(new AddressBook(addressBook), addressBook);

        //same shifts different workers returns false
        assertNotEquals(noWorkerAddressBook, addressBook);

        //same workers different shifts returns false
        assertNotEquals(noShiftAddressBook, addressBook);

        //different workers different shifts returns false
        assertNotEquals(emptyAddressBook, addressBook);

    }


    /**
     * A stub ReadOnlyAddressBook whose workers list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Worker> workers = FXCollections.observableArrayList();
        private final ObservableList<Shift> shifts = FXCollections.observableArrayList();
        private final ObservableList<Assignment> assignments = FXCollections.observableArrayList();
        private final ObservableList<Role> validRoles = FXCollections.observableArrayList();

        private AddressBookStub(Collection<Worker> workers, Collection<Shift> shifts,
                Collection<Assignment> assignments, Collection<Role> validRoles) {
            this.workers.setAll(workers);
            this.shifts.setAll(shifts);
            this.assignments.setAll(assignments);
            this.validRoles.setAll(validRoles);
        }

        public static AddressBookStub createAddressBookStubWithWorkers(Collection<Worker> workers) {
            return new AddressBookStub(workers, Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList());
        }

        public static AddressBookStub createAddressBookStubWithShifts(Collection<Shift> shifts) {
            return new AddressBookStub(Collections.emptyList(), shifts, Collections.emptyList(),
                    Collections.emptyList());
        }

        @Override
        public ObservableList<Worker> getWorkerList() {
            return workers;
        }

        @Override
        public ObservableList<Shift> getShiftList() {
            return shifts;
        }

        @Override
        public ObservableList<Assignment> getAssignmentList() {
            return assignments;
        }

        @Override
        public ObservableList<Role> getRoleList() {
            return validRoles;
        }
    }

}
