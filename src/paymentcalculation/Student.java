
package paymentcalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Student {
    private String name;
    private double balance;
    private boolean hasDebt;
    private double nextMonthHours = 0.0;
    private boolean indGraphic = false;
    private double discount = 0.0;
    
    public String getName(){
        return this.indGraphic ? this.name + " (инд график!)" : this.name;
    }
    public double getBalance(){
        return this.balance;
    }
    public boolean hasDebt(){
        return hasDebt;
    }
    public double getNextMonthHours(){
        return this.nextMonthHours;
    }
    public double getDiscount(){
        return this.discount;
    }    
    
    public void setName(String name){
        this.name = name;
    }
    public void setBalance(double balance){
        this.balance = balance;
        if(balance < 0 && balance > 0.03){
            this.hasDebt = true;
        } else this.hasDebt = false;
    }
    public void setNextMonthHours(double hours){
        this.nextMonthHours += hours;
    }
    public void removeNextMonthHours(double hours){
        this.nextMonthHours -= hours;
    }
    
    public double countPayment(){
        return this.getNextMonthHours() - this.getBalance();
    }
    public double countPayment2N(){
        BigDecimal bd = new BigDecimal(this.getNextMonthHours() - this.getBalance());
//        System.out.println("before: " + bd);
//        System.out.println("after: " + bd.setScale(2, RoundingMode.HALF_UP));
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public void setIndGrahic(boolean indGraph){
        this.indGraphic = indGraph;
    }
    public void setDiscount(double discount) {
        this.discount = discount * 100;
    }
        
}
