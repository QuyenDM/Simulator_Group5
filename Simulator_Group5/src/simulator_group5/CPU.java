/*
 * Operating System Simulator
 * Group 5: DuyNQ, QuyenDM, HaiNT
 */
package simulator_group5;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Random;

/**
 * Define CPU with 6 registers: PC, AC, SP, X, Y, IR <\br>
 * contains: <\br>
 * 1. Memory <\br>
 * 2. Time to interrupt<\br>
 * 3. User mode <\br>
 * method: run to start application
 *
 * @author duynq
 */
public class CPU {

    /**
     * Variable to store PC register
     */
    private int pcReg;
    /**
     * Variable to store AC register
     */
    private int acReg;
    /**
     * Variable to store SP register
     */
    private int spReg;
    /**
     * Variable to store X register
     */
    private int xReg;
    /**
     * Variable to store Y register
     */
    private int yReg;
    /**
     * Variable to store IR register
     */
    private int irReg;
    /**
     * Variable to store Memory
     */
    private Memory memory;
    /**
     * Variable to store status of CPU is running or not
     */
    private boolean isRunning;
    /**
     * Variable to store interrupt time
     */
    private final int interruptTime;
    /**
     * Variable to store counter
     */
    private int counter;
    /**
     * Variable to store CPU mode
     */
    private int mode;

    /**
     * Constructor with interrupt time argument
     *
     * @param interruptTime time for interrupt
     */
    public CPU(int interruptTime) {
        this.interruptTime = interruptTime;
    }

    /**
     * CPU runs
     *
     * @param fileName stored instruction set
     */
    public void runs(String fileName) {
        try {
            //initialize with file
            this.initialize(fileName);
            //while status is running do 
            while (this.isRunning) {
                callFunction();
                interrupt();
            }
        } catch (IOException exp) {
            writeErrorToMemory(exp.getMessage());
        } catch (IndexOutOfBoundsException ex) {
            writeErrorToMemory(ex.getMessage());
        } catch (EmptyStackException stackExp) {
            writeErrorToMemory(Messages.ERR_STACK_IS_EMPTY);
        } catch (StackOverflowError stackOverExp) {
            writeErrorToMemory(stackOverExp.getMessage());
        } catch (Exception ex) {
            writeErrorToMemory(ex.getMessage());
        }
    }

    /**
     * Initialize
     *
     * @param fileName stored instruction set
     * @throws IOException when read file
     */
    private void initialize(String fileName) throws IOException, IndexOutOfBoundsException {
        //Set mode is user
        mode = SysConfig.USER_MODE;
        //Initial new Memory
        memory = new Memory();
        memory.initialize(fileName);
        //Set status is running
        this.isRunning = true;
    }

    /**
     * CPU call function with decoding instruction
     *
     */
    private void callFunction() throws IndexOutOfBoundsException, EmptyStackException, StackOverflowError, Exception {
        //read 
        irReg = readFromMemoryByPCReg();
        //Execute each instruction
        executeInstruction(irReg);
    }

    /**
     * Execute a instruction
     *
     * @param instruction
     */
    private void executeInstruction(int instruction) throws IndexOutOfBoundsException, Exception {
        //read instruction from IR
        switch (instruction) {
            case 1:
                loadValue();
                break;
            case 2:
                loadAddr();
                break;
            case 3:
                loadIndAddr();
                break;
            case 4:
                loadIdXAddr();
                break;
            case 5:
                loadIdYAddr();
                break;
            case 6:
                loadSpX();
                break;
            case 7:
                storeAddr();
                break;
            case 8:
                get();
                break;
            case 9:
                putPort();
                break;
            case 10:
                addX();
                break;
            case 11:
                addY();
                break;
            case 12:
                subX();
                break;
            case 13:
                subY();
                break;
            case 14:
                copyToX();
                break;
            case 15:
                copyFromX();
                break;
            case 16:
                copyToY();
                break;
            case 17:
                copyFromY();
                break;
            case 18:
                copyToSP();
                break;
            case 19:
                copyFromSP();
                break;
            case 20:
                jumpAddr();
                break;
            case 21:
                jumpIfEqualAddr();
                break;
            case 22:
                jumpIfNotEqualAddr();
                break;
            case 23:
                callAddr();
                break;
            case 24:
                ret();
                break;
            case 25:
                incX();
                break;
            case 26:
                decX();
                break;
            case 27:
                push();
                break;
            case 28:
                pop();
                break;
            case 29:
                Int();
                break;
            case 30:
                iRet();
                break;
            case 50:
                end();
                break;
            default:
                doWhenGetWrongInstruction(instruction);
                break;
        }
    }

    /**
     * do when get wrong instruction
     *
     * @param instruction is wrong
     */
    private void doWhenGetWrongInstruction(int instruction) {
        if (instruction == SysConfig.EMPTY_INSTRUCTION_SET && mode == SysConfig.USER_MODE) {
            end();
        } else if (instruction == SysConfig.EMPTY_INSTRUCTION_SET) {
            iRet();
        }
    }

    /**
     * Write error to memory
     *
     * @param error is error message
     */
    private void writeErrorToMemory(String error) {
        memory.clear();
        int address = 0;
        char[] errorCharacters = error.toCharArray();
        for (char character : errorCharacters) {
            //load character to ac
            memory.write(address++, 1);
            memory.write(address++, (int) character);
            //print character
            memory.write(address++, 9);
            memory.write(address++, 2);
        }
        memory.write(address++, 2);
        pcReg = 0;
        mode = SysConfig.USER_MODE;
    }

    /**
     * timer interrupt function
     */
    private void timerInterrupt() {
        if (this.counter == this.interruptTime) {
            //change to system mode
            mode = SysConfig.TIMER_MODE;
            //stored SP
            int temp = spReg;
            //turn sp to point system stack
            spReg = 0;
            pushValue(temp);
            //stored PC
            pushValue(pcReg);
            pcReg = 0;
            //stored AC, X, Y
            pushValue(acReg);
            pushValue(xReg);
            pushValue(yReg);
            counter = 0;
        } else {
            counter++;
        }
    }

    /**
     * turn back from system mode to user mode
     */
    private int readFromMemoryByPCReg() throws IndexOutOfBoundsException {
        int address = getAddressInMemoryViaPC();
        pcReg++;
        return memory.read(address);
    }

    /**
     * get value from memory via PC
     *
     * @return value
     */
    private int getAddressInMemoryViaPC() {
        int address;
        switch (mode) {
            case SysConfig.USER_MODE:
                address = pcReg;
                break;
            case SysConfig.TIMER_MODE:
                address = SysConfig.TOP_TIMER + pcReg;
                break;
            default:
                address = SysConfig.TOP_SYSTEM + pcReg;
                break;
        }
        return address;
    }

    /**
     * push value to stack
     *
     * @param value is pushed
     */
    private void pushValue(int value) throws StackOverflowError {
        if (isMemoryFull()) {
            throw new StackOverflowError(Messages.ERR_STACK_IS_FULL);
        } else {
            int address;
            if (mode == SysConfig.USER_MODE) {
                address = SysConfig.BOTTOM_USER - spReg;
            } else {
                address = SysConfig.BOTTOM_SYSTEM - spReg;
            }
            spReg++;
            memory.write(address, value);
        }
    }

    /**
     * check memory is full or not return true if memory is full
     */
    private boolean isMemoryFull() {
        boolean ret = false;
        int size = pcReg + spReg;
        if (mode == SysConfig.USER_MODE && size > 119) {
            ret = true;
        }
        if (mode == SysConfig.SYSTEM_MODE && size > 499) {
            ret = true;
        }
        if (mode == SysConfig.SYSTEM_MODE && spReg > 499) {
            ret = true;
        }
        return ret;
    }

    /**
     * pop value from stack
     *
     * @return value
     */
    private int popValueFromStack() throws EmptyStackException {
        int address;
        if (spReg == 0) {
            throw new EmptyStackException();
        }
        spReg--;
        if (mode == SysConfig.USER_MODE) {
            address = SysConfig.BOTTOM_USER - spReg;
        } else {
            address = SysConfig.BOTTOM_SYSTEM - spReg;
        }

        return memory.read(address);
    }

    //Begin functions of CPU 
    /**
     * Load the value into the AC
     */
    private void loadValue() throws IndexOutOfBoundsException {
        try {
            int value = readFromMemoryByPCReg();
            acReg = value;
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 1));
        }
    }

    /**
     * Load the value at the address into the AC
     */
    private void loadAddr() throws IndexOutOfBoundsException, Exception {
        try {
            int address = readFromMemoryByPCReg();
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                acReg = memory.read(address);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 2));
        }
    }

    /**
     * Load the value from the address found in the given address into the AC
     */
    private void loadIndAddr() throws IndexOutOfBoundsException, Exception {
        try {
            int address = readFromMemoryByPCReg();
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                int innerAddress = memory.read(address);
                if (mode == SysConfig.USER_MODE && innerAddress > SysConfig.BOTTOM_USER) {
                    writeErrorToMemory(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
                }
                acReg = memory.read(innerAddress);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 3));
        }
    }

    /**
     * Load the value at (address+X) into the AC
     */
    private void loadIdXAddr() throws IndexOutOfBoundsException, Exception {
        try {
            int address = readFromMemoryByPCReg();
            address = address + xReg;
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                acReg = memory.read(address);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 4));
        }
    }

    /**
     * Load the value at (address+Y) into the AC
     */
    private void loadIdYAddr() throws IndexOutOfBoundsException, Exception {
        try {
            int address = readFromMemoryByPCReg();
            address = address + yReg;
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                acReg = memory.read(address);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 5));
        }
    }

    /**
     * Load from (Sp+X) into the AC
     */
    private void loadSpX() throws IndexOutOfBoundsException, Exception {
        try {
            int address = spReg + xReg;
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                acReg = memory.read(address);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 6));
        }
    }

    /**
     * Store the value in the AC into the address
     */
    private void storeAddr() throws IndexOutOfBoundsException, Exception {
        try {
            int address = readFromMemoryByPCReg();
            if (mode == SysConfig.USER_MODE && address > SysConfig.BOTTOM_USER) {
                throw new Exception(String.format(Messages.ERR_IN_COMMAND_INVALID_ACCESS, 2));
            } else {
                memory.write(address, acReg);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 7));
        }
    }

    /**
     * Gets a random int from 1 to 100 into the AC
     */
    private void get() {
        Random random = new Random();
        acReg = random.nextInt(10);
    }

    /**
     * If port=1, writes AC as an int to the screen <\br>
     * If port=2, writes AC as a char to the screen
     */
    private void putPort() throws IndexOutOfBoundsException {
        try {
            int port = readFromMemoryByPCReg();
            if (port == 1) {
                System.out.println(acReg);
            } else {
                System.out.print((char) acReg);
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 9));
        }
    }

    /**
     * Add the value in X to the AC
     */
    private void addX() {
        acReg = acReg + xReg;
    }

    /**
     * Add the value in Y to the AC
     */
    private void addY() {
        acReg = acReg + yReg;
    }

    /**
     * Subtract the value in X from the AC
     */
    private void subX() {
        acReg = acReg - xReg;
    }

    /**
     * Subtract the value in Y from the AC
     */
    private void subY() {
        acReg = acReg - yReg;
    }

    /**
     * Copy the value in the AC to X
     */
    private void copyToX() {
        xReg = acReg;
    }

    /**
     * Copy the value in X to the AC
     */
    private void copyFromX() {
        acReg = xReg;
    }

    /**
     * Copy the value in the AC to Y
     */
    private void copyToY() {
        yReg = acReg;
    }

    /**
     * Copy the value in Y to the AC
     */
    private void copyFromY() {
        acReg = yReg;
    }

    /**
     * Copy the value in AC to the SP
     */
    private void copyToSP() {
        spReg = acReg;
    }

    /**
     * Copy the value in SP to the AC
     */
    private void copyFromSP() {
        acReg = spReg;
    }

    /**
     * Jump to the address
     */
    private void jumpAddr() throws IndexOutOfBoundsException {
        try {
            int address = readFromMemoryByPCReg();
            pcReg = address;
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 20));
        }
    }

    /**
     * Jump to the address only if the value in the AC is zero
     */
    private void jumpIfEqualAddr() throws IndexOutOfBoundsException {
        try {
            int address = readFromMemoryByPCReg();
            if (acReg == 0) {
                pcReg = address;
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 21));
        }
    }

    /**
     * Jump to the address only if the value in the AC is not zero
     */
    private void jumpIfNotEqualAddr() throws IndexOutOfBoundsException {
        try {
            int address = readFromMemoryByPCReg();
            if (acReg != 0) {
                pcReg = address;
            }
        } catch (IndexOutOfBoundsException exp) {
            throw new IndexOutOfBoundsException(exp.getMessage()
                    + SysConfig.HYPHEN + String.format(Messages.ERR_IN_COMMAND_INDEX_OUT_OF_BOUND, 22));
        }
    }

    /**
     * Push return address onto stack, jump to the address
     */
    private void callAddr() {
        int address = readFromMemoryByPCReg();
        pushValue(pcReg);
        pcReg = address;
    }

    /**
     * Pop return address from the stack, jump to the address
     */
    private void ret() {
        int address = popValueFromStack();
        pcReg = address;
    }

    /**
     * Increment the value in X
     */
    private void incX() {
        xReg++;
    }

    /**
     * Decrement the value in X
     */
    private void decX() {
        xReg--;
    }

    /**
     * Push AC onto stack
     */
    private void push() {
        pushValue(acReg);

    }

    /**
     * Pop from stack into AC
     */
    private void pop() {
        acReg = popValueFromStack();
    }

    /**
     * Set system mode, switch stack, push SP and PC, set new SP and PC
     */
    private void Int() {
        //change to system mode
        mode = SysConfig.SYSTEM_MODE;
        //stored SP
        int temp = spReg;
        //turn sp to point system stack
        spReg = 0;
        pushValue(temp);
        //stored PC
        pushValue(pcReg);
        pcReg = 0;
        //stored AC, X, Y
        pushValue(acReg);
        pushValue(xReg);
        pushValue(yReg);
    }

    /**
     * return to user mode
     */
    private void iRet() {
        if (mode == SysConfig.USER_MODE) {
            writeErrorToMemory(Messages.ERR_CANNOT_CALL_IRET);
        } else {
            spReg = 5;
            //restored AC, X, Y register
            yReg = popValueFromStack();
            xReg = popValueFromStack();
            acReg = popValueFromStack();
            //restored PC
            pcReg = popValueFromStack();
            //restored SP
            int temp = popValueFromStack();
            spReg = temp;
            mode = SysConfig.USER_MODE;
        }
    }

    /**
     * Interrupt function
     */
    private void interrupt() {
        //check time is interupt time
        if (mode == SysConfig.USER_MODE && this.isRunning) {
            timerInterrupt();
        }
    }

    /**
     * End execution
     */
    private void end() {
        isRunning = false;
    }
}
