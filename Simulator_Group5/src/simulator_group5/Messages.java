/*
 * Operating System Simulator
 * Group 5: DuyNQ, QuyenDM, HaiNT
 */
package simulator_group5;

/**
 * Define message of system
 *
 * @author quyendm
 */
public class Messages {

    /**
     * invalid access message
     */
    public static final String INVALID_ACCESS_MESSAGE = "Invalid access to memory";

    /**
     * index of range out message
     */
    public static final String INDEX_OUT_OF_RANGE = "Index out of range";

    /**
     * message error in command i index out of bound
     */
    public static final String ERR_IN_COMMAND_INDEX_OUT_OF_BOUND = "Error in command id : %d index out of range";

    /**
     * message error in command i invalid access
     */
    public static final String ERR_IN_COMMAND_INVALID_ACCESS = "Error in command id : %d invalid access to memory";
    /**
     * message error stack is empty
     */
    public static final String ERR_STACK_IS_EMPTY = "Stack is empty";
    /**
     * message error stack is full
     */
    public static final String ERR_STACK_IS_FULL = "Stack is full";

    /**
     * message error out of bound memory
     */
    public static final String ERR_MEMORY_INDEX_OUT_OF_BOUND = "Index out of bound when access memory";

    /**
     * message cannot call iret when in user mode
     */
    public static final String ERR_CANNOT_CALL_IRET = "Can not call IRet when user mode";

    /**
     * message interrupt time is not integer
     */
    public static final String ERR_INTERUPTIME = "Interrupt time must be integer and > 0!";

    /**
     * message for error input format
     */
    public static final String ERR_INPUT_FORMAT = "Please input with format: [file_name] [interrupt_time]";

    /**
     * message for file not found error
     */
    public static final String ERR_FILE_NOT_FOUND = "Can not read file!";

    /**
     * message for file is too big error
     */
    public static final String ERR_FILE_TOO_BIG = "File is too big";
    public static final String ERR_IN_USER_MODE = "In User mode";
    public static final String ERR_IN_SYSTEM_MODE = "In System mode";
}
