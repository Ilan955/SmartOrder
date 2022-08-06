package com.example.smartord.Classes;

public class DbOrder {
    String amountOfPeople;
    String amountOfReservation;



    public DbOrder(String amountOfPeople,String amountOfReservation){
        this.amountOfPeople =amountOfPeople;
        this.amountOfReservation= amountOfReservation;
    }

    public String getAmountOfReservation() { return amountOfReservation; }

    public void setAmountOfReservation(String amountOfReservation) { this.amountOfReservation = amountOfReservation; }

    public String getAmountOfPeople() { return amountOfPeople; }

    public void setAmountOfPeople(String amountOfPeople) { this.amountOfPeople = amountOfPeople; }
}
