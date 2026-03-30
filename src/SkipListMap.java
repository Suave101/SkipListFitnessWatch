import java.util.ArrayList;

public class SkipListMap {
    private ArrayList<SkipListNode> skipListMap = new ArrayList<>();
    private int topHeight = 0;

    public SkipListMap() {
        addLayer(); // Adds S0 with sentinels
    }

    private void addLayer() {
        SkipListNode bBelow = skipListMap.isEmpty() ? null : skipListMap.get(skipListMap.size() - 1);
        SkipListNode eBelow = bBelow == null ? null : getEndSentinel(bBelow);

        SkipListNode beginning = new SkipListNode("Beginning", null, null, null, null, bBelow);
        SkipListNode end = new SkipListNode("End", null, null, beginning, null, eBelow);
        beginning.setNext(end);

        if (bBelow != null) bBelow.setAbove(beginning);
        if (eBelow != null) eBelow.setAbove(end);

        skipListMap.add(beginning);
    }

    private SkipListNode getEndSentinel(SkipListNode node) {
        while (!node.getTime().equals("End")) node = node.getNext();
        return node;
    }

    public boolean put(String time, String activity, int height) {
        int t = Integer.parseInt(time);

        // Ensure the skip list has enough layers for the new tower height
        while (skipListMap.size() <= height) {
            addLayer();
        }

        // Search from the TOP layer to find the insertion path
        ArrayList<SkipListNode> path = new ArrayList<>();
        SkipListNode cur = skipListMap.get(skipListMap.size() - 1);

        while (cur != null) {
            while (cur.getNext() != null && !cur.getNext().getTime().equals("End")
                    && cur.getNext().getIntTime() < t) {
                cur = cur.getNext();
            }
            path.add(cur); // Store the node to the LEFT of where the new node goes
            cur = cur.getBelow();
        }

        SkipListNode atS0 = path.get(path.size() - 1);
        if (atS0.getNext() != null && atS0.getNext().getIntTime() == t) {
            return false;
        }

        // Build tower
        SkipListNode belowNode = null;
        for (int i = 0; i <= height; i++) {
            // Since path was built Top->Down, we get S0 by looking at the end of the list
            SkipListNode left = path.get(path.size() - 1 - i);

            SkipListNode newNode = new SkipListNode(time, activity, left.getNext(), left, null, belowNode);

            // Link horizontally
            left.getNext().setPrev(newNode);
            left.setNext(newNode);

            // Link vertically
            if (belowNode != null) {
                belowNode.setAbove(newNode);
            }
            belowNode = newNode;
        }
        return true;
    }

    public void print() {
        // Standard Skip List printing: Top layer to bottom
        for (int i = skipListMap.size() - 1; i >= 0; i--) {
            System.out.print("(S" + i + ")");
            SkipListNode cur = skipListMap.get(i).getNext();
            if (cur.getTime().equals("End")) {
                System.out.println(" empty");
            } else {
                while (!cur.getTime().equals("End")) {
                    System.out.print(" " + cur.getTime() + ":" + cur.getActivity());
                    cur = cur.getNext();
                }
                System.out.println();
            }
        }
    }

    private SkipListNode findNode(int time) {
        // Start at the top-left
        SkipListNode cur = skipListMap.get(skipListMap.size() - 1);
        while (cur != null) {
            // Move right as far as possible without overshooting the time
            while (cur.getNext() != null && !cur.getNext().getTime().equals("End")
                    && cur.getNext().getIntTime() <= time) {
                cur = cur.getNext();
            }
            // If we can't go down anymore, we've found the closest node in S0
            if (cur.getBelow() == null) return cur;
            cur = cur.getBelow();
        }
        return cur;
    }

    public String get(String time) {
        SkipListNode node = findNode(Integer.parseInt(time));
        return (node != null && node.getTime().equals(time)) ? node.getActivity() : null;
    }

    public String remove(String time) {
        SkipListNode node = findNode(Integer.parseInt(time));
        if (node == null || !node.getTime().equals(time)) return null;
        String act = node.getActivity();
        while (node != null) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
            node = node.getAbove();
        }
        return act;
    }

    public String subMap(String startTime, String endTime) {
        StringBuilder sb = new StringBuilder();
        int start = Integer.parseInt(startTime);
        int end = Integer.parseInt(endTime);
        SkipListNode cur = findNode(start);
        if (cur.getIntTime() < start) cur = cur.getNext();
        while (cur != null && cur.getIntTime() <= end) {
            sb.append(" ").append(cur.getTime()).append(":").append(cur.getActivity());
            cur = cur.getNext();
        }
        return sb.toString();
    }

    private class SkipListNode {
        private SkipListNode next, prev, above, below;
        private String time, activity;
        private int intTime;

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