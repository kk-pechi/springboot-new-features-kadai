package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

//ゲッターやセッターを自動生成
@Data
public class ReviewRegisterForm {
    private Integer houseId;
    
    private Integer userId; 
	
	private Integer rating;
		
	@NotBlank(message = "コメントを入力してください。")
	private String Comment;
}
