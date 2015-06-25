package jp.dbcls.split4blank;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Atsuko
 */

import java.util.*;

public class ClusterBag implements Comparable<ClusterBag>{
    private int a_ntriples;
    private List<Integer> a_clusterIDs;
    
    public ClusterBag(){
        a_ntriples = 0;
        a_clusterIDs = new LinkedList<Integer>();
    }

    public ClusterBag(int p_cls, int p_ntriples){
        a_ntriples = p_ntriples;
        a_clusterIDs = new LinkedList<Integer>();
        a_clusterIDs.add(new Integer(p_cls));
    }

    
    public int getNTriples(){
        return a_ntriples;
    }
    
    public void add2Bag(int p_ntriples, List<Integer> p_clusters){
        a_ntriples += p_ntriples;
        a_clusterIDs.addAll(p_clusters);
    }
    
    public List<Integer> getClusters(){
        return a_clusterIDs;
    }

    public void addBag(ClusterBag p_bag){
        add2Bag(p_bag.getNTriples(), p_bag.getClusters());
    }
    
    @Override
    public int compareTo(ClusterBag p_bag) {
        return this.a_ntriples - p_bag.getNTriples();
    }
}

