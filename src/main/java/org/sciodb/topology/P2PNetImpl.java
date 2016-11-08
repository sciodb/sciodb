package org.sciodb.topology;

import org.sciodb.messages.impl.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public class P2PNetImpl implements Net {

    private List<Node> nodes;
    public Node[][] matrix;

    private int initial = 10;
    private int maximun;

    public P2PNetImpl() {
        nodes = new LinkedList<>();
        matrix = new Node[initial][initial];
        maximun = (int)Math.pow(initial, initial);
    }

    private void resize() {
        initial++;
        final Node[][] clone = new Node[initial][initial];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                clone[i][j] = matrix[i][j];
            }
        }
        matrix = clone;
        maximun = (int)Math.pow(initial, 2) - maximun;
    }

    @Override
    public synchronized void add(final Node node) {

        if (maximun == 0) {
            resize();
        }

        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] == null) {
                    matrix[j][slice -j] = node;
                    maximun--;
                    stop = true;
                }
            }
        }
        printMatrix(matrix);
        System.out.println("----");
    }

    public void printMatrix(final Node[][] m) {
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != null) {
                    sb.append(" " + 1);
                } else {
                    sb.append(" " + 0);
                }
            }
            System.out.println(sb.toString());
        }
    }

    @Override
    public synchronized void remove(final Node node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
        }
    }

    @Override
    public Position getPosition(final Node node) {
        return null;
    }

    @Override
    public List<Node> getPeers(final Node node) {
        int i = nodes.indexOf(node);
        final List<Node> peers = new ArrayList<>();

        if (i + 1 <= nodes.size()) {
            peers.add(nodes.get(i+1));
        }
        if (i - 1 >= 0) {
            peers.add(nodes.get(i-1));
        }

        return peers;
    }

    @Override
    public List<Node> snapshot() {
        return nodes;
    }

    public static void main(String[] args) {
        final P2PNetImpl p2PNet = new P2PNetImpl();

        int port = 0;
        for (; port < 99; port++) {
            p2PNet.add(new Node("0.0.0.0", port));
        }

        p2PNet.printMatrix(p2PNet.matrix);
    }
}
