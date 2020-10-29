package seedu.address.model.tag.exceptions;

/**
 * Signals that the operation will result in duplicate roles.
 */
public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException() {
        super("Operation would result in duplicate roles");
    }
}
