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
public class MacIP {
    
    private String MAC;
    private String IP;

    public MacIP(String MAC, String IP) {
        this.MAC = MAC;
        this.IP = IP;
    }
    
    
    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
