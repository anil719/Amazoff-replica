package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    public HashMap<String, Order> orderHashMap;
    public HashMap<String, DeliveryPartner> deliveryPartnerHashMap;
    public HashMap<String, List<String>> partnerOrderHashMap;
    public HashMap<String, String> orderPartnerPairMap;

    public OrderRepository(){
        this.orderHashMap = new HashMap<>();
        this.deliveryPartnerHashMap = new HashMap<>();
        this.partnerOrderHashMap = new HashMap<>();
        this.orderPartnerPairMap = new HashMap<>();
    }

    public void addOrder(Order order){
        String orderId = order.getId();
        orderHashMap.put(orderId, order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerHashMap.put(partnerId, deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
       List<String> orderlistOfPartner = new ArrayList<>();

        if(partnerOrderHashMap.containsKey(partnerId)){
           orderlistOfPartner = partnerOrderHashMap.get(partnerId);
        }
       orderlistOfPartner.add(orderId);
       partnerOrderHashMap.put(partnerId, orderlistOfPartner);

       //update orderPartnerPairMap
        orderPartnerPairMap.put(orderId, partnerId);

        //update no of orders by the delivery Partner
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartner.setNumberOfOrders(orderlistOfPartner.size());
    }

    public Order getOrderById(String orderId){
        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerHashMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId){
        Integer count = null;
        if(partnerOrderHashMap.containsKey(partnerId)){
            count = partnerOrderHashMap.get(partnerId).size();
        }
        return count;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>();
        if(partnerOrderHashMap.containsKey(partnerId)){
            orderList = partnerOrderHashMap.get(partnerId);
        }
        return orderList;
    }

    public List<String> getAllOrders(){
        List<String> allOrders = new ArrayList<>();
        for(String orderId : orderHashMap.keySet()){
            allOrders.add(orderId);
        }
        return allOrders;
    }

    public Integer getCountOfUnassignedOrders(){
        int count =0;
        for(Order order : orderHashMap.values()){
            String orderId = order.getId();
            if(!orderPartnerPairMap.containsKey(orderId)) count++;
        }
        return count;
        //return ordersMap.size()-orderPartnerPairMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String tyme, String partnerId){
        int count = 0;
        int time = Order.getDeliveryTimeAsInt(tyme);
        List<String> orderList = partnerOrderHashMap.get(partnerId);
        for(String orderId : orderList){
            if(orderHashMap.get(orderId).getDeliveryTime()>time){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int lastDeliveryTime = 0;
        List<String> ordersList = partnerOrderHashMap.get(partnerId);
        for(String orderId : ordersList){
            lastDeliveryTime = Math.max(lastDeliveryTime, orderHashMap.get(orderId).getDeliveryTime());
        }
        return Order.getDeliveryTimeAsString(lastDeliveryTime);
    }

    public void deletePartnerById(String  partnerId){
        //remove delivery Partner from deliveryPartnerHashMap
        deliveryPartnerHashMap.remove(partnerId);
        //delete orders Assigned to that deliveryPartner from partnerOrderHashMap
        List<String> ordersList = partnerOrderHashMap.get(partnerId);
        for(String orderId : ordersList){
            orderPartnerPairMap.remove(orderId);
        }
        //delete order Partner Pair from deliveryPartner Hashmap
        deliveryPartnerHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        //delete it from ordersMap
        orderHashMap.remove(orderId) ;
        if(orderPartnerPairMap.containsKey(orderId)){
            String partnerId = orderPartnerPairMap.get(orderId);
            orderPartnerPairMap.remove(orderId);
            partnerOrderHashMap.get(partnerId).remove(orderId);

            deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(partnerOrderHashMap.get(partnerId).size());
        }
    }
}
