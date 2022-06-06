package work;

public class Job implements Comparable<Job>{

    private int salary;
    private String name;

    public Job(int salary,String name){
        this.salary = salary;
        this.name = name;
    }
    public String getName(){return  name;}
    public int getSalary(){return salary;}
    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Job job = (Job) o;
        return salary == job.getSalary() && name.equals(job.getName());
    }
    @Override
    public int compareTo(Job job){
        if(this.getSalary() < job.getSalary())
            return 1;
        else if(this.getSalary() > job.getSalary())
            return -1;

        return 0;
    }
    @Override
    public Job clone(){
        return new Job(salary,name);
    }
    @Override
    public  String toString(){ return "[Salary :"+salary+" name: "+name+"]";}
}
