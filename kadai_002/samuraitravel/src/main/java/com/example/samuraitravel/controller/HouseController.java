package com.example.samuraitravel.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    
    public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository, ReviewService reviewService) {
        this.houseRepository = houseRepository;   
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
    }
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,  
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
        Page<House> housePage;
                
        if (keyword != null && !keyword.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }            
        } else if (area != null && !area.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
        } else if (price != null) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }            
        } else {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
            }            
        }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price); 
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        House house = houseRepository.findById(id).orElseThrow();
        List<Review> reviews = reviewRepository.findByHouseOrderByCreatedAtDesc(house);

        Integer loginUserId = (userDetails != null) ? userDetails.getUser().getId() : null;
        
        boolean canPostReview = false;
        if (loginUserId != null) {
            boolean alreadyReviewed = reviewRepository.existsByUserIdAndHouseId(loginUserId, id);
            canPostReview = !alreadyReviewed;
        }
        
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reviewList", reviews);
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("canPostReview", canPostReview);

        return "houses/show";
    }
    
	@GetMapping("{houseId}/reviews/new")
	public String reviewNewForm(@PathVariable(name = "houseId") Integer houseId, Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//		コントローラからビューにインスタンスを渡す
		House house = houseRepository.findById(houseId).orElseThrow();
		ReviewRegisterForm form = new ReviewRegisterForm();
	    form.setHouseId(houseId);
	    form.setUserId(userDetails.getUser().getId());
		
		model.addAttribute("house", house);
		model.addAttribute("reviewRegisterForm", form);
		
		return "reviews/new";
	}

	@GetMapping("/{id}/reviews")
	public String reviewList(@PathVariable(name = "id") Integer id,
	                         @PageableDefault(page = 0, size = 10) Pageable pageable,
	                         @AuthenticationPrincipal UserDetailsImpl userDetails,
	                         Model model) {

	    Integer loginUserId = (userDetails != null) ? userDetails.getUser().getId() : null;

	    House house = houseRepository.findById(id).orElseThrow();
	    Page<Review> reviewPage = reviewRepository.findByHouseOrderByCreatedAtDesc(house, pageable);
	    
//	    投稿ボタンの表示可否を判定する
	    boolean canPostReview = false;
	    if (loginUserId != null) {
	        boolean alreadyReviewed = reviewRepository.existsByUserIdAndHouseId(loginUserId, id);
	        canPostReview = !alreadyReviewed;
	    }

	    model.addAttribute("reservationInputForm", new ReservationInputForm());
	    model.addAttribute("house", house);
	    model.addAttribute("reviewPage", reviewPage);
	    model.addAttribute("loginUserId", loginUserId);
	    model.addAttribute("canPostReview", canPostReview);

	    return "reviews/list";
	}

    
    @GetMapping("/{houseId}/reviews/{reviewId}/edit")
    public String edit(@PathVariable(name = "houseId") Integer houseId,
                       @PathVariable(name = "reviewId") Integer reviewId, Model model) {

        Review review = reviewRepository.getReferenceById(reviewId);
        House house = houseRepository.findById(houseId).orElseThrow(); // houseオブジェクトを使っているからモデルに渡す

        ReviewEditForm reviewEditForm = new ReviewEditForm(
            review.getId(), review.getHouse().getId(), review.getUser().getId(), review.getRating(), review.getComment());

        model.addAttribute("reviewEditForm", reviewEditForm);
        model.addAttribute("house", house); //houseオブジェクトを使っているからモデルに渡す

        return "reviews/edit";
    }
        
    @PostMapping("/{houseId}/reviews/create")
    public String create(@PathVariable Integer houseId, @ModelAttribute @Validated ReviewRegisterForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
//      エラーがない場合はメソッドを実行して登録（引数はフォームクラスのインスタンス）
        reviewService.create(form);  // サービス経由で保存

        return "redirect:/houses/" + houseId;
    }
    
    @PostMapping("/{houseId}/review/{id}/delete")
    public String deleteReview(@PathVariable("houseId") Integer houseId,
                               @PathVariable("id") Integer reviewId,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {     
    	reviewService.deleteById(reviewId);
        
        return "redirect:/houses/" + houseId;
    } 
    
    @PostMapping("/{houseId}/reviews/{reviewId}/edit")
    public String updateReview(@PathVariable Integer houseId, @PathVariable Integer reviewId,
                               @ModelAttribute @Validated ReviewEditForm form, BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        if (bindingResult.hasErrors()) {
            House house = houseRepository.findById(houseId).orElseThrow();
            model.addAttribute("house", house);
            return "reviews/edit";
        }

        // 更新処理
        reviewService.edit(form, userDetails.getUser().getId());

        return "redirect:/houses/" + houseId;
    }

    
}
