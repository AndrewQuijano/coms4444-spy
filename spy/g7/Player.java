package spy.g7;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

public class Player implements spy.sim.Player {
    
    private ArrayList<ArrayList<Record>> records;
    private int id;
    private int player_num;
    private Set<Integer> trust;
    private Point loc;
    private HashMap<Point, List<Record>> trustRecords;
    private HashMap<Point, List<Record>> tempRecords;
    private PriorityQueue<Vote> lastResult;

    class PathTime{
        Integer id;
        Integer time;
        PathTime(int id, int time){
            this.id = id;
            this.time = time;
        }
        public Integer getId() {
            return id;
        }
        public Integer getTime() {
            return time;
        }
    }
    class Vote{
       Integer id;
       Integer number;
       Vote(int id, int number){
           this.id = id;
           this.number = number;
       }
       public Integer getId() {
            return id;
       }
       public Integer getNumber() {
            return number;
       }
    }
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        lastResult = null;
        trust  = new HashSet<>();
        trustRecords = new HashMap<>();
        tempRecords = new HashMap<>();
        trust.add(id);
        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
            }
            this.records.add(row);
        }
        player_num = n ;
    }


    private boolean pointsAreAdjacent(Point p1, Point p2)
    {
        return Math.abs(p1.x - p2.x) <= 1 && Math.abs(p1.y - p2.y) <= 1;
    }

    private int moveTime(Point from, Point to)
    {
        if (from.equals(to))
        {
            return 1;
        }
        int base = 2;
        if (from.x != to.x && from.y != to.y)
        {
            base = 3;
        }
        boolean isMuddy = trustRecords.get(from).get(0).getC() == 1 || trustRecords.get(to).get(0).getC() == 1;
        return isMuddy ? base * 2 : base;
    }

    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;

        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            Record record = records.get(p.x).get(p.y);
            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
            List<Record> rl = new ArrayList<>();
            rl.add(record);
            trustRecords.put(p, rl);
        }

    }

    private int calculateTime(List<Point> path){
        int totalTime = 0;
        for (int i = 1; i < path.size(); i++)
        {
            Point from = path.get(i - 1);
            Point to = path.get(i);
            if (!pointsAreAdjacent(from, to))
                return Integer.MAX_VALUE;
            if (!trustRecords.containsKey(from)||!trustRecords.containsKey(to))
                return Integer.MAX_VALUE;
            totalTime += moveTime(from, to) * 5;
        }
        return totalTime;
    }


    public List<Record> sendRecords(int id)
    {
        List<Record> send = new ArrayList<Record>();
        for(List<Record> l :  trustRecords.values()){
            for(Record r:l){
                if(r.getObservations().get(r.getObservations().size()-1).getID()!=this.id)
                    r.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
                else {
                    r.getObservations().remove(r.getObservations().size()-1);
                    r.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
                }
            }
            send.addAll(l);
        }
        return send;
    }
    
    public void receiveRecords(int id, List<Record> records)
    {
        for(Record r: records){
            if(!trustRecords.containsKey(r.getLoc())){
                List<Record> rl = new ArrayList<>();
                rl.add(r);
                trustRecords.put(r.getLoc(), rl);
            }else {
                List<Record> rl = trustRecords.get(r.getLoc());
                rl.add(r);
            }
        }
//        for(Record r:records){
//            List<Record> l = new ArrayList<>();
//            l.add(r);
//            tempRecords.put(r.getLoc() ,l);
//        }
//        for (Map.Entry<Point, List<Record>> entry : tempRecords.entrySet()){
//            List<Record> rl= entry.getValue();
//            for(int i =0; i<rl.size()-1;i++){
//                Set<Integer> playerInI = new HashSet<>();
//                List<Observation> obInI = rl.get(i).getObservations();
//                for(Observation o: obInI)
//                    playerInI.add(o.getID());
//                for(int j=i; j<rl.size();j++){
//                    if(rl.get(i).getC() != rl.get(j).getC() || rl.get(i).getPT() != rl.get(j).getPT()){
//                        Set<Integer> playerInJ = new HashSet<>();
//                        List<Observation> obInJ = rl.get(j).getObservations();
//                        for(Observation o: obInJ)
//                            playerInJ.add(o.getID());
//                        playerInJ.addAll(playerInI);   //If there are conflicts then, spys will not be out of the two sets
//                        Set<Integer> untrust= new HashSet();
//                        for(int k = 0; k<player_num;k++)
//                            untrust.add(k);
//                        untrust.removeAll(playerInJ); //remove all players might be spys
//                        trust.addAll(untrust);
//                    }
//                }
//            }
//        }
//        for(Record r:records){
//            List<Observation> obs= r.getObservations();
//            boolean flag = true;
//            for(Observation ob: obs){
//                if(!trust.contains(ob.getID())) {
//                    flag = false;
//                    break;
//                }
//            }
//            if(flag){
//                List<Record> temp = trustRecords.get(r.getLoc());
//                temp.add(r);
//                trustRecords.put(r.getLoc(), temp);
//            }
//        }

    }
    
    public List<Point> proposePath()
    {
        return null;
    }

    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths)
    {
        PriorityQueue<PathTime> heap = new PriorityQueue<PathTime>((x,y)->(x.getTime()-y.getTime()));
        if(lastResult!=null){
            return new ArrayList<Integer>(lastResult.poll().getId());
        }else{
            for (Map.Entry<Integer, List<Point>> entry : paths.entrySet())
            {
                if(calculateTime(entry.getValue())!=Integer.MAX_VALUE)
                    heap.add(new PathTime(entry.getKey(),calculateTime(entry.getValue())));
            }
            if(heap.size()!=0){
                return new ArrayList<Integer>(heap.poll().getId());
            }
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
        lastResult = new PriorityQueue<Vote>((x, y) -> y.getNumber() - x.getNumber());
        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
           Vote v= new Vote(entry.getKey(), entry.getValue());
           lastResult.add(v);
        }

    }
    
    public Point getMove() {
        System.out.println(loc.x);
        Random rand = new Random();
        int x = rand.nextInt(2) * 2 - 1;
        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
        Point location = new Point(x, y);
        // we want to move to a point if it's not already in trusted records
        while (trustRecords.get(location) != null) {
            rand = new Random();
            x = rand.nextInt(2) * 2 - 1 ;
            y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
            location = new Point(x, y);
        }
        return location;
        
    }

}
