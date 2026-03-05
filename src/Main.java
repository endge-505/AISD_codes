public class Main{
    public static void main(String[] args){
        CustomList customlist_1 = new CustomList(1);
        customlist_1.print();
        customlist_1.addToStart(2);
        customlist_1.print();
        customlist_1.addToEnd(3);
        customlist_1.print();
        customlist_1.addToPosition(35,2);
        customlist_1.print();

        System.out.println();

        int[] mas = new int[] {1, 2, 3, 4, 4, 5};
        CustomList customList_2 = new CustomList(mas);
        customList_2.print();
        customList_2.deleteLast();
        customList_2.print();
        customList_2.addToStart(2);
        customList_2.print();
        customList_2.addToEnd(45);
        customList_2.print();
        customList_2.addToPosition(85,5);
        customList_2.print();
    }
}