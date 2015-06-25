package jp.dbcls.split4blank;

/*
 * GraphTools.java
 *
 * Created on 2008/12/16, 16:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author atsuko
 */


import java.util.*;

public class GraphTools {
    
    /** Creates a new instance of GraphTools */
    public GraphTools() {
    }
    
    public static int[] connectedComponent(boolean[][] p_mat){
        if ( p_mat.length != p_mat[0].length ){
            System.out.println("Error of matrix");
            return null;
        }
        int[] w_compo = new int[p_mat.length];
        // BFS
        for ( int i = 0; i < w_compo.length ; i ++ ){
            w_compo[i] = -1;
        }
        int w_cn = 0;
        for ( int i = 0; i < w_compo.length ; i ++ ){
            if ( w_compo[i] != -1 ){
                continue;
            }
            LinkedList<Integer> w_queue = new LinkedList<Integer>();
            w_compo[i] = w_cn;
            int w_crrnode = i;
            while (true){
                for ( int j = 0 ; j < w_compo.length; j++ ){
                    if ( p_mat[w_crrnode][j] == true && w_compo[j] == -1 ){
                        w_compo[j] = w_cn;
                        w_queue.addLast(j);
                    }
                }
                if ( w_queue.size() == 0 ){
                    break;
                }
                else{
                    w_crrnode = w_queue.removeFirst();
                }
            }
            w_cn ++;
        }
        return w_compo;
    }

    public static int[] connectedComponent(ArrayList<LinkedList<Integer>> p_alist){
        if ( p_alist == null ){
            System.out.println("Error of Adj List");
            return null;
        }
        
        int[] w_compo = new int[p_alist.size()];
        // BFS
        for ( int i = 0; i < w_compo.length ; i ++ ){
            w_compo[i] = -1;
        }
        int w_cn = 0;
        for ( int i = 0; i < w_compo.length ; i ++ ){
            if ( w_compo[i] != -1 ){
                continue;
            }
            LinkedList<Integer> w_queue = new LinkedList<Integer>();
            w_compo[i] = w_cn;
            int w_crrnode = i;
            while (true){
                Iterator<Integer> w_ait = p_alist.get(w_crrnode).iterator();
                while ( w_ait.hasNext()){
                    int w_node = w_ait.next().intValue();
                    if ( w_compo[w_node] == -1 ){
                        w_compo[w_node] = w_cn;
                        w_queue.addLast(w_node);                        
                    }
                }
                if ( w_queue.size() == 0 ){
                    break;
                }
                else{
                    w_crrnode = w_queue.removeFirst();
                }
            }
            w_cn ++;
        }
        return w_compo;
    }
}
