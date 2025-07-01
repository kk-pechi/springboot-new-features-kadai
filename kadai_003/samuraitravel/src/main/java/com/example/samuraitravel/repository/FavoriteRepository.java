package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
	//お気に入り登録済みか確認
	//UserのIDとHouseのIDが一致するレコードが存在するか
	boolean existsByUserIdAndHouseId(Integer userId, Integer houseId);
	
	//指定したユーザと民宿の組み合わせに一致するお気に入り情報を取得
	//お気に入り解除につかう
	List<Favorite> findByUserIdAndHouseId(Integer userId, Integer houseId);
	
	//お気に入りのリストを取得する
	//ページネーションも使う
	Page<Favorite> findByUserId(Integer userId, Pageable pageable);


}
