package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<Car,Long> {

    Iterable<Car> findByModelOrManufacturerContainingIgnoreCase(String model, String manufacturer);
}
