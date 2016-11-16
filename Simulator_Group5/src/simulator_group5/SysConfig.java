/*
 * Operating System Simulator
 * Group 5: DuyNQ, QuyenDM, HaiNT
 */
package simulator_group5;

/**
 * Define constant and message of operation system.
 *
 * @author duynq
 */
public class SysConfig {

    /**
     * memory size
     */
    public static final int MEMORY_SIZE = 240;

    /**
     * top of timer instruction stack
     */
    public static final int TOP_TIMER = 1000;

    /**
     * top of system instruction stack
     */
    public static final int TOP_SYSTEM = 1500;

    /**
     * bottom of user stack
     */
    public static final int BOTTOM_USER = 999;

    /**
     * bottom of system stack
     */
    public static final int BOTTOM_SYSTEM = 1999;

    /**
     * user mode
     */
    public static final int USER_MODE = 0;

    /**
     * timer mode
     */
    public static final int TIMER_MODE = 1;

    /**
     * system mode
     */
    public static final int SYSTEM_MODE = 2;

    /**
     * when empty instruction set
     */
    public static final int EMPTY_INSTRUCTION_SET = 0;

    /**
     * comment interrupt
     */
    public static final String INTERRUPT_COMMENT = ".1000";

    /**
     * Hyphen character
     */
    public static final String HYPHEN = "-";
}
