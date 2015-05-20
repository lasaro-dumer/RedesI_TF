/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tfredes;

/**
 *
 * @author Joao
 */
public class Switch extends Node{

    private int num_ports;
    
    public Switch(String nome, int num_ports) {
        super(nome, Tipo.SWITCH);
        this.num_ports = num_ports;
    }
    
    public int getNum_ports() {
        return num_ports;
    }
}
