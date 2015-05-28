/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redes_20151;

/**
 *
 * @author lasaro
 */
class NetworkElement {

    public static String binaryIPtoIPv4(String binaryIP) {
        String ret = "";
        int[] parts = new int[4];

        int last = 0;
        int next = last +8;
        for (int i = 0; i < parts.length; i++) {
            char[] bits = binaryIP.substring(last, next).toCharArray();
            for (int j = 0, e = bits.length-1; j < bits.length; j++, e--) {
                int bit = Integer.parseInt("" + bits[j]);
                parts[i] += (bit == 1 ? Math.pow(2, e) : 0);
            }
            ret += parts[i] + (i == 3 ? "" : ".");
            last = next;
            next = last +8;
        }

        return ret;
    }

    String getNetMask() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
