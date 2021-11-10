package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.form.InviteUserForm;
import ar.edu.itba.paw.webapp.form.UpdateUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class DashboardController {

	@Autowired
	UserService us;

	@Autowired
	CommunityService cs;

	@RequestMapping("/dashboard/question/view")
	public ModelAndView viewAllQuestions(@RequestParam(name="page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/question/view");
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

		List<Question> questions = us.getQuestions(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("page", page);
		mav.addObject("totalPages", us.getPageAmountForQuestions(currentUser.getId()));
		mav.addObject("questions", questions);

		return mav;
	}

	@RequestMapping("/dashboard/answer/view")
	public ModelAndView viewAllAnswers(@RequestParam(name="page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/answer/view");
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

		List<Answer> answers = us.getAnswers(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("answers", answers);
		mav.addObject("page", page);
		mav.addObject("totalPages", us.getPageAmountForAnswers(currentUser.getId()));

		return mav;
	}

	@RequestMapping("/dashboard/community/manageAccess")
	public ModelAndView manageAccess(@RequestParam(name="invitedPage", required=false, defaultValue = "0") Number invitedPage,
									 @RequestParam(name="requestedPage", required = false, defaultValue = "0") Number requestedPage,
									 @RequestParam(name = "rejectedPage", required = false, defaultValue = "0") Number rejectedPage){
		ModelAndView mav = new ModelAndView("/dashboard/community/manageAccess");
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);


		long requestedPages = us.getCommunitiesByAccessTypePages(currentUser.getId(), AccessType.REQUESTED);
		long invitedPages = us.getCommunitiesByAccessTypePages(currentUser.getId(), AccessType.INVITED);
		long rejectedPages = us.getCommunitiesByAccessTypePages(currentUser.getId(), AccessType.REQUEST_REJECTED);

		adjustPage(requestedPage, requestedPages);
		adjustPage(invitedPage, invitedPages);
		adjustPage(rejectedPage, rejectedPages);

		List<Community> invited = us.getCommunitiesByAccessType(currentUser.getId(), AccessType.INVITED, invitedPage);
		List<Community> requested = us.getCommunitiesByAccessType(currentUser.getId(), AccessType.REQUESTED, requestedPage);
		List<Community> rejected = us.getCommunitiesByAccessType(currentUser.getId(), AccessType.REQUEST_REJECTED, rejectedPage);

		mav.addObject("requested", requested);
		mav.addObject("requestedPage", requestedPage);
		mav.addObject("requestedPages", requestedPages);
		mav.addObject("invited", invited);
		mav.addObject("invitedPage", invitedPage);
		mav.addObject("invitedPages", invitedPages);
		mav.addObject("rejected", rejected);
		mav.addObject("rejectedPage", rejectedPage);
		mav.addObject("rejectedPages", rejectedPages);

		return mav;
	}

	@RequestMapping("/dashboard/community/admitted")
	public ModelAndView viewAdmittedCommunities(@RequestParam(name = "communityPage", required = false, defaultValue = "0") Number communityPage){
		ModelAndView mav = new ModelAndView("/dashboard/community/admitted");
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

		long communityPages = us.getCommunitiesByAccessTypePages(currentUser.getId(), AccessType.ADMITTED);
		adjustPage(communityPage, communityPages);

		List<Community> communities = us.getCommunitiesByAccessType(currentUser.getId(), AccessType.ADMITTED, communityPage);

		mav.addObject("currentUser", currentUser);
		mav.addObject("communities", communities);
		mav.addObject("communityPage", communityPage);
		mav.addObject("communityPages", communityPages);


		return mav;
	}

	@RequestMapping("/dashboard/community/moderated")
	public ModelAndView viewModeratedCommunities(@RequestParam(name = "page", required = false, defaultValue = "0") Number page){
		ModelAndView mav = new ModelAndView("/dashboard/community/moderated");
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

		long totalPages = us.getModeratedCommunitiesPages(currentUser.getId());
		adjustPage(page, totalPages);

		List<Community> communities = us.getModeratedCommunities(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("communities", communities);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);

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
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

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
		User currentUser = AuthenticationUtils.authorizeInView(mav, us).orElseThrow(NoSuchElementException::new);

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
		List<User> rejected = cs.getMembersByAccessType(communityId, AccessType.INVITE_REJECTED, rejectedPage);

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

	@RequestMapping(path ="/dashboard/community/{communityId}/invite", method = RequestMethod.GET)
	public ModelAndView inviteGet(@ModelAttribute("inviteUserForm") InviteUserForm form, @PathVariable("communityId") Number communityId, @RequestParam(value = "e" , required = false) Boolean displayError){
		ModelAndView mav = new ModelAndView("dashboard/inviteUser");
		AuthenticationUtils.authorizeInView(mav, us);
		if(displayError!= null && displayError)
			mav.addObject("displayError", displayError);

		mav.addObject("communityId", communityId);

		return mav;

	}

	@RequestMapping(path="/dashboard/community/{communityId}/invite", method = RequestMethod.POST)
	public ModelAndView invitePost(@PathVariable("communityId") Number communityId ,@ModelAttribute("inviteUserForm")@Valid InviteUserForm form){
		Optional<User> invitedUser = us.findByEmail(form.getEmail());
		if(!invitedUser.isPresent())
			return inviteGet(form, communityId, true);
		boolean inviteSuccess = cs.invite(invitedUser.get().getId(), communityId);
		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/access?&success="+ inviteSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/invite/{userId}", method = RequestMethod.POST)
	public ModelAndView invitePost(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){
		boolean inviteSuccess = cs.invite(userId, communityId); //FIXME: no debería llevar el usuario que lo solicita para hacer chequeos?
		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/access?&success="+ inviteSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}


	@RequestMapping(path="/dashboard/community/{communityId}/admitAccess/{userId}", method = RequestMethod.POST)
	public ModelAndView admitAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean admitSuccess = cs.admitAccess(userId, communityId, currentUser);
		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/access?&success="+ admitSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/rejectAccess/{userId}", method = RequestMethod.POST)
	public ModelAndView rejectAccess(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.rejectAccess(userId, communityId, currentUser);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/access?&success="+ success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}


	@RequestMapping(path="/dashboard/community/{communityId}/kick/{userId}", method = RequestMethod.POST)
	public ModelAndView kick(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean kickSuccess = cs.kick(userId, communityId, currentUser);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ kickSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/ban/{userId}", method = RequestMethod.POST)
	public ModelAndView ban(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean banSuccess = cs.ban(userId, communityId, currentUser);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ banSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/liftBan/{userId}", method = RequestMethod.POST)
	public ModelAndView liftBan(@PathVariable("communityId") Number communityId, @PathVariable("userId") Number userId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean liftBanSuccess = cs.liftBan(userId, communityId, currentUser);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/"+communityId+"/view/members?&success="+ liftBanSuccess);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	/*ACCIONES DE USUARIO*/

	@RequestMapping(path = "/dashboard/community/{communityId}/requestAccess", method = RequestMethod.POST)
	public ModelAndView requestAccess(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.requestAccess(currentUser.getId(), communityId);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/manageAccess?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}


	@RequestMapping(path = "/dashboard/community/{communityId}/acceptInvite", method = RequestMethod.POST)
	public ModelAndView acceptInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.acceptInvite(currentUser.getId(), communityId);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/manageAccess?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/refuseInvite", method = RequestMethod.POST)
	public ModelAndView refuseInvite(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.refuseInvite(currentUser.getId(), communityId);

		ModelAndView mav =  new ModelAndView("redirect:/dashboard/community/manageAccess?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/leaveCommunity", method = RequestMethod.POST)
	public ModelAndView leaveCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.leaveCommunity(currentUser.getId(), communityId);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/admitted?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/blockCommunity", method = RequestMethod.POST)
	public ModelAndView blockCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.blockCommunity(currentUser.getId(), communityId);
		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/admitted?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path="/dashboard/community/{communityId}/unblockCommunity", method = RequestMethod.POST)
	public ModelAndView unblockCommunity(@PathVariable("communityId") Number communityId){

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		boolean success = cs.unblockCommunity(currentUser.getId(), communityId);

		ModelAndView mav = new ModelAndView("redirect:/dashboard/community/admitted?success="+success);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}


	@RequestMapping(path = "/dashboard/user/myProfile")
	public ModelAndView myUserProfile() {
		final ModelAndView mav = new ModelAndView("dashboard/user/myProfile");
		Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
		if ( user.isPresent() ) {
			mav.addObject("karma" , us.getKarma(user.get().getId()).orElse(new Karma(null , -1L)).getKarma());
			mav.addObject("user", user.get());
		}else
		{
			mav.addObject("text_variable" , "No user");
		}
		return mav;
	}


	@RequestMapping(path ="/dashboard/user/updateProfile", method=RequestMethod.GET)
	public ModelAndView updateUserProfileGet(@ModelAttribute("updateUserForm") UpdateUserForm updateUserForm){
		final ModelAndView mav = new ModelAndView("dashboard/user/updateProfile");
		Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
		if ( user.isPresent() ) {
			mav.addObject("karma" , us.getKarma(user.get().getId()).orElse(new Karma(null , -1L)).getKarma());
			mav.addObject("user", user.get());
		}else
		{
			mav.addObject("text_variable" , "No user");
		}
		return mav;
	}


	@RequestMapping(path = "/dashboard/user/updateProfile", method=RequestMethod.POST)
	public ModelAndView updateUserProfilePost(@ModelAttribute("updateUserForm") @Valid UpdateUserForm updateUserForm, BindingResult errors) {

		if(errors.hasErrors()){
			return updateUserProfileGet(updateUserForm);
		}

		return new ModelAndView("redirect:/dashboard/user/myProfile");
	}

}
