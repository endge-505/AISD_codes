import java.util.NoSuchElementException;

public class LinkedList {
    Node head = null;
    Node tail = null;

    public void addFirst(int value){
        Node newNode = new Node(value, this);
        if (isEmpty()){
            initialize(newNode);
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    }

    public IElement addLast(int value){
        Node newNode = new Node(value, this);
        if (isEmpty()){
            initialize(newNode);
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        return newNode;
    }

    public int removeFirst(){
        if(isEmpty()){
            throw new NoSuchElementException();
        }

        Node removedNode = head;

        if(hasOneNode()){
            reset();
        } else {
            Node newHead = head.next;
            head.next = null;
            newHead.prev = null;
            head = newHead;
        }
        removedNode.markAsRemoved();
        return removedNode.value;
    }
    public int removeLast(){
        if(isEmpty()){
            throw new NoSuchElementException();
        }
        Node removedNode = tail;
        if (hasOneNode()){
            reset();
        } else {
            Node prev = tail.prev;
            tail.prev = null;
            prev.next = null;
            tail = prev;
        }
        removedNode.markAsRemoved();
        return removedNode.value;
    }

    public void remove(IElement element){
        if (!(element instanceof Node)){
            throw new RuntimeException(("Invalid element type."));
        }
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        Node node = (Node) element;
        if(node.isRemoved()){
            throw  new NoSuchElementException("Element was removed.");
        }
        if(!node.belongsTo(this)){
            throw new NoSuchElementException("Element does not belong to this list.");
        }
        if (node == head){
            removeFirst();
            return;
        }
        if (node == tail){
            removeLast();
            return;
        }
        Node nextNode = node.next;
        Node prevNode = node.prev;

        node.prev = null;
        node.next = null;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        node.markAsRemoved();
    }
    private boolean hasOneNode(){
        return head == tail;
    }
    private void reset(){
        head = tail = null;
    }
    private boolean isEmpty(){
        return head == null;
    }
    private void initialize(Node node){
        head = tail = node;
    }
}
