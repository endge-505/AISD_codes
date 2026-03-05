public class CustomList {
    private Node head;
    public CustomList(int samVal){
        head = new Node(samVal);
    }
    public CustomList(int[] val){
        if (val == null || val.length == 0){
            head = null;
            return;
        }
        head = new Node(val[0]);
        Node current_m = head;
        for (int i = 0; i < val.length; i++){
            current_m.nextNode = new Node(val[i]);
            current_m = current_m.nextNode;
        }
    }

    public void print(){
        if(head == null){
            System.out.println("Список пуст.");
            return;
        }
        Node pointer = head;
        while (pointer != null){
            if (pointer.nextNode == null){
                System.out.print(pointer.info);
            }
            else {
                System.out.print(pointer.info + ", ");
            }
            pointer = pointer.nextNode;
        }
        System.out.println();
    }

    // Добавление в начало
    public void addToStart(int samVal){
        Node newNode = new Node(samVal);
        newNode.nextNode = head;
        head = newNode;
    }

    // Удаление с начало
    public void deletStart(){
        if (head == null){
            System.out.println("*");
        }
        head = head.nextNode;
    }

    // Добавление в конец
    public void addToEnd(int samVal){
        if (head == null){
            addToStart(samVal);
        }
        Node newNode = new Node(samVal);
        Node pointer = head;
        while (pointer.nextNode != null){
            pointer = pointer.nextNode;
        }
        pointer.nextNode = newNode;
    }

    // Удаляем с конца
    public void deleteLast(){
        if(head == null){
            System.out.println("...");
            return;
        }
        if (head.nextNode == null){
            head = null;
            return;
        }
        Node pointer = head;
        while (pointer.nextNode.nextNode != null){
            pointer = pointer.nextNode;
        }
        pointer.nextNode = null;
    }

    // Добавление в середину
    public void addToPosition(int samVal, int pos){
        Node pointer = head;
        int currepos = 1;
        while (currepos != pos - 1){
            pointer = pointer.nextNode;
            currepos++;
        }
        Node newNode = new Node(samVal);
        newNode.nextNode = pointer.nextNode;
        pointer.nextNode = newNode;
    }
}
