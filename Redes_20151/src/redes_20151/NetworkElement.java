/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
 //Important for the work structure - Does the IP conversion and the base interface for Router and Network
package redes_20151;

/**
 *
 * @author lasaro
 */
abstract class NetworkElement {

    public static String binaryIPtoStringIPv4(String binaryIP) {
        return IntegerIPv4toStringIPv4(binaryIPtoIntegerIPv4(binaryIP));
    }

    public static String IntegerIPv4toStringIPv4(int[] parts) {
        String ret = "";
        for (int i = 0; i < parts.length; i++) {
            ret += parts[i] + (i == (parts.length-1) ? "" : ".");
        }
        return ret;
    }

    public static int[] binaryIPtoIntegerIPv4(String binaryIP) {
        int[] parts = new int[4];

        int last = 0;
        int next = last + 8;
        for (int i = 0; i < parts.length; i++) {
            char[] bits = binaryIP.substring(last, next).toCharArray();
            for (int j = 0, e = bits.length - 1; j < bits.length; j++, e--) {
                int bit = Integer.parseInt("" + bits[j]);
                parts[i] += (bit == 1 ? Math.pow(2, e) : 0);
            }            
            last = next;
            next = last + 8;
        }
        
        return parts;
    }

    abstract String getName();
}
