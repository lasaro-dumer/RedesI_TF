/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tfredes;

import java.util.ArrayList;

/**
 *
 * @author Joao
 */
public class Node {
    
    private String nome;
    
    private Tipo tipo;
    
    private ArrayList<Link> links;
    
    
    public Node(String nome, Tipo tipo){
        this.nome = nome;
        this.tipo = tipo;
        this.links = new ArrayList<>();
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public Tipo getTipo(){
        return tipo;
    }
    
    public void addLink(Link link){
        this.links.add(link);
    }
    
    public ArrayList<Link> getLinks(){
        return links;
    }

}
