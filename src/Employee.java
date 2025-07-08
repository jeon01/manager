public class Employee {
    private String empid;
    private String name;

    public Employee(String empid, String name){
        this.empid=empid;
        this.name=name;
    }

    public String getEmpid(){
        return empid;
    }

    public String getName(){
        return name;
    }
}
