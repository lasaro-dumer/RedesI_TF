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
public class Link {
    private String src;
    private int src_port;
    private String dest;
    private int dest_port;

    public Link(String src, int src_port, String dest, int dest_port) {
        this.src = src;
        this.src_port = src_port;
        this.dest = dest;
        this.dest_port = dest_port;
    }

    public String getSrc() {
        return src;
    }

    public int getSrc_port() {
        return src_port;
    }

    public String getDest() {
        return dest;
    }

    public int getDest_port() {
        return dest_port;
    }
}
