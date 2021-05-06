package com.binary_studio.dependency_detector;

public class FloydsCycleFinding {
    //Simple implication of Floyd's Cycle Finding
    //in LinkedList
    //code is taken from my solutions from the subject Algorithms and Data Structures
    //Based on pseudocode from the lectures
    //rewrite with C++ to Java

    Node head;

    class Node {
        String data;
        Node next;
        Node(String d){
            data = d;
            next = null;
        }
    }

    public void push(String newData) {
        Node newNode = new Node(newData);

        newNode.next = head;
        head = newNode;
    }

    public boolean detectLoop() {
        Node slow = head;
        Node fast = head;
        int flag = 0;

        while (slow != null && fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if(slow == fast) {
                flag = 1;
                break;
            }
        }

        if(flag != 1) {
            return true; //if loop not found return true
        }
        return false; //detect loop
    }

}
