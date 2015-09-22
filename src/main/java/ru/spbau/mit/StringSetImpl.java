package ru.spbau.mit;

import java.io.*;
import java.util.Vector;

/**
 * Created by equi on 22.09.15.
 *
 * @author Kravchenko Dima
 */

public class StringSetImpl implements StringSet, StreamSerializable {
    private int size = 0;
    private final BorNode root = new BorNode('.');
    public static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ#";

    public boolean add(String element) {
        if (contains(element)) {
            return false; // we have set, not multiset, as I understood
        }
        element += "#";

        BorNode current = root;
        root.count++;
        for (char ch : element.toCharArray()) {
            if (current.hasSon(ch)) {
                current = current.getSon(ch);
            } else {
                current.addSon(ch);
                current = current.getSon(ch);
            }
            current.count++;
        }

        current.stringEnd = true;

        size++;
        return true;
    }

    public boolean contains(String element) {
        element += "#";

        BorNode current = root;
        for (char ch : element.toCharArray()) {
            if (current.hasSon(ch)) {
                current = current.getSon(ch);
            } else {
                return false;
            }
        }

        return current.stringEnd;
    }

    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        element += "#";

        BorNode current = root;
        root.count--;
        for (char ch : element.toCharArray()) {
            current = current.getSon(ch);
            current.count--;
        }

        current.stringEnd = false;
        size--;
        return true;
    }

    public int size() {
        return size;
    }

    public int howManyStartsWithPrefix(String prefix) {
        BorNode current = root;
        for (char ch : prefix.toCharArray()) {
            if (current.hasSon(ch)) {
                current = current.getSon(ch);
            } else {
                return 0;
            }
        }

        return current.count;
    }

    private class BorNode {
        public int count;
        public char character;
        public boolean stringEnd;

        private BorNode[] children =
                new BorNode[StringSetImpl.ALPHABET.length()];

        public BorNode(char character) {
            this.count = 0;
            this.character = character;
            this.stringEnd = false;

            for (int i = 0; i < children.length; i++) {
                children[i] = null;
            }
        }

        public boolean hasSon(char character) {
            return children[toInt(character)] != null;
        }

        public void addSon(char character) {
            children[toInt(character)] = new BorNode(character);
        }

        public BorNode getSon(char character) {
            return children[toInt(character)];
        }

        public BorNode deleteSon(char character) {
            BorNode tmp = children[toInt(character)];
            children[toInt(character)] = null;
            return tmp;
        }

        private int toInt(char character) {
            return StringSetImpl.ALPHABET.indexOf(character);
        }
    }

    private void clearTrie() {
        for (char ch : ALPHABET.toCharArray()) {
            root.deleteSon(ch);
        }
        size = 0;
    }

    private void printStrings(BorNode u, OutputStream out, Vector<Character> v)
            throws SerializationException {
        if (!u.equals(root) && u.character != '#') {
            v.add(u.character);
        }

        if (u.stringEnd) {
            try {
                for (Character ch : v) {
                    out.write(ch.toString().getBytes("UTF-8"));
                }
                out.write("\n".getBytes("UTF-8"));
            } catch (IOException e) {
                throw new SerializationException();
            }
        }

        for (char ch : ALPHABET.toCharArray()) {
            if (u.hasSon(ch)) {
                printStrings(u.getSon(ch), out, v);
            }
        }

        if (!u.equals(root) && u.character != '#') {
            v.remove(v.size() - 1);
        }
    }

    public void serialize(OutputStream out)
            throws SerializationException {
        Vector<Character> v = new Vector<>();
        printStrings(root, out, v);
    }

    public void deserialize(InputStream in)
            throws SerializationException {
        clearTrie();

        StringBuilder b = new StringBuilder();
        char ch;

        try {
            while (in.available() != 0) {
                ch = (char)in.read();
                if (ch != '\n') {
                    b.append(ch);
                } else {
                    add(b.toString());
                    b.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }
}
