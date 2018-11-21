package paymentcalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GroupInfo {
    private String sheetName;
    private double pricePerHour;
    private String groupId;
    private String groupLevel;
    private String teacherOne;
    private String teacherTwo;
    private double classDurationOne;
    private double classDurationTwo;
    private String classStartTime;
    private List<String> classDaysOne = new ArrayList<>();
    private List<String> classDaysTwo = new ArrayList<>();
    private List<Student> studentsInfo = new ArrayList<>();
    private List<String> scheduleDays = new ArrayList<>();
    private boolean isIndividual = false;
    private boolean isMonWenFr = false;
    private boolean isTueTh = false;
    private boolean isSat = false;
    private boolean isOtherSchedule = false;    
    private String daysFilterString = "";
    private double nextMonthHours = 0.0;
    
    
    public double getNextMonthHours(){
        return this.nextMonthHours;
    }
    public void addNextMonthHours(double hours){
        this.nextMonthHours += hours;
    }
    public void removeNextMonthHours(double hours){
        this.nextMonthHours -= hours;
    }
	public String getSheetName(){
        return this.sheetName;
    }
    public double getPricePerHour(){
        return this.pricePerHour;
    }
    public String getGroupId(){
        return this.groupId;
    }
    public String getGroupLevel(){
        return this.groupLevel;
    }
    public String getTeacherOne(){
        return this.teacherOne;
    }
    public String getTeacherTwo(){
        return this.teacherTwo;
    }
    public double getClassDurationOne(){
        return this.classDurationOne;
    }
    public double getClassDurationTwo(){
        return this.classDurationTwo;
    }
    public String getClassStartTime(){
        return this.classStartTime;
    }
    public ArrayList<String> getClassDaysOne(){
        return (ArrayList)this.classDaysOne;
    }
    public ArrayList<String> getClassDaysTwo(){
        return (ArrayList)this.classDaysTwo;
    }
    public ArrayList<Student> getStudentsInfo(){
        return (ArrayList)this.studentsInfo;
    }
    public ArrayList<String> getScheduleDays(){
        return (ArrayList)this.scheduleDays;
    }
    public boolean getIsIndividual(){
        return this.isIndividual;
    }
    public boolean getIsMonWenFr(){
        return this.isMonWenFr;
    }
    public boolean getIsTueTh(){
        return this.isTueTh;
    }
    public boolean getIsSat(){
        return this.isSat;
    }
    public boolean getIsOtherSchedule(){
        return this.isOtherSchedule;
    }
    
    public void setSheetName(String sheetName){
        this.sheetName = sheetName;
    }
    public void setPricePerHour(double price){
        this.pricePerHour = price;
        if(price > 1500){
            this.isIndividual = true;
        }
    }
    public void setGroupId(String id){
        this.groupId = id;
    }
    public void setGroupLevel(String level){
        this.groupLevel = level;
    }
    public void setTeacherOne(String teacher){
        this.teacherOne = teacher;
    }
    public void setTeacherTwo(String teacher){
        this.teacherTwo = teacher;
    }
    public void setClassDurationOne(double duration){
        this.classDurationOne = duration;
    }
    public void setClassDurationTwo(double duration){        
        this.classDurationTwo = duration;
    }
    public void setClassStartTime(String start){
        this.classStartTime = start;
    }
    public void addClassDayOne(String day){
        classDaysOne.add(day);
    }
    public void addClassDayTwo(String day){
        classDaysTwo.add(day);
    }
    public void addStudent(Student student){
        studentsInfo.add(student);
    }
    
    public void createSheduleDays(){
        for(String day : this.classDaysOne) {
            this.scheduleDays.add(day);
        }
        for(String day : this.classDaysTwo) {
            if(!this.getScheduleDays().contains(day))
                this.scheduleDays.add(day);
        }
        
        String[] days = {"пн","вт","ср","чт","пт","сб","вс"};
        ArrayList<String> weekDays = new ArrayList<>(Arrays.asList(days));
        
        Iterator<String> iter = weekDays.iterator();
                while(iter.hasNext()){
                    if(!this.scheduleDays.contains(iter.next()))
                    iter.remove();
                }
        this.scheduleDays.clear();
        this.scheduleDays = weekDays; 
        
        if(!this.getIsIndividual()){
             if(scheduleDays.size() == 1 && scheduleDays.get(0).equals("сб")) isSat = true;
            else if(scheduleDays.size() >= 1 && (scheduleDays.contains("вт") || scheduleDays.contains("чт"))) isTueTh = true;
            else if(scheduleDays.size() >= 1 && (scheduleDays.contains("пн") || scheduleDays.contains("ср") ||scheduleDays.contains("пт"))) isMonWenFr = true;
            else isOtherSchedule = true;
        }
       
        
    }
    
    public String sheduleDaysToString(){
        String shedule = "";
        for(String day : this.getScheduleDays()) shedule += day + " ";
        return shedule;
    }
    
    public String getTeachers(){
        String teachers = this.getTeacherOne();
        if(!this.getTeacherTwo().equals("0")) teachers += ", " + this.getTeacherTwo();
        return teachers;
    }
    
    public String getDuration(){
        String duration = String.format("%.2f", this.getClassDurationOne());
        if(this.getClassDurationTwo() != 0 && this.getClassDurationTwo() != this.getClassDurationOne()) duration += ", " + String.format("%.2f", this.getClassDurationTwo());
        return duration;
    }
    
    public String createStudentsList(){
        String student = "";
        for(Student s : this.getStudentsInfo()){
            student += s.getName() + ": ";
            if(s.countPayment() <= 0.004) student += "не должен";
            else
                student += String.format("%.2f", s.countPayment());
            if(s.hasDebt()) student += " (имеется долг " + String.format("%.2f", Math.abs(s.getBalance())) + ")";
            student += " Скидка: " + s.getDiscount() + "%";
            student += "\n";
        }
        return student;
    }
	@Override
	public String toString() {
		return "GroupInfo [sheetName=" + sheetName + ", pricePerHour=" + pricePerHour + ", groupId=" + groupId
				+ ", groupLevel=" + groupLevel + ", teacherOne=" + teacherOne + ", teacherTwo=" + teacherTwo
				+ ", classDurationOne=" + classDurationOne + ", classDurationTwo=" + classDurationTwo
				+ ", classStartTime=" + classStartTime + ", classDaysOne=" + classDaysOne + ", classDaysTwo="
				+ classDaysTwo + ", studentsInfo=" + studentsInfo + ", scheduleDays=" + scheduleDays + ", isIndividual="
				+ isIndividual + ", isMonWenFr=" + isMonWenFr + ", isTueTh=" + isTueTh + ", isSat=" + isSat
				+ ", isOtherSchedule=" + isOtherSchedule + ", daysFilterString=" + daysFilterString
				+ ", nextMonthHours=" + nextMonthHours + "]";
	}
    
    
    
    
    /*public String toString(){
        String days = "";
        this.createSheduleDays();
        for(String day : this.getScheduleDays()){
            days += day + " ";
        }
        
        return "Название листа: " + this.getSheetName() + "\n"
               + "Номер группы: " + this.getGroupId() + "\n" 
               + "Цена за 1 а/ч: " + String.valueOf(this.getPricePerHour()) + " руб.\n" 
               + "Уровень: " + this.getGroupLevel() + "\n" 
               + "Преподаватель: " + this.getTeacherOne() + "\n" 
               + (this.getTeacherTwo().isEmpty() ? "":"Второй преподаватель: " + this.getTeacherOne() + "\n" )
               + "Продолжительность занятия: " + String.valueOf(this.getClassDurationOne()) + " а/ч\n" 
               + (this.getClassDurationTwo() == 0.0 ? "" : (this.getClassDurationTwo() == this.classDurationOne ? "" : "Продолжительность занятия со вторым преподавателем: " + String.valueOf(this.getClassDurationTwo()) + " а/ч\n"))
                
               + "Начало занятия: " + this.getClassStartTime() + "\n"
               + "Расписание: " + days + "\n"
               + this.createStudentsList();
    }*/
    
}

