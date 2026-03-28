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
        // Find the first node with key >= time on the bottom layer (nodeAfter)
        SkipListNode nodeAfter = findRecursively(Integer.parseInt(time), skipListMap.get(skipListMap.size() - 1));

        // Ensure that the element does not exist already
        if (nodeAfter.getTime().equals(time)) {
            return false;
        }

        // Get the node before the node after where the new node will be
        SkipListNode nodeBefore = nodeAfter.getPrev();

        // Insert at bottom level between nodeBefore and nodeAfter
        SkipListNode newNode = new SkipListNode(time, activity, nodeAfter, nodeBefore, null, null);
        nodeBefore.setNext(newNode);
        nodeAfter.setPrev(newNode);

        // Get Random Height
        int height = random.get();

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
    public SkipListNode get(String time) {
        int key = Integer.parseInt(time);

        // Find node recursively
        SkipListNode node = findRecursively(key, skipListMap.get(skipListMap.size() - 1));

        // Check if node was found
        if (node.intTime == key) {
            return node;
        } else {
            return null;
        }
    }

    // Method to remove an item by key from the SkipListMap
    public String remove(String time) {
        // Get top of stack we are looking for
        SkipListNode top = findTop(Integer.parseInt(time), skipListMap.get(skipListMap.size() - 1));

        // Check if the node exists
        if (top == null) {
            return null;
        }

        // Recursively remove the tower
        collapseTower(top);

        // Return that we found and deleted the item well
        return top.getActivity();
    }

    // Method to subMap the map
    public ArrayList<SkipListNode> subMap(String startTime, String endTime) {
        ArrayList<SkipListNode> array = new ArrayList<>();
        int end = Integer.parseInt(endTime);

        // findRecursively already brilliantly finds the first node >= startTime
        SkipListNode cur = findRecursively(Integer.parseInt(startTime), skipListMap.get(skipListMap.size() - 1));

        // Keep going till end or number
        while (cur != null && !cur.getTime().equals("End") && cur.getIntTime() <= end) {
            array.add(cur);
            cur = cur.getNext();
        }

        return array;
    }

    // Helper method to remove a tower recursively
    private void collapseTower(SkipListNode node) {
        // Get nodes neighbors
        SkipListNode leftOfNode = node.getPrev();
        SkipListNode rightOfNode = node.getNext();
        SkipListNode bottomOfNode = node.getBelow();

        // Make neighbors forget node exists
        leftOfNode.setNext(rightOfNode);
        rightOfNode.setPrev(leftOfNode);

        // If bottom neighbor exists, recurse
        if (bottomOfNode != null) {
            bottomOfNode.setAbove(null);
            collapseTower(bottomOfNode);
        }
    }

    // Helper method to find the top of a tower
    private SkipListNode findTop(int time, SkipListNode cur) {
        // Check if this is the top
        if (cur.getIntTime() == time) {
            // If so, return it
            return cur;
        }

        // If cur is less than time, proceed to next unless it is the beginning or end
        if (cur.getIntTime() < time) {
            // Scan Forward
            return findTop(time, cur.getNext());
        } else {
            // Drop Down
            if (cur.getBelow() == null) {
                // Not found
                return null;
            } else {
                // Drop down
                return findTop(time, cur.getBelow());
            }
        }
    }

    // Given the height of a node, ensure that there is an empty layer above its max height
    private void ensureMaxHeight(int height) {
        while (this.topHeight <= height) {
            addLayer();
        }
    }

    // Finds a node by time starting at cur
    private SkipListNode findRecursively(int time, SkipListNode cur) {
        // If cur is less than time, proceed to next unless it is the beginning or end
        if (cur.getIntTime() < time) {
            // Scan Forward
            return findRecursively(time, cur.getNext());
        } else {
            // Drop Down
            // Recurse to the lowest level of this node if greater than or equal to time
            if (cur.getBelow() == null) {
                return cur;
            } else {
                return findRecursively(time, cur.getBelow());
            }
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