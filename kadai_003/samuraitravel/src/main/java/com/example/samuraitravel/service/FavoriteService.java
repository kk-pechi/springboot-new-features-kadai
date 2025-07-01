package com.example.samuraitravel.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class FavoriteService {
	//お気に入り、民宿、ユーザー情報を扱うためのリポジトリ
	private final FavoriteRepository favoriteRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	
	//各リポジトリをSpringから注入するためのコンストラクタ（DI）
	public FavoriteService(FavoriteRepository favoriteRepository, HouseRepository houseRepository, UserRepository userRepository) {
		this.favoriteRepository = favoriteRepository; 
		this.houseRepository = houseRepository; 
		this.userRepository = userRepository; 
	}
	
	@Transactional//お気に入りを追加する
	public void addFavorite(Integer userId, Integer houseId){
		if (!favoriteRepository.existsByUserIdAndHouseId(userId, houseId)) {
			Favorite favorite = new Favorite();
			favorite.setUser(userRepository.getReferenceById(userId));
			favorite.setHouse(houseRepository.getReferenceById(houseId));
			//Favoriteオブジェクトを作成して、ユーザーと民宿情報をセットしDBに登録
			favoriteRepository.save(favorite);
		}
	}
		
	@Transactional
	public void removeFavorite(Integer userId, Integer houseId) {
	    List<Favorite> favorites = favoriteRepository.findByUserIdAndHouseId(userId, houseId);
	    for (Favorite favorite : favorites) {
	        favoriteRepository.delete(favorite);
	    }
	}
	
	//お気に入りに登録しているかチェック
	public boolean isFavorite(Integer userId, Integer houseId) {
		//この組み合わせのデータが存在するかどうかを返す
		return favoriteRepository.existsByUserIdAndHouseId(userId, houseId);
	}
	
	//ページネーションありのリスト取得用
	public Page<Favorite> getFavoritesByUser(Integer userId, Pageable pageable) {
		return favoriteRepository.findByUserId(userId, pageable);
	}
	
	
}
