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
		mav.addObject("page", page);
		mav.addObject("totalPages", us.getPageAmmountForQuestions(currentUser.getId()));
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
		mav.addObject("page", page);
		mav.addObject("totalPages", us.getPageAmmountForAnswers(currentUser.getId()));

		return mav;
	}

	@RequestMapping("/dashboard/community/admitted")
	public ModelAndView viewAdmittedCommunities(@RequestParam(name = "page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/community/admitted");

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

	//Lleva la página al extremo de [0,pageCap] más cercano si se salió del intervalo
	private void adjustPage(Number pageNumber, long pageCap){
		if(pageNumber.longValue() < 0)
			pageNumber = 0;
		else if(pageNumber.longValue() > pageCap)
			pageNumber = pageCap;
	}

	@RequestMapping("/dashboard/community/{communityId}/view/members")
	public ModelAndView viewCommunityMembers(@PathVariable("communityId") Number communityId, @RequestParam(name = "communityPage", required = false, defaultValue = "0") Number communityPage,
	                                         @RequestParam(name="admittedPage", required = false, defaultValue = "0") Number admittedPage, @RequestParam(name="bannedPage", required = false, defaultValue = "0") Number bannedPage,
	                                         @RequestParam(name="success", required = false) Boolean success){
		
		ModelAndView mav = new ModelAndView("/dashboard/community/view/members");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		long communityPages = us.getModeratedCommunitiesPages(currentUser.getId());
		long admittedPages = cs.getMemberByAccessTypePages(communityId, AccessType.ADMITTED);
		long bannedPages = cs.getMemberByAccessTypePages(communityId, AccessType.BANNED);

		adjustPage(communityPage, communityPages);
		adjustPage(admittedPage, admittedPages);
		adjustPage(bannedPage, bannedPages);

		List<Community> moderatedCommunities = us.getModeratedCommunities(currentUser.getId(), communityPage);
		Community community = moderatedCommunities.stream().filter(c -> c.getId() == communityId.longValue()).findFirst().orElseThrow(NoSuchElementException::new);
		List<User> admitted = cs.getMembersByAccessType(communityId, AccessType.ADMITTED, admittedPage);
		List<User> banned = cs.getMembersByAccessType(communityId, AccessType.BANNED, bannedPage);

		mav.addObject("community", community);
		mav.addObject("communityPage", communityPage.intValue());
		mav.addObject("admittedPage", admittedPage.intValue());
		mav.addObject("bannedPage", bannedPage.intValue());
		mav.addObject("moderatedCommunities", moderatedCommunities);
		mav.addObject("communityPages", communityPages);
		mav.addObject("admitted", admitted);
		mav.addObject("admittedPages", admittedPages);
		mav.addObject("banned", banned);
		mav.addObject("bannedPages", bannedPages);
		mav.addObject("operationSuccess", success);

		return mav;
	}

	@RequestMapping("/dashboard/community/{communityId}/view/access")
	public ModelAndView viewCommunityAccess(@PathVariable("communityId") Number communityId, @RequestParam(name = "communityPage", required = false, defaultValue = "0") Number communityPage,
	                                         @RequestParam(name="requestedPage", required = false, defaultValue = "0") Number requestedPage,
	                                         @RequestParam(name="invitedPage", required = false, defaultValue = "0") Number invitedPage,
											 @RequestParam(name="rejectedPage", required = false, defaultValue = "0") Number rejectedPage,
	                                         @RequestParam(name="success", required = false) Boolean success) {
		ModelAndView mav = new ModelAndView("/dashboard/community/view/access");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		long communityPages = us.getModeratedCommunitiesPages(currentUser.getId());
		long requestedPages = cs.getMemberByAccessTypePages(communityId, AccessType.REQUESTED);
		long invitedPages = cs.getMemberByAccessTypePages(communityId, AccessType.INVITED);
		long rejectedPages = cs.getMemberByAccessTypePages(communityId, AccessType.INVITE_REJECTED);

		adjustPage(communityPage, communityPages);
		adjustPage(requestedPage, requestedPages);
		adjustPage(invitedPage, invitedPages);
		adjustPage(rejectedPage, rejectedPages);

		List<Community> moderatedCommunities = us.getModeratedCommunities(currentUser.getId(), communityPage);
		Community community = moderatedCommunities.stream().filter(c -> c.getId() == communityId.longValue()).findFirst().orElseThrow(NoSuchElementException::new);
		List<User> requested = cs.getMembersByAccessType(communityId, AccessType.REQUESTED, requestedPage);
		List<User> invited = cs.getMembersByAccessType(communityId, AccessType.INVITED, invitedPage);
		List<User> rejected = cs.getMembersByAccessType(communityId, AccessType.REQUEST_REJECTED, rejectedPage);

		mav.addObject("community", community);
		mav.addObject("moderatedCommunities", moderatedCommunities);
		mav.addObject("communityPage", communityPage);
		mav.addObject("communityPages", communityPages);
		mav.addObject("requested", requested);
		mav.addObject("requestedPage", requestedPage);
		mav.addObject("requestedPages", requestedPages);
		mav.addObject("invited", invited);
		mav.addObject("invitedPage", invitedPage);
		mav.addObject("invitedPages", invitedPages);
		mav.addObject("rejected", rejected);
		mav.addObject("rejectedPage", rejectedPage);
		mav.addObject("rejectedPages", rejectedPages);
		mav.addObject("operationSuccess", success);

		return mav;
	}



	/*ACCIONES DE MODERADOR*/

	@RequestMapping("/dashboard/community/{communityId}/invite/{userId}")
	public ModelAndView invite(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		boolean inviteSuccess = cs.invite(userId, communityId);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ inviteSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/admitAccess/{userId}")
	public ModelAndView admitAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean admitSuccess = cs.admitAccess(userId, communityId, currentUser);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ admitSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/rejectAccess")
	public ModelAndView rejectAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean admitSuccess = cs.rejectAccess(userId, communityId, currentUser);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ admitSuccess);
	}


	@RequestMapping("/dashboard/community/{communityId}/kick/{userId}")
	public ModelAndView kick(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean kickSuccess = cs.kick(userId, communityId, currentUser);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ kickSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/ban/{userId}")
	public ModelAndView ban(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean banSuccess = cs.ban(userId, communityId, currentUser);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ banSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/liftBan/{userId}")
	public ModelAndView liftBan(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean liftBanSuccess = cs.liftBan(userId, communityId, currentUser);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ liftBanSuccess);
	}

	/*ACCIONES DE USUARIO*/

	@RequestMapping("/dashboard/community/{communityId}/requestAccess")
	public ModelAndView requestAccess(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.requestAccess(currentUser.getId(), communityId);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ success); //TODO: llevarlo al dashboard de usuario
	}


	@RequestMapping("/dashboard/community/{communityId}/acceptInvite")
	public ModelAndView acceptInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.acceptInvite(currentUser.getId(), communityId);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ success);
	}

	@RequestMapping("/dashboard/community/{communityId}/refuseInvite")
	public ModelAndView refuseInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.refuseInvite(currentUser.getId(), communityId);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ success);
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

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ blockSuccess);
	}

	@RequestMapping("/dashboard/community/{communityId}/unblockCommunity")
	public ModelAndView unblockCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean unblockSuccess = cs.unblockCommunity(currentUser.getId(), communityId);

		return new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ unblockSuccess);
	}

}
