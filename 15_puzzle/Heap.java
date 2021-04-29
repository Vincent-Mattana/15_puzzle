import java.util.*;
//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
//                             		  V.W.F Mattana 21128707
//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
//======================================================================================================
//                             				HEAP
//======================================================================================================
public class Heap
{
	//******************************************************************************************************
	//									DECLARATIONS
	//******************************************************************************************************
	private ArrayList<Node> list;
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                DEFAULT CONSTRUCTOR
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Heap()
	{
		list = new ArrayList<Node>();
		list.add(null); 
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                     	ADD
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void add(Node newElement)
	{
		// Add a new leaf
		list.add(null);
		int index = list.size() - 1;

		// Demote parents that are larger than the new element
		while (index > 1 && (getParent(index).getF()) > (newElement.getF())) 
		{
			list.set(index, getParent(index));
			index = getParentIndex(index);
		}

		// Store the new element into the vacant slot
		list.set(index, newElement);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                     	PEEK
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Node peek()
	{
		return list.get(1);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                     	POP
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Node pop()
	{
		Node root = new Node();
		root.copyNode(list.get(1));
		int lastIndex = list.size() - 1;
		Node last = list.remove(lastIndex);

		if (lastIndex > 1)
		{
			list.set(1, last);
			fixHeap();     
		}

		return root;
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                    FIX HEAP
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void fixHeap()
	{
		Node root = list.get(1);
		int lastIndex = list.size() - 1;
		int index = 1;
		boolean more = true;
		while (more)
		{
			int childIndex = getLeftChildIndex(index);
			if (childIndex <= lastIndex)
			{
				Node child = getLeftChild(index);
				if (getRightChildIndex(index) <= lastIndex && (getRightChild(index).getF()) < (child.getF()))
				{
					childIndex = getRightChildIndex(index);
					child = getRightChild(index);
				}
				if ((child.getF()) < (root.getF())) 
				{
					list.set(index, child);
					index = childIndex;
				}
				else
				{
					more = false;
				}
			}
			else 
			{
				more = false; 
			}
		}
		list.set(index, root);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                      Resort Heap
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Heap resortHeap()
	{
		Heap tmpHeap = new Heap();
		for( int i=0; i<list.size();i++)
		{
			tmpHeap.add(pop());
		}
		return tmpHeap;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                      SIZE
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public int size()
	{
		return list.size() - 1;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                    INDEX OF
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public int indexOf(Node element)
	{
		return list.indexOf(element);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                    IS EMPTY
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                GET ELEMENT AT INDEX
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Node get(int x)
	{
		return list.get(x);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                  REMOVE ELEMENT
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public boolean remove(Node element)
	{
		return list.remove(element);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                GET LEFT CHILD INDEX
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static int getLeftChildIndex(int index)
	{
		return 2 * index;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                GET RIGHT CHILD INDEX
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static int getRightChildIndex(int index)
	{
		return 2 * index + 1;
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                  GET PARENT INDEX
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static int getParentIndex(int index)
	{
		return index / 2;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                   GET LEFT CHILD
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private Node getLeftChild(int index)
	{
		return list.get(2 * index);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                   GET RIGHT CHILD
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private Node getRightChild(int index)
	{
		return list.get(2 * index + 1);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                     GET PARENT
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Node getParent(int index)
	{
		return list.get(index / 2);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                     DISPLAY -DEBUG
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void display(Heap heap)
	{
		for(int i = 0; i<heap.size();i++)
		{
			System.out.println("F ="+heap.get(i+1).getF());
		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                       MAIN -DEBUG
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	//public static void main(String[] Args)
	/*
	{
		Heap heap = new Heap();
		
		Node n1 = new Node();
		n1.setG(1);
		n1.setH(1);
		n1.setF();
		Node n2 = new Node();
		n2.setG(2);
		n2.setH(2);
		n2.setF();
		Node n3 = new Node();
		n3.setG(3);
		n3.setH(3);
		n3.setF();
		Node n4 = new Node();
		n4.setG(4);
		n4.setH(4);
		n4.setF();
		Node n5 = new Node();
		n5.setG(5);
		n5.setH(5);
		n5.setF();
		
		System.out.println("empty heap");
		System.out.println("Add n1");
		heap.add(n1);
		heap.display(heap);
		System.out.println("Add n5");
		heap.add(n5);
		heap.display(heap);
		System.out.println("Add n2");
		heap.add(n2);
		heap.display(heap);
		System.out.println("Add n4");
		heap.add(n4);
		heap.display(heap);
		System.out.println("Add n3");
		heap.add(n3);
		heap.display(heap);
		System.out.println("pop: "+heap.pop().getF());
		heap.display(heap);
		System.out.println("pop: "+heap.pop().getF());
		heap.display(heap);
		System.out.println("pop: "+heap.pop().getF());
		heap.display(heap);
		System.out.println("pop: "+heap.pop().getF());
		heap.display(heap);
		System.out.println("pop: "+heap.pop().getF());
		heap.display(heap);
	}
	*/
}