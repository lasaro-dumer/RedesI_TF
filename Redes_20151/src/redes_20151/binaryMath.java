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
public class binaryMath {

    public static String addBinary(String a, String b) {
        // Start typing your Java solution below
        // DO NOT write main() function
        int la = a.length();
        int lb = b.length();

        int max = Math.max(la, lb);

        StringBuilder sum = new StringBuilder("");
        int carry = 0;

        for (int i = 0; i < max; i++) {
            int m = getBit(a, la - i - 1);
            int n = getBit(b, lb - i - 1);
            int add = m + n + carry;
            sum.append(add % 2);
            carry = add / 2;
        }

        if (carry == 1) {
            sum.append("1");
        }

        return sum.reverse().toString();

    }

    public static int getBit(String s, int index) {
        if (index < 0 || index >= s.length()) {
            return 0;
        }

        if (s.charAt(index) == '0') {
            return 0;
        } else {
            return 1;
        }

    }
    
    public static int bitArrayToInt(char[] bits){
        int ret=0;
        for (int j = 0, e = bits.length - 1; j < bits.length; j++, e--) {
            int bit = Integer.parseInt("" + bits[j]);
            ret += (bit == 1 ? Math.pow(2, e) : 0);
        } 
        return ret;
    }
}
