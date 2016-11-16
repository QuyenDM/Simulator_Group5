/*
 * Operating System Simulator
 * Group 5: DuyNQ, QuyenDM, HaiNT
 */
package simulator_group5;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

/**
 * Define Memory class: <\br>
 * 1. Consist of 2000 integer entries <\br>
 * 2. Support 2 operations: read and write <\br>
 * 3. Initialize itself by reading a program file.
 *
 * @author duynq
 */
public class Memory {

    /**
     * entries
     */
    private int[] entries;

    /**
     * initialize
     *
     * @param filePath store instruction
     * @throws IOException when read file
     * @throws IndexOutOfBoundsException when memory is full
     */
    public void initialize(String filePath)
            throws IOException, IndexOutOfBoundsException {
        try {
            this.entries = new int[SysConfig.MEMORY_SIZE];
            int address = 0;
            boolean isReadForUserMode = true;

            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                line = line.trim();
                if (isInterruptComment(line) && isReadForUserMode) {
                    address = SysConfig.TOP_TIMER;
                } else if (!line.isEmpty() && isNotCommentLine(line)) {
                    checkMemoryIsFullForAddress(isReadForUserMode, address);
                    int instruction = getInstructionFromLine(line);
                    write(address++, instruction);
                }
            }
        } catch (IOException e) {
            throw new IOException(Messages.ERR_FILE_NOT_FOUND);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(Messages.ERR_FILE_TOO_BIG
                    + SysConfig.HYPHEN + e.getMessage());
        }
    }

    /**
     * Check memory if full or not, if full throw IndexofBound Error
     */
    private void checkMemoryIsFullForAddress(boolean isReadForUserMode, int address)
            throws IndexOutOfBoundsException {
        if (isReadForUserMode && address == 999) {
            throw new IndexOutOfBoundsException(Messages.ERR_IN_USER_MODE);
        } else if (address == 1499) {
            throw new IndexOutOfBoundsException(Messages.ERR_IN_SYSTEM_MODE);
        }
    }

    /**
     * check line is comment line or not
     *
     * @param line is check return true if line is comment
     */
    private boolean isNotCommentLine(String line) {
        boolean ret;
        char character = line.charAt(0);
        if (isMinus(character) && line.length() < 2) {
            ret = false;
        } else if (isMinus(character)) {
            character = line.charAt(1);
            ret = isDigit(character);
        } else {
            ret = isDigit(character);
        }
        return ret;
    }

    /**
     * check character is digit or not
     *
     * @param character is check return true if character is digit
     */
    private boolean isDigit(char character) {
        return (character >= '0' && character <= '9');
    }

    /**
     * check character is minus or not
     *
     * @param character is check return true if character is minus
     */
    private boolean isMinus(char character) {
        return character == '-';
    }

    /**
     * write
     *
     * @param address of memory
     * @param value is written
     */
    public void write(int address, int value) {
        this.entries[address] = value;
    }

    /**
     * clear memory
     */
    public void clear() {
        this.entries = new int[SysConfig.MEMORY_SIZE];
    }

    /**
     * read
     *
     * @param address of memory
     * @return value
     */
    public int read(int address) throws IndexOutOfBoundsException {
        int memoryAddr;
        try {
            memoryAddr = this.entries[address];
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(Messages.ERR_MEMORY_INDEX_OUT_OF_BOUND);
        }
        return memoryAddr;
    }

    /**
     * get instruction from line
     *
     * @param line return instruction
     */
    private int getInstructionFromLine(String line) {
        int endOfInstruction = 1;
        boolean hasNextDigit = true;
        for (int i = 1; i < line.length() && hasNextDigit; i++) {
            char character = line.charAt(i);
            if (isDigit(character)) {
                endOfInstruction++;
            } else {
                hasNextDigit = false;
            }
        }
        String sInstruction = line.substring(0, endOfInstruction);
        int instruction = Integer.parseInt(sInstruction);
        return instruction;
    }

    /**
     * check line is interrupt comment or not
     *
     * @param line is check return true if line is interrupt comment
     */
    private boolean isInterruptComment(String line) {
        if (line.length() < 6) {
            return line.equals(SysConfig.INTERRUPT_COMMENT);
        } else {
            return line.substring(0, 5).equals(SysConfig.INTERRUPT_COMMENT);
        }
    }
}
