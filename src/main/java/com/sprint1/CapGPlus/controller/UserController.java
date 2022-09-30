package com.sprint1.CapGPlus.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sprint1.CapGPlus.dto.outer.PostDTOOuter;
import com.sprint1.CapGPlus.dto.outer.UserDTO;
import com.sprint1.CapGPlus.entity.Comment;
import com.sprint1.CapGPlus.entity.DataHolder;
import com.sprint1.CapGPlus.entity.Post;
import com.sprint1.CapGPlus.entity.User;
import com.sprint1.CapGPlus.exception.ActionNotAllowedException;
import com.sprint1.CapGPlus.exception.ActionRepititionException;
import com.sprint1.CapGPlus.exception.CommentDoesNotExistException;
import com.sprint1.CapGPlus.exception.CommunityNotFoundException;
import com.sprint1.CapGPlus.exception.InvalidCredentialsException;
import com.sprint1.CapGPlus.exception.PostNotFoundException;
import com.sprint1.CapGPlus.exception.PostUnavailableException;
import com.sprint1.CapGPlus.exception.UserNameAlreadyExistsException;
import com.sprint1.CapGPlus.exception.UserNotFoundException;
import com.sprint1.CapGPlus.service.CommunityService;
import com.sprint1.CapGPlus.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CommunityService communityService;

	// User community starts

	@PostMapping("/user/{userId}/community/join/{communityId}")
	private ResponseEntity<Object> joinCommunity(@PathVariable int userId, @PathVariable int communityId)
			throws CommunityNotFoundException, UserNotFoundException {
		return new ResponseEntity<>(communityService.joinCommunity(userId, communityId), HttpStatus.OK);
	}

	@PostMapping("/user/{userId}/community/leave/{communityId}")
	private ResponseEntity<Object> leaveCommunity(@PathVariable int userId, @PathVariable int communityId)
			throws CommunityNotFoundException, UserNotFoundException {
		return new ResponseEntity<>(communityService.leaveCommunity(userId, communityId), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}/community")
	private ResponseEntity<Object> getCommunitiesbyUserId(@PathVariable int userId) throws UserNotFoundException {
		return new ResponseEntity<>(communityService.getCommunitiesbyUserId(userId), HttpStatus.OK);
	}

	// User community ends

	// User-Auth starts
	@PostMapping("/user")
	public ResponseEntity<String> saveUser(@Valid @RequestBody User user) throws UserNameAlreadyExistsException {
		// Check if password matches pattern
		String password = user.getPassword().trim();
		if (password == null || password.length() == 0)
			return new ResponseEntity<String>("Password is required", HttpStatus.BAD_REQUEST);

		if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"))
			return new ResponseEntity<String>(
					"Password should contain minimum eight characters, at least one letter and one number",
					HttpStatus.BAD_REQUEST);

		userService.saveUser(user);
		return new ResponseEntity<String>("User successfully created", HttpStatus.CREATED);
	}

	@PostMapping("/user/login")
	public ResponseEntity<String> userLogin(@Valid @RequestBody DataHolder dataHolder)
			throws InvalidCredentialsException {
		if (userService.userLogin(dataHolder))
			return new ResponseEntity<String>("Logged in succcessfully", HttpStatus.FOUND);
		return new ResponseEntity<String>("Log in failed", HttpStatus.NOT_FOUND);
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> list = userService.getAllUsers();
		return new ResponseEntity<List<UserDTO>>(list, HttpStatus.FOUND);
	}
	// User-Auth ends

	@GetMapping("/user/{userId}")
	private ResponseEntity<Object> getUserById(@PathVariable int userId) throws UserNotFoundException {
		return new ResponseEntity<>(userService.getUserbyId(userId), HttpStatus.OK);
	}

	// User posts starts

	@GetMapping("/user/{userId}/post")
	private ResponseEntity<List<PostDTOOuter>> getUserPosts(@PathVariable int userId) throws UserNotFoundException {
		return new ResponseEntity<>(userService.getAllUserPosts(userId), HttpStatus.OK);
	}

	@PostMapping("/user/{userId}/{communityId}/post")
	private ResponseEntity<PostDTOOuter> createPostInCommunity(@Valid @PathVariable int userId,
			@PathVariable int communityId, @Valid @RequestBody Post post)
			throws UserNotFoundException, CommunityNotFoundException, ActionNotAllowedException {
		return new ResponseEntity<>(userService.createPost(userId, post, communityId), HttpStatus.CREATED);
	}

	@PutMapping("/user/{userId}/post/{postId}")
	private ResponseEntity<PostDTOOuter> editPostByPostId(@Valid @PathVariable int userId, @PathVariable int postId,
			@RequestBody Post post) throws UserNotFoundException, PostNotFoundException, ActionNotAllowedException {
		return new ResponseEntity<>(userService.editPost(userId, postId, post), HttpStatus.OK);
	}

	@DeleteMapping("/user/{userId}/post/{postId}")
	private ResponseEntity<String> deletePostByPostId(@PathVariable int userId, @PathVariable int postId)
			throws ActionNotAllowedException, UserNotFoundException, PostNotFoundException {
		userService.deletePost(userId, postId);

		return new ResponseEntity<>("Post deleted!", HttpStatus.OK);

	}
	// User posts ends

	// User Feed starts here
	@GetMapping("/user/{userId}/feed")
	private ResponseEntity<Object> getFeed(@PathVariable int userId)
			throws UserNotFoundException, PostUnavailableException {
		List<PostDTOOuter> p = userService.getAllPostsFromCommunities(userId);
		return new ResponseEntity<Object>(p, HttpStatus.FOUND);
	}
	// User Feed ends here

	// User Like starts
	@PostMapping("/user/{userId}/post/{postId}/like")
	private ResponseEntity<String> likeAPost(@PathVariable int userId, @PathVariable int postId)
			throws UserNotFoundException, PostNotFoundException, ActionRepititionException {
		userService.likeAPost(userId, postId);
		return new ResponseEntity<String>("You have liked the post", HttpStatus.ACCEPTED);
	}

	@PostMapping("/user/{userId}/post/{postId}/unlike")
	private ResponseEntity<String> unlikeAPost(@PathVariable int userId, @PathVariable int postId)
			throws UserNotFoundException, PostNotFoundException, ActionNotAllowedException {
		userService.unlikeAPost(userId, postId);
		return new ResponseEntity<String>("You have unliked the post", HttpStatus.ACCEPTED);
	}

	// User like ends

	@PostMapping("/user/{userId}/post/{postId}/comment")
	private ResponseEntity<String> commentOnPost(@PathVariable int postId, @PathVariable int userId,
			@Valid @RequestBody Comment comment)
			throws PostNotFoundException, UserNotFoundException, ActionNotAllowedException {
		userService.commentOnPost(postId, userId, comment);
		return new ResponseEntity<String>("Comment added to the post", HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/user/{userId}/post/{postId}/comment/{commentId}")
	private ResponseEntity<String> deleteComment(@PathVariable int postId, @PathVariable int userId,
			@PathVariable int commentId) throws UserNotFoundException, PostNotFoundException, ActionNotAllowedException,
			CommentDoesNotExistException {
		userService.deleteComment(postId, userId, commentId);
		return new ResponseEntity<String>("Comment Deleted", HttpStatus.OK);
	}

	// User following starts here
	@PostMapping("/user/:userId/follow/:followingId")
	public String followUser(@PathVariable int userId, @PathVariable int followingId) {
		return null;
	}

	@PostMapping("/user/:userId/unfollow/:followingId")
	public ResponseEntity<String> unFollowUser(@PathVariable int userId, @PathVariable int followingId) {
		return null;
	}

	@GetMapping("/user/:userId/followers")
	public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable int userId) {
		return null;
	}

	@GetMapping("/user/{userId}/following")
	public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable int userId) throws UserNotFoundException {
		return new ResponseEntity<List<UserDTO>>(userService.getFollowing(userId), HttpStatus.OK);
	}

	@GetMapping("/user/:userId/following/feed")
	public ResponseEntity<List<PostDTOOuter>> getFeedOfFollowingUsers(int userId) {
		return null;
	}
	// User following starts here
}