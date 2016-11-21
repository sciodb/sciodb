package org.sciodb.topology.impl;

import org.sciodb.messages.impl.Node;
import org.sciodb.topology.Net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public class MatrixNetImpl implements Net {

    private Node[][] matrix;

    private int counter = 0;
    private int initial = 10;
    private int maximum;

    public MatrixNetImpl() {
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
    public boolean contains(Node node) {
        return false;
    }

    @Override
    public Node first() {
        return matrix[0][0];
    }

    @Override
    public synchronized void add(final Node node) {

        if (node == null) return;

        if (maximum == 0) resize();

        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] != null && matrix[j][slice - j].hash().equals(node.hash())) {
                    stop = true;
                } else if (matrix[j][slice - j] == null) {
                    matrix[j][slice - j] = node;
                    maximum--;
                    counter++;
                    stop = true;
                }
            }
        }
        printMatrix(matrix);
    }

    @Override
    public void addAll(List<Node> nodes) {
        for (final Node n: nodes) {
            add(n);
        }

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
        System.out.println("----");
    }

    @Override
    public synchronized void remove(final Node node) {
        if (node == null) return;

        boolean stop = false;

        for (int slice = 0; slice < 2 * initial - 1 && !stop; ++slice) {
            int z = slice < initial ? 0 : slice - initial + 1;
            for (int j = z; j <= slice - z && !stop; ++j) {
                if (matrix[j][slice - j] != null && matrix[j][slice -j].hash().equals(node.hash())) {
                    matrix[j][slice - j] = null;
                    counter--;
                    stop = true;
                }
            }
        }
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

    @Override
    public Iterator<Node> iterator() {
        return snapshot().iterator();
    }

    @Override
    public boolean isEmpty() {
        return (counter == 0);
    }

    @Override
    public int size() {
        return counter;
    }
}
