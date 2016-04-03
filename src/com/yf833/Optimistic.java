package com.yf833;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Optimistic {


    public static int cycle = 0;
    public static int[][] resource_claims;
    public static ArrayList<Task> finished_tasks = new ArrayList<>();



    public static void runFifo(LinkedBlockingQueue<Task> tasks, ArrayList<Integer> resource_amounts){

        ArrayList<Integer> available = resource_amounts;

        //initialize resource_claims[][]
        resource_claims = new int[tasks.size()][resource_amounts.size()];
        for(Task t : tasks){
            for(int j=0; j<resource_amounts.size(); j++){
                resource_claims[t.taskID-1][j] = 0;
            }
        }
//        Util.print2DArray(resource_claims);



        ///// Main Loop /////
        while(!tasks.isEmpty()){

            //for each task in the queue, try to run the next activity (if possible)
            for(Task t : tasks){

                Activity current = t.activities.peek();

                if(current.type.equals("initiate")){
                    t.activities.poll();
                }
                else if(current.type.equals("request")){
                    // try to claim the resource amount
                    if(current.amount <= available.get(current.resourceID-1) && !t.isBlocked){
                        resource_claims[t.taskID-1][current.resourceID-1] += current.amount;
                        available.set(current.resourceID-1, available.get(current.resourceID-1) - current.amount);
                        t.activities.poll();
                    }else{
                        t.waiting_time++;
                        t.isBlocked = true;
                    }

                }
                else if(current.type.equals("release")){
                    resource_claims[t.taskID-1][current.resourceID-1] -= current.amount;
                    available.set(current.resourceID-1, available.get(current.resourceID-1) + current.amount);
                    t.activities.poll();
                }
                else if(current.type.equals("terminate")){
                    finished_tasks.add(t);
                    tasks.remove(t);

                    t.activities.poll();
                }

                t.total_time++;

            }


            cycle++;

            ///// DEBUGGING INFO /////
            System.out.println("=============== AFTER CYCLE " + cycle + " ===============");
            System.out.println(tasks.toString());
            System.out.println("\nclaims:");
            Util.print2DArray(resource_claims);

            System.out.println("\navailable:");
            System.out.println(available.toString() + "\n");
        }


        // print output //
        System.out.println("\n\n");
        Util.sortTasksByID(finished_tasks);
        for(Task t : finished_tasks){
            Util.printTaskSumamry(t);
        }


    }




}
