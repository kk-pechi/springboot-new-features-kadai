package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

//全フィールドに値をセットするための引数つきコンストラクタを自動生成できる
//コントローラからビューに渡すときにレビューデータの値をセットするため
@AllArgsConstructor
@Data
public class ReviewEditForm {
	private Integer id;

	private Integer houseId;
    
	private Integer userId; 
	
	private Integer rating;
		
	@NotBlank(message = "コメントを入力してください。")
	private String Comment;

}
