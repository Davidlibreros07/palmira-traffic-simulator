package org.icesi.implementacionintegradora.models;


import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree<T extends Comparable<T>> {

    private Node<T> root;
    private final Object lock = new Object();

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
        }
    }

    public void insert(T data) {
        synchronized(lock) {
            root = insertRec(root, data);
        }
    }

    private Node<T> insertRec(Node<T> root, T data) {
        if (root == null) {
            return new Node<>(data);
        }

        if (data.compareTo(root.data) < 0) {
            root.left = insertRec(root.left, data);
        } else if (data.compareTo(root.data) > 0) {
            root.right = insertRec(root.right, data);
        }

        return root;
    }

    public boolean search(T data) {
        synchronized(lock) {
            return searchRec(root, data);
        }
    }

    private boolean searchRec(Node<T> root, T data) {
        if (root == null) {
            return false;
        }

        if (data.compareTo(root.data) == 0) {
            return true;
        }

        return data.compareTo(root.data) < 0
                ? searchRec(root.left, data)
                : searchRec(root.right, data);
    }

    public List<T> inOrderTraversal() {
        synchronized(lock) {
            List<T> result = new ArrayList<>();
            inOrderRec(root, result);
            return result;
        }
    }

    private void inOrderRec(Node<T> root, List<T> result) {
        if (root != null) {
            inOrderRec(root.left, result);
            result.add(root.data);
            inOrderRec(root.right, result);
        }
    }

    public T findMin() {
        synchronized(lock) {
            if (root == null) return null;
            Node<T> current = root;
            while (current.left != null) {
                current = current.left;
            }
            return current.data;
        }
    }

}