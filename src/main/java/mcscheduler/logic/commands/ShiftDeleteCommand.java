package mcscheduler.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import mcscheduler.commons.core.Messages;
import mcscheduler.commons.core.index.Index;
import mcscheduler.commons.util.CollectionUtil;
import mcscheduler.logic.commands.exceptions.CommandException;
import mcscheduler.model.Model;
import mcscheduler.model.assignment.Assignment;
import mcscheduler.model.shift.Shift;

/**
 * Deletes a shift identified using its displayed index from the McScheduler.
 */
public class ShiftDeleteCommand extends Command {

    public static final String COMMAND_WORD = "shift-delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the shift identified by the index number used in the displayed shift list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_SHIFT_SUCCESS = "Deleted Shift: %1$s";

    private final Index targetIndex;

    public ShiftDeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Shift> lastShownList = model.getFilteredShiftList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(printOutOfBoundsShiftIndexError(targetIndex));
        }

        Shift shiftToDelete = lastShownList.get(targetIndex.getZeroBased());
        deleteShiftFromAssignments(model, shiftToDelete);
        model.deleteShift(shiftToDelete);

        return new CommandResult(String.format(MESSAGE_DELETE_SHIFT_SUCCESS, shiftToDelete));
    }

    private void deleteShiftFromAssignments(Model model, Shift shiftToDelete) {
        CollectionUtil.requireAllNonNull(model, shiftToDelete);
        List<Assignment> fullAssignmentList = model.getFullAssignmentList();
        List<Assignment> assignmentsToDelete = new ArrayList<>();

        for (Assignment assignment : fullAssignmentList) {
            if (shiftToDelete.isSameShift(assignment.getShift())) {
                assignmentsToDelete.add(assignment);
            }
        }

        assignmentsToDelete.forEach(model::deleteAssignment);
    }

    private String printOutOfBoundsShiftIndexError(Index shiftIndex) {
        return String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                String.format(Messages.MESSAGE_INVALID_SHIFT_DISPLAYED_INDEX, shiftIndex.getOneBased())
                        + MESSAGE_USAGE);
    }


    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ShiftDeleteCommand // instanceof handles nulls
                && targetIndex.equals(((ShiftDeleteCommand) other).targetIndex)); // state check
    }
}
