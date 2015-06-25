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

public class BlankRDFGraph extends MyRDFGraph{
    ArrayList<Integer> a_bnodes; // blank nodes
    HashMap<Integer, Integer> a_bhash; // 
    int[] a_bcluster; // blanknode cluster
    long a_ntriples; // number of all triples (not only for blank node)
    long a_btriples; // number of triples with blank node
    ArrayList<LinkedList<BEdge>> a_bedges;

    public class BEdge{
        boolean a_direction;
        int a_pn;
        int a_nn;
        public BEdge(boolean p_d, int p_pn, int p_nn){
            a_direction = p_d;
            a_pn = p_pn;
            a_nn = p_nn;
        }
    } 
     
    public BlankRDFGraph(){
        super();
        a_bnodes = new ArrayList<Integer>(); 
    }
    
    public boolean checkBSize(int p_split){
        if ( a_ntriples / p_split > a_btriples ){
            return true;
        }
        return false;
    }

    public void createBEdges(){
        a_bedges = new ArrayList<LinkedList<BEdge>>();
        /*boolean[] w_b = new boolean[a_edges.size()];
        for ( int i = 0; i < a_edges.size(); i++ ){
            w_b[i] = false;
        }*/
        for ( int i = 0; i < a_bnodes.size(); i++ ){
            a_bedges.add(new LinkedList<BEdge>());
            Integer w_bn = a_bnodes.get(i);
            //w_b[w_bn] = true;
        }
        for ( int i = 0 ; i < a_edges.size(); i++ ){
            LinkedList<MyEdge> w_edges = a_edges.get(i);
            ListIterator<MyEdge> w_eit = w_edges.listIterator();
            while( w_eit.hasNext() ){
                MyEdge e = w_eit.next();
                if ( a_bhash.containsKey(i) ){ // blank
                    a_bedges.get(a_bhash.get(i)).add(new BEdge(true, e.a_pn, e.a_on)); 
                }else{
                    if ( a_bhash.containsKey(e.a_on) == false){
                        System.err.println("Both are not blank");
                    }
                    int w_bon = a_bhash.get(e.a_on);
                    a_bedges.get(w_bon).add(new BEdge(false, e.a_pn, i));
                }     
            }
            w_edges.clear();
        }
    }
    
    public void addStatement(String p_s, String p_p, String p_o){ 
        a_btriples ++;
        int w_sn, w_pn, w_on;
        if ( a_nhash.containsKey(p_s) ){
            w_sn = a_nhash.get(p_s);
        }else{
            w_sn = a_nodes.size();
            a_nodes.add(p_s);
            a_nhash.put(p_s, w_sn);
            if ( p_s.startsWith("_:")){
                a_bnodes.add(w_sn);
            }
            a_edges.add(new LinkedList<MyEdge>());
            //a_revedges.add(new LinkedList<MyEdge>());
        }
        if ( a_phash.containsKey(p_p) ){
            w_pn = a_phash.get(p_p);
        }else{
            w_pn = a_preds.size();
            a_preds.add(p_p);
            a_phash.put(p_p, w_pn);
        }
        if ( a_nhash.containsKey(p_o) ){
            w_on = a_nhash.get(p_o);
        }else{
            w_on = a_nodes.size();
            a_nodes.add(p_o);
            a_nhash.put(p_o, w_on);
            if ( p_o.startsWith("_:")){
                a_bnodes.add(w_on);
            }
            a_edges.add(new LinkedList<MyEdge>());
            //a_revedges.add(new LinkedList<MyEdge>());
        }
        a_edges.get(w_sn).add(new MyEdge(w_pn, w_on));        
        //a_revedges.get(w_on).add(new MyEdge(w_pn, w_sn));
    }
    
    public void makeBlankNodeCluster(){
        a_bhash = new HashMap<Integer, Integer>();
        ArrayList<LinkedList<Integer>> w_badjlist = new ArrayList<LinkedList<Integer>>();
        for ( int i = 0; i < a_bnodes.size(); i++){
            a_bhash.put(a_bnodes.get(i), i);
        }
        for ( int i = 0; i < a_bnodes.size(); i++){
            LinkedList<Integer> w_badj = new LinkedList<Integer>();
            LinkedList<MyEdge> w_adj = a_edges.get(i);
            ListIterator<MyEdge> w_eit = w_adj.listIterator();
            while( w_eit.hasNext()){
                MyEdge w_edge = w_eit.next();
                if ( a_bhash.containsKey(w_edge.a_on)){
                    Integer w_bnode = a_bhash.get(w_edge.a_on);
                    w_badj.add(w_bnode);
                }
            }
            w_badjlist.add(w_badj);
        }
        
        a_bcluster = GraphTools.connectedComponent(w_badjlist);
    }
    
    public List<ClusterBag> packClusters(int p_split){
        List<ClusterBag> w_ff = new ArrayList<ClusterBag>();
        for (int i = 0; i < p_split; i++ ){
            w_ff.add(new ClusterBag());
        }
        
        int w_nc = 0;
        for (int i = 0 ; i < a_bcluster.length; i++){
            if (w_nc < a_bcluster[i]){
                w_nc = a_bcluster[i];
            }
        }
        
        List<ClusterBag> w_enum = new LinkedList<ClusterBag>();
        int[] w_enum4cls = new int[w_nc + 1]; // the number of triples for each blank clusters
        for (int i = 0 ; i < w_nc + 1 ; i++){ 
            w_enum4cls[i] = 0;
        }
        int w_n = a_edges.size();        
        for ( int i = 0 ; i < w_n; i++ ){ // for all nodes
            // check wherther blanknode or not
            Integer w_sb = a_bhash.get(i);
            if ( w_sb != null ){ // if blank
                w_enum4cls[a_bcluster[w_sb]] += a_edges.get(i).size();
            }else{
                LinkedList<MyRDFGraph.MyEdge> w_el = a_edges.get(i);
                ListIterator<MyRDFGraph.MyEdge> w_eit = w_el.listIterator();
                while ( w_eit.hasNext() ){
                    MyRDFGraph.MyEdge w_edge = w_eit.next();
                    Integer w_ob = a_bhash.get(w_edge.a_on);
                    if ( w_ob != null ){ // blank
                        w_enum4cls[a_bcluster[w_ob]] ++;
                    }else{
                        System.out.println("Both nodes are not blank");
                    }
                }
            }
        }
        for (int i = 0 ; i < w_nc + 1; i++){
           w_enum.add(new ClusterBag(i, w_enum4cls[i])); 
        }
        
        Collections.sort(w_enum);

        ListIterator<ClusterBag> w_eit = w_enum.listIterator(w_enum.size());
        while ( w_eit.hasPrevious() ){ // descending order
            ClusterBag w_crr = w_eit.previous();
            //System.out.println(w_crr.getNTriples());
            ClusterBag w_minb = w_ff.get(0);
            for (int i = 1; i < p_split; i++ ){
                ClusterBag w_crrb = w_ff.get(i);
                if ( w_crrb.getNTriples() < w_minb.getNTriples()){
                    w_minb = w_crrb;
                }
            }
            w_minb.addBag(w_crr);
        }
        
        // For debug
        /*
        System.out.println("#triples");
        Iterator<ClusterBag> w_it = w_ff.iterator();
        while ( w_it.hasNext()){
            System.out.println(w_it.next().getNTriples());
        }
        */
        System.out.println("End");
        return w_ff;
    }
    
    public List<String> transformClusterBag2Triples(ClusterBag p_bag){ //
        List<String> w_triples = new LinkedList<String>();
        //ListIterator<Integer> w_cit = p_bag.getClusters().listIterator();
        Set<String> w_nodes = new HashSet<String>(); // (int s node id)-(int pred id)-(int o node id)
        HashSet<Integer> w_bags = new HashSet(p_bag.getClusters());        
        for ( int i = 0 ; i < a_bcluster.length; i++ ){ 
            if ( w_bags.contains(a_bcluster[i]) ){
                Integer w_node = a_bnodes.get(i);              
                LinkedList<BEdge> w_el = a_bedges.get(i);
                ListIterator<BEdge> w_eit = w_el.listIterator();
                while ( w_eit.hasNext()){
                    BEdge w_edge = w_eit.next();
                    if ( w_edge.a_direction ){
                        w_nodes.add(w_node.toString().concat("-").concat(Integer.toString(w_edge.a_pn))
                                    .concat("-").concat(Integer.toString(w_edge.a_nn))); 
                    }else{
                            w_nodes.add(Integer.toString(w_edge.a_nn).concat("-").concat(Integer.toString(w_edge.a_pn))
                                    .concat("-").concat(w_node.toString()));                             
                    }
                }
            }
        }
        // change nodes to triples
        Iterator<String> w_nit = w_nodes.iterator();
        while( w_nit.hasNext() ){
            String w_t = w_nit.next();
            String[] w_spo = w_t.split("-");
            Integer w_snode = Integer.parseInt(w_spo[0]);
            Integer w_pid = Integer.parseInt(w_spo[1]);
            Integer w_onode = Integer.parseInt(w_spo[2]);
            String triple = a_nodes.get(w_snode).concat(" ").concat(a_preds.get(w_pid)).concat(" ").concat(a_nodes.get(w_onode));
            w_triples.add(triple);
        }
        return w_triples;
    }
    
    public List<String> transformGraph2Triples(){ //
        List<String> w_triples = new LinkedList<String>();
        Set<String> w_nodes = new HashSet<String>(); // (int s node id)-(int pred id)-(int o node id)
        for ( int i = 0 ; i < a_nodes.size(); i++ ){
            LinkedList<MyRDFGraph.MyEdge> w_el = a_edges.get(i);
            ListIterator<MyRDFGraph.MyEdge> w_eit = w_el.listIterator();
            while ( w_eit.hasNext()){
                  MyRDFGraph.MyEdge w_edge = w_eit.next();
                  w_nodes.add(Integer.toString(i).concat("-").concat(Integer.toString(w_edge.a_pn)).concat("-").concat(Integer.toString(w_edge.a_on))); 
            }
        }
        // change nodes to triples
        Iterator<String> w_nit = w_nodes.iterator();
        while( w_nit.hasNext() ){
            String t = w_nit.next();
            String[] spo = t.split("-");
            Integer snode = Integer.parseInt(spo[0]);
            Integer pid = Integer.parseInt(spo[1]);
            Integer onode = Integer.parseInt(spo[2]);
            String triple = a_nodes.get(snode).concat(" ").concat(a_preds.get(pid)).concat(" ").concat(a_nodes.get(onode));
            w_triples.add(triple);
        }
        return w_triples;
    }    
}
