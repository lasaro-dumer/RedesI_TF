package tfredes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Joao
 */
public class RouterTable {
    private String dest;
    private String nextHop;
    private int port;

    public RouterTable(String dest, String nextHop, int port) {
        this.dest = dest;
        this.nextHop = nextHop;
        this.port = port;
    }

    public String getDest() {
        return dest;
    }

    public String getNextHop() {
        return nextHop;
    }

    public int getPort() {
        return port;
    }
}
