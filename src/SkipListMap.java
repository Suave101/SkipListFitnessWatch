/*

  Author: Alexander Prescott Doyle
  Email: adoyle2025@my.fit.edu
  Course: Data Structures and Algorithms
  Section: 2
  Description of this file: A skip list class to use for the fitness watch tracking software in HW5.java

 */

import java.util.ArrayList;
import java.util.Objects;

public class SkipListMap {

    // SkipListNode Array
    private ArrayList<SkipListNode> skipListMap = new ArrayList<SkipListNode>();

    // Random height generator
    private FakeRandHeight random = new FakeRandHeight();

    // Level of the top empty layer 0-indexed
    private int topHeight = 0;

    // Constructor method for SkipList
    public SkipListMap() {
        // Add first layer to skip list map
        SkipListNode beginning = new SkipListNode("Beginning", null, null, null, null, null);
        SkipListNode end = new SkipListNode("End", null, null, beginning, null, null);
        beginning.setNext(end);
        skipListMap.add(beginning);
    }

    // Method to print the array
    public void print() {
        int curLayer = skipListMap.size() - 2;
        System.out.println("(S" + (curLayer + 1) + ") empty");
        // Iterate backwards through the arraylist so top layer prints first
        for (int i = skipListMap.size() - 2; i >= 0; i--) {
            System.out.print("(S" + curLayer + ")");
            SkipListNode cur = skipListMap.get(i).getNext();
            while (!cur.getTime().equals("End")) {
                System.out.print(" " + cur.getTime() + ":" + cur.getActivity());
                cur = cur.getNext();
            }
            System.out.println();
            curLayer--;
        }
    }

    // Method to add an item to the SkipListMap. Returns false if time already exists
    public boolean put(String time, String activity) {
        // Get Random Height
        int height = random.get();

        int t = Integer.parseInt(time);

        // Find the node where this time should go
        SkipListNode nodeBefore = findNode(t);

        // Ensure that the element does not exist already
        if (nodeBefore != null && nodeBefore.getIntTime() == t) {
            return false;
        }

        // Get the node after where the new node will be
        SkipListNode nodeAfter = nodeBefore.getNext();

        // Insert at bottom level between nodeBefore and nodeAfter
        SkipListNode newNode = new SkipListNode(time, activity, nodeAfter, nodeBefore, null, null);
        nodeBefore.setNext(newNode);
        nodeAfter.setPrev(newNode);

        // Ensure there is always an empty layer at top
        ensureMaxHeight(height);

        // Build tower upwards
        SkipListNode belowRef = newNode;
        while (height > 0) {
            // move left until we find a node that has an above pointer
            SkipListNode left = nodeBefore;
            while (left != null && left.getAbove() == null) {
                left = left.getPrev();
            }

            // left should never be null because the "Beginning" sentinel has above pointers
            SkipListNode leftAbove = left.getAbove();
            SkipListNode rightAbove = leftAbove.getNext();

            SkipListNode newAbove = new SkipListNode(time, activity, rightAbove, leftAbove, null, belowRef);

            // Link horizontally
            leftAbove.setNext(newAbove);
            rightAbove.setPrev(newAbove);

            // Link vertically
            belowRef.setAbove(newAbove);

            // Move up
            belowRef = newAbove;
            nodeBefore = leftAbove;
            height--;
        }
        return true;
    }

    // Method to get an item by key from the SkipListMap
    public String get(String time) {
        int key = Integer.parseInt(time);

        // Find node iteratively
        SkipListNode node = findNode(key);

        // Check if node was found
        if (node != null && node.getIntTime() == key) {
            return node.getActivity();
        } else {
            return null;
        }
    }

    // Method to remove an item by key from the SkipListMap
    public String remove(String time) {
        int key = Integer.parseInt(time);

        // Get the bottom node of the stack we are looking for
        SkipListNode bottomNode = findNode(key);

        // Check if the node exists
        if (bottomNode == null || bottomNode.getIntTime() != key) {
            return null;
        }

        String activity = bottomNode.getActivity();

        // Recursively remove the tower from the bottom up
        collapseTower(bottomNode);

        // Return the activity we deleted
        return activity;
    }

    // Method to subMap the map
    public String subMap(String startTime, String endTime) {
        StringBuilder sb = new StringBuilder();
        int start = Integer.parseInt(startTime);
        int end = Integer.parseInt(endTime);

        // findNode gracefully finds the node <= startTime
        SkipListNode cur = findNode(start);

        // If the exact start time doesn't exist, shift forward to the next available time
        if (cur != null && cur.getIntTime() < start) {
            cur = cur.getNext();
        }

        // Keep going till end of our range
        while (cur != null && !cur.getTime().equals("End") && cur.getIntTime() <= end) {
            sb.append(" ").append(cur.getTime()).append(":").append(cur.getActivity());
            cur = cur.getNext();
        }

        return sb.toString();
    }

    // Helper method to remove a tower recursively from bottom to top
    private void collapseTower(SkipListNode node) {
        // Get nodes neighbors
        SkipListNode leftOfNode = node.getPrev();
        SkipListNode rightOfNode = node.getNext();
        SkipListNode topOfNode = node.getAbove();

        // Make horizontal neighbors forget node exists
        leftOfNode.setNext(rightOfNode);
        rightOfNode.setPrev(leftOfNode);

        // If top neighbor exists, recurse upwards
        if (topOfNode != null) {
            collapseTower(topOfNode);
        }
    }

    private SkipListNode findNode(int time) {
        SkipListNode cur = skipListMap.get(skipListMap.size() - 1);
        while (cur != null) {
            // Look ahead
            if (cur.getNext() != null && cur.getNext().getIntTime() <= time) {
                cur = cur.getNext();
            } else {
                // Otherwise drop down
                if (cur.getBelow() == null) {
                    return cur; // Bottom reached
                }
                cur = cur.getBelow();
            }
        }
        return cur;
    }

    // Given the height of a node, ensure that there is an empty layer above its max height
    private void ensureMaxHeight(int height) {
        while (this.topHeight <= height) {
            addLayer();
        }
    }

    // Method to add a layer to the SkipListMap
    private void addLayer() {
        // Get the beginning of the current top layer
        SkipListNode beginningBelow = skipListMap.get(skipListMap.size() - 1);

        // Get the end of the current top layer
        SkipListNode endBelow = findValueHoriz(beginningBelow, "End");

        // Add layer to skip list map
        SkipListNode beginning = new SkipListNode("Beginning", null, null, null, null, beginningBelow);
        SkipListNode end = new SkipListNode("End", null, null, beginning, null, endBelow);
        beginning.setNext(end);
        skipListMap.add(beginning);
        beginningBelow.setAbove(beginning);
        endBelow.setAbove(end);
        // Increment the top height
        topHeight++;
    }

    // A method to recurse through list left to right to find item with str val
    private SkipListNode findValueHoriz(SkipListNode beginning, String value) {
        if (Objects.equals(beginning.getTime(), value)) {
            return beginning;
        } else if (Objects.equals(beginning.getTime(), "End")) {
            return null;
        } else {
            return findValueHoriz(beginning.getNext(), value);
        }
    }

    // A SkipList Node
    private class SkipListNode {
        // The nodes around the node
        private SkipListNode next;
        private SkipListNode prev;
        private SkipListNode above;
        private SkipListNode below;

        // Key
        private String time;
        private int intTime;

        // Value
        private String activity;

        // Constructor method for SkipListNode
        public SkipListNode(String time, String activity, SkipListNode next, SkipListNode prev, SkipListNode above, SkipListNode below) {
            this.time = time;
            if (time.equals("End")) {
                this.intTime = Integer.MAX_VALUE;
            } else if (time.equals("Beginning")) {
                this.intTime = Integer.MIN_VALUE;
            } else {
                this.intTime = Integer.parseInt(time);
            }
            this.activity = activity;
            this.next = next;
            this.prev = prev;
            this.above = above;
            this.below = below;
        }

        // Getter methods
        public String getTime() {
            return this.time;
        }

        public String getActivity() {
            return this.activity;
        }

        public SkipListNode getNext() {
            return this.next;
        }

        public SkipListNode getAbove() {
            return this.above;
        }

        public SkipListNode getPrev() {
            return this.prev;
        }

        public SkipListNode getBelow() {
            return this.below;
        }

        public int getIntTime() {
            return this.intTime;
        }

        // Setter methods
        public void setNext(SkipListNode next) {
            this.next = next;
        }
        public void setPrev(SkipListNode prev) {
            this.prev = prev;
        }

        public void setAbove(SkipListNode above) {
            this.above = above;
        }
        public void setBelow(SkipListNode below) {
            this.below = below;
        }

        // Compare method to compare the times. If larger, pos. If smaller neg. Equal = 0
        public int compareTo(SkipListNode s2) {
            return this.intTime - s2.getIntTime();
        }
    }
}