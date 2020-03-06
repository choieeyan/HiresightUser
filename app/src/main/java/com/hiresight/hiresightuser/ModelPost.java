package com.hiresight.hiresightuser;

public class ModelPost {
    private String postDateTime, startDate, endDate, location, product, pay, paxRequired, profession, clientID, companyName, imageURL;

    public ModelPost() {
    }

    public ModelPost(String postDateTime, String startDate, String endDate, String location, String product, String pay, String paxRequired, String profession, String clientID, String companyName ) {
        this.postDateTime = postDateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.product = product;
        this.pay = pay;
        this.paxRequired = paxRequired;
        this.profession = profession;
        this.clientID = clientID;
        this.companyName = companyName;
    }

    public String getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(String postDateTime) {
        this.postDateTime = postDateTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getPaxRequired() {
        return paxRequired;
    }

    public void setPaxRequired(String paxRequired) {
        this.paxRequired = paxRequired;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getCompanyName() {

        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
