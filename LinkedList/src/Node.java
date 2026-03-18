public class Node implements IElement{
    int value;
    Node prev = null;
    Node next = null;
    public LinkedList list;
    public boolean isRemovedFlag = false;

    public Node(int value, LinkedList list){
        this.value = value;
        this.list = list;
    }
    public void markAsRemoved(){
        this.isRemovedFlag = true;
    }
    public boolean isRemoved(){
        return isRemovedFlag;
    }
    public boolean belongsTo(LinkedList list){
        return this.list == list;
    }
    @Override
    public String toString(){
        return "value = " + value;
    }
    @Override
    public int getValue(){
        return value;
    }
}