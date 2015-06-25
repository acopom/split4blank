package jp.dbcls.split4blank;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Atsuko
 */

import java.util.*;
//import com.hp.hpl.jena.rdf.model.*;

public class MyRDFGraph extends MyGraph{
    ArrayList<String> a_nodes;
    HashMap<String, Integer> a_nhash;
    
    ArrayList<String> a_preds;
    HashMap<String, Integer> a_phash;
    ArrayList<LinkedList<MyEdge>> a_edges;
    //ArrayList<LinkedList<MyEdge>> a_revedges;
    public class MyEdge{
        int a_pn;
        int a_on;
        public MyEdge(){}
        public MyEdge(int p_pn, int p_on){
            a_pn = p_pn;
            a_on = p_on;
        }
    }
    
    public MyRDFGraph(){
        super();
        a_nodes = new ArrayList<String>();
        a_nhash = new HashMap<String, Integer>();
        a_preds = new ArrayList<String>();
        a_phash = new HashMap<String, Integer>();
        a_edges = new ArrayList<LinkedList<MyEdge>>();
        //a_revedges = new ArrayList<LinkedList<MyEdge>>();
    }
    
    /*
    public void createRevEdges(){
        a_revedges.clear();
        for ( int i = 0; i < a_edges.size(); i++ ){
            a_revedges.add(new LinkedList<MyEdge>());
        }
        for ( int i = 0; i < a_edges.size(); i++ ){
            LinkedList<MyEdge> w_edges = a_edges.get(i);
            ListIterator<MyEdge> w_eit = w_edges.listIterator();
            while( w_eit.hasNext() ){
                MyEdge e = w_eit.next();
                a_revedges.get(e.a_on).add(new MyEdge(e.a_pn, i));
            }
        }
    }
    */
    
    public void makeSimpleGraph(){
        a_adjlist.clear();
        for (int i = 0; i < a_edges.size(); i++ ){
            LinkedList<Integer> w_adj = new LinkedList<Integer>();
            a_adjlist.add(w_adj);        
        }
        //HashSet<Integer> w_os = new HashSet<Integer>();
        for (int i = 0; i < a_edges.size(); i++ ){
            LinkedList<MyEdge> w_myedges = a_edges.get(i);
            ListIterator<MyEdge> w_mit = w_myedges.listIterator();
            while (w_mit.hasNext()){
                int w_on = w_mit.next().a_on;
                a_adjlist.get(i).add(w_on);
                a_adjlist.get(w_on).add(i);
            }            
        }
    }
    
}
