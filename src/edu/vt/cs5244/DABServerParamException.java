package edu.vt.cs5244;

/**
 * This unchecked exception is for internal use within a DDP server, to indicate a parameter problem.
 * 
 * Please note: This class must not be altered in any way; you must use it exactly as-is.
 * 
 */
public class DABServerParamException extends DABDistributedException {

    public DABServerParamException() {
        super();
    }

}
