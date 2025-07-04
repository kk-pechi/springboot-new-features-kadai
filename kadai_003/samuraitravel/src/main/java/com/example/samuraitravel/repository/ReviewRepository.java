package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	public Page<Review> findByHouseOrderByCreatedAtDesc(House house, Pageable pageable);
	List<Review> findByHouseOrderByCreatedAtDesc(House house);
	boolean existsByUserIdAndHouseId(Integer userId, Integer houseId);
}
