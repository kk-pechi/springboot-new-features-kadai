package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "favorite")
@Data
public class Favorite {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY) 
		//AUTO_INCREMENTを指定したカラム(idカラム)を利用して値を生成するidを自動採番してくれる
		private Integer id;
		
//		ユーザー
		@ManyToOne
		@JoinColumn(name = "user_id", nullable = false)
		private User user;
		
//		民宿(House)
		@ManyToOne
		@JoinColumn(name = "house_id", nullable = false)
		private House house;
		
		@Column(name = "created_at", insertable = false, updatable = false)
		private Timestamp createdAt;
}
