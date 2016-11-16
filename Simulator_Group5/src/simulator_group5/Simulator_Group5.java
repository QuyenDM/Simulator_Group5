/*
 * Operating System Simulator
 * Group 5: DuyNQ, QuyenDM, HaiNT
 */
package simulator_group5;

import java.io.IOException;

/**
 * Main function to run application
 *
 * @author duynq
 */
public class Simulator_Group5 {

    /**
     * @param args the command line arguments input format: <\br>
     * [file name] [interrupt time]
     */
    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                //Read arguments
                String sfileName = args[0];
                String sTimeInterrupt = args[1];
                //Valid interrupt time is integer
                int timeInterrupt = Integer.parseInt(sTimeInterrupt);
                if (timeInterrupt < 0) {
                    throw new NumberFormatException(Messages.ERR_INTERUPTIME);
                } else {
                    CPU cpu = new CPU(timeInterrupt);
                    //Run program
                    cpu.runs(sfileName);
                }
            } else {
                throw new Exception(Messages.ERR_INPUT_FORMAT);
            }
        } catch (NumberFormatException exp) {
            System.out.println(exp.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
