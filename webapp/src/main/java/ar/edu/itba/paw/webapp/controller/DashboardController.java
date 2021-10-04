package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class DashboardController {

	@Autowired
	UserService us;

	@Autowired
	CommunityService cs;

	@RequestMapping("/dashboard/question/view")
	public ModelAndView viewAllQuestions(@RequestParam("page") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/question/view");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Question> questions = us.getQuestions(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("questions", questions);

		return mav;
	}

	@RequestMapping("/dashboard/answer/view")
	public ModelAndView viewAllAnswers(@RequestParam("page") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/answer/view");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Answer> answers = us.getAnswers(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("answers", answers);

		return mav;
	}

	@RequestMapping("/dashboard/community/admitted")
	public ModelAndView viewAdmittedCommunities(@RequestParam(name = "page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("dashboard/community/admitted");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Community> communities = us.getCommunitiesByAccessType(currentUser.getId(), AccessType.ADMITTED, page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("communities", communities);

		return mav;
	}

	@RequestMapping("/dashboard/community/moderated")
	public ModelAndView viewModeratedCommunities(@RequestParam(name = "page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/community/moderated");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Community> communities = us.getModeratedCommunities(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("communities", communities);

		return mav;
	}

	@RequestMapping("/dashboard/community/{communityId}/view")
	public ModelAndView viewCommunity(@PathVariable("communityId") Number communityId, @RequestParam("success") boolean success){
		ModelAndView mav = new ModelAndView("/dashboard/community/admitted");

		List<User> members = cs.getMembersByAccessType(communityId, null);
		List<User> pending = cs.getMembersByAccessType(communityId, AccessType.REQUESTED);
		List<User> invited = cs.getMembersByAccessType(communityId, AccessType.INVITED);
		List<User> kicked = cs.getMembersByAccessType(communityId, AccessType.KICKED);
		List<User> banned = cs.getMembersByAccessType(communityId, AccessType.BANNED);

		mav.addObject("members", members);
		mav.addObject("pending", pending);
		mav.addObject("invited", invited);
		mav.addObject("kicked", kicked);
		mav.addObject("banned", banned);
		mav.addObject("success", success);

		return mav;
	}

	/*ACCIONES DE MODERADOR*/

	@RequestMapping("/dashboard/community/{communityId}/invite/{userId}")
	public ModelAndView invite(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		boolean inviteSuccess = cs.invite(userId, communityId);

		return viewCommunity(communityId, inviteSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/admitAccess/{userId}")
	public ModelAndView admitAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean admitSuccess = cs.admitAccess(userId, communityId, currentUser);

		return viewCommunity(communityId, admitSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/rejectAccess/{userId}")
	public ModelAndView rejectAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean admitSuccess = cs.rejectAccess(userId, communityId, currentUser);

		return viewCommunity(communityId, admitSuccess);
	}


	@RequestMapping("/dashboard/community/{communityId}/kick/{userId}")
	public ModelAndView kick(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean kickSuccess = cs.kick(userId, communityId, currentUser);

		return viewCommunity(communityId, kickSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/ban/{userId}")
	public ModelAndView ban(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean banSuccess = cs.ban(userId, communityId, currentUser);

		return viewCommunity(communityId, banSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/liftBan/{userId}")
	public ModelAndView liftBan(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean liftBanSuccess = cs.liftBan(userId, communityId, currentUser);

		return viewCommunity(communityId, liftBanSuccess);
	}

	/*ACCIONES DE USUARIO*/

	@RequestMapping("/dashboard/community/{communityId}/requestAccess")
	public ModelAndView requestAccess(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.requestAccess(currentUser.getId(), communityId);

		return viewCommunity(communityId, success);
	}


	@RequestMapping("/dashboard/community/{communityId}/acceptInvite")
	public ModelAndView acceptInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.acceptInvite(currentUser.getId(), communityId);

		return viewCommunity(communityId, success);
	}

	@RequestMapping("/dashboard/community/{communityId}/refuseInvite")
	public ModelAndView refuseInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.refuseInvite(currentUser.getId(), communityId);

		return viewCommunity(communityId, success);
	}

	@RequestMapping("/dashboard/community/{communityId}/leaveCommunity")
	public ModelAndView leaveCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		cs.leaveCommunity(currentUser.getId(), communityId);

		return viewAdmittedCommunities(0);
	}

	@RequestMapping("/dashboard/community/{communityId}/blockCommunity")
	public ModelAndView blockCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean blockSuccess = cs.blockCommunity(currentUser.getId(), communityId);

		return viewCommunity(communityId, blockSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/unblockCommunity")
	public ModelAndView unblockCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean unblockSuccess = cs.unblockCommunity(currentUser.getId(), communityId);

		return viewCommunity(communityId, unblockSuccess);
	}

}
