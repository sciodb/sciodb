package org.sciodb.topology;

import org.sciodb.messages.impl.Node;

import java.util.List;

/**
 * BST tree implementation
 *
 * @author Jesús Navarrete (24/09/16)
 */
public class TreeNetImpl implements Net {

    // operations needed: add, remove, peers, balance,
    private NodeWrapper root;

    @Override
    public void add(final Node node) {
        if (root == null) {
            root = new NodeWrapper();
            root.data = node;
        } else {
            NodeWrapper pointer = root;
            while (true) {
                if (0 <= pointer.data.hash().compareTo(node.hash())) {
                    if (pointer.getRight() == null) {
                        pointer.setRight(new NodeWrapper());
                        pointer = pointer.getRight();
                        break;
                    } else {
                        pointer = pointer.getRight();
                    }
                } else {
                    if (pointer.left == null) {
                        pointer.left = new NodeWrapper();
                        pointer = pointer.left;
                        break;
                    } else {
                        pointer = pointer.left;
                    }
                }
            }
            pointer.data = node;
        }
    }
    // TODO make it private !!!
    public Node search(final Node node) throws Exception {
        if (root == null) {
            throw new Exception("This node is not in the tree");
        } else if (root.data == node) {
            return root.data;
        } else {
            NodeWrapper pointer = root;
            while (true) {
                if (0 <= pointer.data.hash().compareTo(node.hash())) {
                    if (pointer.right != null) {
                        pointer = pointer.right;
                        if (pointer.data.hash().equals(node.hash())) break;
                    } else {
                        throw new Exception("This node is not in the tree");
                    }
                } else {
                    if (pointer.left != null) {
                        pointer = pointer.left;
                        if (pointer.data.hash().equals(node.hash())) break;
                    } else {
                        throw new Exception("This node is not in the tree");
                    }

                }
            }
            return pointer.data;
        }
    }

    @Override
    public void remove(final Node node) {
        if (root == null) return;
        // find and balance ... maybe

    }

    @Override
    public List<Node> getPeers(Node node) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Node> snapshot() {
        throw new RuntimeException("Not implemented");
    }

    class NodeWrapper {
        private Node data;
        private NodeWrapper left;
        private NodeWrapper right;

        public Node getData() {
            return data;
        }

        public void setData(Node data) {
            this.data = data;
        }

        public NodeWrapper getLeft() {
            return left;
        }

        public void setLeft(NodeWrapper left) {
            this.left = left;
        }

        public NodeWrapper getRight() {
            return right;
        }

        public void setRight(NodeWrapper right) {
            this.right = right;
        }
    }
}
