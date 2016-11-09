package org.sciodb.topology;

import org.sciodb.messages.impl.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public class P2PNetImpl implements Net {

    private Node[][] matrix;

    private int initial = 10;
    private int maximum;

    public P2PNetImpl() {
        matrix = new Node[initial][initial];
        maximum = (int)Math.pow(initial, initial);
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
        maximum = (int)Math.pow(initial, 2) - maximum;
    }

    @Override
    public synchronized void add(final Node node) {

        if (maximum == 0) {
            resize();
        }

        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] == null) {
                    matrix[j][slice - j] = node;
                    maximum--;
                    stop = true;
                }
            }
        }
        printMatrix(matrix);
        System.out.println("----");
    }

    private void printMatrix(final Node[][] matrix) {
        for (final Node[] array : matrix) {
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < matrix.length; j++) {
                if (array[j] != null) {
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
        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] != null && matrix[j][slice -j].hash().equals(node.hash())) {
                    matrix[j][slice - j] = null;
                    stop = true;
                }
            }
        }
    }

    @Override
    public Position getPosition(final Node node) {
        return null;
    }

    @Override
    public List<Node> getPeers(final Node node) {
        final List<Node> peers = new ArrayList<>();

        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] != null && matrix[j][slice -j].hash().equals(node.hash())) {
                    for (int i = ((j - 1 >= 0)? (j - 1) : 0); i <= j + 1; i++ ) {
                        if (i >= 0) {
                            for (int k = ((slice - j - 1) >= 0? (slice - j - 1) : 0); k <= (slice - j + 1); k++) {
                                if (matrix[i][k] != null && !matrix[i][k].hash().equals(node.hash())) peers.add(matrix[i][k]);
                            }
                        }
                    }
                    stop = true;
                }
            }
        }

        return peers;
    }

    @Override
    public List<Node> snapshot() {
        final List<Node> nodes = new ArrayList<>();

        for (final Node[] array : matrix) {
            for (int j = 0; j < matrix.length; j++) {
                if (array[j] != null) {
                    nodes.add(array[j]);
                }
            }
        }
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
