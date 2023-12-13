package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.QuestionController;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class QuestionAccessControl {

    @Autowired
    private Commons commons;

    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityAccessControl cas;

    @Autowired
    private QuestionService qs;
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @Transactional(readOnly = true)
    public boolean canAccess(long questionId) {
        return canAccess(commons.currentUser(), questionId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long questionId) {
        return canAccess(ac.getUserIfIsRequester(userId), questionId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(User user, long questionId) {
        try {
            Question question = qs.findById(questionId);
            return cas.canAccess(user, question.getForum().getCommunity().getId());
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean canAsk(HttpServletRequest request) {

        if (!request.getContentType().toLowerCase().startsWith("multipart/")) {
            return true;
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setSizeMax(1024 * 1024 * 20L);
        try {

            List<FileItem> items = upload.parseRequest(request);

            for (FileItem item : items) {
                if (item.isFormField() && (item.getFieldName().equals("communityId"))) {

                    return cas.canAccess(commons.currentUser(), Long.parseLong(item.getString()));

                }
            }

        } catch (Exception ignored) {
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean canSearch(HttpServletRequest request) {
        String userIdString = request.getParameter("userId");
        String moderatorIdString = request.getParameter("moderatorId");
        String communityIdString = request.getParameter("communityId");

        if (communityIdString != null && moderatorIdString != null)
            return true; // 400 bad request

        if (userIdString != null && moderatorIdString != null)
            return true; // 400 bad request

        try {
            if (userIdString != null) {
                long userId = Long.parseLong(userIdString);
                if (communityIdString != null) {
                    long communityId = Long.parseLong(communityIdString);
                    return ac.isLoggedUser(userId) && cas.canAccess(commons.currentUser(), communityId);
                }
                return ac.isLoggedUser(userId);
            }
            if (moderatorIdString != null) {
                long userId = Long.parseLong(moderatorIdString);
                return ac.isLoggedUser(userId);

            }
            return true;
        } catch (NumberFormatException ignored) {
            return true;
        }
    }

}
