//Oscar Rodas

//Scheduling.java
//This program simulates First Come First Served, Shortest Job First, Priority Scheduling, Round Robin, and Shortest Remaining Time First scheduling.
import java.util.*;
import java.io.*;

public class Scheduling
{
	public static job[] array = new job [10];				//the array of jobs sorted by ID
	public static int[] order = new int[10];				//the array of jobs sorted by arrival time
	public static int[] timeRemaining = new int[10];		//for round robin
	public static int[] timeRemaining2 = new int[10];		//for shortest remaining time 1st
	public static int[] started = new int[10];				//for shortest remaining time 1st
	public static double[][] times = new double[5][2];		//the average wait and turnaround times
	public static String[] s = {"First Come First Served","Shortest Job First","Priority Scheduling","Round Robin","Shortest Remaining Time First"};
	public static int totalBurst = 0;
	public static int[] arrivesAt = {0,1,2,3,4,5,6,7,8,9};
	public static Random rand = new Random();
	public static BinaryHeap B = new BinaryHeap(10);
	public static BinaryHeap P = new BinaryHeap(10);
	public static int[] duplicates = new int[10];
	
	public static void main(String args[])
	{
		Arrays.fill(started,0);
		createJobs();
		System.out.println("");
		
		B.ordr = order;
		FCFS();		
		SJF();		
		PS();		
		RR();		
		SRTF();		
		
		for(int i = 0; i < 5; i++)
		{
			System.out.println(s[i]);
			System.out.println("average wait time: " + times[i][0]);
			System.out.println("average turnaround time: " + times[i][1]);
			System.out.println("");
		}
		
	}
	
	public static void createJobs()
	{
		randomize();
		for(int i = 0; i < 10; i++)
		{
			array[i] = new job(i);						//job is created
			array[i].arrival = arrivesAt[i];			//arrival time
			order[array[i].arrival] = array[i].id;		//sorted by arrival time
			array[i].burst = rand.nextInt(20) + 1;
			timeRemaining[i] = array[i].burst;			//for later use
			timeRemaining2[i] = array[i].burst;			//for later use
			totalBurst += array[i].burst;				//total time needed to execute jobs
			array[i].priority = rand.nextInt(10) + 1;
			duplicates[array[i].priority - 1]++;
			System.out.println("id: " + array[i].id + " arrival: " + array[i].arrival + " burst: " + array[i].burst + " priority: " + array[i].priority);
		}
	}
	
	public static void randomize()
	{
		for(int i = 0; i < 10; i++)						//randomly swaps the indexes
		{
			int temp = arrivesAt[i];
			int j = rand.nextInt(10);
			arrivesAt[i] = arrivesAt[j];
			arrivesAt[j] = temp;
		}
	}
	
	public static void FCFS()
	{
		double waitTime = 0;
		double sum = 0;
		double turnaround = 0;
		for(int i = 0; i < 10; i++)
		{
			System.out.println("ID: " + order[i] + " arrival time: " + array[order[i]].arrival);
			if(i < 9)
			{
				sum = array[order[i]].burst + sum - i;		// i subtracts the time before the process is in the waiting queue
				waitTime += sum;
			}
			turnaround += array[order[i]].burst;
		}
		times[0][0] = waitTime / 10;				//stored in the array
		times[0][1] = turnaround / 10;
		System.out.println("");
	}
	
	public static void SJF()
	{
		double waitTime = 0;
		double sum = 0;
		double turnaround = 0;
		for(int i = 0; i < 10; i++)
		{
			B.key[i] = array[i].burst;	//key of job # i = burst time of array[i]
			B.insert(i);
		}
		for(int i = 0; i < 10; i++)
		{
			int lowest = B.deleteMin();
			System.out.println("ID: " + lowest + " burst time: " + B.key[lowest]);
			if(i < 9)
			{
				sum = array[lowest].burst + sum;
				waitTime += sum;
			}
			turnaround += array[lowest].burst;
		}
		times[1][0] = waitTime / 10;
		times[1][1] = turnaround / 10;
		System.out.println("");
	}
	
	public static void PS()
	{
		double waitTime = 0;
		double sum = 0;
		double turnaround = 0;
		B = new BinaryHeap(10);
		B.ordr = order;
		int twin = 0;
		
		for(int i = 0; i < 10; i++)
		{
			B.key[order[i]] = array[order[i]].priority;	//key of job # i = priority of array[i]
			B.insert(order[i]);
		}
		
		for(int i = 0; i < 10; i++)
		{
			int lowest = B.deleteMin();
			if(duplicates[array[lowest].priority - 1] > 1)		//if more than 1 process has this priority
			{
				P.key[lowest] = array[lowest].arrival;
				P.insert(lowest);
				if(!B.isEmpty())
					twin = B.findMin();						//check if this is the process with same priority
				while(array[twin].priority == array[lowest].priority)	//while there is another process with this priority
				{
					P.key[twin] = array[twin].arrival;
					P.insert(twin);			//insert it into a tie breaker heap
					if(!B.isEmpty())
						B.deleteMin();			//delete it from the main heap to avoid endless loop
					if(!B.isEmpty())
						twin = B.findMin();		//look to see if the next lowest in main heap has this priority
				}
				lowest = P.deleteMin();		//delete the lowest one from tie breaker heap
				while(!P.isEmpty())			//fill the main heap with the losers from tie breaker heap
				{
					int temp = P.deleteMin();
					B.insert(temp);
				}
			}
			System.out.println("ID: " + array[lowest].id + " priority: " + array[lowest].priority);
			if(i < 9)
			{
				sum = array[lowest].burst + sum;
				waitTime += sum;
			}
			turnaround += array[lowest].burst;
		}
		
		times[2][0] = waitTime / 10;
		times[2][1] = turnaround / 10;
		System.out.println("");
	}
	
	public static void RR()
	{
		double waitTime = 0;
		double sum = 0;
		double turnaround = 0;
		int notDone = 1;
		int num = 9;
		
		while(notDone > 0)
		{
			notDone = 0;
			for(int i = 0; i < 10; i++)
			{
				if(timeRemaining[i] > 0)
				{
					if(timeRemaining[i] >= 3)
					{
						timeRemaining[i] -= 3;
						waitTime += 3 * num;
					}
					else
					{
						waitTime += timeRemaining[i] * num;
						timeRemaining[i] = 0 ;
					}
					System.out.println("Time remaining for process " + i + " = " + timeRemaining[i]);
				}
				if(timeRemaining[i] > 0)
				{
					notDone = 1;
				}
				else if (timeRemaining[i] == 0) 
				{
					timeRemaining[i]--;
					num--;
				}
			}
		}
		times[3][1] = (waitTime + 30) / 10;		//turnaround
		times[3][0] = waitTime / 10;
		System.out.println("");
	}
	
	public static void SRTF()
	{
		double waitTime = 0;
		double sum = 0;
		double turnaround = 0;
		int numStarted = 0;
		int shortest = order[0];
		int waiting = 0;
		B = new BinaryHeap(10);
		B.ordr = order;
		
		for(int i = 0; i < 10; i++)
		{
			B.key[order[i]] = array[order[i]].burst;	//key of job # i = burst time of array[i]
			B.insert(order[i]);
			shortest = B.findMin();
			
			if(started[shortest] == 0)
				numStarted++;
			
			started[shortest] = 1;
			turnaround += (1 * numStarted);			//all the processes that have been started contribute every second
			waitTime += (1 * waiting);				//all the procceses in waiting queue contribute every second
			System.out.println("Time remaining for (the shortest) process, " + shortest + ", = " + B.key[shortest]);
			B.key[shortest]--;
			
			if(B.key[shortest] <= 0)
			{
				numStarted--;
				B.deleteMin();
				waiting--;
			}
			waiting++;
		}
		
		for(int i = 10; i < totalBurst; i++)
		{
			shortest = B.deleteMin();
			B.insert(shortest);
			shortest = B.findMin();
			
			if(started[shortest] == 0)
				numStarted++;
			
			started[shortest] = 1;
			turnaround += (1 * numStarted);
			waitTime += (1 * waiting);
			System.out.println("Time remaining for (the shortest) process " + shortest + " = " + B.key[shortest]);
			B.key[shortest]--;
			if(B.key[shortest] <= 0)
			{
				System.out.println("process " + shortest + " has finished ");
				numStarted--;
				B.deleteMin();
				waiting--;
			}
		}
		
		times[4][0] = waitTime / 10;
		times[4][1] = turnaround / 10;
		System.out.println("");
	}	
}

class job
{
	int id, arrival, burst, priority;
	job(int index)
	{
		id = index;
	}
}

/** Class BinaryHeap **/
class BinaryHeap    
{
    /** The number of children each node has **/
    public static final int d = 2;
	public static int[] ordr = new int[10];
    public int heapSize;
    public int[] heap;
	public static int[] key = new int[10];
 
    /** Constructor **/    
    public BinaryHeap(int capacity)
    {
        heapSize = 0;
        heap = new int[capacity + 1];
    }
 
    /** Function to check if heap is empty **/
    public boolean isEmpty( )
    {
        return heapSize == 0;
    }
 
    /** Check if heap is full **/
    public boolean isFull( )
    {
        return heapSize == heap.length;
    }
 
    /** Clear heap */
    public void makeEmpty( )
    {
        heapSize = 0;
    }
 
    /** Function to  get index parent of i **/
    public int parent(int i) 
    {
        return (i - 1)/d;
    }
 
    /** Function to get index of k th child of i **/
    public int kthChild(int i, int k) 
    {
        return d * i + k;
    }
 
    /** Function to insert element */
    public void insert(int x)
    {
        if (isFull( ) )
            throw new NoSuchElementException("Overflow Exception");
        /** Percolate up **/
        heap[heapSize++] = x;
        heapifyUp(heapSize - 1);
    }
 
    /** Function to find least element **/
    public int findMin( )
    {
        if (isEmpty() )
            throw new NoSuchElementException("Underflow Exception");           
        return heap[0];
    }
 
    /** Function to delete min element **/
    public int deleteMin()
    {
        int keyItem = heap[0];
        delete(0);
        return keyItem;
    }
 
    /** Function to delete element at an index **/
    public int delete(int ind)
    {
        if (isEmpty() )
            throw new NoSuchElementException("Underflow Exception");
        int keyItem = heap[ind];
        heap[ind] = heap[heapSize - 1];
        heapSize--;
        heapifyDown(ind);        
        return keyItem;
    }
 
    /** Function heapifyUp  **/
    public void heapifyUp(int childInd)
    {
        int tmp = heap[childInd]; 
		
        while ((childInd > 0) && (key[tmp] < key[heap[parent(childInd)]]))
        {
            heap[childInd] = heap[ parent(childInd) ];
            childInd = parent(childInd);
        }
        heap[childInd] = tmp;
    }
 
    /** Function heapifyDown **/
    public void heapifyDown(int ind)
    {
        int child;
        int tmp = heap[ ind ];
        while ((kthChild(ind, 1) < heapSize))
        {
            child = minChild(ind);
           if (key[heap[child]] < key[tmp])
			{
                heap[ind] = heap[child];
			}
            else 
				break;
            ind = child;
        }
        heap[ind] = tmp;
    }
 
    /** Function to get smallest child **/
    public int minChild(int ind) 
    {
        int bestChild = kthChild(ind, 1);
        int k = 2;
        int pos = kthChild(ind, k);
        while ((k <= d) && (pos < heapSize)) 
        {
            if (key[heap[pos]] < key[heap[bestChild]]) 
                bestChild = pos;
            pos = kthChild(ind, k++);
        }    
        return bestChild;
    }
 
    /** Function to print heap **/
    public void printHeap()
    {
        System.out.print("\nHeap = ");
        for (int i = 0; i < heapSize; i++)
            System.out.print(heap[i] +" ");
        System.out.println();
    }     
}