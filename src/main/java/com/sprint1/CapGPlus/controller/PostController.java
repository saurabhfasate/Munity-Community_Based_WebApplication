package com.sprint1.CapGPlus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sprint1.CapGPlus.dto.outer.PostDTOOuter;
import com.sprint1.CapGPlus.exception.CommunityNotFoundException;
import com.sprint1.CapGPlus.exception.PostNotFoundException;
import com.sprint1.CapGPlus.service.PostService;

@RestController
public class PostController {

	@Autowired
	private PostService postService;

	@GetMapping("/post")
	public ResponseEntity<List<PostDTOOuter>> getAllPosts() {
		List<PostDTOOuter> list = postService.getAllPosts();
		return new ResponseEntity<>(list, HttpStatus.FOUND);
	}

	@GetMapping("/post/{postId}")
	private ResponseEntity<PostDTOOuter> getPostById(@PathVariable int postId) throws PostNotFoundException {
		return new ResponseEntity<>(postService.getPostById(postId), HttpStatus.OK);
	}

	@GetMapping("/post/community/{comId}")
	private ResponseEntity<List<PostDTOOuter>> getPostByCommunity(@PathVariable int comId)
			throws CommunityNotFoundException {
		return new ResponseEntity<>(postService.getPostByCommunity(comId), HttpStatus.OK);
	}

}
