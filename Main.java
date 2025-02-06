import java.util.*;

class Process {
    int id;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int finishTime;
    int waitingTime;

    Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

class Scheduler {

    public static void FCFS(List<Process> processList) {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0, totalWaitingTime = 0;

        for (Process process : processList) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }

            process.finishTime = currentTime + process.burstTime;
            process.waitingTime = process.finishTime - process.arrivalTime - process.burstTime;
            totalWaitingTime += process.waitingTime;
            currentTime += process.burstTime;
        }

        System.out.println("Processes\tArrival Time\tBurst Time\tFinish Time\tWaiting Time");
        for (Process process : processList) {
            System.out.printf("Process %d\t%d\t\t%d\t\t%d\t\t%d\n",
                    process.id, process.arrivalTime, process.burstTime, process.finishTime, process.waitingTime);
        }
        System.out.printf("Average Waiting Time = %.2f ms\n\n", (double) totalWaitingTime / processList.size(), "ms");
    }

    public static void RoundRobin(List<Process> processList, int timeQuantum) {
        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> processes = new ArrayList<>();
        for (Process process : processList) {
            processes.add(new Process(process.id, process.arrivalTime, process.burstTime));
        }

        int currentTime = 0, totalWaitingTime = 0;

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            if (readyQueue.isEmpty()) {
                currentTime = !processes.isEmpty() ? processes.get(0).arrivalTime : currentTime + 1;
                continue;
            }

            Process currentProcess = readyQueue.poll();
            int executionTime = Math.min(currentProcess.remainingTime, timeQuantum);
            currentProcess.remainingTime -= executionTime;
            currentTime += executionTime;

            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            if (currentProcess.remainingTime > 0) {
                readyQueue.add(currentProcess);
            } else {
                currentProcess.finishTime = currentTime;
                currentProcess.waitingTime = currentProcess.finishTime - currentProcess.arrivalTime
                        - currentProcess.burstTime;
                totalWaitingTime += currentProcess.waitingTime;
                for (Process process : processList) {
                    if (process.id == currentProcess.id) {
                        process.finishTime = currentProcess.finishTime;
                        process.waitingTime = currentProcess.waitingTime;
                        break;
                    }
                }
            }
        }

        System.out.println("Processes\tArrival Time\tBurst Time\tFinish Time\tWaiting Time");
        for (Process process : processList) {
            System.out.printf("Process %d\t%d\t\t%d\t\t%d\t\t%d\n",
                    process.id, process.arrivalTime, process.burstTime, process.finishTime, process.waitingTime);
        }
        System.out.printf("Average Waiting Time = %.2f ms\n\n", (double) totalWaitingTime / processList.size());
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Process> processList = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int numberOfProcesses = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Arrival Times (space-separated): ");
        String[] arrivalTimes = scanner.nextLine().split(" ");

        System.out.print("Enter Burst Times (space-separated): ");
        String[] burstTimes = scanner.nextLine().split(" ");

        for (int i = 0; i < numberOfProcesses; i++) {
            processList.add(new Process(i + 1, Integer.parseInt(arrivalTimes[i]), Integer.parseInt(burstTimes[i])));
        }

        System.out.println("FCFS : ");
        Scheduler.FCFS(new ArrayList<>(processList));

        System.out.print("Enter Time Quantum for Round Robin: ");
        int timeQuantum = scanner.nextInt();
        System.out.println("Round Robin : ");
        Scheduler.RoundRobin(new ArrayList<>(processList), timeQuantum);

        scanner.close();
    }
}