package edu.vt.cs5244;

/**
 * This unchecked exception is for internal use within a DDP server, to indicate an IO problem.
 * 
 * Please note: This class must not be altered in any way; you must use it exactly as-is.
 * 
 */
public class DABServerIOException extends DABDistributedException {

    public DABServerIOException() {
        super();
    }

}
