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

    // Method to add an item to the SkipListMap
    public void put(String time, String activity) {
        // Get Random Height
        int height = random.get();

        // Ensure there is always an empty layer at top
        ensureMaxHeight(height);

        // Find where the node goes
        SkipListNode nodeBefore = findRecursively(Integer.parseInt(time), skipListMap.get(skipListMap.size() - 1));

        // Create the Skip List Node and add it to the list
        SkipListNode newNode = new SkipListNode(time, activity, nodeBefore, nodeBefore.getPrev(), null, null);

        nodeBefore.getPrev().setNext(newNode);

        if (nodeBefore.getPrev().getAbove() != null) {
            nodeBefore.getPrev().getAbove().setBelow(newNode);
        }

        // Add the node height times
        SkipListNode curRef = newNode;
        while (height > 0) {
            // Create the node
            SkipListNode newNodeAbove = new SkipListNode(time, activity, null, null, null, curRef);

            // Find the node that will be to the right of the new node
            SkipListNode newNodeNext = curRef.getNext();
            while (newNodeNext.getAbove() == null) {
                // Scan forward until we find a node with a node above it
                newNodeNext = newNodeNext.getNext();
            }
            // Correctly set the new node next to the correct layer
            newNodeNext = newNodeNext.getAbove();
            SkipListNode newNodePrev = newNodeNext.getPrev();

            // Set the nodes around to know the current node exists
            newNodeNext.setPrev(newNodeAbove);
            newNodePrev.setNext(newNodeAbove);
            curRef.setAbove(newNodeAbove);

            // Update the node
            newNodeAbove.setNext(newNodeNext);
            newNodeAbove.setPrev(newNodePrev);

            // Update Current Reference Node
            curRef = newNodeAbove;

            // Decrement height to go
            height--;
        }

        nodeBefore.setPrev(newNode);
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
        // If the node is a head node
        private boolean isHead;

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
