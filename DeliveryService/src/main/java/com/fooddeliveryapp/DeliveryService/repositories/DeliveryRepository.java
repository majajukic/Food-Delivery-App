package com.fooddeliveryapp.DeliveryService.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fooddeliveryapp.DeliveryService.entities.DeliveryDetails;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryDetails, UUID> {

}
