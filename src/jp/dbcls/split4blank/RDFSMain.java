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
import java.io.*;

public class RDFSMain {

    static int a_split = 1;
    static String a_filename = null;
    static String a_tmptriplefile = null;
    static String a_nodelistfile = null;
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        a_filename = args[0];
        a_split = Integer.parseInt(args[1]);
        a_tmptriplefile = "triples.txt";
        a_nodelistfile = "nodetmp.txt";
        System.out.println("Loading file...");
        BlankRDFGraph w_graph = RDFSIO.loadNTwithBlank(a_filename, a_tmptriplefile);
        System.out.println("End");
        
        if ( w_graph.checkBSize(a_split) ){
            System.out.println("Writing files ...");
            RDFSIO.writeBEdgesInOne(w_graph, a_split, a_filename, a_tmptriplefile);
            System.out.println("End");
            return;
        }
        
        System.out.println("Writing nodes ...");
        RDFSIO.writeNodes(w_graph, a_nodelistfile);
        System.out.println("End");
        w_graph.a_nodes.clear();
        w_graph.a_nhash.clear();
        w_graph.a_phash.clear();        
        
        System.out.println("Computing CC ...");
        w_graph.makeBlankNodeCluster();
        System.out.println("End");
        
        System.out.println("Computing Packing ...");
        List<ClusterBag> w_packedbags = w_graph.packClusters(a_split);
        System.out.println("End");

        System.out.println("Preparing for writing files ...");
        w_graph.createBEdges();
        w_graph.a_bhash.clear();
        w_graph.a_edges.clear();
        RDFSIO.loadNodes(w_graph, a_nodelistfile);
        System.out.println("End");

        System.out.println("Writing files ...");
        RDFSIO.writePackedEdges(w_graph, w_packedbags, w_packedbags.size(), a_filename, a_tmptriplefile);
        System.out.println("End");
    }
    
    /* For test
    public static void countBNC(BlankRDFGraph p_graph){
        if ( p_graph.a_bcluster == null ){ System.err.println("No data");}
        
        System.out.println("Number of blank nodes: ".concat(String.valueOf(p_graph.a_bcluster.length)));
        int w_nc = 0;
        for (int i = 0 ; i < p_graph.a_bcluster.length; i++){
            if (w_nc < p_graph.a_bcluster[i]){
                w_nc = p_graph.a_bcluster[i];
            }
        }
        System.out.println("Number of blank clusters: ".concat(String.valueOf( w_nc + 1 )));
        
        int[] w_csize = new int[w_nc + 1];
        int[] w_esize = new int[w_nc + 1];
        int w_maxe = 0;
        int w_maxenode = 0;
        for (int i = 0 ; i < p_graph.a_bcluster.length; i++){
           w_csize[p_graph.a_bcluster[i]]++;
           w_esize[p_graph.a_bcluster[i]] += p_graph.a_edges.get(p_graph.a_bnodes.get(i)).size();
           if ( w_maxe < p_graph.a_edges.get(p_graph.a_bnodes.get(i)).size()){
               w_maxenode = p_graph.a_bnodes.get(i);
               w_maxe = p_graph.a_edges.get(p_graph.a_bnodes.get(i)).size();
           }
        }
        System.out.println("Node with maximum number of edges: ".concat(String.valueOf( w_maxenode )));
        System.out.println("The number of edges: ".concat(String.valueOf( w_maxe )));
        
        p_graph.a_edges.clear();
        
        System.out.println("Writing result...");
        RDFSIO.writeResult(w_esize);
        System.out.println("Done");
    }
    
    public static int countEdge(BlankRDFGraph p_graph){
        int w_count = 0;
        for (int i = 0; i < p_graph.a_edges.size(); i++){
            w_count += p_graph.a_edges.get(i).size();
        }
        return w_count;
    }
    */
}
