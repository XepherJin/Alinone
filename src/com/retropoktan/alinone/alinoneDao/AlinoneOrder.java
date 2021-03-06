package com.retropoktan.alinone.alinoneDao;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ALINONE_ORDER.
 */
public class AlinoneOrder {

    /** Not-null value. */
    private String orderID;
    /** Not-null value. */
    private String objectPhone;
    /** Not-null value. */
    private String orderAddress;
    /** Not-null value. */
    private String merchantID;
    /** Not-null value. */
    private java.util.Date orderTime;
    /** Not-null value. */
    private String merchantName;
    private boolean paid;
    private float price;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient AlinoneOrderDao myDao;

    private List<Dish> dishes;

    public AlinoneOrder() {
    }

    public AlinoneOrder(String orderID) {
        this.orderID = orderID;
    }

    public AlinoneOrder(String orderID, String objectPhone, String orderAddress, String merchantID, java.util.Date orderTime, String merchantName, boolean paid, float price) {
        this.orderID = orderID;
        this.objectPhone = objectPhone;
        this.orderAddress = orderAddress;
        this.merchantID = merchantID;
        this.orderTime = orderTime;
        this.merchantName = merchantName;
        this.paid = paid;
        this.price = price;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAlinoneOrderDao() : null;
    }

    /** Not-null value. */
    public String getOrderID() {
        return orderID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    /** Not-null value. */
    public String getObjectPhone() {
        return objectPhone;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setObjectPhone(String objectPhone) {
        this.objectPhone = objectPhone;
    }

    /** Not-null value. */
    public String getOrderAddress() {
        return orderAddress;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    /** Not-null value. */
    public String getMerchantID() {
        return merchantID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    /** Not-null value. */
    public java.util.Date getOrderTime() {
        return orderTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOrderTime(java.util.Date orderTime) {
        this.orderTime = orderTime;
    }

    /** Not-null value. */
    public String getMerchantName() {
        return merchantName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public boolean getPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Dish> getDishes() {
        if (dishes == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DishDao targetDao = daoSession.getDishDao();
            List<Dish> dishesNew = targetDao._queryAlinoneOrder_Dishes(orderID);
            synchronized (this) {
                if(dishes == null) {
                    dishes = dishesNew;
                }
            }
        }
        return dishes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetDishes() {
        dishes = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
