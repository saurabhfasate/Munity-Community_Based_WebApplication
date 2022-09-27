package com.sprint1.CapGPlus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprint1.CapGPlus.entity.Admin;
import com.sprint1.CapGPlus.entity.Community;
import com.sprint1.CapGPlus.exception.CommunityAlreadyExistsException;
import com.sprint1.CapGPlus.exception.CommunityNotFoundException;
import com.sprint1.CapGPlus.repository.AdminRepository;
import com.sprint1.CapGPlus.repository.CommunityRepository;

@Service
public class AdminServiceImpl implements AdminService {
	@Autowired
	private AdminRepository adRepo;

	@Autowired
	private CommunityRepository communityRepository;

	@Override
	public String adminLogin(Admin pass) {
		Admin a = adRepo.findAll().get(0);
		if (!a.getPassword().equals(pass.getPassword())) {
			return "Incorrect Password!";
		}
		return "Admin Login Successful";
	}

	// Admin Community starts

	@Override
	public List<Community> getAllCommunities() {
		return communityRepository.findAll();
	}

	@Override
	public Community getCommunityById(int communityId) throws CommunityNotFoundException {
		if (!communityRepository.existsById(communityId))
			throw new CommunityNotFoundException();
		return communityRepository.findById(communityId).get();
	}

	@Override
	public Community addCommunity(Community community) throws CommunityAlreadyExistsException {
		if (communityRepository.existsById(community.getId()))
			throw new CommunityAlreadyExistsException();
		return communityRepository.save(community);
	}

	// Admin Community ends

}
