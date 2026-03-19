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
        // Find where the node goes
        SkipListNode nodeBefore = findRecursively(Integer.parseInt(time), skipListMap.get(skipListMap.size() - 1));

        // Create the Skip List Node and add it to the list
        assert nodeBefore != null;

        SkipListNode newNode = new SkipListNode(time, activity, nodeBefore, nodeBefore.getPrev(), nodeBefore.getPrev().getAbove(), null);

        nodeBefore.getPrev().setNext(newNode);

        if (nodeBefore.getPrev().getAbove() != null) {
            nodeBefore.getPrev().getAbove().setBelow(newNode);
        }

        nodeBefore.setPrev(newNode);
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

        // Add first layer to skip list map
        SkipListNode beginning = new SkipListNode("Beginning", null, null, null, null, beginningBelow);
        SkipListNode end = new SkipListNode("End", null, null, beginning, null, endBelow);
        beginning.setNext(end);
        skipListMap.add(beginning);
    }

    // A method to recurse through list left to right to find item with str val
    private SkipListNode findValueHoriz(SkipListNode beginning, String value) {
        if (Objects.equals(beginning.getTime(), value)) {
            return beginning;
        } else if (Objects.equals(beginning.getTime(), "End")) {
            return null;
        } else {
            return beginning.getNext();
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
