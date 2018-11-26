package spy.g3;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import java.util.PriorityQueue; 



import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

import java.util.AbstractMap;


public class Player implements spy.sim.Player {

    private class Entry implements Comparable<Entry> {
    public Double key;
    public Point p;

    public Entry(Double key, Point pt) {
        this.key = key;
        this.p = pt;
    }

    // getters

    @Override
    public int compareTo(Entry other) {
        return this.key.compareTo(other.key);
    }
}



public class Player implements spy.sim.Player {

    
    private ArrayList<ArrayList<Record>> records; // 2-dim list of cells on map (personal records)
    private int id;
    private Point loc; // Current location on map

    private Boolean _target;
    private Boolean _package;
    private Point package_Location;
    private Point target_Location;
    private int[][] grid;
    private int[][] visited;
    private int[][] explored;
    private List<Point> proposedPath;
    private Boolean found_path = false;
    private HashMap<Point,Integer> trap_count;

    private ArrayList<ArrayList<Record>> landInfo; // similar to 'records' but global for dry land claims
    private ArrayList<ArrayList<Record>> mudInfo; // similar to 'records' but global for muddy land claims
    
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
	    this._package=false;
        this._target = false;
        this.grid = new int[100][100];
        this.visited = new int[100][100];
        this.explored = new int[100][100];
        this.package_Location = new Point(-1,-1);
        this.target_Location = new Point(-1,-1);
        this.proposedPath = new ArrayList<Point>();
        this.trap_count =  new HashMap<Point,Integer>();
        for(int i=0;i<100;i++)
        {
            for(int j=0;j<100;j++)
            {
                grid[i][j] = -1;
                visited[i][j] = 0;
            }
        }

        for(int i=0;i<waterCells.size();i++)
        {
            Point tmp = waterCells.get(i);
            visited[tmp.x][tmp.y]= -2;
            grid[tmp.x][tmp.y] = -2;
        }
	


    private ArrayList<ArrayList<Record>> landInfo; // similar to 'records' but global for dry land claims
    private ArrayList<ArrayList<Record>> mudInfo; // similar to 'records' but global for muddy land claims

    private int x_dir = 1;
    private int y_dir = 0;
    private int num_moves = 0;
    private List<Point> water;
    
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {

        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
            }

	    // System.out.println(row);
            this.records.add(row);
        }

	    System.out.println(row);
            this.records.add(row);
        }

        water = waterCells;

    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;

        visited[loc.x][loc.y] = 1;
	// System.out.println("Called observe function =========");

	System.out.println("Called observe function =========");

        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            Record record = records.get(p.x).get(p.y);


            if(status.getC()==0)
                {
                    grid[p.x][p.y] = 0;
                    visited[p.x][p.y] = 1;
                }
            else if(status.getC()==1)
                {
                    grid[p.x][p.y] = -1;
                    visited[p.x][p.y] = 1;
                }

            if(status.getPT()==1)
            {
                grid[p.x][p.y] = 1;
                package_Location.x = p.x;
                package_Location.y = p.y;
                _package =true;
            }
            else if (status.getPT()==2)
            {
                grid[p.x][p.y] = 2;
                target_Location.x = p.x;
                target_Location.y = p.y;
                _target =true;
            }
	        // System.out.println(p + " " + status + " " );

	    System.out.println(p + " " + status + " " );

            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
        }
    }
    
    public List<Record> sendRecords(int id)
    {

        // System.out.println("Called sendRecords ======");	  

        System.out.println("Called sendRecords ======");	  

        ArrayList<Record> toSend = new ArrayList<Record>();
        for (ArrayList<Record> row : records)
        {
            for (Record record : row)
            {
                if (record != null)
                {
                    toSend.add(record);
                }
            }
        }
        return toSend;
    }
    
    public void receiveRecords(int id, List<Record> records)
    {

	   // System.out.println("Called receiveRecords Command ========");
       for(int i=0;i<records.size();i++)
       {
         Record new_record = records.get(i);
         Point p = new_record.getLoc();
         Record curr_record = this.records.get(p.x).get(p.y);

         visited[p.x][p.y] = 1;  // to be changed in case of spy

         if(new_record.getC()==0)
            {
                grid[p.x][p.y] = 0;    
            }
         else if(new_record.getC()==1)
            {
                grid[p.x][p.y] = -1;
            }

            if(new_record.getPT()==1)
            {
                grid[p.x][p.y] = 1;
                package_Location.x = p.x;
                package_Location.y = p.y;
                _package =true;
            }
            else if (new_record.getPT()==2)
            {
                grid[p.x][p.y] = 2;
                target_Location.x = p.x;
                target_Location.y = p.y;
                _target =true;
            }

          if(curr_record==null)
          {
            curr_record = new Record(new_record);
            this.records.get(p.x).set(p.y, curr_record);

            } 

           else
            curr_record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));

       }


	System.out.println("Called receiveRecords Command ========");

    }
    
    public List<Point> proposePath()
    {

        if(proposedPath.size()>1)
            return proposedPath;


        return null;
    }
    
    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths)
    {
        for (Map.Entry<Integer, List<Point>> entry : paths.entrySet())
        {
            ArrayList<Integer> toReturn = new ArrayList<Integer>();
            toReturn.add(entry.getKey());
            return toReturn;
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {

       	// System.out.println("Called receiveResults Command =======");
    }

    private Point explore()
    {
        int i = loc.x;
        int j = loc.y;


        if(i+1<100 && grid[i+1][j]!=-2)
            return new Point(i+1,j);

        if(i-1>=0 && grid[i-1][j]!=-2)
            return new Point(i-1,j);

        if(j+1<100 && grid[i][j+1]!=-2)
            return new Point(i,j+1);

        if(j-1>=0 && grid[i][j-1]!=-2)
            return new Point(i,j-1);

        if(i+1<100 && j+1<100 && grid[i+1][j+1]!=-2)
            return new Point(i+1,j+1);

        if(i-1>=0 && j+1<100 && grid[i-1][j+1]!=-2)
            return new Point(i-1,j+1);

        if(i+1<100 && j-1>=0 && grid[i+1][j-1]!=-2)
            return new Point(i+1,j-1);

        if(j-1>=0 &&  i-1>=0 && grid[i-1][j-1]!=-2)
            return new Point(i-1,j-1);


        return new Point(-1000,-1000);

    }

    // private Point getNearestUnExplored(Point curr)
    // {
    //     if(curr.x<0 || curr.y<0 || curr.x>=100 || curr.y>=100)
    //         return new Point(-1000,-1000);

    //     if(visited[curr.x][curr.y]==0)
    //         {
    //             explored[curr.x][curr.y] = 1;
    //             return new Point(curr.x,curr.y);
    //         }


    //     int min_dist = Integer.MAX_VALUE;
    //     Point next_move = new Point(-2000,-2000);

    //     explored[curr.x][curr.y] = 1;

    //     for(int i=curr.x-1;i<=curr.x+1;i++)
    //     {
    //         for(int j=curr.y-1;j<=curr.y+1;j++)
    //         {
    //             if(i < 0 || i>=100 || j<0 || j>=100 || explored[i][j]==1) continue;

    //             Point move = getNearestUnExplored(new Point(i,j));

    //             if(move.x<0 || move.y <0) continue;
    //             int manhattan_dist = Math.abs(move.x-loc.x) + Math.abs(move.y-loc.y) - grid[move.x][move.y];
    //             if(manhattan_dist<min_dist)
    //             {
    //                 min_dist = manhattan_dist;
    //                 next_move = move;
    //             }
    //         }
    //     }

    //     return next_move;

    // }

    private Point getNearestUnExplored(Point curr)
    {

        double min_dist = Integer.MAX_VALUE;
        Point next_move = new Point(-2000,-2000);

        for(int i=0;i<100;i++)
        {
            for(int j=0;j<100;j++)
            {
                if(grid[i][j]==-2 || visited[i][j]==1) continue;

                double dist_curr = Math.abs(curr.x-i) + Math.abs(curr.y-j) - grid[i][j];

                if(dist_curr<min_dist)
                {
                    min_dist = dist_curr;
                    next_move = new Point(i,j);
                }
            }
        }
        return next_move;

    }


    private Point getNextOnPath(Point loc,Point destination,Boolean safe)
    {
        HashMap<Point, Double> dist = new HashMap<Point, Double>();
        HashMap<Point, Point> parent = new HashMap<Point, Point>();

        Boolean found = false;

        for(int i=0;i<100;i++)
        {
            for(int j=0;j<100;j++)
            {
                dist.put(new Point(i,j),Double.POSITIVE_INFINITY);
            }
        }

        dist.put(loc,0.0);

        PriorityQueue<Entry> q = new PriorityQueue<>();
        Entry s = new Entry(0.0,loc);
        q.add(s);
        
        while(q.peek()!=null && !found)
            {
                Entry tmp = q.poll();
                Point next = tmp.p;
                explored[next.x][next.y]=1;
                for(int i = next.x-1;i<=next.x+1;i++)
                {
                    for(int j = next.y-1;j<=next.y+1;j++)
                    {
                        double diff = Math.abs(next.x-i) + Math.abs(next.y-j);
                        Double val = Double.POSITIVE_INFINITY; 

                        if(i < 0 || i>=100 || j<0 || j>=100 || explored[i][j]==1 || grid[i][j]==-2) continue;
                        if(safe && grid[i][j]<0) continue;

                        if(diff>1)
                            val = tmp.key + 1.5 - 2*grid[i][j];
                        else
                            val = tmp.key + 1 - 2*grid[i][j];

                        Point pt = new Point(i,j);
                        Double distance = dist.get(pt);


                        if(val<distance)
                        {
                            dist.put(pt,val);
                            parent.put(pt,next);
                            Entry new_entry = new Entry(val,pt);
                            q.add(new_entry);
                        }

                        if(destination.x == i && destination.y ==j)
                           {
                            found =true;
                            System.out.println("location is  " + loc + " destination is " + destination);
                            System.out.println("found the destination at distance "  + val);
                        }

                             
                    }

                }
            }


            Point next = new Point(destination);
            Point prev = new Point(-1000,-1000);

            if(!found_path)
            proposedPath.clear();

            while(parent.get(next)!=null)
            {
                System.out.println(next);
                prev = new Point(next.x,next.y);
                if(!found_path)
                proposedPath.add(0,new Point(prev.x,prev.y));
                next = new Point(parent.get(next));
                
            }


            return prev;


    }

    
    public Point getMove()
    {

    for(int i=0;i<100;i++)
    {
        for(int j=0;j<100;j++)
            explored[i][j] = 0;
    }

    visited[loc.x][loc.y] = 1;
	// System.out.println("Called getMove Command =======");
    Point move = new Point(-1000,-1000);

    if(_target && _package)
    {
        //wait
        if(!found_path)
        {
            proposedPath.clear();
            
            Point start = package_Location;
            getNextOnPath(start,target_Location,true);

            proposedPath.add(0,start);

            Point reach_pt = proposedPath.get(proposedPath.size()-1);

            if(reach_pt.x==target_Location.x && reach_pt.y == target_Location.y)
                found_path = true;
            
            for(int i=0;i<proposedPath.size();i++)
            {
                System.out.println(proposedPath.get(i));
            }
        }
        //announce shortest path
    }


    if(_target && _package && found_path && (loc.x!=package_Location.x || loc.y!=package_Location.y))
    {
        //go to package
        Point next = getNextOnPath(loc,package_Location,false);
        move = next;
        System.out.println("location is " + loc + " moving to " + move );
        int x  = move.x - loc.x;
        int y = move.y - loc.y;

        
        return new Point(x,y);
    }
    else if(_target && _package && found_path)
    {
        return new Point(0,0);
    }


    
    

    Point next_loc = getNearestUnExplored(loc);
    for(int i=0;i<100;i++)
    {
        for(int j=0;j<100;j++)
            explored[i][j] = 0;
    }
    Point next = getNextOnPath(loc,next_loc,false);

    if(trap_count.containsKey(next))
    {
        trap_count.put(next,trap_count.get(next)+1);
    }
    else
    {
        trap_count.put(next,0);
    }

    if(trap_count.get(next)<10)
    {
        move = next;
        int x  = move.x - loc.x;
        int y = move.y - loc.y;
        System.out.println("moving to closest unexplored from " + loc + " moving to " + next_loc + "via "  + move );
        System.out.println("the cell condition for " + move +   " is  " + grid[move.x][move.y] );
        return new Point(x,y);
    }


    move = explore();
    

    if(move.x>=0 && move.y>=0)
        {
            System.out.println("current location is " + loc + " moving to unvisited location  " + move );
            int x  = move.x - loc.x;
            int y = move.y - loc.y;
            return new Point(x,y);
        }

        return move;

       	System.out.println("Called receiveResults Command =======");
    }
    
    public List<Point> line(Point a, Point b)
    {
        Integer dx = b.x - a.x;
        Integer dy = b.y - a.y;

        ArrayList<Point> pts = new ArrayList<Point>();

        if (a.x == b.x)
        { 
            if (a.y < b.y)
            {
                for (int i = a.y; i <= b.y; i++)
                {
                    pts.add(new Point(a.x, i));
                }
            }
            else
            {
                for (int i = a.y; i >= b.y; i--)
                {
                    pts.add(new Point(a.x, i));
                }
            }
        }
        else if (a.y == b.y)
        { 
            if (a.x < b.x)
            {
                for (int i = a.x; i <= b.x; i++)
                {
                    pts.add(new Point(i, a.y));
                }
            }
            else
            {
                for (int i = a.x; i >= b.x; i--)
                {
                    pts.add(new Point(i, a.y));
                }
            }
        }

        return pts;
    }

    public Boolean validMove()
    {
        Point move = new Point(loc.x + x_dir, loc.y + y_dir);
        if ((!water.contains(move) && (loc.x >= 2) && (loc.x <= 97) && (loc.y >= 2) && (loc.y <= 97)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Point getMove()
    {
	    System.out.println("Called getMove Command =======");

        Integer x_prop = loc.x + x_dir;
        Integer y_prop = loc.y + y_dir;
        Point candidate = new Point(x_prop, y_prop); while ((water.contains(candidate) || (x_prop < 2) || (x_prop > 97) || (y_prop < 2) || (y_prop > 97))) { if (x_dir == 1)
            {
                x_dir = 0;
                y_dir = 1;
            }
            else if (y_dir == 1)
            {
                x_dir = -1;
                y_dir = 0;
            }
            else if (x_dir == -1)
            {
                x_dir = 0;
                y_dir = -1;
            }
            else
            {
                x_dir = 1;
                y_dir = 0;
            }
            x_prop = loc.x + x_dir;
            y_prop = loc.y + y_dir;

            candidate = new Point(x_prop, y_prop);
        }

        return new Point(x_dir, y_dir);

        //if ((!water.contains(new Point(loc.x + 1, loc.y))) && (loc.x + 1 <= 97))
        //{
        //    move = new Point(1, 0);
        //}
        //else if ((!water.contains(new Point(loc.x, loc.y + 1))) && (loc.y + 1 <= 97))
        //{
        //    move = new Point(0, 1);
        //}
        //else if ((!water.contains(new Point(loc.x - 1, loc.y))) && (loc.x - 1 >= 2))
        //{
        //    move = new Point(-1, 0);
        //}
        //else if ((!water.contains(new Point(loc.x, loc.y - 1))) && (loc.y - 1 >= 2))
        //{
        //    move = new Point(0, -1);
        //}
        //else
        //{
        //    move = new Point(-1, 0);
        //}

        //ArrayList<Point> points = new ArrayList<Point>();

        //points.add(new Point(3, 4));
        //points.add(new Point(4, 4));
        //points.add(new Point(4, 3));

        //points.add(new Point(3, 96));
        //points.add(new Point(10, 96));
        //points.add(new Point(10, 3));
        //points.add(new Point(17, 3));
        //points.add(new Point(17, 96));
        //points.add(new Point(24, 96));
        //points.add(new Point(24, 3));
        //points.add(new Point(31, 3));
        //points.add(new Point(31, 96));
        //points.add(new Point(38, 96));
        //points.add(new Point(38, 3));
        //points.add(new Point(45, 3));
        //points.add(new Point(45, 96));
        //points.add(new Point(52, 96));
        //points.add(new Point(52, 3));
        //points.add(new Point(59, 3));
        //points.add(new Point(59, 96));
        //points.add(new Point(66, 96));
        //points.add(new Point(66, 3));
        //points.add(new Point(73, 3));
        //points.add(new Point(73, 96));
        //points.add(new Point(80, 96));
        //points.add(new Point(80, 3));
        //points.add(new Point(87, 3));
        //points.add(new Point(87, 96));
        //points.add(new Point(94, 96));
        //points.add(new Point(94, 3));
        //points.add(new Point(96, 3));
        //points.add(new Point(96, 96));

        //ArrayList<Point> path = new ArrayList<Point>();

        //Point a = new Point(3, 3);
        //Point b;

        //for (Point p: points)
        //{
        //    b = p;
        //    List<Point> mini_path = line(a, b);
        //    for (Point p_: mini_path)
        //    {
        //        path.add(p_);
        //    }
        //    a = p;
        //}
 
        //Point dest = path.get(num_moves);
        //System.out.println(dest);
        //num_moves += 1;

        //return move;
    }
}
