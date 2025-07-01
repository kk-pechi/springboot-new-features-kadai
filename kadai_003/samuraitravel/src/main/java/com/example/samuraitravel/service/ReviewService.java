package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	
	public ReviewService(ReviewRepository reviewRepository, HouseRepository houseRepository, UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
		
	}

//	データベースの操作のまとまり
	@Transactional
	public void create(ReviewRegisterForm reviewRegisterForm) {
//		エンティティをインスタンス化
		Review review = new Review();
		
		House house = houseRepository.getReferenceById(reviewRegisterForm.getHouseId());
		User user = userRepository.getReferenceById(reviewRegisterForm.getUserId());
	    
		review.setHouse(house);
		review.setUser(user);
		review.setRating(reviewRegisterForm.getRating());
	    review.setComment(reviewRegisterForm.getComment());
		
//      エンティティをデータベースに登録する
		reviewRepository.save(review);
	}
	
	
	@Transactional
	public void edit(ReviewEditForm reviewEditForm, Integer userId) {
//		updateメソッドはidを使って更新するエンティティを取得する
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		House house = houseRepository.getReferenceById(reviewEditForm.getHouseId());
		User user = userRepository.getReferenceById(reviewEditForm.getUserId());
		
	    
		review.setHouse(house);
		review.setUser(user);
		review.setRating(reviewEditForm.getRating());
	    review.setComment(reviewEditForm.getComment());
		
//      エンティティをデータベースに登録する
		reviewRepository.save(review);
	}
	
//	HouseControllerでfindAllをつかうため
	public List<Review> findAll() {
		return reviewRepository.findAll();
	}

	public void deleteById(Integer reviewId) {
		reviewRepository.deleteById(reviewId);
		
	}
	
}