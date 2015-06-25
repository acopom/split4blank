package jp.dbcls.split4blank;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Atsuko
 */

import java.io.*;
import java.util.*;

public class RDFSIO {
        
    public static BlankRDFGraph loadNTwithBlank(String p_filename, String p_tmpfile){
        BlankRDFGraph w_rdfgraph = new BlankRDFGraph();
        File w_file = new File(p_filename);
        File w_tmp = new File(p_tmpfile);
        try{
            BufferedReader w_br = new BufferedReader(new FileReader(w_file));
            BufferedWriter w_bw = new BufferedWriter(new FileWriter(w_tmp));
            String w_buf;
            long w_count = 0;
            while ( (w_buf = w_br.readLine()) != null ){
                if ( w_buf.length() == 0 ){continue;}
                String[] w_triple = w_buf.split("[\\s]+");
                if ( w_triple.length < 3 ){ continue; }
                String w_o = w_triple[2];
                if ( w_triple[2].charAt(w_triple[2].length() - 1) == '.' ){
                    w_o = w_triple[2].substring(0, w_triple[2].length() - 1).trim();
                }
                if ( w_triple[0].startsWith("_:") || w_o.startsWith("_:")){
                    w_rdfgraph.addStatement(w_triple[0], w_triple[1], w_o);
                }else{
                    // write the triple to the work file
                    w_bw.write(w_buf); w_bw.newLine();
                }
                if (w_count == ( w_count / 1000000)*1000000 ){
                    System.out.println(w_count);
                }
                w_count ++;
            }
            w_rdfgraph.a_ntriples = w_count;
            w_br.close();
            w_bw.close();
        }catch(IOException e){
          System.err.println(e);
        }

        return w_rdfgraph;
    }
    
    public static void writeResult(int[] p_esize){
        //Arrays.sort(p_esize);
        int w_max = 0;
        int[] w_result = new int[15]; // 0 - 9 => 1 - 10, 10 => 11 - 100, 11 => 101 - 1000, 12 => 1001-10001 
        for (int i = 0 ; i < 15 ; i++ ){
            w_result[i] = 0;
        }
        for (int i = 0 ; i < p_esize.length; i++ ){
            if ( w_max < p_esize[i] ){
                w_max = p_esize[i];
            }
            if ( p_esize[i] <= 10 ){
                w_result[p_esize[i] - 1] ++;
            }
            else if ( p_esize[i] <= 100 ){
                w_result[10]++;
            }
            else if ( p_esize[i] <= 1000 ){
                w_result[11]++;
            }
            else if ( p_esize[i] <= 10000 ){
                w_result[12]++;
            }
            else if ( p_esize[i] <= 100000 ){
                w_result[13]++;
            }
            else {
               w_result[14]++; 
            }
        }
        File w_file = new File("result");
        try{
            BufferedWriter w_bw = new BufferedWriter(new FileWriter(w_file));
            w_bw.write("Number of Edges of Max Cluster: ");
            w_bw.write(String.valueOf(w_max));
            w_bw.newLine(); w_bw.newLine();
            w_bw.write("Distribution: ");  
            w_bw.newLine();
            for (int i = 0; i < 15 ; i++ ){
                w_bw.write(String.valueOf(w_result[i]));
                w_bw.newLine();
            }
            //for (int i = 0; i < p_esize.length; i++ ){
            //    w_bw.write(String.valueOf(p_esize[i]));
            //    w_bw.newLine();
            //}
            
            w_bw.close();
        }catch(IOException e){
          System.err.println(e);
        }
    }
    
    public static void writeNodes(MyRDFGraph p_graph, String p_filename){
        File w_file = new File(p_filename);
        try{
            BufferedWriter w_bw = new BufferedWriter(new FileWriter(w_file));
            for (int i = 0; i < p_graph.a_nodes.size(); i++ ){
                w_bw.write(p_graph.a_nodes.get(i));
                w_bw.newLine();
            }
            w_bw.close();
        }catch(IOException e){
          System.err.println(e);
        }        
    }
    
    public static void loadNodes(MyRDFGraph p_graph, String p_filename){
        File w_file = new File(p_filename);
        try{
            BufferedReader w_br = new BufferedReader(new FileReader(w_file));
            String w_buf;            
            while ( (w_buf = w_br.readLine()) != null ){
                p_graph.a_nodes.add(w_buf);
            }
            w_br.close();
        }catch(IOException e){
          System.err.println(e);
        }        
    }

    public static void writePackedEdges(BlankRDFGraph p_graph, List<ClusterBag> p_bags, int p_split, 
            String p_orgfile, String p_triplefile){
        if ( p_bags.size() != p_split ){
            System.err.println("#packed bag does not equal to #split.");
        }

        // Preparing files for splitted data
        File[] w_files = new File[p_split];
        int w_len = String.valueOf(p_split).length();
        for (int i = 0 ; i < p_split; i++){
            String w_i = Integer.toString(i);
            StringBuilder w_pud = new StringBuilder("");
            for ( int j = 0 ; j < w_len - w_i.length() ; j++ ){
                w_pud.append("0");
            }
            w_pud.append(w_i).append("_").append(p_orgfile);
            w_files[i] = new File(w_pud.toString());
        }
        
        BufferedWriter[] w_bws = new BufferedWriter[p_split]; 
        List<Integer> w_under = new LinkedList<Integer>();
        try{
            // pack for triples with blank nodes
            ListIterator<ClusterBag> w_bit = p_bags.listIterator();            
            for ( int i = 0; i < p_split; i++ ){
                w_bws[i] = new BufferedWriter(new FileWriter(w_files[i]));
                ClusterBag w_bag = w_bit.next(); 
                if ( w_bag.getNTriples() < ((double) p_graph.a_ntriples / (double) p_split - 1) ){
                    w_under.add(i);
                }
                System.out.println(Integer.toString(i).concat("th bag: ".concat(Integer.toString(w_bag.getNTriples()))));
                System.out.println("cls: ".concat(Integer.toString(w_bag.getClusters().size())));
                List<String> w_triples = p_graph.transformClusterBag2Triples(w_bag);
                System.out.println("triples generated: ".concat(Integer.toString(w_triples.size())));
                ListIterator<String> w_tit = w_triples.listIterator();
                while ( w_tit.hasNext() ){
                    w_bws[i].write(w_tit.next());
                    w_bws[i].write(" .");
                    w_bws[i].newLine();
                }
            }
            System.out.println("Start padding...");            
            // pack for the other triples            
            ListIterator<Integer> w_it = w_under.listIterator();
            BufferedReader w_br = new BufferedReader(new FileReader(new File(p_triplefile)));
            String w_buf;
            int w_th = (int) Math.ceil(((double)( p_graph.a_ntriples)) / ((double)p_split));
            while( w_it.hasNext() ){
                Integer w_i = w_it.next();
                int w_nt = p_bags.get(w_i).getNTriples();
                for ( int i = w_nt + 1; i <= w_th ; i++ ){
                    w_buf = w_br.readLine();
                    if ( w_buf == null){
                        break;
                    }
                    w_bws[w_i].write(w_buf);
                    w_bws[w_i].newLine();
                }
            }
            w_br.close();
            for (int i = 0 ; i < p_split; i++ ){
                w_bws[i].close();
            }            
        }catch(IOException e){
            System.err.println(e);
        }
    }

    public static void writeBEdgesInOne(BlankRDFGraph p_graph, int p_split, String p_orgfile, String p_triplefile){
        // Preparing files for splitted data
        File[] w_files = new File[p_split];
        int w_len = String.valueOf(p_split).length();
        for (int i = 0 ; i < p_split; i++){
            String w_i = Integer.toString(i);
            StringBuilder w_pud = new StringBuilder("");
            for ( int j = 0 ; j < w_len - w_i.length() ; j++ ){
                w_pud.append("0");
            }
            w_pud.append(w_i).append("_").append(p_orgfile);
            w_files[i] = new File(w_pud.toString());
        }
        BufferedWriter[] w_bws = new BufferedWriter[p_split];
        try{
            BufferedReader w_br = new BufferedReader(new FileReader(new File(p_triplefile)));
            // pack for triples with blank nodes
            for (int i = 0 ; i < p_split; i++ ){
                w_bws[i] = new BufferedWriter(new FileWriter(w_files[i]));
            }
            // For first bag
            List<String> w_triples = p_graph.transformGraph2Triples();
            ListIterator<String> w_tit = w_triples.listIterator();
            while ( w_tit.hasNext() ){
                w_bws[0].write(w_tit.next());
                w_bws[0].write(" .");
                w_bws[0].newLine();
            }
            String w_buf;
            int w_th = (int) Math.ceil(((double)( p_graph.a_ntriples)) / ((double)p_split));
            for ( int i = w_triples.size() ; i < w_th ; i++){
                w_buf = w_br.readLine();
                if ( w_buf == null){
                    for (int j = 0; j < p_split; j++ ){
                        w_bws[j].close();
                    }
                    w_br.close();
                    return;
                }
                w_bws[0].write(w_buf);
                w_bws[0].newLine();                
            }
            
            for ( int i = 1; i < p_split; i++ ){
                for ( int k = 0 ; k < w_th; k++ ){ 
                    w_buf = w_br.readLine();
                    if ( w_buf == null){
                        for (int j = 0; j < p_split; j++ ){
                            w_bws[j].close();
                        }
                        w_br.close();
                        return;
                    }
                    w_bws[i].write(w_buf);
                    w_bws[i].newLine(); 
                }
            }
            for (int j = 0; j < p_split; j++ ){
                w_bws[j].close();
            }
                w_br.close();
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    /*
    public static void writePackedEdgesWithBigbag(BlankRDFGraph p_graph, List<ClusterBag> p_bags, 
            String p_orgfile, String p_triplefile){
        // Get the largest bag
        ListIterator<ClusterBag> w_bit = p_bags.listIterator();
        int w_maxsize = 0;
        ClusterBag w_maxbag = null;
        while( w_bit.hasNext() ){
            ClusterBag w_bag = w_bit.next();
            if ( w_maxsize < w_bag.getNTriples() ){
                w_maxbag = w_bag;
                w_maxsize = w_bag.getNTriples();
            }
        }
        
        // Preparing files for splitted data
        File[] w_files = new File[p_bags.size()];
        int w_len = String.valueOf(p_bags.size()).length();
        for (int i = 0 ; i < p_bags.size(); i++){
            String w_i = Integer.toString(i);
            StringBuilder w_pud = new StringBuilder("");
            for ( int j = 0 ; j < w_len - w_i.length() ; j++ ){
                w_pud.append("0");
            }
            w_pud.append(w_i).append("_").append(p_orgfile);
            w_files[i] = new File(w_pud.toString());
        }
        
        BufferedWriter[] w_bws = new BufferedWriter[p_bags.size()]; 
        int w_bindex = 0; // index for p_bags
        try{
            BufferedReader w_br = new BufferedReader(new FileReader(new File(p_triplefile)));
            for ( int i = 0; i < p_bags.size(); i++ ){
                w_bws[i] = new BufferedWriter(new FileWriter(w_files[i]));
                List<ClusterBag> w_bags = new LinkedList<ClusterBag>();
                int w_num = 0;
                for ( ; ; ){ // add bags
                    if ( w_bindex >= p_bags.size() ){
                        break;
                    }
                    ClusterBag w_bag = p_bags.get(w_bindex);
                    w_num += w_bag.getNTriples();
                    if ( w_num > w_maxsize ){
                        break;
                    }
                    w_bags.add(w_bag);
                    w_bindex ++;
                }
                
                // KOKO
                //List<String> w_triples = p_graph.transformClusterBag2Triples();
                
                
                //if ( w_bag.getNTriples() < ((double) p_graph.a_ntriples / (double) p_split - 1) ){
                //    w_under.add(i);
                //}
                ListIterator<String> w_tit = w_triples.listIterator();
                while ( w_tit.hasNext() ){
                    w_bws[i].write(w_tit.next());
                    w_bws[i].write(" .");
                    w_bws[i].newLine();
                }
            }
            
            // pack for the other triples            
            ListIterator<Integer> w_it = w_under.listIterator();
            String w_buf;
            int w_th = (int) Math.ceil(((double)( p_graph.a_ntriples)) / ((double)p_split));
            while( w_it.hasNext() ){
                Integer w_i = w_it.next();
                int w_nt = p_bags.get(w_i).getNTriples();
                for ( int i = w_nt + 1; i <= w_th ; i++ ){
                    w_buf = w_br.readLine();
                    if ( w_buf == null){
                        break;
                    }
                    w_bws[w_i].write(w_buf);
                    w_bws[w_i].newLine();
                }
            }
            w_br.close();
            for (int i = 0 ; i < p_split; i++ ){
                w_bws[i].close();
            }            
        }catch(IOException e){
            System.err.println(e);
        }
    }*/
}
