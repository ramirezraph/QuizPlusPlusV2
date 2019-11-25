package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 2/17/2018.
 */

public class Room {
    private String roomId;
    private String roomTitle, roomOwner, roomPassword, roomQuizID, userListID, owner_name;
    private Boolean isPrivate;
    private int roomNumberOfUser, roomSize;


    public Room() {
    }

    public Room(String roomId, String roomTitle, String roomOwner, String roomPassword, String roomQuizID, String userListID, Boolean isPrivate, int roomNumberOfUser, int roomSize, String owner_name) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.roomOwner = roomOwner;
        this.roomPassword = roomPassword;
        this.roomQuizID = roomQuizID;
        this.userListID = userListID;
        this.isPrivate = isPrivate;
        this.roomNumberOfUser = roomNumberOfUser;
        this.roomSize = roomSize;
        this.owner_name = owner_name;
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getRoomQuizID() {
        return roomQuizID;
    }

    public void setRoomQuizID(String roomQuizID) {
        this.roomQuizID = roomQuizID;
    }

    public String getUserListID() {
        return userListID;
    }

    public void setUserListID(String userListID) {
        this.userListID = userListID;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getRoomNumberOfUser() {
        return roomNumberOfUser;
    }

    public void setRoomNumberOfUser(int roomNumberOfUser) {
        this.roomNumberOfUser = roomNumberOfUser;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }
}
